package com.ajna.workshiftlogger.fragments;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
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
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;

import com.ajna.workshiftlogger.R;
import com.ajna.workshiftlogger.adapters.FactorsRecyclerViewAdapter;
import com.ajna.workshiftlogger.database.ClientsContract;
import com.ajna.workshiftlogger.database.FactorsContract;
import com.ajna.workshiftlogger.model.Client;
import com.ajna.workshiftlogger.model.Factor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NewClientFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NewClientFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewClientFragment extends Fragment implements FactorsRecyclerViewAdapter.OnFactorClickListener,
        LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "NewClientFragment";
    public static final int LOADER_ID = 1;


    public enum NewClientFragmentMode {ADD, EDIT}

    private static final String INIT_CLIENT_NAME = "initClientName";

    private NewClientFragmentMode mode;
    private String initClientName;
    private long initClientId;

    private OnFragmentInteractionListener mListener;

    private List<Factor> factors;
    private RecyclerView rvFactors;
    private FactorsRecyclerViewAdapter factorsRVAdapter;

    private EditText etBasePayment;
    private EditText etName;
    private EditText etOfficialName;
    private EditText etAddress;
    private RadioGroup radioGroup;

    public NewClientFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param initClientName set to null when create new client
     *                       set to name of Client when editing.
     * @return A new instance of fragment NewClientFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NewClientFragment newInstance(String initClientName) {
        NewClientFragment fragment = new NewClientFragment();
        Bundle args = new Bundle();
        args.putString(INIT_CLIENT_NAME, initClientName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            initClientName = args.getString(INIT_CLIENT_NAME);
        }

        mode = (initClientName == null) ? NewClientFragmentMode.ADD : NewClientFragmentMode.EDIT;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_new_client, container, false);

        etName = view.findViewById(R.id.et_name);
        etOfficialName = view.findViewById(R.id.et_official_name);
        etAddress = view.findViewById(R.id.et_address);
        etBasePayment = view.findViewById(R.id.et_payment);
        radioGroup = view.findViewById(R.id.radiogroup);
        rvFactors = view.findViewById(R.id.rv_factors);

        if (mode == NewClientFragmentMode.EDIT) {
            getLoaderManager().initLoader(LOADER_ID, null, this);
        }

        if (factors == null) {
            factors = new ArrayList<>();
        }
        factorsRVAdapter = new FactorsRecyclerViewAdapter(factors, this);
        rvFactors.setAdapter(factorsRVAdapter);
        rvFactors.setLayoutManager(new LinearLayoutManager(getContext()));

        ImageView btnAddFactor = view.findViewById(R.id.btn_add_factor);
        btnAddFactor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                LayoutInflater dialogInflater = getActivity().getLayoutInflater();
                final View dialogView = dialogInflater.inflate(R.layout.dialog_add_factor, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Add factor")
                        .setView(dialogView)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                EditText etHours = dialogView.findViewById(R.id.et_hours);
                                EditText etFactor = dialogView.findViewById(R.id.et_factor);
                                int hours = Integer.parseInt(etHours.getText().toString());
                                int factor = Integer.parseInt(etFactor.getText().toString());
                                factors.add(new Factor(hours, factor));
                                Collections.sort(factors);
                                factorsRVAdapter.notifyDataSetChanged();

                                InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                if (inputManager != null) {
                                    inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                                }
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                if (inputManager != null) {
                                    inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                                }
                            }
                        });
                builder.create().show();
            }
        });

        Button btnSave = view.findViewById(R.id.btn_save);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (saveClient()) {
                    mListener.onSaveClicked();
                }
            }
        });

        return view;
    }

    private void initializeValues(Client client, List<Factor> factors) {
        Log.d(TAG, "initializeValues: starts");
        etName.setText(client.getName());
        etOfficialName.setText(client.getOfficialName());
        etAddress.setText(client.getAddress());
        etBasePayment.setText(Integer.toString(client.getBasicPayment()));
        radioGroup.clearCheck();
        if (client.getPaymentType() == 0) {
            radioGroup.check(R.id.radio_flat_rate);
        } else {
            radioGroup.check(R.id.radio_per_h);
        }
        
        this.factors.clear();
        this.factors.addAll(factors);
    }

    public boolean saveClient() {
        if (!validateInput()) {
            return false;
        }

        String name = etName.getText().toString().trim();
        String paymentText = etBasePayment.getText().toString().trim();
        int payment = Integer.parseInt(paymentText);
        String officialName = etOfficialName.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        int paymentType;
        int radioId = radioGroup.getCheckedRadioButtonId();
        if (radioId == R.id.radio_flat_rate) {
            paymentType = 0;
        } else if (radioId == R.id.radio_per_h) {
            paymentType = 1;
        } else {
            throw new IllegalArgumentException("No radio button selected");
        }


        Client client = new Client(name, officialName, address, paymentType, payment);
        long clientId = addClientToDB(client);

        if(clientId < 0){
            return false;
        }

        if ((factors != null) && factors.size() > 0) {
            addFactorsToDB(clientId);
        }

        return true;
    }

    private boolean validateInput() {
        return (validateName() & validatePayment());
    }

    private boolean validateName() {
        String name = etName.getText().toString().trim();
        if (name.isEmpty()) {
            etName.setError(getString(R.string.field_cannot_be_empty));
            return false;
        }
        if(name.equals(getString(R.string.select_client))){
            etName.setError("Name not allowed");
            return false;
        }
        return true;
    }

    private boolean validatePayment() {
        String paymentText = etBasePayment.getText().toString().trim();
        if (paymentText.isEmpty()) {
            etBasePayment.setError(getString(R.string.field_cannot_be_empty));
            return false;
        }

        try {
            int payment = Integer.parseInt(paymentText);
        } catch (NumberFormatException e) {
            etBasePayment.setError("Enter digits only");
            return false;
        }

        return true;
    }

    private long addClientToDB(Client client) {
        ContentResolver contentResolver = getActivity().getContentResolver();
        ContentValues values = new ContentValues();

        values.put(ClientsContract.Columns.NAME, client.getName());
        values.put(ClientsContract.Columns.BASE_PAYMENT, client.getBasicPayment());
        values.put(ClientsContract.Columns.PAY_TYPE, client.getPaymentType());

        if (!client.getOfficialName().isEmpty()) {
            values.put(ClientsContract.Columns.OFFICIAL_NAME, client.getOfficialName());
        }

        if (!client.getAddress().isEmpty()) {
            values.put(ClientsContract.Columns.ADDRESS, client.getAddress());
        }

        if (mode == NewClientFragmentMode.EDIT) {
            contentResolver.update(ClientsContract.buildUri(initClientId), values, null, null);
            return initClientId;
        } else {
            Uri clientUri = contentResolver.insert(ClientsContract.CONTENT_URI, values);
            return ClientsContract.getId(clientUri);
        }
    }

    private void addFactorsToDB(long clientId) {
        ContentResolver contentResolver = getActivity().getContentResolver();
        ContentValues values = new ContentValues();

        for (int i = 0; i < factors.size(); i++) {
            values.put(FactorsContract.Columns.CLIENT_ID, clientId);
            values.put(FactorsContract.Columns.START_HOUR, factors.get(i).getHours());
            values.put(FactorsContract.Columns.VALUE, factors.get(i).getFactorInPercent());

            if (mode == NewClientFragmentMode.EDIT) {
                String WHERE = FactorsContract.Columns.CLIENT_ID + " = ?";
                String[] ARGS = {String.valueOf(initClientId)};
                contentResolver.delete(FactorsContract.CONTENT_URI, WHERE, ARGS);
            } else {
                contentResolver.insert(FactorsContract.CONTENT_URI, values);
            }
        }

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
        String[] PROJECTION = {ClientsContract.TABLE_NAME + "." + ClientsContract.Columns._ID,
                ClientsContract.TABLE_NAME + "." + ClientsContract.Columns.NAME,
                ClientsContract.TABLE_NAME + "." + ClientsContract.Columns.OFFICIAL_NAME,
                ClientsContract.TABLE_NAME + "." + ClientsContract.Columns.ADDRESS,
                ClientsContract.TABLE_NAME + "." + ClientsContract.Columns.PAY_TYPE,
                ClientsContract.TABLE_NAME + "." + ClientsContract.Columns.BASE_PAYMENT,
                FactorsContract.TABLE_NAME + "." + FactorsContract.Columns.CLIENT_ID,
                FactorsContract.TABLE_NAME + "." + FactorsContract.Columns.START_HOUR,
                FactorsContract.TABLE_NAME + "." + FactorsContract.Columns.VALUE
        };

        String SELECTION = ClientsContract.Columns.NAME + " = ? ";
        String SELECTION_ARGS[] = {initClientName};

        return new CursorLoader(getContext(), ClientsContract.CONTENT_URI, PROJECTION, SELECTION, SELECTION_ARGS, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        Log.d(TAG, "onLoadFinished: starts");
        cursor.moveToFirst();

        initClientId = cursor.getLong(cursor.getColumnIndex(ClientsContract.Columns._ID));
        Client client = new Client(cursor.getString(cursor.getColumnIndex(ClientsContract.Columns.NAME)),
                cursor.getString(cursor.getColumnIndex(ClientsContract.Columns.OFFICIAL_NAME)),
                cursor.getString(cursor.getColumnIndex(ClientsContract.Columns.ADDRESS)),
                cursor.getInt(cursor.getColumnIndex(ClientsContract.Columns.PAY_TYPE)),
                cursor.getInt(cursor.getColumnIndex(ClientsContract.Columns.BASE_PAYMENT)));
        factors.clear();
        do {
            List<Factor> factors = new ArrayList<>();
            Factor factor = new Factor(cursor.getInt(cursor.getColumnIndex(FactorsContract.Columns.START_HOUR)),
                    cursor.getInt(cursor.getColumnIndex(FactorsContract.Columns.VALUE)));
            factors.add(factor);
        } while (cursor.moveToNext());
        factorsRVAdapter.notifyDataSetChanged();

        initializeValues(client, factors);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }

    @Override
    public void onDeleteClick(Factor factor) {
        // TODO
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
        void onSaveClicked();
    }
}