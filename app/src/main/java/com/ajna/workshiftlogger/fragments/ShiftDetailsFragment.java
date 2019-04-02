package com.ajna.workshiftlogger.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
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
import android.widget.TimePicker;

import com.ajna.workshiftlogger.R;
import com.ajna.workshiftlogger.database.FactorsContract;
import com.ajna.workshiftlogger.database.ShiftsContract;
import com.ajna.workshiftlogger.databinding.FragmentShiftDetailsBinding;
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

    // == constants ==
    private static final String TAG = "ShiftDetailsFragment";

    private static final String ARG_SHIFT = "argShift";
    private static final int LOADER_ID = 8;

    // == fields ==
    FragmentShiftDetailsBinding binding;

    private Shift shift;
    private java.text.DateFormat dateFormat;
    private java.text.DateFormat timeFormat;
    private OnFragmentInteractionListener mListener;


    // == constructors and newInstance method ==
    public ShiftDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ShiftDetailsFragment.
     */
    public static ShiftDetailsFragment newInstance(Shift shift) {
        ShiftDetailsFragment fragment = new ShiftDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_SHIFT, shift);
        fragment.setArguments(args);
        return fragment;
    }

    // == callback methods ==
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

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_shift_details, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated: starts");
        super.onViewCreated(view, savedInstanceState);

        binding.viewStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimeDatePicker(true);
            }
        });

        binding.viewEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimeDatePicker(false);
            }
        });

        binding.tvProjectClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onSelectProjectClicked();
            }
        });

        binding.viewPause.setOnClickListener(new View.OnClickListener() {
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
        loader.cancelLoad();
    }


    // == public methods ==

    /**
     * This method updates field of Shift object related to project
     * It's typically called after user picks a project for shift
     * @param projectName name of new project
     * @param projectId id of new project
     * @param clientName name of client related to new project
     */
    public void updateCurrentProject(String projectName, long projectId, String clientName){
        shift.setProjectId(projectId);
        shift.setProjectName(projectName);
        shift.setClientName(clientName);
    }
    // == private methods ==

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


    /**
     * This method shows time and date pickers
     * after user picks time and date it's calling method to update date time value
     * @param isStartDateTime set to true when updating start date/time.
     *                        set to false when updating end date/time.
     */
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
                        updateDateTime(isStartDateTime, dateInMillis);
                    }
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
            }
        }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show();
    }

    /**
     * This method updates date and time on Shift object
     * and then calls initializeFields() method so that other ui fields values are calculated again
     * @param isStartDateTime set to true when updating start date/time.
     *                        set to false when updating end date/time.
     * @param dateInMillis date time to be set
     */
    private void updateDateTime(boolean isStartDateTime, long dateInMillis){
        if (isStartDateTime) {
            shift.setStartTime(dateInMillis);
        } else {
            shift.setEndTime(dateInMillis);
        }
        initializeFields();
    }

    /**
     * This method initializes ui fields based on the Shift object
     */
    private void initializeFields(){
        Log.d(TAG, "initializeFields: starts");
        long duration = shift.calculateDuration();
        long durationHours = duration / 3600000;
        long durationMins = (duration % 3600000) / 60000;
        double factor = shift.getActualFactorInPercent().getFactorInPercent() / 100.0;

        binding.tvStartTime.setText(String.format("%s %s", timeFormat.format(shift.getStartTime()), dateFormat.format(shift.getStartTime())));
        binding.tvEndTime.setText(String.format("%s %s", timeFormat.format(shift.getEndTime()), dateFormat.format(shift.getEndTime())));
        binding.tvProjectClient.setText(String.format("%s\n%s", shift.getProjectName(), shift.getClientName()));
        binding.tvBasePayment.setText(String.valueOf(shift.getBasePayment()));
        binding.tvPause.setText(String.format("%smin", String.valueOf(shift.getPause())));
        binding.tvDuration.setText(String.format("%.2sh %smin", String.valueOf(durationHours), String.valueOf(durationMins)));
        binding.tvPayment.setText(String.format("%sEUR", String.valueOf(shift.calculatePayment())));
        binding.tvFactor.setText(String.valueOf(factor));
    }

    /**
     * This method reads data from cursor and converts it into a list
     * @param cursor cursor with data
     * @return list of factors
     */
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

    /**
     * This method saves(updates) currently edited shift into database
     * based on data currently entered in ui input fields
     */
    private void saveShift(){
        ContentResolver contentResolver = getActivity().getContentResolver();
        ContentValues values = new ContentValues();

        values.put(ShiftsContract.Columns.START_TIME, shift.getStartTime());
        values.put(ShiftsContract.Columns.END_TIME, shift.getEndTime());
        values.put(ShiftsContract.Columns.PROJECT_ID, shift.getProjectId());
        values.put(ShiftsContract.Columns.PAUSE, shift.getPause());

        contentResolver.update(ShiftsContract.buildUri(shift.get_id()), values, null, null);
    }


    // == interfaces ==
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
         * Method called when user wants to pick one project from projects list
         * for currently edited shift
         */
        void onSelectProjectClicked();

        /**
         * Method called when the user is done with updating the shift
         * and can leave this fragment
         */
        void onShiftUpdated();
    }
}