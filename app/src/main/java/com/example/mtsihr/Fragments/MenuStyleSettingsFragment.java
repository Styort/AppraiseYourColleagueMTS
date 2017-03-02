package com.example.mtsihr.Fragments;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mtsihr.BlurBuilder;
import com.example.mtsihr.R;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

import info.hoang8f.android.segmented.SegmentedGroup;
import jp.wasabeef.blurry.Blurry;

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
    private ImageView previewImageNavDrawIV;
    private SeekBar seekBar;
    private Button chooseImageButt, defaultSettingButt;
    private Bitmap previewImage;
    private int navTextColor, blurValue = 1, imageEffect = 0;
    private NavigationView navigationView;
    private RadioButton noneEffRB, lightEffRB, darkEffRB;
    private SegmentedGroup segmentedGroup;
    private RelativeLayout chooseColorRelative;

    public MenuStyleSettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_menu_style_settings, container, false);
        //заголовок тулбара
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Настройки меню");

        //получаем preference
        pref = getActivity().getSharedPreferences("settings", 0);
        editPref = pref.edit();
        //инициализируем элементы view
        initElements();
        //обрабатываем клики
        initClicks();
        return rootView;
    }

    private void initElements() {
        //получаем настройки из preference
        navTextColor = pref.getInt("navTextColor", 0);
        blurValue = pref.getInt("blur_value", 1);
        imageEffect = pref.getInt("img_effect", 0);
        previewImageNavDrawIV = (ImageView) rootView.findViewById(R.id.nav_draw_image_settings);
        seekBar = (SeekBar) rootView.findViewById(R.id.seek_bar_settings);
        //устанавливаем максимум по размытию изображения
        seekBar.setMax(25);
        //считываем сколько было размытие
        seekBar.setProgress(blurValue);
        chooseColorTV = (TextView) rootView.findViewById(R.id.chooseColorTV);
        chooseImageButt = (Button) rootView.findViewById(R.id.choose_image_button);
        defaultSettingButt = (Button) rootView.findViewById(R.id.default_settings_butt);
        navigationView = (NavigationView) getActivity().findViewById(R.id.nav_view);
        noneEffRB = (RadioButton) rootView.findViewById(R.id.img_eff_none_rb);
        lightEffRB = (RadioButton) rootView.findViewById(R.id.img_eff_light_rb);
        darkEffRB = (RadioButton) rootView.findViewById(R.id.img_eff_dark_rb);
        segmentedGroup = (SegmentedGroup) rootView.findViewById(R.id.segmented);
        chooseColorRelative = (RelativeLayout) rootView.findViewById(R.id.choose_color_relative);
        chooseColorTV.setBackgroundColor(navTextColor);
        initEffect();
        previewImage = getPreviewImage();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            previewImageNavDrawIV.setImageBitmap(BlurBuilder.blur(getActivity(), getPreviewImage(), blurValue));
        } else {
            previewImageNavDrawIV.setImageBitmap(getPreviewImage());
        }
        //getPreviewImage();
    }

    //загружаем сохраненный эффект на превью
    public void initEffect() {
        switch (imageEffect) {
            case 0:
                //previewImageNavDrawIV.setImageBitmap(previewImage); //проверить
                noneEffRB.setChecked(true);
                break;
            case 1:
                previewImageNavDrawIV.setColorFilter(new LightingColorFilter(0xFFFFFFFF, 0x00222222)); //осветляем фото
                lightEffRB.setChecked(true);
                break;
            case 2:
                previewImageNavDrawIV.setColorFilter(new LightingColorFilter(0xFF7F7F7F, 0x00000000)); //затемняем фото
                darkEffRB.setChecked(true);
                break;
        }
    }

    private void initClicks() {
        //выбираем цвет текста и иконок в меню
        chooseColorRelative.setOnClickListener(new View.OnClickListener() {
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
                                navigationView.setItemTextColor(ColorStateList.valueOf(selectedColor));
                                setIconColorMenu(selectedColor);
                                editPref.putInt("navTextColor", selectedColor);
                                editPref.apply();
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
                    previewImageNavDrawIV.setImageBitmap(previewImage);
                    //сохраняем обработанное изображение
                    saveNavDrImage(previewImage);
                    blurValue = i;
                    if (lightEffRB.isChecked()) {
                        setFilter(1);
                    } else if (darkEffRB.isChecked()) {
                        setFilter(2);
                    } else {
                        setFilter(0);
                    }
                } else {
                    Blurry.with(getContext()).radius(i).from(previewImage).into(previewImageNavDrawIV);
                    blurValue = i;
                    //сохраняем обработанное изображение
                    //saveNavDrImage(blurBM);
                    if (lightEffRB.isChecked()) {
                        setFilter(1);
                    } else if (darkEffRB.isChecked()) {
                        setFilter(2);
                    } else {
                        setFilter(0);
                    }
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
                //элементы диалогового окна
                final CharSequence[] items = {"Выбрать из галлереи", "Сделать фото",
                        "Отмена"};
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Изменить картинку");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        switch (item) {
                            case 0:
                                //открываем галлерею
                                galleryIntent();
                                break;
                            case 1:
                                //открываем камеру
                                cameraIntent();
                                break;
                            case 2:
                                //закрываем диалоговое окно
                                dialog.dismiss();
                                break;
                        }
                    }
                });
                builder.show();
            }
        });
        //настройки по умолчанию
        defaultSettingButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editPref.putInt("navTextColor", -16777216);
                previewImage = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.nav_draw_back);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                previewImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                String strByteArrBackImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
                previewImageNavDrawIV.clearColorFilter();
                navigationView.getBackground().clearColorFilter();
                noneEffRB.setChecked(true);
                editPref.putString("nav_back_preview", strByteArrBackImage);
                editPref.putString("nav_back", strByteArrBackImage);
                editPref.putInt("img_effect", 0);
                editPref.putInt("blur_value", 0);
                editPref.apply();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    seekBar.setProgress(0);
                }
                setIconColorMenu(Color.BLACK);
                chooseColorTV.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorBlack));
                navigationView.setItemTextColor(ColorStateList.valueOf(Color.BLACK));
                saveNavDrImage(previewImage);
                savePreviewSetting(previewImage);
            }
        });
        //нажатие на radio button с фильтрами
        segmentedGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.img_eff_none_rb:
                        //убрать фильтры
                        setFilter(0);
                        imageEffect = 0;
                        editPref.putInt("img_effect", imageEffect);
                        break;
                    case R.id.img_eff_light_rb:
                        //осветлить фото
                        setFilter(1);
                        imageEffect = 1;
                        editPref.putInt("img_effect", imageEffect);
                        break;
                    case R.id.img_eff_dark_rb:
                        //затемнить фото
                        setFilter(2);
                        imageEffect = 2;
                        editPref.putInt("img_effect", imageEffect);
                        break;
                }
            }
        });
    }

    public void setIconColorMenu(int color) {
        Drawable colleagueIcon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_supervisor_account_white_24dp, null);
        Drawable settingsIcon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_settings_white_24dp, null);
        Drawable justIcon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_check_circle_white_24dp, null);
        Drawable historyIcon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_history_white_24dp, null);
        Drawable helpIcon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_live_help_white_24dp, null);
        Drawable shareIcon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_rss_feed_white_24dp, null);
        ArrayList<Drawable> drawArr = new ArrayList<>();
        drawArr.addAll(Arrays.asList(colleagueIcon, justIcon, historyIcon, settingsIcon, helpIcon, shareIcon));
        for (int i = 0; i < drawArr.size(); i++) {
            drawArr.get(i).setColorFilter(color, PorterDuff.Mode.MULTIPLY);
            navigationView.getMenu().getItem(i).setIcon(drawArr.get(i));
        }
    }

    public void setFilter(int num) {
        switch (num) {
            case 0:
                //убираем фильтры
                previewImageNavDrawIV.clearColorFilter();
                navigationView.getBackground().clearColorFilter();
                break;
            case 1:
                //осветляем фон в превью и navDraw
                previewImageNavDrawIV.setColorFilter(new LightingColorFilter(0xFFFFFFFF, 0x00222222));
                navigationView.getBackground().setColorFilter(new LightingColorFilter(0xFFFFFFFF, 0x00222222));
                break;
            case 2:
                //затемняем фон в превью и navDraw
                previewImageNavDrawIV.setColorFilter(new LightingColorFilter(0xFF7F7F7F, 0x00000000));
                navigationView.getBackground().setColorFilter(new LightingColorFilter(0xFF7F7F7F, 0x00000000));
                break;
        }
    }

    //открываем камеру
    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    //открываем галлерею
    private void galleryIntent() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, SELECT_FILE);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmapMain;
        if (resultCode == getActivity().RESULT_OK) {
            if (requestCode == SELECT_FILE) {
                //получаем выбранное изображение из галлереи
                Uri selectedImage = data.getData();
                /*
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picPath = cursor.getString(columnIndex);
                cursor.close();*/
                bitmapMain = getScaledBitmap(selectedImage);
                bitmapMain = scaleCenterCrop(bitmapMain
                        , navigationView.getHeight(), navigationView.getWidth());
                saveNavDrImage(bitmapMain);
                savePreviewSetting(bitmapMain);
                previewImage = getPreviewImage();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    seekBar.setProgress(0);
                }

                noneEffRB.setChecked(true);


            } else if (requestCode == REQUEST_CAMERA) {
                //получаем сделаное изображение с камеры
                Uri selectedImage = data.getData();
                bitmapMain = getScaledBitmap(selectedImage);
                bitmapMain = scaleCenterCrop(bitmapMain
                        , navigationView.getHeight(), navigationView.getWidth());
                saveNavDrImage(bitmapMain);
                savePreviewSetting(bitmapMain);
                previewImage = getPreviewImage();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    seekBar.setProgress(0);
                }
                noneEffRB.setChecked(true);
            }
        }
    }

    //получаем изображение из pref
    public Bitmap getPreviewImage() {
        //получаем картинку в текстовом формате
        String previewImageNavDrawIVSt = pref.getString("nav_back_preview", "");
        Bitmap bm;
        if (previewImageNavDrawIVSt.equals("")) {
            bm = BitmapFactory.decodeResource(getContext().getResources(),
                    R.drawable.nav_draw_back);
        } else {
            //конвертируем строку в массив байтов
            byte[] imageByteArr = Base64.decode(previewImageNavDrawIVSt, Base64.DEFAULT);
            //конвертируем массив байтов в изображение
            bm = BitmapFactory.decodeByteArray(imageByteArr, 0, imageByteArr.length);
            previewImageNavDrawIV.setImageBitmap(bm);
        }
        return bm;
    }

    //сохраняем изображение без обработки и сохраняем значение размытия
    public void savePreviewSetting(Bitmap preview) {
        previewImageNavDrawIV.setImageBitmap(preview);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        preview.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        String strByteArrBackImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
        //преобразовав массив байтов в строку, отправляем ее в preference
        editPref.putInt("blur_value", blurValue);
        editPref.putString("nav_back_preview", strByteArrBackImage);
        editPref.commit();
    }

    //сохраняем обработанное изображение в preference и применяем его к меню
    public void saveNavDrImage(Bitmap bm) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        String strByteArrBackImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
        //преобразовав массив байтов в строку, отправляем ее в preference
        editPref.putString("nav_back", strByteArrBackImage);
        editPref.commit();

        Drawable drNavImg = new BitmapDrawable(getResources(), bm);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            navigationView.setBackground(drNavImg);
        } else {
            navigationView.setBackgroundDrawable(drNavImg);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (previewImage != null) {
            /*
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                saveNavDrImage(BlurBuilder.blur(getActivity(), previewImage, blurValue));
            } else {
                saveNavDrImage(previewImage);
            }*/
            //сохраняем необработанное изображение и значение размытия
            savePreviewSetting(previewImage);
            if (lightEffRB.isChecked()) {
                setFilter(1);
            } else if (darkEffRB.isChecked()) {
                setFilter(2);
            } else {
                setFilter(0);
            }
        }
    }

    //аналог cropCenter в ImageView для Bitmap
    public Bitmap scaleCenterCrop(Bitmap source, int newHeight, int newWidth) {
        int sourceWidth = source.getWidth();
        int sourceHeight = source.getHeight();

        // Compute the scaling factors to fit the new height and width, respectively.
        // To cover the final image, the final scaling will be the bigger
        // of these two.
        float xScale = (float) newWidth / sourceWidth;
        float yScale = (float) newHeight / sourceHeight;
        float scale = Math.max(xScale, yScale);

        // Now get the size of the source bitmap when scaled
        float scaledWidth = scale * sourceWidth;
        float scaledHeight = scale * sourceHeight;

        // Let's find out the upper left coordinates if the scaled bitmap
        // should be centered in the new size give by the parameters
        float left = (newWidth - scaledWidth) / 2;
        float top = (newHeight - scaledHeight) / 2;

        // The target rectangle for the new, scaled version of the source bitmap will now
        // be
        RectF targetRect = new RectF(left, top, left + scaledWidth, top + scaledHeight);

        // Finally, we create a new bitmap of the specified size and draw our new,
        // scaled bitmap onto it.
        Bitmap dest = Bitmap.createBitmap(newWidth, newHeight, source.getConfig());
        Canvas canvas = new Canvas(dest);
        canvas.drawBitmap(source, null, targetRect, null);

        return dest;
    }

    //сжатие изображения
    private Bitmap getScaledBitmap(Uri uri) {
        Bitmap thumb = null;
        try {
            ContentResolver cr = getActivity().getContentResolver();
            InputStream in = cr.openInputStream(uri);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 3;
            thumb = BitmapFactory.decodeStream(in, null, options);
        } catch (FileNotFoundException e) {
            Toast.makeText(getActivity(), "File not found", Toast.LENGTH_SHORT).show();
        }
        return thumb;
    }
}
