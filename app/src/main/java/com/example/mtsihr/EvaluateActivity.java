package com.example.mtsihr;

import android.content.Intent;
import android.media.Image;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.mtsihr.Adapters.ChooseEvaluateAdapter;
import com.example.mtsihr.Adapters.EvaluateAdapter;
import com.example.mtsihr.Models.ChooseEvaluate;
import com.example.mtsihr.Models.Evaluate;

import java.util.ArrayList;
import java.util.List;

public class EvaluateActivity extends AppCompatActivity {

    ListView evalLV;
    ChooseEvaluateAdapter chooseEvalAdapter;
    ArrayList<ChooseEvaluate> evaluateArr;
    String[] evalArr = {"Качество не проявлено", "Не соответствует ожиданиям", "Соответствует ожиданиям",
            "Превосходит ожидания", "Значительно превосходит ожидания"};
    int pos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluate);

        initElements();
    }

    private void initElements() {
        evalLV = (ListView) findViewById(R.id.concreteEvalList);
        evaluateArr = new ArrayList<>();
        for (int i = 0; i < evalArr.length; i++) {
            evaluateArr.add(new ChooseEvaluate(evalArr[i]));
        }

        initAdapter();


        evalLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent setEvalIntent = new Intent();
                Intent getPosIntent = getIntent();
                pos = getPosIntent.getIntExtra("position",0);
                setEvalIntent.putExtra("eval", evalArr[i]);
                setEvalIntent.putExtra("pos", pos);
                setResult(RESULT_OK, setEvalIntent);
                finish();
            }
        });
    }


    private void initAdapter() {
        chooseEvalAdapter = new ChooseEvaluateAdapter(this, R.layout.choose_eval_list_item, evaluateArr);
        evalLV.setAdapter(chooseEvalAdapter);
        chooseEvalAdapter.notifyDataSetChanged();
    }
}
