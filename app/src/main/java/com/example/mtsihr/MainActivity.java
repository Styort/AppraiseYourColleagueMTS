package com.example.mtsihr;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.mtsihr.Fragments.ColleagueFragment;
import com.example.mtsihr.Fragments.HelpFragment;
import com.example.mtsihr.Fragments.HistoryFragment;
import com.example.mtsihr.Fragments.JustFragment;
import com.example.mtsihr.Fragments.SettingsFragment;
import com.example.mtsihr.Models.Colleague;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmConfiguration;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    NavigationView navigationView = null;
    Toolbar toolbar = null;
    ArrayList<Colleague> colleagues;


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
    }

    @Override
    public void onBackPressed() {
        int count = getFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            super.onBackPressed();
            //additional code
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) { //переходим на выбранный фрагмент
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentTransaction fragmentTransaction;
        switch (id) {
            case R.id.nav_colleagues:
                ColleagueFragment colleagueFragment = new ColleagueFragment();
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, colleagueFragment, "colleagueFrag").addToBackStack(null).commit();
                break;
            case R.id.nav_history:
                HistoryFragment historyFragment = new HistoryFragment();
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, historyFragment).addToBackStack(null).commit();
                break;
            case R.id.nav_settings:
                SettingsFragment settingsFragment = new SettingsFragment();
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, settingsFragment).addToBackStack(null).commit();
                break;
            case R.id.nav_help:
                HelpFragment helpFragment = new HelpFragment();
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, helpFragment).addToBackStack(null).commit();
                break;
            case R.id.nav_share:
                ColleagueFragment fragment = new ColleagueFragment();
                FragmentManager fm = this.getSupportFragmentManager();

                fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                fragmentTransaction.replace(R.id.fragment_container, fragment).addToBackStack(null).commit();

                Bundle bundle = new Bundle();
                bundle.putBoolean("share", true); //передаем в фрагмент Share знак, для того, чтобы понять какое действие выполнять там.
                fragment.setArguments(bundle);
                break;
            case R.id.nav_just:
                JustFragment justFragment = new JustFragment();
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, justFragment).addToBackStack(null).commit();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
