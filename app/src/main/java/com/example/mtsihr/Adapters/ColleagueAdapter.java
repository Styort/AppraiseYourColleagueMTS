package com.example.mtsihr.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mtsihr.Models.Colleague;
import com.example.mtsihr.R;
import com.example.mtsihr.ViewHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Filter;

/**
 * Created by Виктор on 09.08.2016.
 */
public class ColleagueAdapter extends ArrayAdapter {
    Activity context;
    List<Colleague> colleagueList;
    List<Colleague> mCopyColleagueList;

    public ColleagueAdapter(Context context, int resource, ArrayList objects) {
        super(context, resource, objects);
        this.context = (Activity) context;
        this.mCopyColleagueList = objects;
        this.colleagueList = objects;
    }

    @Override
    public int getCount() {
        return colleagueList.size();
    }

    @Override
    public Colleague getItem(int position) {
        return colleagueList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void filter(String charText) { //реализация фильтра поиска
        charText = charText.toLowerCase(Locale.getDefault());
        colleagueList = new ArrayList<>();
        if (charText.length() == 0) {
            colleagueList.addAll(mCopyColleagueList);
        } else {
            for (Colleague item : mCopyColleagueList) {
                if (item.getPhone() != null) {
                    String phone = item.getPhone().replace(" ", "").replace("-", ""); //убираем пробелым и знаки "-" в номере телефона
                    if (item.subdivision != null & item.email != null & item.post != null) { //поиск для случая когда все данные заполнены
                        if (item.getName().toLowerCase(Locale.getDefault()).contains(charText) ||
                                phone.contains(charText) ||
                                item.getEmail().toLowerCase(Locale.getDefault()).contains(charText) ||
                                item.getSubdivision().toLowerCase(Locale.getDefault()).contains(charText) ||
                                item.getPost().toLowerCase(Locale.getDefault()).contains(charText)) {
                            colleagueList.add(item);
                        }
                    } else {
                        if (item.subdivision != null & item.post != null) { //поиск для случая когда все данные кроме email заполнены
                            if (item.getName().toLowerCase(Locale.getDefault()).contains(charText) ||
                                    phone.contains(charText) ||
                                    item.getSubdivision().toLowerCase(Locale.getDefault()).contains(charText) ||
                                    item.getPost().toLowerCase(Locale.getDefault()).contains(charText)) {
                                colleagueList.add(item);
                            }
                        } else {
                            if (item.getName().toLowerCase(Locale.getDefault()).contains(charText) || //поиск для случая когда заполнены только имя и телефон
                                    phone.contains(charText)) {
                                colleagueList.add(item);
                            }
                        }
                    }
                } else { //когда телефон не заполнен
                    if (item.subdivision != null & item.email != null & item.post != null) {
                        if (item.getName().toLowerCase(Locale.getDefault()).contains(charText) ||
                                item.getEmail().toLowerCase(Locale.getDefault()).contains(charText) ||
                                item.getSubdivision().toLowerCase(Locale.getDefault()).contains(charText) ||
                                item.getPost().toLowerCase(Locale.getDefault()).contains(charText)) {
                            colleagueList.add(item);
                        }
                    } else {
                        if (item.email != null) {
                            if (item.getName().toLowerCase(Locale.getDefault()).contains(charText) ||
                                    item.getEmail().toLowerCase(Locale.getDefault()).contains(charText)) {
                                colleagueList.add(item);
                            }
                        } else {
                            if (item.getName().toLowerCase(Locale.getDefault()).contains(charText)) {
                                colleagueList.add(item);
                            }
                        }
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.colleague_item, null);
            viewHolder = new ViewHolder();
            viewHolder.colleagueName = (TextView) convertView.findViewById(R.id.name);
            viewHolder.colleaguePost = (TextView) convertView.findViewById(R.id.post);
            viewHolder.colleagueSubdiv = (TextView) convertView.findViewById(R.id.subdivision);
            viewHolder.icArrowColleague = (ImageView) convertView.findViewById(R.id.ic_colleague_iv);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.colleagueName.setText(colleagueList.get(position).name); //номер телефона
        viewHolder.colleaguePost.setText(colleagueList.get(position).post); //должность
        viewHolder.colleagueSubdiv.setText(colleagueList.get(position).subdivision); //подразделение
        //меняем цвет у стрелочки
        int color = Color.parseColor("#858585"); //The color u want
        viewHolder.icArrowColleague.setColorFilter(color);

        return convertView;
    }

}
