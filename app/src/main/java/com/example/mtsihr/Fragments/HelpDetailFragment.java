package com.example.mtsihr.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mtsihr.R;

/**
 * Страницы для ViewPager во вкладке Help.
 */
public class HelpDetailFragment extends Fragment {


    public HelpDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_help_detail, container, false);
        TextView textView = (TextView) view.findViewById(R.id.detailsText);
        textView.setText("Page");
        return view;
    }

}
