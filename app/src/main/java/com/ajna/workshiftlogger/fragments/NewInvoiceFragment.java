package com.ajna.workshiftlogger.fragments;

import android.content.Context;
import android.database.Cursor;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.ajna.workshiftlogger.R;
import com.ajna.workshiftlogger.database.ClientsContract;
import com.ajna.workshiftlogger.database.FactorsContract;
import com.ajna.workshiftlogger.database.ShiftsContract;
import com.ajna.workshiftlogger.model.Factor;
import com.ajna.workshiftlogger.model.Project;
import com.ajna.workshiftlogger.model.Shift;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NewInvoiceFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NewInvoiceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewInvoiceFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "NewInvoiceFragment";

    public static final int LOADER_CLIENT = 1;
    public static final int LOADER_FACTORS = 2;
    public static final int LOADER_SHIFTS = 3;

    private OnFragmentInteractionListener mListener;
    private Project project;
    private boolean areGeneralFieldsInitialized = false;
    private boolean wasProjectChosen = false;
    private List<Factor> factors;

    private TextView tvSelectProject;

    private CheckBox cbInvoiceNr, cbDate, cbProjectName, cbExtra1, cbExtra2, cbName, cbAddress1, cbAddress2,
            cbPhone, cbEmail, cbClientName, cbClientOffName, cbClientAddress;
    private EditText etFileName, etInvoiceNr, etDate, etProjectName, etExtra1, etExtra2, etName, etAddress1, etAddress2,
            etPhone, etEmail, etClientName, etClientOffName, etClientAddress;

    public NewInvoiceFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment NewInvoiceFragment.
     */

    public static NewInvoiceFragment newInstance() {

        return new NewInvoiceFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: starts");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_invoice, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated: starts");
        super.onViewCreated(view, savedInstanceState);
        tvSelectProject = view.findViewById(R.id.tv_select_project);

        cbInvoiceNr = view.findViewById(R.id.cb_invoice_nr);
        cbDate = view.findViewById(R.id.cb_date);
        cbProjectName = view.findViewById(R.id.cb_project_name);
        cbExtra1 = view.findViewById(R.id.cb_extra_1);
        cbExtra2 = view.findViewById(R.id.cb_extra_2);
        cbName = view.findViewById(R.id.cb_name);
        cbAddress1 = view.findViewById(R.id.cb_address);
        cbAddress2 = view.findViewById(R.id.cb_address_2);
        cbPhone = view.findViewById(R.id.cb_phone);
        cbEmail = view.findViewById(R.id.cb_email);
        cbClientName = view.findViewById(R.id.cb_client_name);
        cbClientOffName = view.findViewById(R.id.cb_official_name);
        cbClientAddress = view.findViewById(R.id.cb_client_address);

        etFileName = view.findViewById(R.id.et_file_name);
        etInvoiceNr = view.findViewById(R.id.et_invoice_nr);
        etDate = view.findViewById(R.id.et_date);
        etProjectName = view.findViewById(R.id.et_project_name);
        etExtra1 = view.findViewById(R.id.et_extra_1);
        etExtra2 = view.findViewById(R.id.et_extra_2);
        etName = view.findViewById(R.id.et_name);
        etAddress1 = view.findViewById(R.id.et_address);
        etAddress2 = view.findViewById(R.id.et_address_2);
        etPhone = view.findViewById(R.id.et_phone);
        etEmail = view.findViewById(R.id.et_email);
        etClientName = view.findViewById(R.id.et_client_name);
        etClientOffName = view.findViewById(R.id.et_official_name);
        etClientAddress = view.findViewById(R.id.et_client_address);

        tvSelectProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onSelectProjectClicked();
            }
        });

        if (!areGeneralFieldsInitialized) {
            initializeGeneralFields();
        }
    }

    @Override
    public void onStart() {
        if (project != null) {
            updateProjectFields(project.getName());
        }
        super.onStart();
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

    public void updateCurrentProject(String projectName, long projectId, long clientId, String clientName) {
        Log.d(TAG, "updateCurrentProject: starts");
        project = new Project(projectId, projectName, clientName, clientId);
        updateProjectFields(projectName);
        LoaderManager lm = getLoaderManager();
        if(wasProjectChosen){
            lm.restartLoader(LOADER_CLIENT, null, this);
            lm.restartLoader(LOADER_FACTORS, null, this);
        } else {
            lm.initLoader(LOADER_CLIENT, null, this);
            lm.initLoader(LOADER_FACTORS, null, this);
        }

        wasProjectChosen = true;
    }

    private void initializeGeneralFields() {
        Log.d(TAG, "initializeGeneralFields: starts");
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getContext());
        etDate.setText(dateFormat.format(date));

        SimpleDateFormat invoiceTitleFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        etFileName.setText(String.format("INV-%s", invoiceTitleFormat.format(date)));

        if (project != null) {
            etProjectName.setText(project.getName());
        }

        areGeneralFieldsInitialized = true;
    }

    private void updateProjectFields(String projectName) {
        Log.d(TAG, "updateProjectFields: starts projectName = " + projectName);
        tvSelectProject.setText(projectName);
        etProjectName.setText(projectName);
    }

    private void updateClientFields(Cursor cursor) {
        cursor.moveToFirst();
        etClientName.setText(cursor.getString(cursor.getColumnIndex(ClientsContract.Columns.NAME)));
        etClientOffName.setText(cursor.getString(cursor.getColumnIndex(ClientsContract.Columns.OFFICIAL_NAME)));
        etClientAddress.setText(cursor.getString(cursor.getColumnIndex(ClientsContract.Columns.ADDRESS)));

        cursor.close();
    }

    private void updateFactors(Cursor cursor){
        cursor.moveToFirst();
        factors = new ArrayList<>();
        if (cursor.getCount() > 0) {
            do {
                Factor factor = new Factor(cursor.getInt(cursor.getColumnIndex(FactorsContract.Columns.START_HOUR)),
                        cursor.getInt(cursor.getColumnIndex(FactorsContract.Columns.VALUE)));
                factors.add(factor);
            } while (cursor.moveToNext());
        }

        cursor.close();
    }
    private void updateShiftsFields(Cursor cursor){
        List<Shift> shifts = getShiftsListFromCursor(cursor);

        // TODO

        cursor.close();
    }
    private List<Shift> getShiftsListFromCursor(Cursor cursor){
        List<Shift> shifts = new ArrayList<>();
        cursor.moveToFirst();
        if(cursor.getCount() > 0){
            do {
                Shift shift = new Shift();
                shift.set_id(cursor.getLong(cursor.getColumnIndex(ShiftsContract.Columns._ID)));
                shift.setStartTime(cursor.getLong(cursor.getColumnIndex(ShiftsContract.Columns.START_TIME)));
                shift.setEndTime(cursor.getLong(cursor.getColumnIndex(ShiftsContract.Columns.END_TIME)));
                shift.setPause(cursor.getLong(cursor.getColumnIndex(ShiftsContract.Columns.PAUSE)));
                shift.setFactors(factors);

                shifts.add(shift);
            } while (cursor.moveToNext());
        }
        return shifts;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Log.d(TAG, "onCreateLoader: ID = " + i);
        switch (i) {
            case LOADER_CLIENT:
                String selectionClient = ClientsContract.Columns._ID + " = ?";
                String[] selectionArgsClient = {String.valueOf(project.getClientId())};
                return new CursorLoader(getContext(), ClientsContract.CONTENT_URI, null, selectionClient, selectionArgsClient, null);
            case LOADER_FACTORS:
                String selectionFactors = FactorsContract.Columns.CLIENT_ID + " = ?";
                String[] selectionArgsFactors = {String.valueOf(project.getClientId())};
                String sortOrder = FactorsContract.Columns.START_HOUR;
                return new CursorLoader(getContext(), FactorsContract.CONTENT_URI, null, selectionFactors, selectionArgsFactors, sortOrder);
            case LOADER_SHIFTS:
                String selectionShifts = ShiftsContract.Columns.PROJECT_ID + " = ?";
                String[] selectionArgsShifts = {String.valueOf(project.getId())};
                String sortOrderShifts = ShiftsContract.Columns.START_TIME;
                return new CursorLoader(getContext(), ShiftsContract.CONTENT_URI, null, selectionShifts, selectionArgsShifts, sortOrderShifts);
            default:
                throw new IllegalArgumentException("Loader id not recognized");
        }
    }

    @Override
    public void onLoadFinished(@NonNull android.support.v4.content.Loader<Cursor> loader, Cursor cursor) {
        int loaderID = loader.getId();
        switch (loaderID) {
            case LOADER_CLIENT:
                updateClientFields(cursor);
                break;
            case LOADER_FACTORS:
                updateFactors(cursor);
                // loader for shifts is initialized when loading factors is finished - the reason is to make sure
                // that when loader for shifts is finished factors are already initialized
                // and so calculations for shifts can be processed
                getLoaderManager().initLoader(LOADER_SHIFTS, null, this);
                break;
            case LOADER_SHIFTS:
                updateShiftsFields(cursor);
                break;
        }
    }

    @Override
    public void onLoaderReset(@NonNull android.support.v4.content.Loader<Cursor> loader) {

    }


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
        void onSelectProjectClicked();
    }
}
