package com.example.mtsihr.Adapters;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mtsihr.Models.Colleague;
import com.example.mtsihr.R;
import com.example.mtsihr.ViewHolder;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Filter;

import de.hdodenhof.circleimageview.CircleImageView;

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
            viewHolder.colleaguePhoto = (CircleImageView) convertView.findViewById(R.id.photo);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.colleagueName.setText(colleagueList.get(position).name); //номер телефона
        viewHolder.colleaguePost.setText(colleagueList.get(position).post); //должность
        viewHolder.colleagueSubdiv.setText(colleagueList.get(position).subdivision); //подразделение
        if (colleagueList.get(position).getPhoto() != null) {
            //ПОФИКСИТЬ OutOfMemory
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(colleagueList.get(position).getPhoto(), 0,
                    colleagueList.get(position).getPhoto().length, options);

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, 250, 250);
            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            Bitmap bm = BitmapFactory.decodeByteArray(colleagueList.get(position).getPhoto(), 0,
                    colleagueList.get(position).getPhoto().length, options);

            viewHolder.colleaguePhoto.setImageBitmap(bm);
        } else {
            viewHolder.colleaguePhoto.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.photo));
        }
        //меняем цвет у стрелочки
        int color = Color.parseColor("#858585"); //The color u want
        viewHolder.icArrowColleague.setColorFilter(color);

        return convertView;
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}
