package com.example.onecare.reporting;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.onecare.R;
import com.example.onecare.login.Singleton;
import com.example.onecare.questionnaire.QuestionClass;
import com.example.onecare.questionnaire.Questionnaire;
import com.example.onecare.questionnaire.QuestionnaireClass;
import com.example.onecare.webservice.WebServiceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Reporting extends AppCompatActivity {
    private Button pending;
    private Button completed;
    private ListView viewQuestionnaires;
    private List<QuestionnaireClass> listQuestionnaires= new ArrayList<>();
    private boolean isShowingCompleted;
    private List<QuestionnaireClass> filteredQuestionnaires= new ArrayList<>();
    private ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reporting);
        getQuestionnaires();
        filterQuestionnaire();
        setupViews();
        updateButtonState();
        System.out.println(Singleton.getInstance().getUsername());
    }

    private void setupViews(){
        pending= (Button) findViewById(R.id.btnPending);
        completed=(Button) findViewById(R.id.btnCompleted);
        viewQuestionnaires=(ListView) findViewById(R.id.viewQuestionnaires);
        pending.setOnClickListener(this::onPendingClick);
        completed.setOnClickListener(this::onCompletedClick);

        adapter= new ArrayAdapter<QuestionnaireClass>(this,R.layout.questionnaire_row,filteredQuestionnaires);
        viewQuestionnaires.setAdapter(adapter);
        viewQuestionnaires.setOnItemClickListener(this::onQuestionnaireClick);
    }

    private void onQuestionnaireClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent= new Intent(Reporting.this, Questionnaire.class);
        QuestionnaireClass questionnaireClass= filteredQuestionnaires.get(i);
        intent.putExtra("QUESTIONNAIRE_ID", questionnaireClass.id);
        intent.putExtra("QUESTIONNAIRE_COMPLETED",questionnaireClass.isCompleted);
        startActivity(intent);
    }

    private void onPendingClick(View v){
        Toast.makeText(getApplicationContext(),"Showing Pending Questionnaires",Toast.LENGTH_SHORT).show();
        isShowingCompleted= false;
        refreshListView();
        updateButtonState();
    }
    private void onCompletedClick(View v){
        Toast.makeText(getApplicationContext(),"Showing Completed Questionnaires",Toast.LENGTH_SHORT).show();
        isShowingCompleted= true;
        refreshListView();
        updateButtonState();

    }
    private void refreshListView(){
        filterQuestionnaire();
        adapter.clear();
        adapter.addAll(filteredQuestionnaires);
        adapter.notifyDataSetChanged();
        viewQuestionnaires.invalidateViews();

    }

    private void getQuestionnaires(){
        String url ="http://10.3.4.252:8081";
        RequestQueue queue = Volley.newRequestQueue(this);
        String getQuestionnaireUrl= url+"/qolList/"+ Singleton.getInstance().getUsername();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, getQuestionnaireUrl,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        List<QuestionnaireClass> questionnaireList = new ArrayList<>();

                        try {
                            JSONArray questionnaires= response.getJSONArray("questions");
                            for(int i=0; i<questionnaires.length();i++){
                                JSONObject object= questionnaires.getJSONObject(i);
                                QuestionnaireClass questionnaireClass= new QuestionnaireClass(object.getInt("id"),
                                        object.getString("status").equals("Completed"), null);
                                questionnaireList.add(questionnaireClass);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        listQuestionnaires.addAll(questionnaireList);
                        refreshListView();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error.getLocalizedMessage());
            }
        });
        queue.add(jsonObjectRequest);

    }

    private void filterQuestionnaire(){
        if(listQuestionnaires!= null){
            filteredQuestionnaires = listQuestionnaires.stream()
                    .filter(questionnaire -> questionnaire.isCompleted== this.isShowingCompleted).collect(Collectors.toList());
        }

    }
    private void updateButtonState(){
        if(isShowingCompleted){
            pending.setBackgroundColor(Color.GRAY);
            completed.setBackgroundColor(Color.parseColor("#2979FF"));
            completed.setEnabled(false);
            pending.setEnabled(true);
        }else{
            pending.setBackgroundColor(Color.parseColor("#2979FF"));
            completed.setBackgroundColor(Color.GRAY);
            completed.setEnabled(true);
            pending.setEnabled(false);

        }
    }
}