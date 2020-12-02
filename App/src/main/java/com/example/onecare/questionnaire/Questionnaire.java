package com.example.onecare.questionnaire;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.onecare.R;
import com.example.onecare.login.MainActivity;
import com.example.onecare.login.Singleton;
import com.example.onecare.reporting.Reporting;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class Questionnaire extends AppCompatActivity {
    private int questionnaireId;
    private boolean isCompleted;
    private List<QuestionClass> questions= new ArrayList<>();
    private ListView listView;
    private QuestionAdapter questionAdapter;
    private Button buttonSubmit;
    String url ="http://10.254.0.1:8081";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire);
        questionnaireId= getIntent().getIntExtra("QUESTIONNAIRE_ID",-1);
        isCompleted= getIntent().getBooleanExtra("QUESTIONNAIRE_COMPLETED", false);
        getQuestions();
        setupViews();
    }
    private void setupViews(){
        listView= (ListView) findViewById(R.id.listView);
        questionAdapter= new QuestionAdapter(this,questions,isCompleted);
        listView.setAdapter(questionAdapter);
        buttonSubmit =(Button) findViewById(R.id.buttonSubmit);
        if(isCompleted){
            buttonSubmit.setVisibility(View.GONE);
        }else{
            buttonSubmit.setOnClickListener(this::onSubmitClick);
        }
    }
    private void onSubmitClick(View v){
        for(QuestionClass questionClass: questions) {
            System.out.println(questionClass.answer);
        }
        submitResponse();
        Intent intent= new Intent(this, Reporting.class);
        startActivity(intent);
    }

    private void refreshListView(){
       // questionAdapter.clear();
       // questionAdapter.addAll(filteredQuestionnaires);
        questionAdapter.notifyDataSetChanged();
        listView.invalidateViews();

    }
    private void getQuestions(){
        RequestQueue queue = Volley.newRequestQueue(this);
        String getQuestionnaireUrl= url+"/qol/"+ Singleton.getInstance().getUsername() +"/"+ questionnaireId;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, getQuestionnaireUrl,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        List<QuestionClass> questionList = new ArrayList<>();

                        try {
                            JSONArray questionsArray= response.getJSONArray("questions");
                            for(int i=0; i<questionsArray.length();i++){
                                JSONObject object= questionsArray.getJSONObject(i);
                                QuestionClass.Type type;
                                if(object.getString("type").equalsIgnoreCase("rating")){
                                    type= QuestionClass.Type.RATING;
                                } else if(object.getString("type").equalsIgnoreCase("Yes/No")){
                                    type= QuestionClass.Type.RADIO;
                                } else{
                                    type= QuestionClass.Type.TEXT;
                                }
                                QuestionClass questionClass= new QuestionClass(object.getInt("id"),object.getString("question"),type,object.getString("answer"));
                                questionList.add(questionClass);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //questionList.add(new QuestionClass(7,"What is your name?", QuestionClass.Type.TEXT, "Hello"));
                        questions.addAll(questionList);
                        refreshListView();
                        System.out.println(questionList);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error.getLocalizedMessage());
            }
        });
        queue.add(jsonObjectRequest);
    }

    private void submitResponse(){
        try{
            RequestQueue queue = Volley.newRequestQueue(this);
            String getQuestionnaireUrl= url+"/qol";
            JSONObject jsonBody= new JSONObject();
            jsonBody.put("userID",Singleton.getInstance().getUsername());
            jsonBody.put("questionnaireID",questionnaireId);
            JSONArray jsonArray= new JSONArray();
            for(QuestionClass questionClass: questions){
                if(questionClass.answer != null && !questionClass.answer.isEmpty()) {
                    JSONObject questionObject = new JSONObject();
                    questionObject.put("id",questionClass.id);
                    questionObject.put("answer",questionClass.answer);
                    jsonArray.put(questionObject);
                }
            }
            jsonBody.put("userResponse", jsonArray);
            final String requestBody= jsonBody.toString();
            System.out.println(jsonBody);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, getQuestionnaireUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                           System.out.println(response);
                            //Reporting next = new Reporting();
                           if(response.equalsIgnoreCase("Success")){
                               Toast.makeText(getApplicationContext(),"Success",Toast.LENGTH_SHORT).show();
                               finish();
                               //refreshListView();
                               //next.getQuestionnaires();
                               //refreshListView();
                           }else {
                               Toast.makeText(getApplicationContext(),"Something went wrong",Toast.LENGTH_SHORT).show();
                           }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println(error.getLocalizedMessage());
                }
            }){
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }
                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        uee.printStackTrace();
                        return null;
                    }
                }
            };
            queue.add(stringRequest);

        } catch (Exception e){
            e.printStackTrace();
        }
    }
}