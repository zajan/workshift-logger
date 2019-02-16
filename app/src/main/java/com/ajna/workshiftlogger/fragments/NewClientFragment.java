package com.ajna.workshiftlogger.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.ajna.workshiftlogger.R;
import com.ajna.workshiftlogger.adapters.FactorsRecyclerViewAdapter;
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
public class NewClientFragment extends Fragment implements FactorsRecyclerViewAdapter.OnFactorClickListener {
    private static final String TAG = "NewClientFragment";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private List<Factor> factors;
    private RecyclerView rvFactors;
    private FactorsRecyclerViewAdapter factorsRVAdapter;

    public NewClientFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NewClientFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NewClientFragment newInstance(String param1, String param2) {
        NewClientFragment fragment = new NewClientFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    EditText etBasePayment;
    EditText etPayPerH;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_client, container, false);
        etBasePayment = view.findViewById(R.id.et_payment);

        if(factors == null) {
            factors = new ArrayList<>();
        }
        rvFactors = view.findViewById(R.id.rv_factors);
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
                                inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);

                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                            }
                        });
                builder.create().show();
            }
        });

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
          //  mListener.onFragmentInteraction(uri);
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
        // TODO: Update argument type and name
        //void onFragmentInteraction(Uri uri);
    }
}