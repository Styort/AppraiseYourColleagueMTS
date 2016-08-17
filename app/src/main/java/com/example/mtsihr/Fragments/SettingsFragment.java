package com.example.mtsihr.Fragments;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;



import com.example.mtsihr.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Настройки"); //заголовок тулбара
        PreferenceManager.setDefaultValues(getActivity(), R.xml.settings, false);
        addPreferencesFromResource(R.xml.settings);
    }
}
