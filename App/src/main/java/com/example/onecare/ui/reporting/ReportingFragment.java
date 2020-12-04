package com.example.onecare.ui.reporting;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.onecare.R;
import com.example.onecare.utility.Singleton;
import com.example.onecare.navigation.NavigationActivity;
import com.example.onecare.ui.questionnaire.Questionnaire;
import com.example.onecare.ui.questionnaire.QuestionnaireClass;
import com.example.onecare.utility.NotificationPublisher;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.onecare.utility.Constants.API_GET_QUESTIONNAIRES;
import static com.example.onecare.utility.Constants.PIPE;
import static com.example.onecare.utility.Constants.SERVER_URL;
import static com.example.onecare.utility.NotificationPublisher.NOTIFICATION_CHANNEL_ID;

public class ReportingFragment extends Fragment {
    private TabLayout tabLayout;
    private ListView viewQuestionnaires;
    private List<QuestionnaireClass> listQuestionnaires= new ArrayList<>();
    private boolean isShowingCompleted = false;
    private List<QuestionnaireClass> filteredQuestionnaires= new ArrayList<>();
    private ArrayAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_reporting, container, false);
        setupViews(root);
        getQuestionnaires();
        return root;
    }

    private void setupViews(View root){
        viewQuestionnaires=(ListView) root.findViewById(R.id.viewQuestionnaires);
        tabLayout = (TabLayout) root.findViewById(R.id.tabLayout);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0){
                    showPendingQuestionnaires();
                }
                else
                {
                    showCompletedQuestionnaires();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        adapter= new ArrayAdapter<QuestionnaireClass>(this.getActivity(), R.layout.questionnaire_row, filteredQuestionnaires);
        viewQuestionnaires.setAdapter(adapter);
        viewQuestionnaires.setOnItemClickListener(this::onQuestionnaireClick);
    }

    private void onQuestionnaireClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent= new Intent(this.getActivity(), Questionnaire.class);
        QuestionnaireClass questionnaireClass= filteredQuestionnaires.get(i);
        intent.putExtra("QUESTIONNAIRE_ID", questionnaireClass.id);
        intent.putExtra("QUESTIONNAIRE_COMPLETED",questionnaireClass.isCompleted);
        startActivityForResult(intent, 5);
    }

    private void showPendingQuestionnaires(){
        Toast.makeText(this.getContext(),"Showing Pending Questionnaires",Toast.LENGTH_SHORT).show();
        isShowingCompleted= false;
        refreshListView();
    }
    private void showCompletedQuestionnaires(){
        Toast.makeText(this.getContext(),"Showing Completed Questionnaires",Toast.LENGTH_SHORT).show();
        isShowingCompleted= true;
        refreshListView();
    }
    private void refreshListView(){
        filterQuestionnaire();
        adapter.clear();
        adapter.addAll(filteredQuestionnaires);
        adapter.notifyDataSetChanged();
        viewQuestionnaires.invalidateViews();
    }

    public void getQuestionnaires(){
        listQuestionnaires.clear();
        RequestQueue queue = Volley.newRequestQueue(this.getContext());
        String getQuestionnaireUrl= SERVER_URL + PIPE + API_GET_QUESTIONNAIRES + PIPE + Singleton.getInstance().getUsername();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, getQuestionnaireUrl,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println(response);
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
                        filterQuestionnaire();
                        if(questionnaireList.size() > 0) {
                            scheduleNotification();
                        }
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

    private void scheduleNotification () {
        for(QuestionnaireClass questionnaireClass: listQuestionnaires){
            if(!questionnaireClass.isCompleted && this.getActivity().getIntent() != null &&
                    !this.getActivity().getIntent().getBooleanExtra("IS_NOTIFICATION_INTENT", false)){
                Notification notification= buildQuestionnaireNotification();
                Intent notificationIntent = new Intent( this.getContext(), NotificationPublisher. class ) ;
                notificationIntent.putExtra(NotificationPublisher. NOTIFICATION_ID , questionnaireClass.id ) ;
                notificationIntent.putExtra(NotificationPublisher. NOTIFICATION , notification) ;
                PendingIntent pendingIntent = PendingIntent. getBroadcast ( this.getContext(), 0 , notificationIntent ,0) ;
                long futureInMillis = System.currentTimeMillis() + 15*1000 ;
                AlarmManager alarmManager = (AlarmManager) this.getActivity().getSystemService(Context. ALARM_SERVICE ) ;
                assert alarmManager != null;
                alarmManager.setAndAllowWhileIdle(AlarmManager. RTC_WAKEUP, futureInMillis , pendingIntent); ;;
                break;
            }
        }
    }
    private Notification buildQuestionnaireNotification () {
        NotificationCompat.Builder builder = new NotificationCompat.Builder( this.getContext(),"GatorMind_101") ;
        builder.setContentTitle( "GatorMind" ) ;
        builder.setContentText("Please complete the pending tasks") ;
        builder.setSmallIcon(R.drawable. icon_small ) ;
        builder.setAutoCancel( true ) ;
        builder.setChannelId( NOTIFICATION_CHANNEL_ID ) ;
        builder.setOngoing(true);
        Intent intent = new Intent(this.getContext(), NavigationActivity.class);
        intent.putExtra("IS_NOTIFICATION_INTENT", true);
        PendingIntent pendingIntent = PendingIntent.getActivity(this.getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT );
        builder.setContentIntent(pendingIntent);
        return builder.build() ;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==5)
        {
            refreshListView();
            getQuestionnaires();
        }
    }
}