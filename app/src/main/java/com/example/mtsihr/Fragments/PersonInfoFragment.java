package com.example.mtsihr.Fragments;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.ImageView;
import android.widget.ListAdapter;
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
import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * A simple {@link Fragment} subclass.
 */
public class PersonInfoFragment extends Fragment {

    private View rootView;
    private ListView contactLV, actionsLV;
    private TextView nameTV, postTV, subdivTV;
    private Bundle getDataBundle;
    private ArrayList<HashMap<String, String>> conArr = new ArrayList<>();
    private Realm realm;
    private CircleButton callColleagueCB, smsColleagueCB;
    private CircleImageView circlePhotoColleague;
    private ImageView photoBackground;
    private String name, post, subdiv, phone, email;
    private byte[] photo = null;

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
        getDataBundle = getArguments(); //получение данных с предыдущего фрагмента
        getData();
        initElemets();
        initAdapter();
        itemClick();

        return rootView;
    }

    public void getData() {
        name = getDataBundle.getString("name");
        post = getDataBundle.getString("post");
        subdiv = getDataBundle.getString("subdiv");
        phone = getDataBundle.getString("phone");
        email = getDataBundle.getString("email");
        if (getDataBundle.getByteArray("photo") != null) {
            photo = getDataBundle.getByteArray("photo");
        }
    }

    private void initElemets() {
        nameTV = (TextView) rootView.findViewById(R.id.name_tv);
        postTV = (TextView) rootView.findViewById(R.id.post_tv);
        subdivTV = (TextView) rootView.findViewById(R.id.subdiv_tv);
        contactLV = (ListView) rootView.findViewById(R.id.lv_contacts);
        actionsLV = (ListView) rootView.findViewById(R.id.lv_action);
        callColleagueCB = (CircleButton) rootView.findViewById(R.id.call_colleague_butt);
        smsColleagueCB = (CircleButton) rootView.findViewById(R.id.sms_colleague_butt);
        circlePhotoColleague = (CircleImageView) rootView.findViewById(R.id.profile_image);
        photoBackground = (ImageView) rootView.findViewById(R.id.profile_image_back);


        contactLV.setBackgroundColor(Color.WHITE);
        actionsLV.setBackgroundColor(Color.WHITE);

        //ловим данные с предыдущей активности
        nameTV.setText(name);
        postTV.setText(post);
        subdivTV.setText(subdiv);
        if (photo != null) {
            byte[] photoByte = photo;
            Bitmap bm = BitmapFactory.decodeByteArray(photoByte, 0, photoByte.length);
            circlePhotoColleague.setImageBitmap(bm);
            photoBackground.setImageBitmap(bm);
        }
    }

    private void initAdapter() {
        //создаем HashMap с контактными данными
        HashMap<String, String> map = new HashMap<String, String>();
        if (conArr.size() == 0) { //не добавлять больше элементов, если номер телефона и эмейл есть в conArr
            map.put("Name", "сотовый");
            if (phone != null) {
                map.put("Data", phone);
            } else {
                map.put("Data", "Данные не заполнены");
            }
            conArr.add(map);
            try {
                if (email != null) {
                    map = new HashMap<String, String>();
                    map.put("Name", "рабочий");
                    map.put("Data", email);
                    conArr.add(map);
                }
            } catch (NullPointerException ex) {
                ex.printStackTrace();
            }
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
        String[] actionName = {"Оценить коллегу", "Заказать детальный отчет", "Просмотреть историю оценок"};
        actionsLV.setAdapter(new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, actionName));
        setListViewHeightBasedOnChildren(contactLV); //задаем высоту листу в зависимости от кол-ва его элементов
        setListViewHeightBasedOnChildren(actionsLV);
    }

    private void itemClick() {
        contactLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) { //реализация клика на телефон или email
                //choose app for call
                if (i == 0) {
                    if (conArr.get(i).get("Data") != "Данные не заполнены") {
                        String phoneNum = conArr.get(i).get("Data");
                        Intent callActivity = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNum));
                        startActivity(Intent.createChooser(callActivity, "Позвонить коллеге"));
                    }
                } else {
                    //send email
                    String email = conArr.get(i).get("Data");
                    Intent emailIntent = new Intent(Intent.ACTION_SEND);
                    emailIntent.setType("message/rfc822");
                    emailIntent.putExtra(Intent.EXTRA_EMAIL, email);
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "");
                    emailIntent.putExtra(Intent.EXTRA_TEXT, "");

                    startActivity(Intent.createChooser(emailIntent, "Отправить email..."));
                }
            }
        });
        actionsLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) { //переход на фрагмент с оценкой
                switch (i) {
                    case 0:
                        Fragment fragment = new JustFragment();
                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        FragmentTransaction transaction = fm.beginTransaction();
                        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                        transaction.replace(((ViewGroup) getView().getParent()).getId(), fragment).addToBackStack(null).commit();

                        Bundle getDataBundle = getArguments(); //получаем данные от предыдущего фрагмента
                        Bundle passDataBundle = new Bundle(); //передаем данные в слудующий фрагмент

                        passDataBundle.putString("name", name);
                        passDataBundle.putString("post", post);
                        passDataBundle.putString("subdiv", subdiv);
                        passDataBundle.putString("phone", phone);
                        passDataBundle.putString("email", email);
                        if (photo != null) {
                            passDataBundle.putByteArray("photo", photo);
                        }
                        passDataBundle.putInt("position", getDataBundle.getInt("position", 0));
                        fragment.setArguments(passDataBundle);
                        break;
                    case 1:
                        //send email
                        Intent emailIntent = new Intent(Intent.ACTION_SEND);
                        emailIntent.setType("message/rfc822");
                        emailIntent.putExtra(Intent.EXTRA_EMAIL, "prosto_mail@mts.ru"); //кому отправить
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Детальный отчет по оценке"); //тема письма
                        emailIntent.putExtra(Intent.EXTRA_TEXT, "<Имя>" + name + "</Имя> \n <Телефон>" + phone + "</Телефон>"); //текст письма

                        startActivity(Intent.createChooser(emailIntent, "Send Email"));
                        break;
                    case 2:
                        Fragment historyFragment = new HistoryFragment();
                        FragmentManager historyFm = getActivity().getSupportFragmentManager();
                        FragmentTransaction historyTrans = historyFm.beginTransaction();
                        historyTrans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                        historyTrans.replace(((ViewGroup) getView().getParent()).getId(), historyFragment).addToBackStack(null).commit();
                        break;
                }
            }
        });

        callColleagueCB.setOnClickListener(new View.OnClickListener() { //звоним коллеге
            @Override
            public void onClick(View view) { //позвонить коллеге
                if (conArr.get(0).get("Data") != "Данные не заполнены") {
                    Intent callActivity = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + conArr.get(0).get("Data"))); //активность звонка
                    startActivity(callActivity);
                } else {
                    Toast.makeText(getActivity(), "Не заполнен номер телефона коллеги!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        smsColleagueCB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { //написать смс коллеге
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

    public static void setListViewHeightBasedOnChildren(ListView listView) { //выставляем высоту листа в зависимости от кол-ва элементов
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }
}
