package com.ajna.workshiftlogger.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ajna.workshiftlogger.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NewProjectFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NewProjectFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewProjectFragment extends Fragment {

    private static final String INIT_PROJECT_NAME = "initProjectName";

    private String initProjectName;

    private OnFragmentInteractionListener mListener;

    public NewProjectFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param name Name of project.
     * @return A new instance of fragment NewProjectFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NewProjectFragment newInstance(String name) {
        NewProjectFragment fragment = new NewProjectFragment();
        Bundle args = new Bundle();
        args.putString(INIT_PROJECT_NAME, name);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
           initProjectName = getArguments().getString(INIT_PROJECT_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_project, container, false);
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
    }
}
