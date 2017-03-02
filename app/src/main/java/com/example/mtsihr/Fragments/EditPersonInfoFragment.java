package com.example.mtsihr.Fragments;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mtsihr.MainActivity;
import com.example.mtsihr.Models.Colleague;
import com.example.mtsihr.R;
import com.github.pinball83.maskededittext.MaskedEditText;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditPersonInfoFragment extends Fragment {

    private View rootView;
    private TextView colleagueNameTV;
    private MaskedEditText editPhoneET;
    private EditText editEmailET, editOrgET, editPostET;
    private Bundle getDataBundle;
    private Realm realm;
    private int position;

    public EditPersonInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_edit_person_info, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Редактирование информации"); //заголовок тулбара
        realm = Realm.getDefaultInstance();

        getDataBundle = getArguments();
        initElements();
        setHasOptionsMenu(true);
        getData();
        return rootView;
    }

    private void initElements() {
        colleagueNameTV = (TextView) rootView.findViewById(R.id.colleague_name_ei_tv);
        editPhoneET = (MaskedEditText) rootView.findViewById(R.id.edit_phone_ei_et);
        editEmailET = (EditText) rootView.findViewById(R.id.edit_email_ei_et);
        editOrgET = (EditText) rootView.findViewById(R.id.edit_org_ei_et);
        editPostET = (EditText) rootView.findViewById(R.id.edit_post_ei_et);
    }

    //получаем данные с предыдущуего фрагмента
    public void getData() {
        if (getDataBundle != null) {
            colleagueNameTV.setText(getDataBundle.getString("name"));
            String phoneNum = getDataBundle.getString("phone");
            if(phoneNum!=null){
                //удаление лишних символов из номера телефона
                if(phoneNum.length()>10){
                    phoneNum = getPhoneNum(phoneNum);
                    phoneNum = phoneNum.substring(phoneNum.length()-10,phoneNum.length());
                }
            }
            editPhoneET.setMaskedText(phoneNum);
            editEmailET.setText(getDataBundle.getString("email"));
            editOrgET.setText(getDataBundle.getString("post"));
            editPostET.setText(getDataBundle.getString("subdiv"));
            position = getDataBundle.getInt("position");
        }
    }

    @Override
    //добавляем меню в тулбар с кнопкой "Сохранить информацию"
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.menu_save_person_info, menu);
    }

    @Override
    //обрабатываем нажатие на элмент тулбара
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save_info:

                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Colleague toEdit = realm.where(Colleague.class)
                                .equalTo("name", colleagueNameTV.getText().toString()).findFirst();
                        if (toEdit != null) {
                            //добавляем данные о коллеге в бд
                            String et = editPhoneET.getText().toString();
                            if(getPhoneNum(et).length()==11){
                                toEdit.setPhone(editPhoneET.getText().toString());
                            }
                            toEdit.setEmail(editEmailET.getText().toString());
                            toEdit.setSubdivision(editPostET.getText().toString());
                            toEdit.setPost(editOrgET.getText().toString());
                        }
                    }
                });

                Intent itentTo = new Intent(getContext(), MainActivity.class);
                startActivity(itentTo);

                break;
        }

        return (super.onOptionsItemSelected(item));
    }
    public String getPhoneNum(String num){
        num = num.replaceAll("\\(", "");
        num = num.replaceAll("\\)", "");
        num = num.replaceAll("\\+", "");
        num = num.replaceAll(" ", "");
        num = num.replaceAll("\\-", "");
        num = num.replaceAll("\\(", "");
        num = num.replaceAll("\\)", "");

        return num;
    }
}
