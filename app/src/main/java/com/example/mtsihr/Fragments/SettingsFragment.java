package com.example.mtsihr.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.mtsihr.Models.Colleague;
import com.example.mtsihr.R;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {

    private Button deleteAllDataButton;
    private View rootView;
    private Realm realm;
    private RealmResults colleagueRealmResults;
    private RealmResults historyRealmResult;

    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Настройки"); //заголовок тулбара
        rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        initElem();
        realm = Realm.getDefaultInstance();
        colleagueRealmResults = realm.where(Colleague.class).findAll(); //считываем все данные что есть в бд

        return rootView;
    }

    private void initElem() {
        deleteAllDataButton = (Button) rootView.findViewById(R.id.deleteAllDataButt);

        deleteAllDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                realm.beginTransaction();
                colleagueRealmResults.deleteAllFromRealm();
                historyRealmResult.deleteAllFromRealm();
                realm.commitTransaction();
                Toast.makeText(getActivity(), "Все данные удалены!", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        realm = Realm.getDefaultInstance();
    }

    @Override
    public void onStop() {
        super.onStop();
        realm.close();
    }
}
