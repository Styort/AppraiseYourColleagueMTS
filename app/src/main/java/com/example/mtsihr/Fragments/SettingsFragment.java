package com.example.mtsihr.Fragments;


import android.content.SharedPreferences;
import android.os.Build;
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
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.mtsihr.Models.Colleague;
import com.example.mtsihr.Models.HistoryModel;
import com.example.mtsihr.R;
import com.kyleduo.switchbutton.SwitchButton;


import io.realm.Realm;
import io.realm.RealmResults;

public class SettingsFragment extends Fragment {
    private Realm realm;
    private RealmResults colleagueRealmResults;
    private RealmResults historyRealmResult;
    private View rootView;
    private Button clearHistoryData, clearColleagueData;
    private Switch historySaveSwitch;
    private SwitchButton historySaveTB;
    private SharedPreferences pref;
    private RelativeLayout aboutAppRelative,
            menuStyleRelative, shareRelative, updateRelative, switchRelative;

    public SettingsFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Настройки");

        realm = Realm.getDefaultInstance();
        //считываем все данные, что есть в бд
        colleagueRealmResults = realm.where(Colleague.class).findAll();
        historyRealmResult = realm.where(HistoryModel.class).findAll();
        pref = getActivity().getSharedPreferences("settings", 0);


        initElements();
        initClicks();
        return rootView;
    }

    private void initElements() {
        clearHistoryData = (Button) rootView.findViewById(R.id.delete_history_list_butt);
        clearColleagueData = (Button) rootView.findViewById(R.id.delete_colleague_list_butt);
        Boolean saveHistory = pref.getBoolean("save_history", true);
        //получаем настройку сохранения истории оценок
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            historySaveSwitch = (Switch) rootView.findViewById(R.id.history_save_switch);
            historySaveSwitch.setChecked(saveHistory);
        } else {
            historySaveTB = (SwitchButton) rootView.findViewById(R.id.history_save_switch);
            historySaveTB.setChecked(saveHistory);
        }
        aboutAppRelative = (RelativeLayout) rootView.findViewById(R.id.about_relative);
        menuStyleRelative = (RelativeLayout) rootView.findViewById(R.id.menu_style_relative);
        shareRelative = (RelativeLayout) rootView.findViewById(R.id.share_relative);
        updateRelative = (RelativeLayout) rootView.findViewById(R.id.update_relative);
        switchRelative = (RelativeLayout) rootView.findViewById(R.id.history_save_relative);

    }

    private void initClicks() {
        final SharedPreferences.Editor editPref = pref.edit();
        //очищаем список коллег
        clearColleagueData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                realm.beginTransaction();
                //удаляем данные из бд
                colleagueRealmResults.deleteAllFromRealm();
                realm.commitTransaction();
                Toast.makeText(getActivity(), "Список коллег очищен!", Toast.LENGTH_SHORT).show();
            }
        });
        //очищаем историю оценок
        clearHistoryData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                realm.beginTransaction();
                //удаляем данные из бд
                historyRealmResult.deleteAllFromRealm();
                realm.commitTransaction();
                Toast.makeText(getActivity(), "История оценок очищена!", Toast.LENGTH_SHORT).show();
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            historySaveSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        //сохранять историю
                        editPref.putBoolean("save_history", true);
                        editPref.commit();
                    } else {
                        //не сохранять историю
                        editPref.putBoolean("save_history", false);
                        editPref.commit();
                    }
                }
            });
        } else {
            historySaveTB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        //сохранять историю
                        editPref.putBoolean("save_history", true);
                        editPref.commit();
                    } else {
                        //не сохранять историю
                        editPref.putBoolean("save_history", false);
                        editPref.commit();
                    }
                }
            });
        }
        //переходим на фрагмент "О приложении"
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
        //переходим на фрагмент настройки меню
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
        //переходим на фрагмент "Поделиться"
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
                bundle.putBoolean("share", true);
                fragment.setArguments(bundle);
            }
        });
        //переходим на фрагмент "Обновить приложение"
        updateRelative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateFragment fragment = new UpdateFragment();
                FragmentManager fm = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction;

                transaction = fm.beginTransaction();
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                transaction.replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
            }
        });
        //при нажатии на relative смена switch button
        switchRelative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (historySaveSwitch.isChecked()) {
                        historySaveSwitch.setChecked(false);
                        //не сохранять историю
                        editPref.putBoolean("save_history", false);
                        editPref.commit();
                    } else {
                        historySaveSwitch.setChecked(true);
                        //сохранять историю
                        editPref.putBoolean("save_history", true);
                        editPref.commit();
                    }
                } else {
                    if (historySaveTB.isChecked()) {
                        historySaveTB.setChecked(false);
                        //не сохранять историю
                        editPref.putBoolean("save_history", false);
                        editPref.commit();
                    } else {
                        historySaveTB.setChecked(true);
                        //сохранять историю
                        editPref.putBoolean("save_history", true);
                        editPref.commit();
                    }
                }
            }
        });
    }

}
