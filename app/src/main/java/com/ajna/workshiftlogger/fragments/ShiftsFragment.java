package com.ajna.workshiftlogger.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ajna.workshiftlogger.R;

import java.util.ArrayList;
import java.util.List;

import static com.ajna.workshiftlogger.fragments.ActiveShiftFragment.SHARED_PREFS_PROJECT_NAME;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ShiftsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ShiftsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShiftsFragment extends Fragment{
    private static final String TAG = "ShiftsFragment";

    private OnFragmentInteractionListener mListener;

    private MyFragmentPagerAdapter adapter;

    public ShiftsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ShiftsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ShiftsFragment newInstance() {
        ShiftsFragment fragment = new ShiftsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shifts, container, false);
        // Setting ViewPager for each Tabs
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        adapter = new MyFragmentPagerAdapter(getChildFragmentManager());
        adapter.addFragment(ActiveShiftFragment.newInstance(), "Active shift");
        adapter.addFragment(ShiftsListFragment.newInstance(), "Finished");

        viewPager.setAdapter(adapter);
        // Set Tabs inside Toolbar
        TabLayout tabs = (TabLayout) view.findViewById(R.id.result_tabs);
        tabs.setupWithViewPager(viewPager);
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

    public void updateCurrentProject(String name){
        SharedPreferences sharedPrefs = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(SHARED_PREFS_PROJECT_NAME, name);
        editor.apply();

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     */
    public interface OnFragmentInteractionListener {

    }

    static class MyFragmentPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public MyFragmentPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}