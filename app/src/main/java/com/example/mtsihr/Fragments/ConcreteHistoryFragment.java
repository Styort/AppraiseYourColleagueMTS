package com.example.mtsihr.Fragments;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.mtsihr.R;

import java.util.ArrayList;
import java.util.Arrays;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConcreteHistoryFragment extends Fragment {
    public static final String ARG_TEXT = "item_text";
    public static final String ARG_POSITION = "item_position";
    public static final String ARG_COUNT = "item_count";
    private View rootView;
    private TextView  partnershipTV, efficiencyTV, responsibilityTV,
            courageTV, creativityTV, opennessTV, nameCollTV, dateOfEvalTV,
            postCollTV, subdivCollTV, commentTV;
    private Bundle getDataBundle;
    private CircleImageView colleagueCirclePhoto;
    public ConcreteHistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Оценка"); //заголовок тулбара
        rootView = inflater.inflate(R.layout.fragment_concrete_history, container, false);

        getDataBundle = getArguments();
        initElements();

        return rootView;
    }

    private void initElements() {
        partnershipTV = (TextView) rootView.findViewById(R.id.partnership_tv);
        efficiencyTV = (TextView) rootView.findViewById(R.id.efficiency_tv);
        responsibilityTV = (TextView) rootView.findViewById(R.id.responsibility_tv);
        courageTV = (TextView) rootView.findViewById(R.id.courage_tv);
        creativityTV = (TextView) rootView.findViewById(R.id.creativity_tv);
        opennessTV = (TextView) rootView.findViewById(R.id.openness_tv);
        nameCollTV = (TextView) rootView.findViewById(R.id.name_concr_hist_tv);
        dateOfEvalTV = (TextView) rootView.findViewById(R.id.concrete_hist_date_tv);
        postCollTV = (TextView) rootView.findViewById(R.id.post_concr_hist_tv);
        subdivCollTV = (TextView) rootView.findViewById(R.id.subdivision_concr_hist_tv);
        commentTV = (TextView) rootView.findViewById(R.id.comment_tv);
        colleagueCirclePhoto = (CircleImageView) rootView.findViewById(R.id.photo_concr_hist_civ);

        //заполнение полей данными
        nameCollTV.setText(getDataBundle.getString("name"));
        postCollTV.setText(getDataBundle.getString("post"));
        subdivCollTV.setText(getDataBundle.getString("subdiv"));
        dateOfEvalTV.setText(getDataBundle.getString("date"));
        partnershipTV.setText(getDataBundle.getString("parthnership"));
        responsibilityTV.setText(getDataBundle.getString("responsibility"));
        efficiencyTV.setText(getDataBundle.getString("efficiency"));
        courageTV.setText(getDataBundle.getString("courage"));
        creativityTV.setText(getDataBundle.getString("creativity"));
        opennessTV.setText(getDataBundle.getString("openness"));
        commentTV.setText(getDataBundle.getString("comment"));
        if(getDataBundle.getByteArray("photo")!=null){
            byte[] photoByte = getDataBundle.getByteArray("photo");
            Bitmap bm = BitmapFactory.decodeByteArray(photoByte, 0, photoByte.length);
            colleagueCirclePhoto.setImageBitmap(bm);
        }

        //массив TextView для проставления нужного цвета
        ArrayList<TextView> arrTVcolor = new ArrayList<>();
        //добавление всех textView в массив
        arrTVcolor.addAll(Arrays.asList(partnershipTV,efficiencyTV,responsibilityTV,creativityTV,opennessTV,courageTV));

        for (int i = 0; i < arrTVcolor.size() ; i++) {
            //цвет шрифта textview в соответствии с выбранной оценкой
            switch (arrTVcolor.get(i).getText().toString()) {
                case "Не соответствует ожиданиям":
                    arrTVcolor.get(i).setTextColor(ContextCompat.getColor(getActivity(), R.color.colorOrange));
                    break;
                case "Соответствует ожиданиям":
                    arrTVcolor.get(i).setTextColor(ContextCompat.getColor(getActivity(), R.color.colorGreen));
                    break;
                case "Превосходит ожидания":
                    arrTVcolor.get(i).setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPurple));
                    break;
                case "Значительно превосходит ожидания":
                    arrTVcolor.get(i).setTextColor(ContextCompat.getColor(getActivity(), R.color.colorBlue));
                    break;
                default:
                    arrTVcolor.get(i).setTextColor(ContextCompat.getColor(getActivity(), R.color.colorRed));
            }
        }
    }

}
