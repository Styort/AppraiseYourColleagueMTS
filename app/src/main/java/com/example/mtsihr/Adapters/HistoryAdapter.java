package com.example.mtsihr.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mtsihr.Models.Colleague;
import com.example.mtsihr.Models.HistoryModel;
import com.example.mtsihr.R;
import com.example.mtsihr.ViewHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Виктор on 15.08.2016.
 */
public class HistoryAdapter extends ArrayAdapter {
    private Activity context;
    private List<HistoryModel> historyEvaluate;
    private List<HistoryModel> mCopyhistoryEvaluate;

    public HistoryAdapter(Context context, int resource, List objects) {
        super(context, resource, objects);
        this.context = (Activity) context;
        this.historyEvaluate = objects;
        this.mCopyhistoryEvaluate = objects;
    }

    @Override
    public int getCount() {
        return historyEvaluate.size();
    }

    @Override
    public HistoryModel getItem(int position) {
        return historyEvaluate.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void filter(String charText) { //реализация фильтра поиска
        charText = charText.toLowerCase(Locale.getDefault());
        historyEvaluate = new ArrayList<>();
        if (charText.length() == 0) {
            historyEvaluate.addAll(mCopyhistoryEvaluate);
        } else {
            for (HistoryModel item : mCopyhistoryEvaluate) {
                if(item.getName().toLowerCase(Locale.getDefault()).contains(charText) ||
                        item.getDateOfEval().toLowerCase(Locale.getDefault()).contains(charText)){
                    historyEvaluate.add(item);
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
            convertView = inflater.inflate(R.layout.history_item, null);
            viewHolder = new ViewHolder();
            viewHolder.evalHistoryName = (TextView) convertView.findViewById(R.id.hist_coll_name_tv);
            viewHolder.evalHistoryDate = (TextView) convertView.findViewById(R.id.hist_eval_data_tv);
            viewHolder.icArrowColleague = (ImageView) convertView.findViewById(R.id.ic_colleague_iv);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.evalHistoryName.setText(historyEvaluate.get(position).getName()); //номер телефона
        viewHolder.evalHistoryDate.setText(historyEvaluate.get(position).getDateOfEval()); //должность
        //меняем цвет у стрелочки
        int color = Color.parseColor("#858585"); //The color u want
        viewHolder.icArrowColleague.setColorFilter(color);

        return convertView;
    }
}
