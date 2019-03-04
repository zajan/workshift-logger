package com.ajna.workshiftlogger.fragments;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.ajna.workshiftlogger.R;
import com.ajna.workshiftlogger.database.ClientsContract;
import com.ajna.workshiftlogger.database.ProjectsContract;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProjectsListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProjectsListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProjectsListFragment extends ListFragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * use SHOW value when open this fragment to display and/or edit client records
     * use PICK value when open this fragment to pick one client record for project
     */
    public enum ProjectListShowOrPick {SHOW, PICK}
    private ProjectListShowOrPick mode;

    private static final String MODE_SHOW_OR_PICK = "modeShowOrPick";

        SimpleCursorAdapter cursorAdapter;

    private OnFragmentInteractionListener mListener;

    public ProjectsListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ProjectsListFragment.
     */
    public static ProjectsListFragment newInstance(ProjectListShowOrPick mode) {
        ProjectsListFragment fragment = new ProjectsListFragment();
        Bundle args = new Bundle();
        args.putSerializable(MODE_SHOW_OR_PICK, mode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            mode = (ProjectsListFragment.ProjectListShowOrPick) args.getSerializable(MODE_SHOW_OR_PICK);
        }

        String[] fromColumns = {ClientsContract.Columns.NAME};
        int[] toViews = {android.R.id.text1}; // The TextView in simple_list_item_1

        // Create an empty adapter we will use to display the loaded data.
        // We pass null for the cursor, then update it in onLoadFinished()
        cursorAdapter = new SimpleCursorAdapter(getContext(),
                android.R.layout.simple_list_item_1, null,
                fromColumns, toViews, 0);

        setListAdapter(cursorAdapter);

        // Prepare the loader. Either re-connect with an existing one,
        // or start a new one.
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_projects_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onNewProjectClicked();
            }
        });

        ListView listView = getListView();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor cursor =(Cursor) adapterView.getItemAtPosition(i);
                String projectName = cursor.getString(cursor.getColumnIndex(ProjectsContract.Columns.NAME));
                long clientId = cursor.getLong(cursor.getColumnIndex(ProjectsContract.FullInfoColumns.CLIENT_ID));
                String clientName = cursor.getString(cursor.getColumnIndex(ProjectsContract.FullInfoColumns.CLIENT_NAME));

                if(mode == ProjectListShowOrPick.PICK){
                    mListener.onProjectPicked(projectName, clientId, clientName);
                } else {
                    mListener.onProjectClicked(projectName);
                }

            }
        });
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

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] PROJECTION = new String[] {ProjectsContract.TABLE_NAME + "." + ProjectsContract.Columns._ID,
                ProjectsContract.TABLE_NAME + "." + ProjectsContract.Columns.NAME,
                ProjectsContract.TABLE_NAME + "." + ProjectsContract.Columns.CLIENT_ID + " AS " + ProjectsContract.FullInfoColumns.CLIENT_ID,
                ClientsContract.TABLE_NAME + "." + ClientsContract.Columns.NAME + " AS " + ProjectsContract.FullInfoColumns.CLIENT_NAME};

        String SELECTION = "((" +
                ProjectsContract.TABLE_NAME + "." + ProjectsContract.Columns.NAME + " NOTNULL) AND (" +
                ProjectsContract.TABLE_NAME + "." + ProjectsContract.Columns.NAME + " != '' ))";

        return new CursorLoader(getContext(), ProjectsContract.CONTENT_URI,
                PROJECTION, SELECTION, null, null);    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        cursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnFragmentInteractionListener {
        /**
         * method called when project is clicked in ProjectsListShowOrPick.SHOW mode
         * @param name name of the picked project
         */
        void onProjectClicked(String name);
        /**
         * method called when project is clicked in ProjectsListShowOrPick.PICK mode
         * @param projectName name of the picked project
         * @param clientId id of client related to picked project
         * @param clientName name of client related to picked project
         */
        void onProjectPicked(String projectName, long clientId, String clientName);

        /**
         * method called after new project button is clicked
         */
        void onNewProjectClicked();
    }
}
