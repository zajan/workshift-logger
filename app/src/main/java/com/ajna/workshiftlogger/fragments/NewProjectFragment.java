package com.ajna.workshiftlogger.fragments;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ajna.workshiftlogger.R;
import com.ajna.workshiftlogger.database.ClientsContract;
import com.ajna.workshiftlogger.database.FactorsContract;
import com.ajna.workshiftlogger.database.ProjectsContract;
import com.ajna.workshiftlogger.model.Client;
import com.ajna.workshiftlogger.model.Project;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NewProjectFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NewProjectFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewProjectFragment extends Fragment  implements LoaderManager.LoaderCallbacks<Cursor>{
    // == constants ==
    private static final String TAG = "NewProjectFragment";
    private static final String INIT_PROJECT_NAME = "initProjectName";

    // == fields ==
    public enum NewProjectFragmentMode {ADD, EDIT;}
    public static final int LOADER_ID = 8;

    private NewProjectFragmentMode mode;
    private String initProjectName;
    private long initProjectId;
    private String clientName;
    private OnFragmentInteractionListener mListener;
    private EditText etProjectName;
    private TextView tvClientName;
    private Button btnSaveProject;

    // == constructor and newInstance() =
    public NewProjectFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param name Name of project.
     * @return A new instance of fragment NewProjectFragment.
     */
    public static NewProjectFragment newInstance(String name) {
        NewProjectFragment fragment = new NewProjectFragment();
        Bundle args = new Bundle();
        args.putString(INIT_PROJECT_NAME, name);
        fragment.setArguments(args);
        return fragment;
    }

    // == callback methods ==
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            initProjectName = getArguments().getString(INIT_PROJECT_NAME);
        }
        if(initProjectName != null){
            mode = NewProjectFragmentMode.EDIT;
        } else {
            mode = NewProjectFragmentMode.ADD;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_project, container, false);
        if (mode == NewProjectFragmentMode.EDIT) {
            getLoaderManager().initLoader(LOADER_ID, null, this);
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etProjectName = view.findViewById(R.id.et_project_name);
        tvClientName = view.findViewById(R.id.tv_client_for_project);
        btnSaveProject = view.findViewById(R.id.btn_save_project);

        if (clientName != null) {
            tvClientName.setText(clientName);
        }
        btnSaveProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (inputManager != null) {
                    inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                if (saveProject()) {
                    mListener.onProjectSaved();
                }
            }
        });

        tvClientName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onSelectClientClicked();
            }
        });
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] PROJECTION = {ProjectsContract.TABLE_NAME + "." + ProjectsContract.Columns._ID,
                ProjectsContract.TABLE_NAME + "." + ProjectsContract.Columns.NAME + " AS " + ProjectsContract.FullInfoColumns.PROJECT_NAME,
                ClientsContract.TABLE_NAME + "." + ClientsContract.Columns.NAME + " AS " + ProjectsContract.FullInfoColumns.CLIENT_NAME};
        String SELECTION = ProjectsContract.TABLE_NAME + "." + ProjectsContract.Columns.NAME + " = ? ";
        String SELECTION_ARGS[] = {initProjectName};

        return new CursorLoader(getContext(), ProjectsContract.CONTENT_URI, PROJECTION, SELECTION, SELECTION_ARGS, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        cursor.moveToFirst();

        initProjectId = cursor.getLong(cursor.getColumnIndex(ProjectsContract.Columns._ID));
        String projectName = cursor.getString(cursor.getColumnIndex(ProjectsContract.FullInfoColumns.PROJECT_NAME));
        String clientName = cursor.getString(cursor.getColumnIndex(ProjectsContract.FullInfoColumns.CLIENT_NAME));
        Log.d(TAG, "onLoadFinished: ---------------------");
        Log.d(TAG, "onLoadFinished: projectName: " + projectName + "  clientName: " + clientName);
        Project project = new Project(projectName, clientName);
        initializeValues(project);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }

    // == public methods ==

    public void updateClient(String name) {
        tvClientName.setText(name);
        clientName = name;
    }

    // == private methods ==
    private boolean saveProject() {
        if (!validateName() | !validateClient()) {
            return false;
        }
        String projectName = etProjectName.getText().toString();
        String clientName = tvClientName.getText().toString();
        long clientId = findClientId(clientName);

        return addProjectToDB(new Project(projectName, clientId)) >= 0;
    }
    private long findClientId(String name){
        long id = -1;

        String[] PROJECTION = {ClientsContract.TABLE_NAME + "." + ClientsContract.Columns._ID};
        String SELECTION = ClientsContract.TABLE_NAME + "." + ClientsContract.Columns.NAME + " = ? ";
        String[] SELECTION_ARGS = {name};
        Cursor cursor = getActivity().getContentResolver().query(ClientsContract.CONTENT_URI, PROJECTION, SELECTION, SELECTION_ARGS, null);
        if(cursor != null && cursor.getCount() >0){
            cursor.moveToFirst();
            id = cursor.getLong(cursor.getColumnIndex(ClientsContract.Columns._ID));
            cursor.close();
        }
        return id;
    }
    private long addProjectToDB(Project project){
        ContentResolver contentResolver = getActivity().getContentResolver();
        ContentValues values = new ContentValues();

        values.put(ProjectsContract.Columns.NAME, project.getName());
        values.put(ProjectsContract.Columns.CLIENT_ID, project.getClientId());
        Uri projectUri;
        if(mode == NewProjectFragmentMode.EDIT) {
            contentResolver.update(ProjectsContract.buildUri(initProjectId), values, null, null);
            return initProjectId;
        } else {
            projectUri = contentResolver.insert(ProjectsContract.CONTENT_URI, values);
            return ProjectsContract.getId(projectUri);
        }
    }

    private boolean validateName() {
        String name = etProjectName.getText().toString().trim();
        if (name.isEmpty()) {
            etProjectName.setError(getString(R.string.field_cannot_be_empty));
            return false;
        }
        return true;
    }

    private boolean validateClient() {
        String clientName = tvClientName.getText().toString().trim();
        if (clientName.isEmpty() || clientName.equals(getString(R.string.select_client))) {
            tvClientName.setError(getString(R.string.client_must_be_selected));
            return false;
        }
        return true;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    private void initializeValues(Project project){
        etProjectName.setText(project.getName());
        tvClientName.setText(project.getClientName());
    }

    // == interface ==
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        /**
         * This method is called when user wants to pick client from the list
         * for currently edited/created project.
         */
        void onSelectClientClicked();

        /**
         * This method is called when user is done with editing/adding project
         * and project is saved
         */
        void onProjectSaved();
    }
}
