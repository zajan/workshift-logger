package com.ajna.workshiftlogger.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.ajna.workshiftlogger.R;
import com.ajna.workshiftlogger.database.FactorsContract;
import com.ajna.workshiftlogger.database.ShiftsContract;
import com.ajna.workshiftlogger.model.Factor;
import com.ajna.workshiftlogger.model.Shift;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ShiftDetailsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ShiftDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShiftDetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "ShiftDetailsFragment";

    private static final String ARG_SHIFT = "argShift";
    private static final int LOADER_ID = 8;

    private Shift shift;
    private java.text.DateFormat dateFormat;
    private java.text.DateFormat timeFormat;
    private OnFragmentInteractionListener mListener;

    private TextView tvStartTime, tvEndTime, tvPause, tvProjectClientName, tvDuration, tvBasePayment, tvPayment, tvFactor;
    private View viewStartTime, viewEndTime, viewPause, viewProjectClientName;

    public ShiftDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ShiftDetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ShiftDetailsFragment newInstance(Shift shift) {
        ShiftDetailsFragment fragment = new ShiftDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_SHIFT, shift);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: starts");
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (getArguments() != null) {
            shift = (Shift) getArguments().getSerializable(ARG_SHIFT);
            Log.d(TAG, "onCreate: arguments != null");
        } else {
            Log.d(TAG, "onCreate: arguments = null");
        }
        dateFormat = android.text.format.DateFormat.getDateFormat(getContext());
        timeFormat = android.text.format.DateFormat.getTimeFormat(getContext());

        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_shift_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated: starts");
        super.onViewCreated(view, savedInstanceState);
        tvStartTime = view.findViewById(R.id.tv_start_time);
        tvEndTime = view.findViewById(R.id.tv_end_time);
        tvBasePayment = view.findViewById(R.id.tv_base_payment);
        tvPayment = view.findViewById(R.id.tv_payment);
        tvPause = view.findViewById(R.id.tv_pause);
        tvProjectClientName = view.findViewById(R.id.tv_project_client);
        tvDuration = view.findViewById(R.id.tv_duration);
        tvFactor = view.findViewById(R.id.tv_factor);
        viewStartTime = view.findViewById(R.id.view_start_time);
        viewEndTime = view.findViewById(R.id.view_end_time);
        viewPause = view.findViewById(R.id.view_pause);
        viewProjectClientName = view.findViewById(R.id.view_project);

        viewStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimeDatePicker(true);
            }
        });

        viewEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimeDatePicker(false);
            }
        });

        viewProjectClientName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onSelectProjectClicked();
            }
        });

        viewPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPauseTimePicker();
            }
        });

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.shift_details_fragment_options, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.save_shift){
            saveShift();
            mListener.onShiftUpdated();
        }
        return super.onOptionsItemSelected(item);
    }

    public void updateCurrentProject(String projectName, long projectId, String clientName){
        Log.d(TAG, "updateCurrentProject: starts");

        shift.setProjectId(projectId);
        shift.setProjectName(projectName);
        shift.setClientName(clientName);
    }

    private void showPauseTimePicker(){

        LayoutInflater dialogInflater = getActivity().getLayoutInflater();
        final View dialogView = dialogInflater.inflate(R.layout.dialog_number_picker, null);
        final NumberPicker numberPicker = dialogView.findViewById(R.id.number_picker_pause);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(1000);
        numberPicker.setValue((int)shift.getPause());
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Edit pause time")
                .setView(dialogView)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        shift.setPause(numberPicker.getValue());
                        initializeFields();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        builder.create().show();
    }

    private void showTimeDatePicker(final boolean isStartDateTime) {
        final Calendar c = Calendar.getInstance();
        if(isStartDateTime){
            c.setTime(new Date(shift.getStartTime()));
        } else {
            c.setTime(new Date(shift.getEndTime()));
        }

        new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, final int hour, final int minute) {
                new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, month, day, hour, minute);
                        long dateInMillis = calendar.getTimeInMillis();
                        if (isStartDateTime) {
                            shift.setStartTime(dateInMillis);
                        } else {
                            shift.setEndTime(dateInMillis);
                        }
                        initializeFields();
                    }
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
            }
        }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show();
    }

    private void initializeFields(){
        Log.d(TAG, "initializeFields: starts");
        long duration = shift.calculateDuration();
        long durationHours = duration / 3600000;
        long durationMins = (duration % 3600000) / 60000;
        double factor = shift.getActualFactorInPercent().getFactorInPercent() / 100.0;

        tvStartTime.setText(String.format("%s %s", timeFormat.format(shift.getStartTime()), dateFormat.format(shift.getStartTime())));
        tvEndTime.setText(String.format("%s %s", timeFormat.format(shift.getEndTime()), dateFormat.format(shift.getEndTime())));
        tvProjectClientName.setText(String.format("%s\n%s", shift.getProjectName(), shift.getClientName()));
        tvBasePayment.setText(String.valueOf(shift.getBasePayment()));
        tvPause.setText(String.format("%smin", String.valueOf(shift.getPause())));
        tvDuration.setText(String.format("%.2sh %smin", String.valueOf(durationHours), String.valueOf(durationMins)));
        tvPayment.setText(String.format("%sEUR", String.valueOf(shift.calculatePayment())));
        tvFactor.setText(String.valueOf(factor));
    }

    private List<Factor> getFactorsListFromCursor(Cursor cursor) {
        cursor.moveToFirst();
        List<Factor> factors = new ArrayList<>();
        if (cursor.getCount() > 0) {
            do {
                Factor factor = new Factor(cursor.getInt(cursor.getColumnIndex(FactorsContract.Columns.START_HOUR)),
                        cursor.getInt(cursor.getColumnIndex(FactorsContract.Columns.VALUE)));
                factors.add(factor);
            } while (cursor.moveToNext());
        }
        return factors;
    }
    private void saveShift(){

        ContentResolver contentResolver = getActivity().getContentResolver();
        ContentValues values = new ContentValues();

        values.put(ShiftsContract.Columns.START_TIME, shift.getStartTime());
        Log.d(TAG, "saveShift: starttime = " + shift.getStartTime());
        values.put(ShiftsContract.Columns.END_TIME, shift.getEndTime());
        Log.d(TAG, "saveShift: shiftendtime = " + shift.getEndTime());
        values.put(ShiftsContract.Columns.PROJECT_ID, shift.getProjectId());
        Log.d(TAG, "saveShift: shiftProjectid = " + shift.getProjectId());
        values.put(ShiftsContract.Columns.PAUSE, shift.getPause());
        Log.d(TAG, "saveShift: shiftpause  = " + shift.getPause());


        int i = contentResolver.update(ShiftsContract.buildUri(shift.get_id()), values, null, null);
        Log.d(TAG, "saveShift: i = " + i);
    }

    @Override
    public void onAttach(Context context) {
        Log.d(TAG, "onAttach: starts");
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
        Log.d(TAG, "onDetach: starts");
        super.onDetach();
        mListener = null;
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        Log.d(TAG, "onCreateLoader: starts");
        String[] projection = {FactorsContract.Columns.START_HOUR,
                FactorsContract.Columns.VALUE};
        String selection = FactorsContract.Columns.CLIENT_ID + " = ?";
        String[] selectionArgs = {String.valueOf(shift.getClientId())};
        String sortOrder = FactorsContract.Columns.START_HOUR;

        return new CursorLoader(getContext(), FactorsContract.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "onLoadFinished: starts");
        shift.setFactors(getFactorsListFromCursor(data));
        initializeFields();
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

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
        void onShiftUpdated();
    }
}
