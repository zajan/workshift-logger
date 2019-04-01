package com.ajna.workshiftlogger.fragments;

import android.content.Context;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.ajna.workshiftlogger.R;
import com.ajna.workshiftlogger.adapters.InvoiceShiftsRecyclerViewAdapter;
import com.ajna.workshiftlogger.database.ClientsContract;
import com.ajna.workshiftlogger.database.FactorsContract;
import com.ajna.workshiftlogger.database.ShiftsContract;
import com.ajna.workshiftlogger.databinding.FragmentNewInvoiceBinding;
import com.ajna.workshiftlogger.model.Client;
import com.ajna.workshiftlogger.model.Factor;
import com.ajna.workshiftlogger.model.Project;
import com.ajna.workshiftlogger.model.Shift;
import com.ajna.workshiftlogger.utils.InvoicePDFGenerator;

import java.io.File;
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
    private Client client;
    private List<Factor> factors;
    private List<Shift> shifts = new ArrayList<>();

    private boolean areGeneralFieldsInitialized = false;
    private boolean wasProjectChosen = false;
    private boolean wasShiftQueried = false;

    FragmentNewInvoiceBinding binding;


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
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_new_invoice, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        binding.tvSelectProject.setOnClickListener(new View.OnClickListener() {
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.new_invoice_fragment_options, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.create_pdf){
            createPdf();
        }
        return super.onOptionsItemSelected(item);
    }

    private void createPdf(){
        if(!validateFileName()){
            return;
        }
        InvoicePDFGenerator invoice = new InvoicePDFGenerator(binding.etFileName.getText().toString().trim(), getString(R.string.folder_name));

        String name = binding.etName.getText().toString().trim();
        if(binding.cbName.isChecked() && (name.length() > 0)){
            invoice.setPersonName(name);
        }
        String address1 = binding.etAddress.getText().toString().trim();
        if(binding.cbAddress.isChecked() && (address1.length() > 0)){
            invoice.setPersonAddress1(address1);
        }
        String address2 = binding.etAddress2.getText().toString().trim();
        if(binding.cbAddress2.isChecked() && (address2.length() > 0)){
            invoice.setPersonAddress2(address2);
        }
        String phone = binding.etPhone.getText().toString().trim();
        if(binding.cbPhone.isChecked() && (phone.length() > 0)){
            invoice.setPhoneNr(phone);
        }
        String email = binding.etEmail.getText().toString().trim();
        if(binding.cbEmail.isChecked() && (email.length() > 0)){
            invoice.setEmail(email);
        }
        String clientName = binding.etOfficialName.getText().toString().trim();
        if(binding.cbOfficialName.isChecked() && (clientName.length() > 0)){
            invoice.setClientOfficialName(clientName);
        }
        String clientAddress = binding.etClientAddress.getText().toString().trim();
        if(binding.cbClientAddress.isChecked() && (clientAddress.length() > 0)){
            invoice.setClientAddress(clientAddress);
        }

        String invoiceNr = binding.etInvoiceNr.getText().toString().trim();
        if(binding.cbInvoiceNr.isChecked() && (invoiceNr.length() > 0)){
            invoice.setInvoiceNr(invoiceNr);
        }

        invoice.setShifts(shifts, android.text.format.DateFormat.getDateFormat(getContext()), android.text.format.DateFormat.getTimeFormat(getContext()));

        File pdfFile = invoice.generateInvoice();
        if(pdfFile != null){
            invoice.previewPDF(pdfFile, getContext());
        }
    }
private boolean validateFileName(){
        String fileName = binding.etFileName.getText().toString();
        if(fileName.isEmpty() || fileName.trim().length() < 1){
            binding.etFileName.setError("Enter file name");
            return false;
        }
        return true;
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
            wasProjectChosen = true;
        }

    }

    private void initializeGeneralFields() {
        Log.d(TAG, "initializeGeneralFields: starts");
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getContext());
        binding.etDate.setText(dateFormat.format(date));

        SimpleDateFormat invoiceTitleFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        binding.etFileName.setText(String.format("INV-%s", invoiceTitleFormat.format(date)));

        if (project != null) {
            binding.etProjectName.setText(project.getName());
        }

        areGeneralFieldsInitialized = true;
    }

    private void updateProjectFields(String projectName) {
        Log.d(TAG, "updateProjectFields: starts projectName = " + projectName);
        binding.tvSelectProject.setText(projectName);
        binding.etProjectName.setText(projectName);
    }

    private void updateClientFields() {
        binding.etClientName.setText(client.getName());
        binding.etOfficialName.setText(client.getOfficialName());
        binding.etClientAddress.setText(client.getAddress());
    }

    private void updateClient(Cursor cursor){
        cursor.moveToFirst();
        client = new Client(cursor.getString(cursor.getColumnIndex(ClientsContract.Columns.NAME)),
                cursor.getString(cursor.getColumnIndex(ClientsContract.Columns.OFFICIAL_NAME)),
                cursor.getString(cursor.getColumnIndex(ClientsContract.Columns.ADDRESS)),
                cursor.getInt(cursor.getColumnIndex(ClientsContract.Columns.PAY_TYPE)),
                cursor.getColumnIndex(ClientsContract.Columns.BASE_PAYMENT));
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
    }
    private void updateShiftsFields(Cursor cursor){
        shifts = getShiftsListFromCursor(cursor);

        Log.d(TAG, "updateShiftsFields: shifts.size() = " + shifts.size());

    }
    private List<Shift> getShiftsListFromCursor(Cursor cursor){
        cursor.moveToFirst();
        if(cursor.getCount() > 0){
            do {
                Shift shift = new Shift();
                shift.set_id(cursor.getLong(cursor.getColumnIndex(ShiftsContract.Columns._ID)));
                shift.setStartTime(cursor.getLong(cursor.getColumnIndex(ShiftsContract.Columns.START_TIME)));
                shift.setEndTime(cursor.getLong(cursor.getColumnIndex(ShiftsContract.Columns.END_TIME)));
                shift.setPause(cursor.getLong(cursor.getColumnIndex(ShiftsContract.Columns.PAUSE)));
                shift.setFactors(factors);
                shift.setBasePayment(client.getBasicPayment());
                shift.setPaymentType(client.getPaymentType());

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
                updateClient(cursor);
                updateClientFields();
                break;
            case LOADER_FACTORS:
                updateFactors(cursor);
                // loader for shifts is initialized when loading factors is finished - the reason is to make sure
                // that when loader for shifts is finished factors are already initialized
                // and so calculations for shifts can be processed
                if(wasShiftQueried){
                    getLoaderManager().restartLoader(LOADER_SHIFTS, null, this);
                } else {
                    getLoaderManager().initLoader(LOADER_SHIFTS, null, this);
                    wasShiftQueried = true;
                }
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