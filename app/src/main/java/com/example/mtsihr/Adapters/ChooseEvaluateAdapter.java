package com.example.mtsihr.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mtsihr.Models.ChooseEvaluate;
import com.example.mtsihr.Models.Evaluate;
import com.example.mtsihr.R;
import com.example.mtsihr.ViewHolder;

import java.util.List;

/**
 * Created by Виктор on 12.08.2016.
 */
public class ChooseEvaluateAdapter extends ArrayAdapter {
    List<ChooseEvaluate> evaluateList;
    Activity context;

    public ChooseEvaluateAdapter(Context context, int resource, List objects) {
        super(context, resource, objects);

        this.context = (Activity) context;
        this.evaluateList = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.choose_eval_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.chooseEval = (TextView) convertView.findViewById(R.id.choose_eval_tv);
            viewHolder.tvColorEval = (TextView) convertView.findViewById(R.id.tv_color_eval);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.chooseEval.setText((evaluateList.get(position).evaluate));
        //цвет бокового лэйаута в соответствии с надписью
        switch (evaluateList.get(position).evaluate) {
            case "Не соответствует ожиданиям":
                viewHolder.tvColorEval.setBackgroundColor(ContextCompat.getColor(context, R.color.colorOrange));
                break;
            case "Соответствует ожиданиям":
                viewHolder.tvColorEval.setBackgroundColor(ContextCompat.getColor(context, R.color.colorGreen));
                break;
            case "Превосходит ожидания":
                viewHolder.tvColorEval.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPurple));
                break;
            case "Значительно превосходит ожидания":
                viewHolder.tvColorEval.setBackgroundColor(ContextCompat.getColor(context, R.color.colorBlue));
                break;
            default:
                viewHolder.tvColorEval.setBackgroundColor(ContextCompat.getColor(context, R.color.colorRed));
        }
        return convertView;
    }
}
