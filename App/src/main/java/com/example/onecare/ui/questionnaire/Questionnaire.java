package com.example.onecare.ui.questionnaire;

import androidx.appcompat.app.AppCompatActivity;

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
import com.example.onecare.utility.Singleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static com.example.onecare.utility.Constants.API_GET_QUESTIONNAIRE;
import static com.example.onecare.utility.Constants.API_POST_QUESTIONNAIRE;
import static com.example.onecare.utility.Constants.PIPE;
import static com.example.onecare.utility.Constants.SERVER_URL;

public class Questionnaire extends AppCompatActivity {
    private int questionnaireId;
    private boolean isCompleted;
    private List<QuestionClass> questions= new ArrayList<>();
    private ListView listView;
    private QuestionAdapter questionAdapter;
    private Button buttonSubmit;

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
        setTitle("Questionnaire "+ questionnaireId);
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
    }

    private void refreshListView(){
        questionAdapter.notifyDataSetChanged();
        listView.invalidateViews();
    }

    private void getQuestions(){
        RequestQueue queue = Volley.newRequestQueue(this);
        String getQuestionnaireUrl= SERVER_URL + PIPE + API_GET_QUESTIONNAIRE + PIPE + Singleton.getInstance().getUsername() +"/"+ questionnaireId;
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
            String getQuestionnaireUrl= SERVER_URL + PIPE + API_POST_QUESTIONNAIRE;
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
                           if(response.equalsIgnoreCase("Success")){
                               Toast.makeText(getApplicationContext(),"Success",Toast.LENGTH_SHORT).show();
                               setResult(5);
                               finish();
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