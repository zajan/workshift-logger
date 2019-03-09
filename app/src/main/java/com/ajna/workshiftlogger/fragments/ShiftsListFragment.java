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
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ajna.workshiftlogger.R;
import com.ajna.workshiftlogger.adapters.ShiftsRecyclerViewAdapter;
import com.ajna.workshiftlogger.database.ClientsContract;
import com.ajna.workshiftlogger.database.FactorsContract;
import com.ajna.workshiftlogger.database.ProjectsContract;
import com.ajna.workshiftlogger.database.ShiftsContract;
import com.ajna.workshiftlogger.model.Shift;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ShiftsListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ShiftsListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShiftsListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        ShiftsRecyclerViewAdapter.OnShiftInteractionListener{
    private static final String TAG = "ShiftsListFragment";

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
            shiftsRVAdapter = new ShiftsRecyclerViewAdapter(getContext(), null, this);
        }
        rvShifts.addItemDecoration(new DividerItemDecoration(rvShifts.getContext(), DividerItemDecoration.VERTICAL));

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

        String[] projection = {ShiftsContract.TABLE_NAME + "." + ShiftsContract.Columns._ID,
                ShiftsContract.Columns.START_TIME,
                ShiftsContract.Columns.END_TIME,
                ProjectsContract.TABLE_NAME + "." + ProjectsContract.Columns.NAME + " AS " + ShiftsContract.FullInfoColumns.PROJECT_NAME,
                ClientsContract.TABLE_NAME + "." + ClientsContract.Columns._ID + " AS " + ShiftsContract.FullInfoColumns.CLIENT_ID,
                ClientsContract.TABLE_NAME + "." + ClientsContract.Columns.NAME + " AS " + ShiftsContract.FullInfoColumns.CLIENT_NAME,
                ClientsContract.TABLE_NAME + "." + ClientsContract.Columns.BASE_PAYMENT,
                ClientsContract.TABLE_NAME + "." + ClientsContract.Columns.PAY_TYPE,
                ShiftsContract.TABLE_NAME + "." + ShiftsContract.Columns.PAUSE,
                FactorsContract.TABLE_NAME + "." + FactorsContract.Columns.VALUE,
                FactorsContract.TABLE_NAME + "." + FactorsContract.Columns.START_HOUR};
        String selection = null;
        String[] selectionArgs = null;
        String sortOrder = ShiftsContract.Columns.START_TIME + " DESC";

        return new CursorLoader(getContext(), ShiftsContract.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "onLoadFinished: cursor.getCount(): " + data.getCount());
        shiftsRVAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        shiftsRVAdapter.swapCursor(null);
    }


    @Override
    public void onShiftClicked(Shift shift) {
        Log.d(TAG, "onShiftClicked: " + shift.getClientName());
        mListener.onShiftClicked(shift);
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
        void onShiftClicked(Shift shift);
    }
}
