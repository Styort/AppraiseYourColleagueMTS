package com.example.mtsihr.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mtsihr.Models.Colleague;
import com.example.mtsihr.Models.Evaluate;
import com.example.mtsihr.R;
import com.example.mtsihr.ViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Виктор on 11.08.2016.
 */
public class EvaluateAdapter extends ArrayAdapter {

    Activity context;
    List<Evaluate> evaluateList;

    public EvaluateAdapter(Context context, int resource, List objects) {
        super(context, resource, objects);

        this.context = (Activity) context;
        this.evaluateList = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.evaluate_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.firstSignEvaluate = (TextView) convertView.findViewById(R.id.first_sign_tv);
            viewHolder.evaluateName = (TextView) convertView.findViewById(R.id.eval_name_tv);
            viewHolder.concreteEvaluate = (TextView) convertView.findViewById(R.id.eval_tv);
            viewHolder.icArrowChoseEval = (ImageView) convertView.findViewById(R.id.ic_eval_iv);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.firstSignEvaluate.setText(evaluateList.get(position).firstSign); //первая буква(красная) того, что оцениваем
        viewHolder.evaluateName.setText(evaluateList.get(position).nameEv); //что оцениваем
        viewHolder.concreteEvaluate.setText(evaluateList.get(position).eval); //оценка
        switch (evaluateList.get(position).eval) { //цвет шрифта в соответствии с выбранной оценкой
            case "Не соответствует ожиданиям":
                viewHolder.concreteEvaluate.setTextColor(ContextCompat.getColor(context, R.color.colorOrange));
                break;
            case "Соответствует ожиданиям":
                viewHolder.concreteEvaluate.setTextColor(ContextCompat.getColor(context, R.color.colorGreen));
                break;
            case "Превосходит ожидания":
                viewHolder.concreteEvaluate.setTextColor(ContextCompat.getColor(context, R.color.colorPurple));
                break;
            case "Значительно превосходит ожидания":
                viewHolder.concreteEvaluate.setTextColor(ContextCompat.getColor(context, R.color.colorBlue));
                break;
            default:
                viewHolder.concreteEvaluate.setTextColor(ContextCompat.getColor(context, R.color.colorRed));
        }

        //меняем цвет у стрелочки
        int color = Color.parseColor("#858585"); //The color u want
        viewHolder.icArrowChoseEval.setColorFilter(color);

        return convertView;
    }
}
