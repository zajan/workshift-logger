package com.ajna.workshiftlogger.fragments;

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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ajna.workshiftlogger.R;
import com.ajna.workshiftlogger.adapters.ShiftsRecyclerViewAdapter;
import com.ajna.workshiftlogger.database.ShiftFullInfoViewContract;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ShiftsListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ShiftsListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShiftsListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    public static final int LOADER_ID = 5;

    private OnFragmentInteractionListener mListener;

    private ShiftsRecyclerViewAdapter shiftsRVAdapter;

    public ShiftsListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ShiftsListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ShiftsListFragment newInstance() {
        ShiftsListFragment fragment = new ShiftsListFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shifts_list, container, false);

        RecyclerView rvShifts = view.findViewById(R.id.rv_shifts);
        rvShifts.setLayoutManager(new LinearLayoutManager(getContext()));
        // Create an empty adapter we will use, to display the loaded data.
        if(shiftsRVAdapter == null) {
            shiftsRVAdapter = new ShiftsRecyclerViewAdapter(getContext(), null);
        }
        rvShifts.setAdapter(shiftsRVAdapter);

        return view;
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
        String[] projection = {ShiftFullInfoViewContract.Columns._ID,
                ShiftFullInfoViewContract.Columns.START_TIME,
                ShiftFullInfoViewContract.Columns.END_TIME,
                ShiftFullInfoViewContract.Columns.PROJECT_NAME,
                ShiftFullInfoViewContract.Columns.CLIENT_NAME,
                ShiftFullInfoViewContract.Columns.BASE_PAYMENT,
                ShiftFullInfoViewContract.Columns.PAYMENT_TYPE,
                ShiftFullInfoViewContract.Columns.PAUSE,
                ShiftFullInfoViewContract.Columns.FACTOR_VALUE,
                ShiftFullInfoViewContract.Columns.FACTOR_HOUR};
        String selection = null;
        String[] selectionArgs = null;
        String sortOrder = null;

        return new CursorLoader(getContext(), ShiftFullInfoViewContract.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        shiftsRVAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        shiftsRVAdapter.swapCursor(null);
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

    }
}