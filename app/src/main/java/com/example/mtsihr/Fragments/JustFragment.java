package com.example.mtsihr.Fragments;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.balysv.materialripple.MaterialRippleLayout;
import com.example.mtsihr.Adapters.EvaluateAdapter;
import com.example.mtsihr.EvaluateActivity;
import com.example.mtsihr.MainActivity;
import com.example.mtsihr.Models.Colleague;
import com.example.mtsihr.Models.Evaluate;
import com.example.mtsihr.Models.HistoryModel;
import com.example.mtsihr.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * A simple {@link Fragment} subclass.
 */
public class JustFragment extends Fragment {


    private Realm realm;
    private View rootView;
    private ListView evalLV;
    private EvaluateAdapter evaluateAdapter;
    private ArrayList<Evaluate> evaluateArr;
    private String[] firstSymbol = {"П", "Р", "О", "С", "Т", "О"};
    private String[] quality = {"артнерство", "езультативность", "тветственность", "мелость", "ворчество", "ткрытость"};
    private TextView nameEvTV, postEvTV, subdivEvTV;
    private LinearLayout showColleagueLL;
    private Bundle getDataBundle;
    private CircleImageView circlePhotoColleague;
    private String name, post, subdiv, phone, email;
    private EditText commentET;
    private ImageView justBackgrIV;
    private byte[] photo = null;

    public JustFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_just, container, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("ПРОСТО");

        realm.getDefaultInstance();
        //получаем данные с предыдущего фрагмента
        getDataBundle = getArguments();
        getData();
        initElements();
        //показываем элемент тулбара "Отпраивть оценку"
        setHasOptionsMenu(true);
        initClicks();

