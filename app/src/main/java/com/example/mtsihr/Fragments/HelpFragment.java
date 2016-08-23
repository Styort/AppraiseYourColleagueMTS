package com.example.mtsihr.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mtsihr.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class HelpFragment extends Fragment {
    private MyAdapter mAdapter;
    private ViewPager mPager;
    private View rootView;

    public HelpFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_help, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Помощь"); //заголовок тулбара

        mAdapter = new MyAdapter(getFragmentManager());

        mPager = (ViewPager) rootView.findViewById(R.id.help_pager);
        mPager.setAdapter(mAdapter);
        //mPager.setCurrentItem(0); // выводим первый экран экран

        return rootView;
    }


    public static class MyAdapter extends FragmentPagerAdapter {
        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 5;
        } //количество экранов

        @Override
        public Fragment getItem(int position) {
            switch (position) { //что именно показываем в новом экране
                case 0:
                    return new HelpDetailFragment();
                case 1:
                    return new HelpDetailFragment();
                case 2:
                    return new HistoryFragment();
                case 3:
                    return new ColleagueFragment();
                case 4:
                    return new HelpDetailFragment();
                default:
                    return null;
            }
        }
    }


}
