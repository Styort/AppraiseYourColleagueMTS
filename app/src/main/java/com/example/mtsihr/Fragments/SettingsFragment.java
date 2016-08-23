package com.example.mtsihr.Fragments;



import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.mtsihr.Models.Colleague;
import com.example.mtsihr.Models.HistoryModel;
import com.example.mtsihr.R;


import io.realm.Realm;
import io.realm.RealmResults;

public class SettingsFragment extends Fragment {
    private Realm realm;
    private RealmResults colleagueRealmResults;
    private RealmResults historyRealmResult;
    private View rootView;
    private Button clearHistoryData, clearColleagueData;
    private ToggleButton historySaveToggle;
    private SharedPreferences pref;
    private RelativeLayout saveHistRelativeToggleChange, aboutAppRelative,
            menuStyleRelative, shareRelative;

    public SettingsFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Настройки"); //заголовок тулбара

        realm = Realm.getDefaultInstance();
        colleagueRealmResults = realm.where(Colleague.class).findAll(); //считываем все данные, что есть в бд
        historyRealmResult = realm.where(HistoryModel.class).findAll(); //считыввем всю историю оценок, что есть в бд
        pref = getActivity().getSharedPreferences("settings", 0);


        initElements();
        initClicks();
        return rootView;
    }

    private void initElements() {
        clearHistoryData = (Button) rootView.findViewById(R.id.delete_history_list_butt);
        clearColleagueData = (Button) rootView.findViewById(R.id.delete_colleague_list_butt);
        historySaveToggle = (ToggleButton) rootView.findViewById(R.id.history_save_toggle);
        saveHistRelativeToggleChange = (RelativeLayout) rootView.findViewById(R.id.history_save_relative);
        aboutAppRelative = (RelativeLayout) rootView.findViewById(R.id.about_relative);
        menuStyleRelative = (RelativeLayout) rootView.findViewById(R.id.menu_style_relative);
        shareRelative = (RelativeLayout) rootView.findViewById(R.id.share_relative);
        Boolean saveHistory = pref.getBoolean("save_history", false); //получаем настройку сохранения истории оценок
        historySaveToggle.setChecked(saveHistory);
    }

    private void initClicks() {
        final SharedPreferences.Editor editPref = pref.edit(); //очищаем список коллег
        clearColleagueData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                realm.beginTransaction();
                colleagueRealmResults.deleteAllFromRealm(); //удаляем данные из бд
                realm.commitTransaction();
                Toast.makeText(getActivity(),"Список коллег очищен!",Toast.LENGTH_SHORT).show();
            }
        });
        clearHistoryData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { //очищаем историю оценок
                realm.beginTransaction();
                historyRealmResult.deleteAllFromRealm(); //удаляем данные из бд
                realm.commitTransaction();
                Toast.makeText(getActivity(),"История оценок очищена!",Toast.LENGTH_SHORT).show();
            }
        });

        saveHistRelativeToggleChange.setOnClickListener(new View.OnClickListener() { //изменение toggleButton при нажатии на Layout и сохранение настроек
            @Override
            public void onClick(View view) {
                if(historySaveToggle.isChecked()){
                    historySaveToggle.setChecked(false);
                    editPref.putBoolean("save_history", true);
                    editPref.commit();
                }else {
                    historySaveToggle.setChecked(true);
                    editPref.putBoolean("save_history", false);
                    editPref.commit();
                }
            }
        });
        historySaveToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) { //обработка клика на toggleButton
                if (isChecked) {
                    editPref.putBoolean("save_history", true); //сохранять историю
                    editPref.commit();
                } else {
                    editPref.putBoolean("save_history", false); //не сохранять историю
                    editPref.commit();
                }
            }
        });
        aboutAppRelative.setOnClickListener(new View.OnClickListener() { //переходим на фрагмент "о приложении"
            @Override
            public void onClick(View view) {
                AboutAppFragment fragment = new AboutAppFragment();
                FragmentManager fm = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.replace(((ViewGroup) getView().getParent()).getId(), fragment).addToBackStack(null).commit();
            }
        });
        menuStyleRelative.setOnClickListener(new View.OnClickListener() { //переходим на фрагмент "настройка меню"
            @Override
            public void onClick(View view) {
                MenuStyleSettingsFragment fragment = new MenuStyleSettingsFragment();
                FragmentManager fm = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.replace(((ViewGroup) getView().getParent()).getId(), fragment).addToBackStack(null).commit();
            }
        });
        shareRelative.setOnClickListener(new View.OnClickListener() { //переходим на фрагмент "поделиться приложением"
            @Override
            public void onClick(View view) {
                ColleagueFragment fragment = new ColleagueFragment();
                FragmentManager fm = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction;

                transaction = fm.beginTransaction();
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                transaction.replace(R.id.fragment_container, fragment).addToBackStack(null).commit();

                Bundle bundle = new Bundle();
                bundle.putBoolean("share", true); //передаем в фрагмент Share знак, для того, чтобы понять какое действие выполнять там.
                fragment.setArguments(bundle);
            }
        });
    }

}