        return rootView;
    }

    public void getData() {
        if (getDataBundle != null) {
            name = getDataBundle.getString("name");
            post = getDataBundle.getString("post");
            subdiv = getDataBundle.getString("subdiv");
            phone = getDataBundle.getString("phone");
            email = getDataBundle.getString("email");
            if (getDataBundle.getByteArray("photo") != null) {
                photo = getDataBundle.getByteArray("photo");
            }
        }
    }

    private void initElements() {
        nameEvTV = (TextView) rootView.findViewById(R.id.name_from_ev);
        postEvTV = (TextView) rootView.findViewById(R.id.post_from_ev);
        subdivEvTV = (TextView) rootView.findViewById(R.id.subdivision_from_ev);
        showColleagueLL = (LinearLayout) rootView.findViewById(R.id.show_colleague_ll);
        circlePhotoColleague = (CircleImageView) rootView.findViewById(R.id.photo_from_ev);
        commentET = (EditText) rootView.findViewById(R.id.comment_et);
        justBackgrIV = (ImageView) rootView.findViewById(R.id.just_bg_iv);
        getRandomBackgroundImg();


        showColleagueLL.setOnClickListener(new View.OnClickListener() {
            @Override
            //переходим к выбору коллеги/к просмотру инфы о коллеге
            public void onClick(View view) {
                Fragment fragment;
                FragmentManager fm = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                //передаем данные в следующий фрагмент
                Bundle passDataBundle = new Bundle();
                if (getDataBundle != null) {
                    fragment = new PersonInfoFragment();
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    transaction.replace(((ViewGroup) getView().getParent()).getId(), fragment).addToBackStack(null).commit();

                    passDataBundle.putString("name", name);
                    passDataBundle.putString("post", post);
                    passDataBundle.putString("subdiv", subdiv);
                    passDataBundle.putString("phone", phone);
                    passDataBundle.putString("email", email);
                    if (photo != null) {
                        passDataBundle.putByteArray("photo", photo);
                    }
                    passDataBundle.putInt("position", getDataBundle.getInt("position"));
                    fragment.setArguments(passDataBundle);
                } else {
                    fragment = new ColleagueFragment();
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    transaction.replace(((ViewGroup) getView().getParent()).getId(), fragment).addToBackStack(null).commit();

                    //даем знак следующему фрагменту , что нужно выбрать коллегу для оценки, а не перейти на его инфо.
                    passDataBundle.putBoolean("just", true);
                    fragment.setArguments(passDataBundle);
                }

            }
        });

        /** получение данных о сотруднике */
        //ловим данные с предыдущей активности
        if (getDataBundle != null) {
            nameEvTV.setText(name);
            postEvTV.setText(post);
            subdivEvTV.setText(subdiv);
            if (photo != null) {
                byte[] photoByte = getDataBundle.getByteArray("photo");
                Bitmap bm = BitmapFactory.decodeByteArray(photoByte, 0, photoByte.length);
                circlePhotoColleague.setImageBitmap(bm);
            }
        } else {
            nameEvTV.setText("Выберите коллегу");
        }


        evalLV = (ListView) rootView.findViewById(R.id.evaluate_list);
        evaluateArr = new ArrayList<>();

        for (int i = 0; i < quality.length; i++) {
            evaluateArr.add(new Evaluate(firstSymbol[i], quality[i], "Качество не проявлено"));
        }
        initAdapter();
        setListViewHeightBasedOnChildren(evalLV);
    }

    public void getRandomBackgroundImg() {
        int[] images = new int[]{R.drawable.just_1, R.drawable.just_2, R.drawable.just_3,
                R.drawable.just_4, R.drawable.just_5, R.drawable.just_6};

        // Get a random between 0 and images.length-1
        int imageId = (int) (Math.random() * images.length);

        // Set the image
        justBackgrIV.setBackgroundResource(images[imageId]);
    }

    private void initAdapter() {
        evaluateAdapter = new EvaluateAdapter(getActivity(), R.layout.evaluate_list_item, evaluateArr);
        evalLV.setAdapter(evaluateAdapter);
        evaluateAdapter.notifyDataSetChanged();
    }

    private void initClicks() {
        //переход к выбору оценки
        evalLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent getEvelIntent = new Intent(getContext(), EvaluateActivity.class);
                getEvelIntent.putExtra("evalName", firstSymbol[i] + quality[i]);
                getEvelIntent.putExtra("position", i);
                getEvelIntent.putExtra("eval", evaluateArr.get(i).eval);
                startActivityForResult(getEvelIntent, 1);
            }
        });
    }

    //выставляем высоту листа в зависимости от кол-ва элементов
    public static void setListViewHeightBasedOnChildren(ListView listView) {
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }
        //получаем оценку
        String name = data.getStringExtra("eval");

        int pos = data.getIntExtra("pos", 0);
        evaluateArr.set(pos, new Evaluate(firstSymbol[pos], quality[pos], name));
        evaluateAdapter.notifyDataSetChanged();
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
    //добавляем меню в тулбар с кнопкой "Удалить коллегу из списка"
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.send_eval_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_send:
                //обрабатываем нажатие на элмент тулбара (отправить оценку)
                SharedPreferences sharedPref = getActivity().getSharedPreferences("settings", 0);
                //проверяем, сохранять историю или нет
                Boolean saveHistory = sharedPref.getBoolean("save_history", true);

                //если в настройках включено сохранение, то сохраняем
                if (saveHistory) {
                    realm.beginTransaction();
                    HistoryModel history = realm.createObject(HistoryModel.class);
                    history.setName(name);
                    Date d = new Date();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy hh:mm");
                    history.setPost(post);
                    history.setSubdiv(subdiv);
                    history.setDateOfEval(dateFormat.format(d));
                    history.setCourage(evaluateArr.get(3).eval);
                    history.setCreativity(evaluateArr.get(4).eval);
                    history.setEfficiency(evaluateArr.get(1).eval);
                    history.setOpenness(evaluateArr.get(5).eval);
                    history.setPartnership(evaluateArr.get(0).eval);
                    history.setResponsibility(evaluateArr.get(2).eval);
                    if (commentET.getText() != null) {
                        history.setComment(commentET.getText().toString());
                    } else {
                        commentET.setText("");
                    }
                    history.setPhone(phone);
                    if (email != null) {
                        history.setEmail(email);
                    }
                    if (photo != null) {
                        history.setPhoto(photo);
                    }
                    realm.commitTransaction();
                    Toast.makeText(getActivity(), "Данные сохранены в истории!", Toast.LENGTH_SHORT).show();
                }

                int assessment[] = new int[6];
                for (int i = 0; i < evaluateArr.size(); i++) {
                    switch (evaluateArr.get(i).eval) {
                        case "Не соответствует ожиданиям":
                            assessment[i] = 1;
                            break;
                        case "Соответствует ожиданиям":
                            assessment[i] = 2;
                            break;
                        case "Превосходит ожидания":
                            assessment[i] = 3;
                            break;
                        case "Значительно превосходит ожидания":
                            assessment[i] = 4;
                            break;
                        default:
                            assessment[i] = 0;
                    }
                }
                //переходим на отправку email
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:"));
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"prosto_mail@mts.ru"}); //кому отправляем
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Оценка [" + name + "," + phone + "]"); //тема письма
                emailIntent.putExtra(Intent.EXTRA_TEXT, "<Имя>" + name + "</Имя> \n <Телефон>" + phone + "</Телефон> \n " +  //текст письма
                        "<Партнерство>" + assessment[0] + "</Партнерство> \n <Результативность>" + assessment[1] + "</Результативность>" +
                        "<Ответственность>" + assessment[2] + "</Ответственность> \n <Смелость>" + assessment[3] + "</Смелость>" +
                        "<Творчество>" + assessment[4] + "</Творчество> \n <Открытость>" + assessment[5] + "</Открытость> +" +
                        "\n <Комментарий>" + commentET.getText().toString() + "</Комментарий>");

                if (emailIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(Intent.createChooser(emailIntent, "Отправить email..."));
                }
                break;
        }

        return (super.onOptionsItemSelected(item));
    }
}
