package com.ajna.workshiftlogger.fragments;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ajna.workshiftlogger.R;
import com.ajna.workshiftlogger.database.ShiftsContract;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ActiveShiftFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ActiveShiftFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ActiveShiftFragment extends Fragment {
    private static final String TAG = "ActiveShiftFragment";

    public static final String SHARED_PREFS_START_TIME = "SharedPrefsStartTime";
    public static final String SHARED_PREFS_PROJECT_NAME = "SharedPrefsProjectName";
    public static final String SHARED_PREFS_PROJECT_ID = "ProjectId";
    public static final String SHARED_PREFS_CLIENT_NAME = "SharedPrefsClientName";
    public static final String SHARED_PREFS_PAUSE = "SharedPrefsPause";

    private boolean isShiftStarted = false;
    private OnFragmentInteractionListener mListener;

    public ActiveShiftFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ActiveShiftFragment.
     */
    public static ActiveShiftFragment newInstance() {
        ActiveShiftFragment fragment = new ActiveShiftFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    private ConstraintLayout activeShiftView;
    private TextView tvStartTime, tvNoActiveShift, tvProjectName, tvClientName;
    private EditText etPause;
    private Button btnStartStop;
    private ImageView btnEditStartTime, btnEditProject;

    private long currentStartTimeInMillis;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_active_shift, container, false);

        activeShiftView = view.findViewById(R.id.active_shift_view);
        tvStartTime = view.findViewById(R.id.tv_start_time);
        tvProjectName = view.findViewById(R.id.tv_project_name);
        tvClientName = view.findViewById(R.id.tv_client_name);
        etPause = view.findViewById(R.id.et_report_pause);
        tvNoActiveShift = view.findViewById(R.id.tv_no_active_shift);
        etPause = view.findViewById(R.id.et_report_pause);
        btnEditStartTime = view.findViewById(R.id.btn_edit_start_time);
        btnEditProject = view.findViewById(R.id.btn_edit_project);
        btnStartStop = view.findViewById(R.id.btn_start_stop);


        btnStartStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isShiftStarted) {
                    finishShift();
                } else {
                    startNewShift();
                }
            }
        });

        btnEditProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onSelectProjectClicked();
            }
        });

        return view;
    }

    private void startNewShift() {
        Log.d(TAG, "startNewShift: starts");
        Date date = Calendar.getInstance().getTime();
        currentStartTimeInMillis = date.getTime();
        updateDate(currentStartTimeInMillis);

        isShiftStarted = true;
        switchLayout(true);
    }

    private void finishShift() {
        boolean isSaveSuccessful = addShiftToDB();
        showSnackbarMessage(isSaveSuccessful);

        resetSharedPrefs();
        resetInputFields();

        isShiftStarted = false;
        switchLayout(false);
    }

    private void resetSharedPrefs() {
        SharedPreferences sharedPrefs = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();

        editor.putLong(SHARED_PREFS_START_TIME, 0);
        editor.putString(SHARED_PREFS_PROJECT_NAME, getString(R.string.default_empty_text_value));
        editor.putString(SHARED_PREFS_CLIENT_NAME, getString(R.string.default_empty_text_value));
        editor.putLong(SHARED_PREFS_PROJECT_ID, 0);
        editor.putLong(SHARED_PREFS_PAUSE, 0);

        editor.apply();
    }
    private void resetInputFields(){
        tvStartTime.setText(getString(R.string.default_empty_text_value));
        tvProjectName.setText(getString(R.string.default_empty_text_value));
        tvClientName.setText(getString(R.string.default_empty_text_value));
        etPause.setText("");
    }

    private boolean addShiftToDB() {
        Log.d(TAG, "addShiftToDB: starts");
        Date date = Calendar.getInstance().getTime();
        long currentTime = date.getTime();
        long projectId = 0;

        String projectName = tvProjectName.getText().toString().trim();
        if(!(projectName.isEmpty()) | (!projectName.equals(getString(R.string.default_empty_text_value)))){
            SharedPreferences sharedPrefs = getActivity().getPreferences(Context.MODE_PRIVATE);
            projectId = sharedPrefs.getLong(SHARED_PREFS_PROJECT_ID, 0);
        }

        String pauseText = etPause.getText().toString().trim();
        int pause;

        try{
            pause = Integer.valueOf(pauseText);
        } catch (NumberFormatException e){
            pause = 0;
        }

        ContentResolver contentResolver = getActivity().getContentResolver();
        ContentValues values = new ContentValues();

        values.put(ShiftsContract.Columns.START_TIME, currentStartTimeInMillis);
        values.put(ShiftsContract.Columns.END_TIME, currentTime);
        values.put(ShiftsContract.Columns.PROJECT_ID, projectId);
        values.put(ShiftsContract.Columns.PAUSE, pause);


        Uri uri = contentResolver.insert(ShiftsContract.CONTENT_URI, values);
        long id = ShiftsContract.getId(uri);

        return (id > 0);
    }

    private void updateDate(long dateInMillis) {
        DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(getContext());
        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getContext());
        tvStartTime.setText(String.format("%s %s", timeFormat.format(dateInMillis), dateFormat.format(dateInMillis)));
    }

    private void showSnackbarMessage(boolean isSavedSuccessful) {
        int messageRes;
        if (isSavedSuccessful) {
            messageRes = R.string.message_successfull_added;
        } else {
            messageRes = R.string.message_problem_adding;
        }
        Snackbar.make(getActivity().findViewById(android.R.id.content), messageRes, Snackbar.LENGTH_SHORT).show();
    }

    private void switchLayout(boolean isActiveShift) {
        Log.d(TAG, "switchLayout: isActiveShift = " + isActiveShift);
        if (isActiveShift) {
            activeShiftView.setVisibility(View.VISIBLE);
            tvNoActiveShift.setVisibility(View.INVISIBLE);
            btnStartStop.setText(R.string.stop);
        } else {
            activeShiftView.setVisibility(View.INVISIBLE);
            tvNoActiveShift.setVisibility(View.VISIBLE);
            btnStartStop.setText(getString(R.string.start));
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

    @Override
    public void onStart() {
        Log.d(TAG, "onStart: starts. isShiftStarted = " + isShiftStarted);
        super.onStart();

        isShiftStarted = readValuesFromSharedPrefs();
        switchLayout(isShiftStarted);
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop: starts");
        super.onStop();

        saveShiftInSharedPrefs();
    }

    private void saveShiftInSharedPrefs() {
        Log.d(TAG, "saveShiftInSharedPrefs: starts");

        if ((!isShiftStarted) || currentStartTimeInMillis <= 0) {
            Log.d(TAG, "saveShiftInSharedPrefs: !isShiftStarted) || currentStartTimeInMillis <= 0  =  true");
            return;
        }

        SharedPreferences sharedPrefs = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();

        editor.putLong(SHARED_PREFS_START_TIME, currentStartTimeInMillis);
        String projectName = tvProjectName.getText().toString();
        Log.d(TAG, "saveShiftInSharedPrefs: projectName = " + projectName);


        if (!projectName.trim().isEmpty()) {
            editor.putString(SHARED_PREFS_PROJECT_NAME, projectName);
        }

        String clientName = tvClientName.getText().toString();
        Log.d(TAG, "saveShiftInSharedPrefs: clientName = " + clientName);
        if (!clientName.trim().isEmpty()) {
            editor.putString(SHARED_PREFS_CLIENT_NAME, clientName);
        }

        String pauseText = etPause.getText().toString();
        Log.d(TAG, "saveShiftInSharedPrefs: pauseText = " + pauseText);
        if (!pauseText.trim().isEmpty()) {
            long pause = Long.valueOf(pauseText);
            editor.putLong(SHARED_PREFS_PAUSE, pause);
        }
        editor.apply();
    }

    /**
     * @return true - when there was a started shift to read in;
     * false - when no shift was started
     */
    private boolean readValuesFromSharedPrefs() {

        SharedPreferences sharedPrefs = getActivity().getPreferences(Context.MODE_PRIVATE);
        long startTime = sharedPrefs.getLong(SHARED_PREFS_START_TIME, 0);

        if (startTime > 0) {

            String projectName = sharedPrefs.getString(SHARED_PREFS_PROJECT_NAME, getString(R.string.default_empty_text_value));
            String clientName = sharedPrefs.getString(SHARED_PREFS_CLIENT_NAME, getString(R.string.default_empty_text_value));
            long pause = sharedPrefs.getLong(SHARED_PREFS_PAUSE, 0);

            currentStartTimeInMillis = startTime;
            updateDate(startTime);

            tvProjectName.setText(projectName);
            tvClientName.setText(clientName);
            etPause.setText(String.valueOf(pause));

            return true;
        }
        return false;
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
