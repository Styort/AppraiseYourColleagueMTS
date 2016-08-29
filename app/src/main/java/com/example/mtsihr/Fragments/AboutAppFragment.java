package com.example.mtsihr.Fragments;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.example.mtsihr.BlurBuilder;
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

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("О программе"); //заголовок тулбара

        final ImageView blur = (ImageView) rootView.findViewById(R.id.blur_iv);
        SeekBar seekBar = (SeekBar) rootView.findViewById(R.id.seekBar);
        seekBar.setMax(25); //устанавливаем максимум по размытию изображения

        Bitmap bm = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.photo);

        //inal Bitmap blurredBitmap = BlurBuilder.blur( getActivity(), bm );

        // blur.setImageBitmap(blurredBitmap);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                blur.setImageBitmap(BlurBuilder.blur(getActivity(),
                        BitmapFactory.decodeResource(getContext().getResources(), R.drawable.nav_draw_back), i));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        return rootView;
    }

}
