package com.example.mtsihr.Fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.mtsihr.Blur;
import com.example.mtsihr.R;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 */
public class MenuStyleSettingsFragment extends Fragment {
    final int REQUEST_CAMERA = 123;
    final int SELECT_FILE = 125;
    private View rootView;
    private TextView chooseColorTV;
    private SharedPreferences pref;
    private SharedPreferences.Editor editPref;
    private Blur blur = new Blur();
    private ImageView previewImageNavDrawIV;
    private SeekBar seekBar;
    private Button chooseImageButt;
    private Bitmap previewImage;
    private int navTextColor, blurValue = 1;


    public MenuStyleSettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_menu_style_settings, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Настройки меню"); //заголовок тулбара

        pref = getActivity().getSharedPreferences("settings", 0); //получаем preference
        editPref = pref.edit();
        initElements(); //инициализируем элементы view
        initClicks(); //обрабатываем клики
        return rootView;
    }

    private void initElements() {
        navTextColor = pref.getInt("navTextColor", 0); //получаем текущий цвет текста navDrawer
        previewImageNavDrawIV = (ImageView) rootView.findViewById(R.id.nav_draw_image_settings);
        seekBar = (SeekBar) rootView.findViewById(R.id.seek_bar_settings);
        chooseColorTV = (TextView) rootView.findViewById(R.id.chooseColorTV);
        chooseImageButt = (Button) rootView.findViewById(R.id.choose_image_button);
        chooseColorTV.setBackgroundColor(navTextColor);
        previewImage = getNavDrawImage(); //присваиваем previewImage к элементу previewImageNavDrawIV
        seekBar.setMax(50); //устанавливаем максимум по размытию изображения
    }

    private void initClicks() {
        chooseColorTV.setOnClickListener(new View.OnClickListener() { //открываем colorPicker
            @Override
            public void onClick(View view) {
                ColorPickerDialogBuilder
                        .with(getContext())
                        .setTitle("Выберите цвет")
                        .initialColor(navTextColor)
                        .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                        .density(12)
                        .setOnColorSelectedListener(new OnColorSelectedListener() {
                            @Override
                            public void onColorSelected(int selectedColor) {
                                //toast("onColorSelected: 0x" + Integer.toHexString(selectedColor));
                            }
                        })
                        .setPositiveButton("Ок", new ColorPickerClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                                chooseColorTV.setBackgroundColor(selectedColor);
                                editPref.putInt("navTextColor", selectedColor);
                                editPref.commit();
                            }
                        })
                        .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .build()
                        .show();
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (i == 0) {
                    previewImageNavDrawIV.setImageBitmap(blur.fastblur(previewImage, 1, 1));
                    blurValue = 1;
                } else {
                    previewImageNavDrawIV.setImageBitmap(blur.fastblur(previewImage, 1, i));
                    blurValue = i;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        chooseImageButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CharSequence[] items = {"Выбрать из галлереи", "Сделать фото",
                        "Отмена"}; //элементы диалогового окна
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()); //показываем диалоговое окно
                builder.setTitle("Изменить картинку");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        switch (item) {
                            case 0:
                                galleryIntent(); //открываем галлерею
                                break;
                            case 1:
                                cameraIntent(); //открываем камеру
                                break;
                            case 2:
                                dialog.dismiss(); //закрываем диалоговое окно
                                break;
                        }
                    }
                });
                builder.show();
            }
        });
    }

    private void cameraIntent() //открываем камеру
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void galleryIntent() //открываем галлерею
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Выберите изображение"), SELECT_FILE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap;
        if (resultCode == getActivity().RESULT_OK) {
            try {
                if (requestCode == SELECT_FILE) { //получаем выбранное изображение из галлереи
                    bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), data.getData());
                    saveNavDrImage(bitmap);
                    previewImage = getNavDrawImage(); //присваиваем previewImage к элементу previewImageNavDrawIV
                } else if (requestCode == REQUEST_CAMERA) { //получаем сделаное изображение с камеры
                    bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), data.getData());
                    saveNavDrImage(bitmap);
                    previewImage = getNavDrawImage(); //присваиваем previewImage к элементу previewImageNavDrawIV
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public Bitmap getNavDrawImage(){ //получаем изображение из pref
        String previewImageNavDrawIVSt = pref.getString("nav_back", ""); //получаем картинку в текстовом формате
        byte[] imageByteArr = Base64.decode(previewImageNavDrawIVSt, Base64.DEFAULT); //конвертируем строку в массив байтов
        Bitmap bm = BitmapFactory.decodeByteArray(imageByteArr, 0, imageByteArr.length); //конвертируем массив байтов в изображение
        previewImageNavDrawIV.setImageBitmap(bm); //применяем картинку в imageView
        return bm;
    }
    public void saveNavDrImage(Bitmap bm) { //сохраняем изображение в preference и применяем его к меню
        previewImageNavDrawIV.setImageBitmap(bm); //применяем картинку к imageView в настройках
        ByteArrayOutputStream stream = new ByteArrayOutputStream(); //получаем поток ByteArray
        bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray(); //преобразуем поток в массив байтов
        String strByteArrBackImage = Base64.encodeToString(byteArray, Base64.DEFAULT); //конвертируем byteArray в строку
        editPref.putString("nav_back", strByteArrBackImage); //сохраняем изображение в preference
        editPref.commit();
    }

    @Override
    public void onStop() {
        super.onStop();
        saveNavDrImage(blur.fastblur(previewImage,1,blurValue));
    }
}
