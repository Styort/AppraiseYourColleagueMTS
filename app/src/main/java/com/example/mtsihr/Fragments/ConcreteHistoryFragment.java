package com.example.mtsihr.Fragments;


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

/**
 * A simple {@link Fragment} subclass.
 */
public class ConcreteHistoryFragment extends Fragment {

    private View rootView;
    private TextView  partnershipTV, efficiencyTV, responsibilityTV,
            courageTV, creativityTV, opennessTV, nameCollTV, dateOfEvalTV,
            postCollTV, subdivCollTV;
    private Bundle getDataBundle;
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

        ArrayList<TextView> arrTVcolor = new ArrayList<>(); //массив TextView для проставления нужного цвета
        arrTVcolor.addAll(Arrays.asList(partnershipTV,efficiencyTV,responsibilityTV,creativityTV,opennessTV,courageTV)); //добавление textView

        for (int i = 0; i < arrTVcolor.size() ; i++) {
            switch (arrTVcolor.get(i).getText().toString()) { //цвет шрифта в соответствии с выбранной оценкой
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
