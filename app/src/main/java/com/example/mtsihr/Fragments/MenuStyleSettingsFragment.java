package com.example.mtsihr.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mtsihr.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MenuStyleSettingsFragment extends Fragment {
    private View rootView;

    public MenuStyleSettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_menu_style_settings, container, false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Настройки меню"); //заголовок тулбара


        return rootView;
    }

}