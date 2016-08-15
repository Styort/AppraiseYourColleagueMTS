package com.example.mtsihr;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mtsihr.Adapters.ColleagueAdapter;
import com.example.mtsihr.Adapters.EvaluateAdapter;
import com.example.mtsihr.Models.Colleague;
import com.example.mtsihr.Models.Evaluate;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class JustActivity extends AppCompatActivity {

    private ListView evalLV;
    private EvaluateAdapter evaluateAdapter;
    private ArrayList<Evaluate> evaluateArr;
    private String[] firstSymbol = {"П", "Р", "О", "С", "Т", "О"};
    private String[] quality = {"артнерство", "езультативность", "тветственность", "мелость", "ворчество", "ткрытость"};
    private TextView nameEvTV, postEvTV, subdivEvTV;
    private LinearLayout showColleagueLL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_just);
        getSupportActionBar().setTitle("ПРОСТО"); //заголовок тулбара
        initElements();
        evaluateClick();
    }

    private void evaluateClick() {
        evalLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent getEvelIntent = new Intent(getApplicationContext(), EvaluateActivity.class);
                getEvelIntent.putExtra("evalName",firstSymbol[i]+quality[i]);
                getEvelIntent.putExtra("position", i);
                getEvelIntent.putExtra("eval", evaluateArr.get(i).eval);
                startActivityForResult(getEvelIntent, 1);
            }
        });
    }

    private void initElements() {
        nameEvTV = (TextView)findViewById(R.id.name_from_ev);
        postEvTV = (TextView)findViewById(R.id.post_from_ev);
        subdivEvTV = (TextView)findViewById(R.id.subdivision_from_ev);
        showColleagueLL = (LinearLayout)findViewById(R.id.show_colleague_ll);

        showColleagueLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),PersonInfoActivity.class);
                Intent getDataIntent = getIntent();

                String email = getDataIntent.getStringExtra("email");
                intent.putExtra("name", getDataIntent.getStringExtra("name"));
                intent.putExtra("post", getDataIntent.getStringExtra("post"));
                intent.putExtra("subdiv", getDataIntent.getStringExtra("subdiv"));
                intent.putExtra("phone", getDataIntent.getStringExtra("phone"));
                intent.putExtra("email", getDataIntent.getStringExtra("email"));
                intent.putExtra("position", getDataIntent.getIntExtra("position",0));

                startActivity(intent);
            }
        });

        /** получение данных о сотруднике */
        Intent intent = getIntent();

        //ловим данные с предыдущей активности
        nameEvTV.setText(intent.getStringExtra("name"));
        postEvTV.setText(intent.getStringExtra("post"));
        subdivEvTV.setText(intent.getStringExtra("subdiv"));

        evalLV = (ListView) findViewById(R.id.evaluate_list);
        evaluateArr = new ArrayList<>();

        for (int i = 0; i < quality.length; i++) {
            evaluateArr.add(new Evaluate(firstSymbol[i], quality[i], "Качество не проявлено"));
        }

        initAdapter();
        setListViewHeightBasedOnChildren(evalLV);
    }

    private void initAdapter() {
        evaluateAdapter = new EvaluateAdapter(this, R.layout.evaluate_list_item, evaluateArr);
        evalLV.setAdapter(evaluateAdapter);
        evaluateAdapter.notifyDataSetChanged();
    }

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }
        //получаем оценку
        String name = data.getStringExtra("eval");

        int pos = data.getIntExtra("pos", 0);
        evaluateArr.set(pos, new Evaluate(firstSymbol[pos], quality[pos], name));
        evaluateAdapter.notifyDataSetChanged();
    }
}
