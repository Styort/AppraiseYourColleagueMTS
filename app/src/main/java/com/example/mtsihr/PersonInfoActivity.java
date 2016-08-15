package com.example.mtsihr;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.mtsihr.Models.Colleague;

import java.util.ArrayList;
import java.util.HashMap;

import at.markushi.ui.CircleButton;
import io.realm.Realm;
import io.realm.RealmResults;


public class PersonInfoActivity extends AppCompatActivity {

    private ListView contactLV, actionsLV;
    private TextView nameTV, postTV, subdivTV;
    private ArrayList<HashMap<String, String>> conArr = new ArrayList<>();
    private Realm realm;
    private CircleButton callColleagueCB,smsColleagueCB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_info);
        getSupportActionBar().setTitle("Информация о коллеге"); //заголовок тулбара

        realm = Realm.getDefaultInstance();

        initElemets();
        itemClick();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.person_info_menu, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_delete) {

            AlertDialog.Builder alert = new AlertDialog.Builder(
                    this);
            alert.setMessage("Удалить коллегу из списка?");
            alert.setPositiveButton("Да", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = getIntent();
                    int pos = intent.getIntExtra("position",0);
                    RealmResults<Colleague> colleagueRealmResults = realm
                            .where(Colleague.class).findAll();
                    realm.beginTransaction();
                    colleagueRealmResults.deleteFromRealm(pos);
                    realm.commitTransaction();

                    dialog.dismiss();
                    Intent itentTo = new Intent(getApplicationContext(), MainActivity.class);
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

        }
        return (super.onOptionsItemSelected(item));
    }

    private void itemClick() {
        contactLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //choose app for call
                if (i == 0) {
                    String phoneNum = conArr.get(i).get("Data");
                    Intent callActivity = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNum));
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    startActivity(callActivity);
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
                    Intent intent = new Intent(getApplicationContext(), JustActivity.class);
                    Intent getDataIntent = getIntent();

                    String email = getDataIntent.getStringExtra("email");
                    intent.putExtra("name", getDataIntent.getStringExtra("name"));
                    intent.putExtra("post", getDataIntent.getStringExtra("post"));
                    intent.putExtra("subdiv", getDataIntent.getStringExtra("subdiv"));
                    intent.putExtra("phone", getDataIntent.getStringExtra("phone"));
                    intent.putExtra("email", getDataIntent.getStringExtra("email"));
                    intent.putExtra("position", getDataIntent.getIntExtra("position",0));

                    startActivity(intent);
                } else {

                }
            }
        });
    }

    private void initElemets() {
        nameTV = (TextView) findViewById(R.id.name_tv);
        postTV = (TextView) findViewById(R.id.post_tv);
        subdivTV = (TextView) findViewById(R.id.subdiv_tv);
        contactLV = (ListView) findViewById(R.id.lv_contacts);
        actionsLV = (ListView) findViewById(R.id.lv_action);
        callColleagueCB = (CircleButton) findViewById(R.id.call_colleague_butt);
        smsColleagueCB = (CircleButton) findViewById(R.id.sms_colleague_butt);

        callColleagueCB.setOnClickListener(new View.OnClickListener() { //звоним коллеге
            @Override
            public void onClick(View view) {
                Intent callActivity = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + conArr.get(0).get("Data"))); //активность звонка
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                startActivity(callActivity);
            }
        });

        smsColleagueCB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                smsIntent.setType("vnd.android-dir/mms-sms");
                smsIntent.putExtra("address", conArr.get(0).get("Data"));
                smsIntent.putExtra("sms_body","Здравствуйте!");
                startActivity(smsIntent);
            }
        });

        contactLV.setBackgroundColor(Color.WHITE);
        actionsLV.setBackgroundColor(Color.WHITE);

        /** получение данных о сотруднике */
        Intent intent = getIntent();

        //ловим данные с предыдущей активности
        nameTV.setText(intent.getStringExtra("name"));
        postTV.setText(intent.getStringExtra("post"));
        subdivTV.setText(intent.getStringExtra("subdiv"));


        HashMap<String, String> map;

        //создаем лист с контактными данными
        map = new HashMap<String, String>();
        map.put("Name", "сотовый");
        map.put("Data", intent.getStringExtra("phone"));
        conArr.add(map);
        try{
            if(!intent.getStringExtra("email").isEmpty()){
                map = new HashMap<String, String>();
                map.put("Name", "рабочий");
                map.put("Data", intent.getStringExtra("email"));
                conArr.add(map);
            }
        }catch (NullPointerException ex){
            ex.printStackTrace();
        }


        SimpleAdapter adapter = new SimpleAdapter(this, conArr, android.R.layout.simple_list_item_2,
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
        actionsLV.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, actionName));
    }

}
