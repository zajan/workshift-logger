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
import android.widget.TextView;

import com.ajna.workshiftlogger.R;
import com.ajna.workshiftlogger.database.FactorsContract;
import com.ajna.workshiftlogger.model.Factor;
import com.ajna.workshiftlogger.model.Shift;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ShiftDetailsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ShiftDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShiftDetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final String TAG = "ShiftDetailsFragment";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_SHIFT = "param1";
    public static final int LOADER_ID = 8;

    // TODO: Rename and change types of parameters
    private Shift shift;
    private java.text.DateFormat dateFormat;
    private java.text.DateFormat timeFormat;
    private OnFragmentInteractionListener mListener;

    private TextView tvStartTime, tvEndTime, tvPause, tvProjectClientName, tvDuration, tvBasePayment, tvPayment, tvFactor;

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
        if (getArguments() != null) {
            shift = (Shift) getArguments().getSerializable(ARG_SHIFT);
            Log.d(TAG, "onCreate: arguments != null");
        } else {
            Log.d(TAG, "onCreate: arguments = null");
        }
        dateFormat = android.text.format.DateFormat.getDateFormat(getContext());
        timeFormat = android.text.format.DateFormat.getTimeFormat(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getLoaderManager().initLoader(LOADER_ID, null, this);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_shift_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvStartTime = view.findViewById(R.id.tv_start_time);
        tvEndTime = view.findViewById(R.id.tv_end_time);
        tvBasePayment = view.findViewById(R.id.tv_base_payment);
        tvPayment = view.findViewById(R.id.tv_payment);
        tvPause = view.findViewById(R.id.tv_pause);
        tvProjectClientName = view.findViewById(R.id.tv_project_client);
        tvDuration = view.findViewById(R.id.tv_duration);
        tvFactor = view.findViewById(R.id.tv_factor);

    }

    private void initializeFields(Cursor factorsCursor){
        shift.setFactors(getFactorsListFromCursor(factorsCursor));

        long duration = shift.calculateDuration();
        long durationHours = duration / 3600000;
        long durationMins = (duration % 3600000) / 60000;
        double factor = shift.getActualFactorInPercent().getFactorInPercent()/100.0;

        tvStartTime.setText(String.format("%s %s", timeFormat.format(shift.getStartTime()), dateFormat.format(shift.getStartTime())));
        tvEndTime.setText(String.format("%s %s", timeFormat.format(shift.getEndTime()), dateFormat.format(shift.getEndTime())));
        tvProjectClientName.setText(String.format("%s\n%s", shift.getProjectName(), shift.getClientName()));
        tvBasePayment.setText(String.valueOf(shift.getBasePayment()));
        tvPause.setText(String.format("%smin", String.valueOf(shift.getPause())));
        tvDuration.setText(String.format("%.2sh %smin", String.valueOf(durationHours), String.valueOf(durationMins)));
        tvPayment.setText(String.format("%sEUR", String.valueOf(shift.calculatePayment())));
        tvFactor.setText(String.valueOf(factor));
    }

    private List<Factor> getFactorsListFromCursor(Cursor cursor){
        cursor.moveToFirst();
        List<Factor> factors = new ArrayList<>();
        if(cursor.getCount() > 0){
            do {
                Factor factor = new Factor(cursor.getInt(cursor.getColumnIndex(FactorsContract.Columns.START_HOUR)),
                        cursor.getInt(cursor.getColumnIndex(FactorsContract.Columns.VALUE)));
                factors.add(factor);
            } while (cursor.moveToNext());
        }
        return factors;
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
        String[] projection = {FactorsContract.Columns.START_HOUR,
                                FactorsContract.Columns.VALUE};
        String selection = FactorsContract.Columns.CLIENT_ID + " = ?";
        String[] selectionArgs = {String.valueOf(shift.getClientId())};
        String sortOrder = FactorsContract.Columns.START_HOUR;

        return new CursorLoader(getContext(), FactorsContract.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        initializeFields(data);
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
        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
    }
}
