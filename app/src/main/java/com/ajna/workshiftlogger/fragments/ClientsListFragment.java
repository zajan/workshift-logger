package com.ajna.workshiftlogger.fragments;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.ajna.workshiftlogger.R;
import com.ajna.workshiftlogger.database.ClientsContract;
import com.ajna.workshiftlogger.model.Client;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ClientsListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ClientsListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ClientsListFragment extends ListFragment
                implements LoaderManager.LoaderCallbacks<Cursor>{
    /**
     * use SHOW value when open this fragment to display and/or edit client records
     * use PICK value when open this fragment to pick one client record for project
     */
    public enum ClientListShowOrPick {SHOW, PICK}

    public static final int LOADER_CLIENTS = 1;

    static final String[] PROJECTION = new String[] {ClientsContract.TABLE_NAME + "." + ClientsContract.Columns._ID,
            ClientsContract.TABLE_NAME + "." + ClientsContract.Columns.NAME};

    // This is the select criteria
    static final String SELECTION = "((" +
            ClientsContract.TABLE_NAME + "." + ClientsContract.Columns.NAME + " NOTNULL) AND (" +
            ClientsContract.TABLE_NAME + "." + ClientsContract.Columns.NAME + " != '' ))";

    private ClientListShowOrPick mode;
    private static final String MODE_SHOW_OR_PICK = "modeShowOrPick";

    SimpleCursorAdapter cursorAdapter;

    private OnFragmentInteractionListener mListener;

    public ClientsListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ClientsListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ClientsListFragment newInstance(ClientListShowOrPick modeShowOrPick) {
        ClientsListFragment fragment = new ClientsListFragment();
        Bundle args = new Bundle();
        args.putSerializable(MODE_SHOW_OR_PICK, modeShowOrPick);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            mode = (ClientListShowOrPick) args.getSerializable(MODE_SHOW_OR_PICK);
        }

        // For the cursor adapter, specify which columns go into which views
        String[] fromColumns = {ClientsContract.Columns.NAME};
        int[] toViews = {android.R.id.text1}; // The TextView in simple_list_item_1

        // Create an empty adapter we will use to display the loaded data.
        // We pass null for the cursor, then update it in onLoadFinished()
        cursorAdapter = new SimpleCursorAdapter(getContext(),
                android.R.layout.simple_list_item_1, null,
                fromColumns, toViews, 0);

        setListAdapter(cursorAdapter);

        // Prepare the loader. Either re-connect with an existing one,
        // or start a new one.
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

        ListView listView = getListView();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor cursor =(Cursor) adapterView.getItemAtPosition(i);
                String clientName = cursor.getString(cursor.getColumnIndex(ClientsContract.Columns.NAME));
                if(mode == ClientListShowOrPick.SHOW){
                    mListener.onClientClicked(clientName);
                } else {
                    mListener.onClientPicked(clientName);
                }
            }
        });
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
        return new CursorLoader(getContext(), ClientsContract.CONTENT_URI,
                PROJECTION, null, null, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        cursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
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
        void onClientClicked(String name);
        void onClientPicked(String name);
        void onNewClientClicked();
    }
}
