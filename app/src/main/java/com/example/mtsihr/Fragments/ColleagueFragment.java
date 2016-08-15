package com.example.mtsihr.Fragments;


import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.example.mtsihr.Adapters.ColleagueAdapter;
import com.example.mtsihr.Models.Colleague;
import com.example.mtsihr.PersonInfoActivity;
import com.example.mtsihr.R;

import java.util.ArrayList;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;


/**
 * A simple {@link Fragment} subclass.
 */
public class ColleagueFragment extends Fragment {

    private Realm realm;
    private ListView colleagueList;
    private ColleagueAdapter colleagueAdapter;
    private ArrayList<Colleague> colleagues;
    private EditText searachColleagueEdit;
    private View rootView;
    private final int PICK_CONTACT = 1;

    public ColleagueFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_colleague, container, false);

        //убираем автооткрытие клавиатуры
        this.getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        // Create the Realm configuration
        // Open the Realm for the UI thread.
        realm = Realm.getDefaultInstance();
        colleagues = new ArrayList<>();
        searachColleagueEdit = (EditText) rootView.findViewById(R.id.search_et);
        colleagueList = (ListView) rootView.findViewById(R.id.colleague_list);

        RealmResults<Colleague> colleagueRealmResults = realm
                .where(Colleague.class).findAll(); //считываем все данные что есть в бд
        colleagues = (ArrayList<Colleague>) realm.copyFromRealm(colleagueRealmResults); //переносим данные в наш лист
        //Добавление нового контакта
        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, PICK_CONTACT);
            }
        });


        initAdapter();
        showInfo();
        searchFilter();

        return rootView;
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


    private void showInfo() {
        colleagueList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity().getApplicationContext(), PersonInfoActivity.class);

                Colleague concreteColleague = (Colleague) adapterView.getItemAtPosition(i);

                intent.putExtra("name", concreteColleague.getName());
                intent.putExtra("post", concreteColleague.getPost());
                intent.putExtra("subdiv", concreteColleague.getSubdivision());
                intent.putExtra("email", concreteColleague.getEmail());
                intent.putExtra("phone", concreteColleague.getPhone());
                intent.putExtra("position", i);
                startActivity(intent);
            }
        });
    }

    private void initAdapter() {
        colleagueAdapter = new ColleagueAdapter(this.getActivity(), R.layout.colleague_item, colleagues);
        colleagueList.setAdapter(colleagueAdapter);
        colleagueAdapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_CONTACT) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                Uri contactData = data.getData();
                Cursor c =  getActivity().getContentResolver().query(contactData, null, null, null, null);
                String mContactId,mContactName,mPhoneNumber = null,mEmail = null;
                if (c.moveToNext()) {
                    mContactId = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
                    mContactName = c.getString(c.getColumnIndexOrThrow(
                            ContactsContract.Contacts.DISPLAY_NAME));

                    String hasPhone = c.getString(c.getColumnIndex(
                            ContactsContract.Contacts.HAS_PHONE_NUMBER));

                    // если есть телефоны, получаем и выводим их
                    if (hasPhone.equalsIgnoreCase("1")) {
                        Cursor phones = getActivity().getContentResolver().query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + mContactId,
                                null,
                                null);

                        while (phones.moveToNext()) {
                            mPhoneNumber = phones.getString(phones.getColumnIndex(
                                    ContactsContract.CommonDataKinds.Phone.NUMBER));
                        }
                        phones.close();
                    }

                    // Достаем email-ы
                    Cursor emails = getActivity().getContentResolver().query(
                            ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + mContactId,
                            null,
                            null);
                    while (emails.moveToNext()) {
                        mEmail = emails.getString(
                                emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                    }
                    emails.close();

                    realm.beginTransaction();
                    // Set its fields
                    Colleague colleague = realm.createObject(Colleague.class);
                    colleague.setName(mContactName);
                    colleague.setPhone(mPhoneNumber);
                    colleague.setEmail(mEmail);

                    realm.commitTransaction();

                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.detach(this).attach(this).commit();
                }
            }
        }
    }

    public void searchFilter() {
        searachColleagueEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String text = searachColleagueEdit.getText().toString().toLowerCase(Locale.getDefault());
                colleagueAdapter.filter(text);
            }
        });
    }
}
