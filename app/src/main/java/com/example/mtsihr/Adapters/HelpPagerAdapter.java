package com.example.mtsihr.Adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.mtsihr.Fragments.HelpDetailFragment;
import com.example.mtsihr.Models.DetailHelp;

import java.util.ArrayList;

/**
 * Created by Виктор on 24.08.2016.
 */
public class HelpPagerAdapter extends FragmentStatePagerAdapter {
    final String ARG_DESC = "item_desc";
    final String ARG_TITLE = "item_title";
    final String ARG_POSITION = "item_position";
    final String ARG_COUNT = "item_count";
    ArrayList<DetailHelp> data;
    public HelpPagerAdapter(FragmentManager fm, ArrayList<DetailHelp> data) {
        super(fm);
        this.data = data;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = new HelpDetailFragment();

        Bundle args = new Bundle();
        args.putString(ARG_DESC, data.get(position).description);
        args.putString(ARG_TITLE, data.get(position).title);
        args.putInt(ARG_POSITION, position+1);
        args.putInt(ARG_COUNT, getCount());

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public int getCount() {
        return data.size();
    }

}
