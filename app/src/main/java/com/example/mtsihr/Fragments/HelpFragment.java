package com.example.mtsihr.Fragments;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.mtsihr.Adapters.HelpPagerAdapter;
import com.example.mtsihr.Models.DetailHelp;
import com.example.mtsihr.Models.HistoryModel;
import com.example.mtsihr.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class HelpFragment extends Fragment {
    private ViewPager mPager;
    private View rootView;
    private ArrayList<DetailHelp> helpArray;
    private HelpPagerAdapter mHelpPagerAdapter;
    private Bundle args;

    public HelpFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_help, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Помощь"); //заголовок тулбара

        args = getArguments();
        //mPager.setCurrentItem(0); // выводим первый экран экран
        mPager = (ViewPager) rootView.findViewById(R.id.help_pager);
        setHasOptionsMenu(true); //показываем элемент тулбара "Отпраивть оценку"

        initArray();
        initAdapter();
        return rootView;
    }

    private void initArray() {
        helpArray = new ArrayList<>();
        helpArray.add(new DetailHelp(0, "Оценка ценностей ПРОСТО", getString(R.string.help_just_desc), 1));
        helpArray.add(new DetailHelp(0, "Партнерство", getString(R.string.help_partnership_desc) , 2));
        helpArray.add(new DetailHelp(0, "Результативность", getString(R.string.help_effectiveness_desc), 3));
        helpArray.add(new DetailHelp(0, "Ответственность",getString(R.string.help_responsibility_desc) , 4));
        helpArray.add(new DetailHelp(0, "Смелость ", getString(R.string.help_courage_desc), 5));
        helpArray.add(new DetailHelp(0, "Творчество" , getString(R.string.help_creation_desc), 6));
        helpArray.add(new DetailHelp(0, "Открытость", getString(R.string.help_openness_desc), 7));
    }

    @Override
    public void onStart() {
        super.onStart();
        initArray();
        initAdapter();
    }

    private void initAdapter() {
        mHelpPagerAdapter = new HelpPagerAdapter(
                getFragmentManager(),helpArray );

        mPager.setAdapter(mHelpPagerAdapter);

        if(args!=null){
            mPager.setCurrentItem(args.getInt("content_pos",0));
        }
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) { //добавляем меню в тулбар с кнопкой "Удалить коллегу из списка"
        getActivity().getMenuInflater().inflate(R.menu.helo_info_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { //обрабатываем нажатие на элмент тулбара (отправить оценку)
        switch (item.getItemId()) {
            case R.id.action_help_info:
                Fragment fragment = new HelpContentFragment();
                FragmentManager fm = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.replace(((ViewGroup) getView().getParent()).getId(), fragment).addToBackStack(null).commit();
                break;
        }
        return (super.onOptionsItemSelected(item));
    }
}
