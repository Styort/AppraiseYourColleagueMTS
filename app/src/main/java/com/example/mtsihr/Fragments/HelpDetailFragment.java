package com.example.mtsihr.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mtsihr.Models.DetailHelp;
import com.example.mtsihr.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Страницы для ViewPager во вкладке Help.
 */
public class HelpDetailFragment extends Fragment {
    final String ARG_DESC = "item_desc";
    final String ARG_TITLE = "item_title";
    final String ARG_POSITION = "item_position";
    final String ARG_COUNT = "item_count";
    private View rootView;
    private TextView descTv, pageNum, titleTv;
    private ImageView image;
    private Bundle args;

    public HelpDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_help_detail, container, false);
        args = getArguments();
        initElements();
        return rootView;
    }


    private void initElements() {
        titleTv = (TextView) rootView.findViewById(R.id.title_help_tv);
        descTv = (TextView) rootView.findViewById(R.id.description_help_tv);
        pageNum = (TextView) rootView.findViewById(R.id.page_number_tv);
        image = (ImageView) rootView.findViewById(R.id.image_help_iv);

        titleTv.setText(args.getString(ARG_TITLE));
        descTv.setText(args.getString(ARG_DESC));
        pageNum.setText(args.getInt(ARG_POSITION) + " из " + args.getInt(ARG_COUNT));
    }

}
