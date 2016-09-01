package com.example.mtsihr.Fragments;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.example.mtsihr.BlurBuilder;
import com.example.mtsihr.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AboutAppFragment extends Fragment {
    private View rootView;
    private RelativeLayout helpShowRelative, feedbackRelative;

    public AboutAppFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_about_app, container, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("О программе"); //заголовок тулбара


        initElements();
        return rootView;
    }

    private void initElements() {
        helpShowRelative = (RelativeLayout) rootView.findViewById(R.id.go_to_help_relative);
        feedbackRelative = (RelativeLayout) rootView.findViewById(R.id.feedback_relative);

        helpShowRelative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new HelpFragment();
                FragmentManager fm = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.replace(((ViewGroup) getView().getParent()).getId(), fragment).addToBackStack(null).commit();
            }
        });
        feedbackRelative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:"));
                //кому отправить
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"prosto_mail@mts.ru"});
                //тема письма
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Обратная связь");
                //текст письма
                emailIntent.putExtra(Intent.EXTRA_TEXT, "");

                if (emailIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(Intent.createChooser(emailIntent, "Отправить отзыв..."));
                }
            }
        });
    }

}
