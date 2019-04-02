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

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ClientsListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ClientsListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ClientsListFragment extends ListFragment
        implements LoaderManager.LoaderCallbacks<Cursor> {
    // == constants ==
    public static final int LOADER_CLIENTS = 1;
    private static final String MODE_SHOW_OR_PICK = "modeShowOrPick";


    // == fields ==
    private ClientListShowOrPick mode;
    private SimpleCursorAdapter cursorAdapter;
    private OnFragmentInteractionListener mListener;


    // == constructors and newInstance() ==
    public ClientsListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ClientsListFragment.
     */
    public static ClientsListFragment newInstance(ClientListShowOrPick modeShowOrPick) {
        ClientsListFragment fragment = new ClientsListFragment();
        Bundle args = new Bundle();
        args.putSerializable(MODE_SHOW_OR_PICK, modeShowOrPick);
        fragment.setArguments(args);
        return fragment;
    }


    // == callback methods ==
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            mode = (ClientListShowOrPick) args.getSerializable(MODE_SHOW_OR_PICK);
        }
        setupListViewAdapter();

        getLoaderManager().initLoader(LOADER_CLIENTS, null, this);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_clients_list, container, false);

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onNewClientClicked();
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupListView();
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
        String[] projection = new String[]{ClientsContract.TABLE_NAME + "." + ClientsContract.Columns._ID,
                ClientsContract.TABLE_NAME + "." + ClientsContract.Columns.NAME};
        return new CursorLoader(getContext(), ClientsContract.CONTENT_URI,
                projection, null, null, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        cursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }

    // == private methods ==
    private void setupListViewAdapter(){
        String[] fromColumns = {ClientsContract.Columns.NAME};
        int[] toViews = {android.R.id.text1}; // The TextView in simple_list_item_1

        cursorAdapter = new SimpleCursorAdapter(getContext(),
                android.R.layout.simple_list_item_1, null,
                fromColumns, toViews, 0);

        setListAdapter(cursorAdapter);
    }
    private void setupListView(){
        ListView listView = getListView();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(i);
                String clientName = cursor.getString(cursor.getColumnIndex(ClientsContract.Columns.NAME));
                if (mode == ClientListShowOrPick.SHOW) {
                    mListener.onClientClicked(clientName);
                } else {
                    mListener.onClientPicked(clientName);
                }
            }
        });
    }

    // == interface ==
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
         * This method is called when list item representing client was clicked
         * while mode is set to ClientListShowOrPick.SHOW
         * @param name name of the clicked client
         */
        void onClientClicked(String name);

        /**
         * This method is called when list item representing client was clicked
         * while mode is set to ClientListShowOrPick.PICK
         * @param name name of the clicked client
         */
        void onClientPicked(String name);

        /**
         * This method is called when user clicks new client button
         */
        void onNewClientClicked();
    }

    // == enum ==

    /**
     * use SHOW value when open this fragment to display and/or edit client records
     * use PICK value when open this fragment to pick one client record for project
     */
    public enum ClientListShowOrPick {
        SHOW, PICK
    }
}