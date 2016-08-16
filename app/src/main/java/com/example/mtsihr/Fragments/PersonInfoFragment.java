package com.example.mtsihr.Fragments;


import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mtsihr.MainActivity;
import com.example.mtsihr.Models.Colleague;
import com.example.mtsihr.R;

import java.util.ArrayList;
import java.util.HashMap;

import at.markushi.ui.CircleButton;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * A simple {@link Fragment} subclass.
 */
public class PersonInfoFragment extends Fragment {

    private View rootView;
    private ListView contactLV, actionsLV;
    private TextView nameTV, postTV, subdivTV;
    private ArrayList<HashMap<String, String>> conArr = new ArrayList<>();
    private Realm realm;
    private CircleButton callColleagueCB, smsColleagueCB;

    public PersonInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_person_info, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Информация о коллеге"); //заголовок тулбара

        realm = Realm.getDefaultInstance();
        setHasOptionsMenu(true);

        initElemets();
        itemClick();

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.person_info_menu, menu);
    }

    @Override
    public void onStart() {
        super.onStart();
        realm = Realm.getDefaultInstance();
    }

    @Override
    public void onStop() {
        super.onStop();
        realm.close();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                AlertDialog.Builder alert = new AlertDialog.Builder(
                        getActivity());
                alert.setMessage("Удалить коллегу из списка?");
                alert.setPositiveButton("Да", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Bundle getDataBundle = getArguments();
                        int pos = getDataBundle.getInt("position", 0);
                        RealmResults<Colleague> colleagueRealmResults = realm
                                .where(Colleague.class).findAll();
                        realm.beginTransaction();
                        colleagueRealmResults.deleteFromRealm(pos);
                        realm.commitTransaction();

                        dialog.dismiss();
                        Intent itentTo = new Intent(getContext(), MainActivity.class);
                        startActivity(itentTo);
                    }
                });
                alert.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                alert.show();
                break;
        }

        return (super.onOptionsItemSelected(item));
    }

    private void itemClick() {
        contactLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //choose app for call
                if (i == 0) {
                    if (conArr.get(i).get("Data") != "Данные не заполнены") {
                        String phoneNum = conArr.get(i).get("Data");
                        Intent callActivity = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNum));
                        startActivity(callActivity);
                    }
                } else {
                    //send email
                    String email = conArr.get(i).get("Data");
                    Intent emailIntent = new Intent(Intent.ACTION_SEND);
                    emailIntent.setType("text/plain");
                    emailIntent.putExtra(Intent.EXTRA_EMAIL, email);
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
                    emailIntent.putExtra(Intent.EXTRA_TEXT, "Здравствуйте!");

                    startActivity(Intent.createChooser(emailIntent, "Send Email"));
                }
            }
        });
        actionsLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    Fragment fragment = new JustFragment();
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    FragmentTransaction transaction = fm.beginTransaction();
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    transaction.replace(((ViewGroup) getView().getParent()).getId(), fragment);
                    transaction.commit();

                    Bundle getDataBundle = getArguments(); //получаем данные от предыдущего фрагмента
                    Bundle passDataBundle = new Bundle(); //передаем данные в слудующий фрагмент

                    passDataBundle.putString("name", getDataBundle.getString("name"));
                    passDataBundle.putString("post", getDataBundle.getString("post"));
                    passDataBundle.putString("subdiv", getDataBundle.getString("subdiv"));
                    passDataBundle.putString("phone", getDataBundle.getString("phone"));
                    passDataBundle.putString("email", getDataBundle.getString("email"));
                    passDataBundle.putInt("position", getDataBundle.getInt("position", 0));
                    fragment.setArguments(passDataBundle);

                } else {

                }
            }
        });
    }

    private void initElemets() {
        nameTV = (TextView) rootView.findViewById(R.id.name_tv);
        postTV = (TextView) rootView.findViewById(R.id.post_tv);
        subdivTV = (TextView) rootView.findViewById(R.id.subdiv_tv);
        contactLV = (ListView) rootView.findViewById(R.id.lv_contacts);
        actionsLV = (ListView) rootView.findViewById(R.id.lv_action);
        callColleagueCB = (CircleButton) rootView.findViewById(R.id.call_colleague_butt);
        smsColleagueCB = (CircleButton) rootView.findViewById(R.id.sms_colleague_butt);

        callColleagueCB.setOnClickListener(new View.OnClickListener() { //звоним коллеге
            @Override
            public void onClick(View view) {
                if (conArr.get(0).get("Data") != "Данные не заполнены") {
                    Intent callActivity = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + conArr.get(0).get("Data"))); //активность звонка
                    startActivity(callActivity);
                }
            }
        });

        smsColleagueCB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (conArr.get(0).get("Data") != "Данные не заполнены") {
                    Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                    smsIntent.setType("vnd.android-dir/mms-sms");
                    smsIntent.putExtra("address", conArr.get(0).get("Data"));
                    smsIntent.putExtra("sms_body", "Здравствуйте!");
                    startActivity(smsIntent);
                } else {
                    Toast.makeText(getActivity(), "Не заполнен номер телефона коллеги!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        contactLV.setBackgroundColor(Color.WHITE);
        actionsLV.setBackgroundColor(Color.WHITE);

        /** получение данных о сотруднике */
        Bundle getDataBundle = getArguments();

        //ловим данные с предыдущей активности
        nameTV.setText(getDataBundle.getString("name"));
        postTV.setText(getDataBundle.getString("post"));
        subdivTV.setText(getDataBundle.getString("subdiv"));


        HashMap<String, String> map;

        //создаем лист с контактными данными
        map = new HashMap<String, String>();
        map.put("Name", "сотовый");
        if (getDataBundle.getString("phone") != null) {
            map.put("Data", getDataBundle.getString("phone"));
        } else {
            map.put("Data", "Данные не заполнены");
        }
        conArr.add(map);
        try {
            if (!getDataBundle.getString("email").isEmpty()) {
                map = new HashMap<String, String>();
                map.put("Name", "рабочий");
                map.put("Data", getDataBundle.getString("email"));
                conArr.add(map);
            }
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }


        SimpleAdapter adapter = new SimpleAdapter(getActivity(), conArr, android.R.layout.simple_list_item_2,
                new String[]{"Name", "Data"},
                new int[]{android.R.id.text1, android.R.id.text2}) {
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                TextView text2 = (TextView) view.findViewById(android.R.id.text2);
                text1.setTextColor(Color.parseColor("#76b9f1"));
                return view;
            }
        };
        contactLV.setAdapter(adapter);

        //лист с действиями
        String[] actionName = {"Оценить коллегу", "Заказать детальный отчет"};
        actionsLV.setAdapter(new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, actionName));
    }


}
