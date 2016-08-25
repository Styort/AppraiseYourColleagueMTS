package com.example.mtsihr.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.mtsihr.R;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class HelpContentFragment extends Fragment {
    private View rootView;
    private ListView contentLV;
    private Bundle passDataBundle = new Bundle(); //передаем данные в слудующий фрагмент
    private Fragment fragment;


    public HelpContentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_help_content, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Содержание"); //заголовок тулбара

        contentLV = (ListView) rootView.findViewById(R.id.help_content_lv);

        String[] contentArr = new String[]{"ПРОСТО", "Партнерство", "Результативность",
                "Ответственность", "Смелость", "Творчество", "Открытость"};
        ArrayAdapter<String> contentAdapter = new ArrayAdapter<String>(
                getActivity(), android.R.layout.simple_list_item_1, contentArr);

        contentLV.setAdapter(contentAdapter);
        contentClick();
        return rootView;
    }

    private void contentClick() {
        contentLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                showHelp();
                passDataBundle.putInt("content_pos", i);
                fragment.setArguments(passDataBundle);
            }
        });
    }

    public void showHelp() {
        fragment = new HelpFragment();
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.replace(((ViewGroup) getView().getParent()).getId(), fragment).addToBackStack(null).commit();
    }
}
