package com.example.mtsihr.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mtsihr.Adapters.HistoryAdapter;
import com.example.mtsihr.Models.Colleague;
import com.example.mtsihr.Models.HistoryModel;
import com.example.mtsihr.R;

import java.util.ArrayList;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends Fragment {

    private Realm realm;
    private View rootView;
    private TextView histColleagueNameTV, histDataTV;
    private ListView historyLV;
    private HistoryAdapter historyAdapter;
    private ArrayList<HistoryModel> historyArray;
    private EditText searachHistoryEdit;
    private RealmResults<HistoryModel> historyRealmResults;
    private FragmentTransaction transaction;
    private Bundle getDataBundle;

    public HistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("История"); //заголовок тулбара
        rootView = inflater.inflate(R.layout.fragment_history, container, false);

        realm = Realm.getDefaultInstance();
        historyArray = new ArrayList<>();

        historyRealmResults = realm.where(HistoryModel.class).findAll(); //считываем все данные что есть в бд
        getDataBundle = getArguments(); //получение данных с предыдущего фрагмента
        initHistoryArray(); //получаем массив с историей

        initElements();
        initAdapter();
        searchFilter();
        showHistoryInfo(); //показываем фрагмент с информацией об оценке
        registerForContextMenu(historyLV); //создаем контекстное меню для списка истории оценок
        return rootView;
    }

    private void initHistoryArray() {
        String email,phone;
        if(getDataBundle!=null){
            email = getDataBundle.getString("email");
            phone = getDataBundle.getString("phone");
            for (HistoryModel hist: historyRealmResults ) {
                if(phone!=null){
                    if (hist.getPhone().equals(phone)){
                        historyArray.add(hist);
                    }
                }else {
                    if (hist.getEmail().equals(email)){
                        historyArray.add(hist);
                    }
                }

            }

        }else {
            historyArray = (ArrayList<HistoryModel>) realm.copyFromRealm(historyRealmResults); //переносим данные в наш лист
        }
    }

    private void showHistoryInfo() {
        historyLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Fragment fragment = new ConcreteHistoryFragment();
                FragmentManager fm = getActivity().getSupportFragmentManager();
                transaction = fm.beginTransaction();
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.replace(((ViewGroup) getView().getParent()).getId(), fragment).addToBackStack(null).commit();

                HistoryModel concreteHistory = (HistoryModel) adapterView.getItemAtPosition(i); //получаем позицию выбранного коллеги
                // в листе с учетом фильтра
                //передаем данные о истории оценки в фрагмент ConcreteHistory
                Bundle bundle = new Bundle();
                bundle.putString("name", concreteHistory.getName());
                bundle.putString("post", concreteHistory.getPost());
                bundle.putString("subdiv", concreteHistory.getSubdiv());
                bundle.putString("efficiency", concreteHistory.getEfficiency());
                bundle.putString("parthnership", concreteHistory.getPartnership());
                bundle.putString("responsibility", concreteHistory.getResponsibility());
                bundle.putString("courage", concreteHistory.getCourage());
                bundle.putString("creativity", concreteHistory.getCreativity());
                bundle.putString("openness", concreteHistory.getOpenness());
                bundle.putString("date", concreteHistory.getDateOfEval());
                bundle.putString("comment", concreteHistory.getComment());
                if (concreteHistory.getPhoto() != null) {
                    bundle.putByteArray("photo", concreteHistory.getPhoto());
                }

                fragment.setArguments(bundle);
            }
        });
    }

    private void initAdapter() {
        historyAdapter = new HistoryAdapter(this.getActivity(), R.layout.history_item, historyArray);
        historyLV.setAdapter(historyAdapter);
        historyAdapter.notifyDataSetChanged();
    }

    private void initElements() {
        historyLV = (ListView) rootView.findViewById(R.id.history_lv);
        histColleagueNameTV = (TextView) rootView.findViewById(R.id.hist_coll_name_tv);
        histDataTV = (TextView) rootView.findViewById(R.id.hist_eval_data_tv);
        searachHistoryEdit = (EditText) rootView.findViewById(R.id.search_history_et);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.menu_context_history, menu);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        String title = (String) ((TextView) info.targetView.findViewById(R.id.hist_coll_name_tv)).getText(); //имя коллеги
        menu.setHeaderTitle(title);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Object origObj = historyAdapter.getItem(info.position); //получаем нажатый обьект в отфильтрованном листе
        int position = historyAdapter.getPosition(origObj); //находим позицию объекта origObj в неотфильтрованном листе
        switch (item.getItemId()) {
            case R.id.delete_menu: //удаляем историю оценки из списка
                realm.beginTransaction();
                historyRealmResults.deleteFromRealm(position); //удаляем историю из бд
                realm.commitTransaction();
                transaction = getFragmentManager().beginTransaction(); //обновляем фрагмент
                transaction.detach(this).attach(this).commit();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public void searchFilter() {
        searachHistoryEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String text = searachHistoryEdit.getText().toString().toLowerCase(Locale.getDefault());
                historyAdapter.filter(text);
                historyAdapter.notifyDataSetChanged();
            }
        });
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
