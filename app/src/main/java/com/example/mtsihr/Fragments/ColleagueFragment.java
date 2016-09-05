package com.example.mtsihr.Fragments;


import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mtsihr.Adapters.ColleagueAdapter;
import com.example.mtsihr.Models.Colleague;
import com.example.mtsihr.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Locale;
import java.util.jar.Manifest;

import io.realm.Realm;
import io.realm.RealmResults;


/**
 * A simple {@link Fragment} subclass.
 */
public class ColleagueFragment extends Fragment {

    private static final int REQUEST_READ_CONTACT = 555;
    private Realm realm;
    private ListView colleagueList;
    private ColleagueAdapter colleagueAdapter;
    private ArrayList<Colleague> colleagues;
    private EditText searachColleagueEdit;
    private View rootView;
    private final int PICK_CONTACT = 1;
    private RealmResults<Colleague> colleagueRealmResults;
    private FragmentTransaction transaction;
    private FloatingActionButton fab;
    private Bundle getDataBundle;
    private Intent dataContact;
    private Boolean colleagueIsExists = false;

    public ColleagueFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_colleague, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Коллеги"); //заголовок тулбара
        //убираем автооткрытие клавиатуры при старте
        this.getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //получение данных с предыдущего фрагмента
        getDataBundle = getArguments();
        realm = Realm.getDefaultInstance();
        colleagues = new ArrayList<>();

        //считываем всех коллег, которые есть бд
        colleagueRealmResults = realm.where(Colleague.class).findAll();
        //переносим данные в наш лист
        colleagues = (ArrayList<Colleague>) realm.copyFromRealm(colleagueRealmResults);


        initElements(); //инициализируем элементы view
        initAdapter(); //инициализируем адаптер
        showInfo(); //переход на фрагмент PersonInfo
        fabShowHide(); //скрываем кнопку добавления коллеги при скроле листа
        registerForContextMenu(colleagueList); //создаем контекстное меню для списка коллег
        searchFilter(); //фильтр поиска

