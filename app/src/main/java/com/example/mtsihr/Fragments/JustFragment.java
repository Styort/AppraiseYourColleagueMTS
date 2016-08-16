package com.example.mtsihr.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mtsihr.Adapters.EvaluateAdapter;
import com.example.mtsihr.EvaluateActivity;
import com.example.mtsihr.Models.Colleague;
import com.example.mtsihr.Models.Evaluate;
import com.example.mtsihr.Models.HistoryModel;
import com.example.mtsihr.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.realm.Realm;

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
    private Button sendEvalButt;
    private Bundle getDataBundle;

    public JustFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_just, container, false);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("ПРОСТО"); //заголовок тулбара

        realm.getDefaultInstance();
        getDataBundle = getArguments(); //получаем данные от предыдущего фрагмента

        initElements();
        initClicks();

        return rootView;
    }


    private void initClicks() {
        evalLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) { //переход к выбору оценки
                Intent getEvelIntent = new Intent(getContext(), EvaluateActivity.class);
                getEvelIntent.putExtra("evalName", firstSymbol[i] + quality[i]);
                getEvelIntent.putExtra("position", i);
                getEvelIntent.putExtra("eval", evaluateArr.get(i).eval);
                startActivityForResult(getEvelIntent, 1);
            }
        });
        sendEvalButt.setOnClickListener(new View.OnClickListener() { //отправка данных
            @Override
            public void onClick(View view) {
                realm.beginTransaction();
                // Set its fields
                HistoryModel history = realm.createObject(HistoryModel.class);
                history.setName(getDataBundle.getString("name"));
                Date d = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy hh:mm");
                history.setPost(getDataBundle.getString("post"));
                history.setSubdiv(getDataBundle.getString("subdiv"));
                history.setDateOfEval(dateFormat.format(d));
                history.setCourage(evaluateArr.get(3).eval);
                history.setCreativity(evaluateArr.get(4).eval);
                history.setEfficiency(evaluateArr.get(1).eval);
                history.setOpenness(evaluateArr.get(5).eval);
                history.setPartnership(evaluateArr.get(0).eval);
                history.setResponsibility(evaluateArr.get(2).eval);

                realm.commitTransaction();
                Toast.makeText(getActivity(),"Данные сохранены в истории!",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initElements() {
        nameEvTV = (TextView) rootView.findViewById(R.id.name_from_ev);
        postEvTV = (TextView) rootView.findViewById(R.id.post_from_ev);
        subdivEvTV = (TextView) rootView.findViewById(R.id.subdivision_from_ev);
        showColleagueLL = (LinearLayout) rootView.findViewById(R.id.show_colleague_ll);
        sendEvalButt = (Button) rootView.findViewById(R.id.send_data_button);

        showColleagueLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Fragment fragment = new PersonInfoFragment();
                FragmentManager fm = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.replace(((ViewGroup)getView().getParent()).getId(), fragment);
                transaction.commit();


                Bundle passDataBundle = new Bundle(); //передаем данные в слудующий фрагмент

                passDataBundle.putString("name", getDataBundle.getString("name"));
                passDataBundle.putString("post", getDataBundle.getString("post"));
                passDataBundle.putString("subdiv", getDataBundle.getString("subdiv"));
                passDataBundle.putString("phone", getDataBundle.getString("phone"));
                passDataBundle.putString("email", getDataBundle.getString("email"));
                passDataBundle.putInt("position", getDataBundle.getInt("position"));
                fragment.setArguments(passDataBundle);
            }
        });

        /** получение данных о сотруднике */
        //ловим данные с предыдущей активности
        nameEvTV.setText(getDataBundle.getString("name"));
        postEvTV.setText(getDataBundle.getString("post"));
        subdivEvTV.setText(getDataBundle.getString("subdiv"));

        evalLV = (ListView) rootView.findViewById(R.id.evaluate_list);
        evaluateArr = new ArrayList<>();

        for (int i = 0; i < quality.length; i++) {
            evaluateArr.add(new Evaluate(firstSymbol[i], quality[i], "Качество не проявлено"));
        }

        initAdapter();
        setListViewHeightBasedOnChildren(evalLV);
    }

    private void initAdapter() {
        evaluateAdapter = new EvaluateAdapter(getActivity(), R.layout.evaluate_list_item, evaluateArr);
        evalLV.setAdapter(evaluateAdapter);
        evaluateAdapter.notifyDataSetChanged();
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
}
