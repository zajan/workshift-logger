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

import static com.ajna.workshiftlogger.fragments.ActiveShiftFragment.SHARED_PREFS_CLIENT_NAME;
import static com.ajna.workshiftlogger.fragments.ActiveShiftFragment.SHARED_PREFS_PROJECT_ID;
import static com.ajna.workshiftlogger.fragments.ActiveShiftFragment.SHARED_PREFS_PROJECT_NAME;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ShiftsFragment#newInstance} factory method to
 * create an instance of this fragment.
 * This fragment manages two children fragments
 * and is using ViewPager for navigation between them
 */
public class ShiftsFragment extends Fragment{
    // == constants ==
    private static final String TAG = "ShiftsFragment";

    // == fields ==
    private MyFragmentPagerAdapter adapter;

    // == constructors and newInstance() ==
    public ShiftsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ShiftsFragment.
     */
    public static ShiftsFragment newInstance() {
        ShiftsFragment fragment = new ShiftsFragment();
        return fragment;
    }

    // == callback methods ==
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shifts, container, false);
        setupViewPager(view);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    // == private methods ==
    private void setupViewPager(View view){
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        adapter = new MyFragmentPagerAdapter(getChildFragmentManager());
        adapter.addFragment(ActiveShiftFragment.newInstance(), getString(R.string.active_shift));
        adapter.addFragment(ShiftsListFragment.newInstance(), getString(R.string.finished));

        viewPager.setAdapter(adapter);
        TabLayout tabs = (TabLayout) view.findViewById(R.id.result_tabs);
        tabs.setupWithViewPager(viewPager);
    }

    /**
     * This method updates project of currently active shift
     * which is stored in shared preferences
     * @param projectName
     * @param projectId
     * @param clientName
     */
    public void updateCurrentProject(String projectName, long projectId, String clientName){
        SharedPreferences sharedPrefs = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(SHARED_PREFS_PROJECT_NAME, projectName);
        editor.putLong(SHARED_PREFS_PROJECT_ID, projectId);
        editor.putString(SHARED_PREFS_CLIENT_NAME, clientName);
        editor.apply();
    }

    // == inner class ==

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