        return rootView;
    }

    //скрыть кнопку добавить коллегу при скроле
    private void fabShowHide() {
        colleagueList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                if (i == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    fab.show();
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                if (i > 0 || i1 < 0 && fab.isShown())
                    fab.hide();
            }
        });
    }


    private void initElements() {
        searachColleagueEdit = (EditText) rootView.findViewById(R.id.search_et);
        colleagueList = (ListView) rootView.findViewById(R.id.colleague_list);
        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);

        //Добавление нового контакта
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //открываем список контактов
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, PICK_CONTACT);
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.menu_context_colleague, menu);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        String title = (String) ((TextView) info.targetView.findViewById(R.id.name)).getText();
        menu.setHeaderTitle(title); //добавляем в контекстное меню заголовок с именем коллеги
    }

    @Override
    //обрабатываем нажатия на элементы контекстного меню
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        //получаем нажатый обьект в отфильтрованном листе
        Object origObj = colleagueAdapter.getItem(info.position);
        //находим позицию объекта origObj в неотфильтрованном листе
        int position = colleagueAdapter.getPosition(origObj);
        switch (item.getItemId()) {
            //переходим к оцениванию коллеги
            case R.id.estimate_menu:
                Fragment fragment = new JustFragment();
                FragmentManager fm = getActivity().getSupportFragmentManager();
                transaction = fm.beginTransaction();
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                transaction.replace(((ViewGroup) getView().getParent()).getId(), fragment).addToBackStack(null).commit();

                Bundle bundle = new Bundle();
                bundle.putString("name", colleagues.get(position).getName());
                bundle.putString("post", colleagues.get(position).getPost());
                bundle.putString("subdiv", colleagues.get(position).getSubdivision());
                bundle.putString("phone", colleagues.get(position).getPhone());
                bundle.putString("email", colleagues.get(position).getEmail());
                if (colleagues.get(position).getPhoto() != null) {
                    bundle.putByteArray("photo", colleagues.get(position).getPhoto());
                }
                bundle.putInt("position", position);
                fragment.setArguments(bundle);
                return true;
            //удаляем коллегу из списка
            case R.id.delete_menu:
                realm.beginTransaction();
                //удаляем коллегу из бд
                colleagueRealmResults.deleteFromRealm(position);
                realm.commitTransaction();
                //обновляем фрагмент
                transaction = getFragmentManager().beginTransaction();
                transaction.detach(this).attach(this).commit();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
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
        boolean isShare = false, isJust = false;
        if (getDataBundle != null) {
            //получаем данные с предыдущего фрагмента
            isShare = getDataBundle.getBoolean("share"); //знак о том, что при нажатии на коллегу, мы поделимся с ним приложением
            isJust = getDataBundle.getBoolean("just"); //знак о том, что при нажатии на коллегу, мы перейдем выбору оценки
        }
        if (isShare) {
            //делимся приложением
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Поделиться приложением");
            colleagueList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Object origObj = colleagueAdapter.getItem(i);
                    int position = colleagueAdapter.getPosition(origObj);
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                    emailIntent.setData(Uri.parse("mailto:")); // only email apps should handle this
                    emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{colleagues.get(position).getEmail()});
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "");
                    if (emailIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivity(Intent.createChooser(emailIntent, "Отправить email..."));
                    }

                }
            });
        } else if (isJust) {
            //переходим к оценке коллеги
            colleagueList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Fragment fragment = new JustFragment();
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    transaction = fm.beginTransaction();
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    transaction.replace(((ViewGroup) getView().getParent()).getId(), fragment).addToBackStack(null).commit();

                    //получаем позицию выбранного коллеги
                    Colleague concreteColleague = (Colleague) adapterView.getItemAtPosition(i);

                    //передаем данные в слудующий фрагмент
                    Bundle passDataBundle = new Bundle();

                    passDataBundle.putString("name", concreteColleague.getName());
                    passDataBundle.putString("post", concreteColleague.getPost());
                    passDataBundle.putString("subdiv", concreteColleague.getSubdivision());
                    passDataBundle.putString("phone", concreteColleague.getPhone());
                    passDataBundle.putString("email", concreteColleague.getEmail());
                    if (concreteColleague.getPhoto() != null) {
                        passDataBundle.putByteArray("photo", concreteColleague.getPhoto());
                    }
                    Object origObj = colleagueAdapter.getItem(i);
                    int position = colleagueAdapter.getPosition(origObj);
                    passDataBundle.putInt("position", position);
                    fragment.setArguments(passDataBundle);
                }
            });
        } else {
            //инфо о коллеге
            colleagueList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Fragment fragment = new PersonInfoFragment();
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    transaction = fm.beginTransaction();
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    transaction.replace(((ViewGroup) getView().getParent()).getId(), fragment).addToBackStack(null).commit();

                    //получаем позицию выбранного коллеги в листе с учетом фильтра
                    Colleague concreteColleague = (Colleague) adapterView.getItemAtPosition(i);
                    //передаем данные о коллеге в фрагмент PersonInfo
                    Bundle bundle = new Bundle();
                    bundle.putString("name", concreteColleague.getName());
                    bundle.putString("post", concreteColleague.getPost());
                    bundle.putString("subdiv", concreteColleague.getSubdivision());
                    bundle.putString("phone", concreteColleague.getPhone());
                    bundle.putString("email", concreteColleague.getEmail());
                    if (concreteColleague.getPhoto() != null) {
                        bundle.putByteArray("photo", concreteColleague.getPhoto());
                    }
                    Object origObj = colleagueAdapter.getItem(i);
                    int position = colleagueAdapter.getPosition(origObj);
                    bundle.putInt("position", position);
                    fragment.setArguments(bundle);
                }
            });
        }
    }

    private void initAdapter() {
        colleagueAdapter = new ColleagueAdapter(this.getActivity(), R.layout.colleague_item, colleagues);
        colleagueList.setAdapter(colleagueAdapter);
        colleagueAdapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_CONTACT) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                //получаем данные с контакт листа
                if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.READ_CONTACTS)
                        == PackageManager.PERMISSION_GRANTED) {
                    dataContact = data;
                    colleagueIsExists = false;
                    //добавляем коллегу в список в новом потоке
                    new Thread(new Runnable() {
                        public void run() {
                            readContacts(data);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (colleagueIsExists){
                                        Toast.makeText(getActivity(), "Коллега уже есть в вашем списке!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }).start();
                } else {
                    if (shouldShowRequestPermissionRationale(android.Manifest.permission.READ_CONTACTS)) {
                        Toast.makeText(getActivity(), "Read contacts needed to show contacts preview", Toast.LENGTH_LONG).show();
                    }
                    requestPermissions(new String[]{android.Manifest.permission.READ_CONTACTS}, REQUEST_READ_CONTACT);
                }
            }
        }
    }
    public InputStream openDisplayPhoto(long contactId) { //получаем поток с фоткой коллеги
        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
        Uri displayPhotoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.DISPLAY_PHOTO);
        try {
            AssetFileDescriptor fd =
                    getActivity().getContentResolver().openAssetFileDescriptor(displayPhotoUri, "r");
            return fd.createInputStream();
        } catch (IOException e) {
            return null;
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

    public void readContacts(Intent data){
        Uri contactData = data.getData();
        Cursor c = getActivity().getContentResolver().query(contactData, null, null, null, null);
        String mContactId, mContactName, mPhoneNumber = null, mEmail = null, orgName = null, orgTitle = null;
        if (c.moveToNext()) {
            boolean isExists = false;
            mContactId = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
            InputStream isPhoto = openDisplayPhoto(Long.parseLong(mContactId));
            byte[] byteArray = null;
            if (isPhoto != null) {
                Bitmap photoBmp = BitmapFactory.decodeStream(isPhoto);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                photoBmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                //конвертируем bitmap в bytearray для того, чтобы его можно было передать след. фрагмент
                byteArray = stream.toByteArray();
                try {
                    isPhoto.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            mContactName = c.getString(c.getColumnIndexOrThrow(
                    ContactsContract.Contacts.DISPLAY_NAME));

            String hasPhone = c.getString(c.getColumnIndex(
                    ContactsContract.Contacts.HAS_PHONE_NUMBER));

            // если есть телефон, получаем его
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
                    //проверка, если ли коллега с данным номером в бд
                    for (Colleague col : colleagues) {
                        if (col.getPhone() != null) {
                            if (col.getPhone().equals(mPhoneNumber)) {
                                isExists = true;
                                colleagueIsExists = true;
                                break;
                            }
                        }
                    }
                }
                phones.close();
            }
            if (!isExists) {
                //если человек с таким телефоном уже есть в списке, то не добавлять
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
                    for (Colleague col : colleagues) {
                        if (col.getEmail() != null) {
                            if (col.getEmail().equals(mEmail)) {
                                isExists = true;
                                colleagueIsExists = true;
                                break;
                            }
                        }
                    }
                }
                emails.close();
                if (!isExists) {
                    String orgWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
                    String[] orgWhereParams = new String[]{mContactId,
                            ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE};

                    //Получаем данные о работе
                    Cursor orgCur = getActivity().getContentResolver().query(
                            ContactsContract.Data.CONTENT_URI,
                            null,
                            orgWhere,
                            orgWhereParams,
                            null
                    );
                    if (orgCur.moveToFirst()) {
                        orgName = orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.DATA));
                        orgTitle = orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.TITLE));
                    }
                    orgCur.close();

                    Realm realmTh = Realm.getDefaultInstance();

                    realmTh.beginTransaction();
                    // Set its fields
                    Colleague colleague = realmTh.createObject(Colleague.class);
                    colleague.setName(mContactName);
                    colleague.setPhone(mPhoneNumber);
                    colleague.setEmail(mEmail);
                    colleague.setPost(orgName);
                    colleague.setSubdivision(orgTitle);
                    colleague.setPhoto(byteArray);

                    realmTh.commitTransaction();

                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.detach(this).attach(this).commit();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_CONTACT: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    readContacts(dataContact);
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    Toast.makeText(getActivity(), "Permission was not granted", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
