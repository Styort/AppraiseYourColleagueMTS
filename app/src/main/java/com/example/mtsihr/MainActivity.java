package com.example.mtsihr;

import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.MenuItem;

import com.example.mtsihr.Fragments.ColleagueFragment;
import com.example.mtsihr.Fragments.HelpFragment;
import com.example.mtsihr.Fragments.HistoryFragment;
import com.example.mtsihr.Fragments.JustFragment;
import com.example.mtsihr.Fragments.SettingsFragment;
import com.example.mtsihr.Models.Colleague;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

import io.realm.Realm;
import io.realm.RealmConfiguration;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private NavigationView navigationView = null;
    private Toolbar toolbar = null;
    private ArrayList<Colleague> colleagues;
    private int color;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        colleagues = new ArrayList<>();

        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this).deleteRealmIfMigrationNeeded().build();
        Realm.setDefaultConfiguration(realmConfiguration);

        //set the fragment initially
        ColleagueFragment colleagueFragment = new ColleagueFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, colleagueFragment, "colleagueFrag");
        fragmentTransaction.commit();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        initSettingsNavDraw();

        //String[] fingerprints = VKUtil.getCertificateFingerprint(this, this.getPackageName());
        //System.out.println(Arrays.asList(fingerprints));
    }

    private void initSettingsNavDraw() {
        //получаем данные с настроек
        SharedPreferences sharedPref = this.getSharedPreferences("settings", 0);
        SharedPreferences.Editor editPref = sharedPref.edit();

        int blurValue = sharedPref.getInt("blurValue",0);
        color = sharedPref.getInt("navTextColor", 0);
        //получаем картинку в текстовом формате
        String navBackImageSt = sharedPref.getString("nav_back", "");
        if (color == 0) {
            editPref.putInt("navTextColor", -16777216);
            navigationView.setItemTextColor(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorBlack)));
        } else {
            navigationView.setItemTextColor(ColorStateList.valueOf(color));
        }
        if (navBackImageSt.equals("")) {
            navigationView.setBackgroundResource(R.drawable.nav_draw_back);
            //преобразуем строку в bitmap
            Bitmap preview = BitmapFactory.decodeResource(this.getResources(), R.drawable.nav_draw_back);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            preview.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            String strByteArrBackImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
            editPref.putString("nav_back_preview", strByteArrBackImage);
            editPref.apply();
        } else {
            byte[] imageByteArr = Base64.decode(navBackImageSt, Base64.DEFAULT);
            Bitmap bm = BitmapFactory.decodeByteArray(imageByteArr, 0, imageByteArr.length);
            BitmapDrawable bmDr = new BitmapDrawable(getResources(), bm);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                navigationView.setBackground(bmDr);
            } else {
                navigationView.setBackgroundDrawable(bmDr);
            }
        }

        setIconColorMenu();

        //получаем сохраненный эффект на задний фон
        int effect = sharedPref.getInt("img_effect", 0);

        //подгружает фильтр на изображение
        switch (effect) {
            case 0:
                navigationView.getBackground().clearColorFilter();
                break;
            case 1:
                navigationView.getBackground().setColorFilter(new LightingColorFilter(0xFFFFFFFF, 0x00222222));
                break;
            case 2:
                navigationView.getBackground().setColorFilter(new LightingColorFilter(0xFF7F7F7F, 0x00000000));
                break;
        }

    }

    //устанавливаем цвет иконкам в меню
    public void setIconColorMenu() {
        Drawable colleagueIcon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_supervisor_account_white_24dp, null);
        Drawable settingsIcon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_settings_white_24dp, null);
        Drawable justIcon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_check_circle_white_24dp, null);
        Drawable historyIcon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_history_white_24dp, null);
        Drawable helpIcon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_live_help_white_24dp, null);
        Drawable shareIcon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_rss_feed_white_24dp, null);
        ArrayList<Drawable> drawArr = new ArrayList<>();
        drawArr.addAll(Arrays.asList(colleagueIcon, justIcon, historyIcon, settingsIcon, helpIcon, shareIcon));

        if (color==0){
            color = Color.BLACK;
        }
        for (int i = 0; i < drawArr.size(); i++) {
            drawArr.get(i).setColorFilter(color, PorterDuff.Mode.MULTIPLY);
            navigationView.getMenu().getItem(i).setIcon(drawArr.get(i));
        }
    }

    @Override
    public void onBackPressed() {
        int count = getFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            super.onBackPressed();
        } else {
            getFragmentManager().popBackStack();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }


    //переходим на выбранный фрагмент
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        FragmentTransaction fragmentTransaction;
        Fragment fragment = null;
        switch (id) {
            case R.id.nav_colleagues:
                fragment = new ColleagueFragment();
                break;
            case R.id.nav_history:
                fragment = new HistoryFragment();
                break;
            case R.id.nav_settings:
                fragment = new SettingsFragment();
                break;
            case R.id.nav_help:
                fragment = new HelpFragment();
                break;
            case R.id.nav_share:
                fragment = new ColleagueFragment();

                Bundle bundle = new Bundle();
                bundle.putBoolean("share", true);
                fragment.setArguments(bundle);
                break;
            case R.id.nav_just:
                fragment = new JustFragment();
                break;
        }
        if (fragment != null) {
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
