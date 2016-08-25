package com.example.mtsihr.Fragments;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.mtsihr.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AboutAppFragment extends Fragment {
    private View rootView;


    public AboutAppFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_about_app, container, false);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("О программе"); //заголовок тулбара

        ImageView blur = (ImageView) rootView.findViewById(R.id.blur_iv);

        return rootView;
    }

}
