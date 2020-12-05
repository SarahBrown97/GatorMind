package com.example.onecare.ui.peerSupport;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.onecare.R;
import com.example.onecare.ui.questionnaire.QuestionClass;
import com.example.onecare.utility.Singleton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import us.zoom.sdk.JoinMeetingOptions;
import us.zoom.sdk.JoinMeetingParams;
import us.zoom.sdk.MeetingService;
import us.zoom.sdk.StartMeetingOptions;
import us.zoom.sdk.ZoomApiError;
import us.zoom.sdk.ZoomAuthenticationError;
import us.zoom.sdk.ZoomSDK;
import us.zoom.sdk.ZoomSDKAuthenticationListener;
import us.zoom.sdk.ZoomSDKInitParams;
import us.zoom.sdk.ZoomSDKInitializeListener;

import static com.example.onecare.utility.Constants.API_POST_QUESTIONNAIRE;
import static com.example.onecare.utility.Constants.KEY_GROUP_ID;
import static com.example.onecare.utility.Constants.PIPE;
import static com.example.onecare.utility.Constants.SERVER_URL;
import static  com.example.onecare.utility.Constants.API_PEER_NOTES;

public class ZoomActivity extends AppCompatActivity {
    private EditText editTextNotes;
    private int groupID;

    private ZoomSDKAuthenticationListener authListener = new ZoomSDKAuthenticationListener() {
        /**
         * This callback is invoked when a result from the SDK's request to the auth server is
         * received.
         */
        @Override
        public void onZoomSDKLoginResult(long result) {
            if (result == ZoomAuthenticationError.ZOOM_AUTH_ERROR_SUCCESS) {
                // Once we verify that the request was successful, we may start the meeting
                startMeeting(ZoomActivity.this);
            }
        }

        @Override
        public void onZoomSDKLogoutResult(long l) {}@Override
        public void onZoomIdentityExpired() {}@Override
        public void onZoomAuthIdentityExpired() {}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.onecare.R.layout.zoom_front);

        initializeSdk(this);
       String groupIDString = getIntent().getStringExtra(KEY_GROUP_ID);
       groupID = Integer.parseInt(groupIDString);

        initViews();
    }

    /**
     * Initialize the SDK with your credentials. This is required before accessing any of the
     * SDK's meeting-related functionality.
     */
    public void initializeSdk(Context context) {
        ZoomSDK sdk = ZoomSDK.getInstance();
        // TODO: Do not use hard-coded values for your key/secret in your app in production!
        ZoomSDKInitParams params = new ZoomSDKInitParams();
        params.appKey = "wu0hl6U4wkvjis0kqG8j2a0K9v2HubaFEh2M"; // TODO: Retrieve your SDK key and enter it here
        params.appSecret = "P1jMaERpAT2W5z0I1vG4ozFt9E2BWDeCxpLZ"; // TODO: Retrieve your SDK secret and enter it here
        params.domain = "zoom.us";
        params.enableLog = true;
        // TODO: Add functionality to this listener (e.g. logs for debugging)
        ZoomSDKInitializeListener listener = new ZoomSDKInitializeListener() {
            /**
             * @param errorCode {@link us.zoom.sdk.ZoomError#ZOOM_ERROR_SUCCESS} if the SDK has been initialized successfully.
             */
            @Override
            public void onZoomSDKInitializeResult(int errorCode, int internalErrorCode) {}

            @Override
            public void onZoomAuthIdentityExpired() {}
        };
        sdk.initialize(context, listener, params);
    }

    private void initViews() {
        findViewById(R.id.join_button).setOnClickListener(new View.OnClickListener() {@Override
        public void onClick(View view) {
            createJoinMeetingDialog();
        }
        });

        findViewById(R.id.login_button).setOnClickListener(new View.OnClickListener() {@Override
        public void onClick(View view) {
            if (ZoomSDK.getInstance().isLoggedIn()) {
                startMeeting(ZoomActivity.this);
            } else {
                createLoginDialog();
            }
        }
        });
        findViewById(R.id.btnSubmit).setOnClickListener(this::submitNotes);
    }

    /**
     * Join a meeting without any login/authentication with the meeting's number & password
     */
    public void joinMeeting(Context context, String meetingNumber, String password) {
        MeetingService meetingService = ZoomSDK.getInstance().getMeetingService();
        JoinMeetingOptions options = new JoinMeetingOptions();
        JoinMeetingParams params = new JoinMeetingParams();
        params.displayName = Singleton.getInstance().getUsername(); // TODO: Enter your name
        params.meetingNo = meetingNumber;
        params.password = password;
        meetingService.joinMeetingWithParams(context, params, options);
    }

    /**
     * Log into a Zoom account through the SDK using your email and password. For more information,
     * see {@link ZoomSDKAuthenticationListener#onZoomSDKLoginResult} in the {@link #authListener}.
     */
    public void login(String username, String password) {
        int result = ZoomSDK.getInstance().loginWithZoom(username, password);
        if (result == ZoomApiError.ZOOM_API_ERROR_SUCCESS) {
            // Request executed, listen for result to start meeting
            ZoomSDK.getInstance().addAuthenticationListener(authListener);
        }
    }

    /**
     * Start an instant meeting as a logged-in user. An instant meeting has a meeting number and
     * password generated when it is created.
     */
    public void startMeeting(Context context) {
        ZoomSDK sdk = ZoomSDK.getInstance();
        if (sdk.isLoggedIn()) {
            MeetingService meetingService = sdk.getMeetingService();
            StartMeetingOptions options = new StartMeetingOptions();
            meetingService.startInstantMeeting(context, options);
        }
    }

    /**
     * Prompt the user to input the meeting number and password and uses the Zoom SDK to join the
     * meeting.
     */
    private void createJoinMeetingDialog() {
        new AlertDialog.Builder(this).setView(com.example.onecare.R.layout.zoom_join).setPositiveButton("Join", new DialogInterface.OnClickListener() {@Override
        public void onClick(DialogInterface dialogInterface, int i) {
            AlertDialog dialog = (AlertDialog) dialogInterface;
            TextInputEditText numberInput = dialog.findViewById(com.example.onecare.R.id.meeting_no_input);
            TextInputEditText passwordInput = dialog.findViewById(com.example.onecare.R.id.password_input);
            if (numberInput != null && numberInput.getText() != null && passwordInput != null && passwordInput.getText() != null) {
                String meetingNumber = numberInput.getText().toString();
                String password = passwordInput.getText().toString();
                if (meetingNumber.trim().length() > 0 && password.trim().length() > 0) {
                    joinMeeting(ZoomActivity.this, meetingNumber, password);
                }
            }
            dialog.dismiss();
        }
        }).show();
    }

    /**
     * Prompts the user to input their account email and password and uses the Zoom SDK to login.
     * See {@link ZoomSDKAuthenticationListener#onZoomSDKLoginResult} in the {@link #authListener} for more information.
     */
    private void createLoginDialog() {
        new AlertDialog.Builder(this).setView(com.example.onecare.R.layout.zoom_login).setPositiveButton("Log in", new DialogInterface.OnClickListener() {@Override
        public void onClick(DialogInterface dialogInterface, int i) {
            AlertDialog dialog = (AlertDialog) dialogInterface;
            TextInputEditText emailInput = dialog.findViewById(com.example.onecare.R.id.email_input);
            TextInputEditText passwordInput = dialog.findViewById(com.example.onecare.R.id.pw_input);
            if (emailInput != null && emailInput.getText() != null && passwordInput != null && passwordInput.getText() != null) {
                String email = emailInput.getText().toString();
                String password = passwordInput.getText().toString();
                if (email.trim().length() > 0 && password.trim().length() > 0) {
                    login(email, password);
                }
            }
            dialog.dismiss();
        }
        }).show();
    }

    private void submitNotes(View view){
        try{
            RequestQueue queue = Volley.newRequestQueue(this);
            editTextNotes = (EditText) findViewById(R.id.editTextNotes);
            String getQuestionnaireUrl= SERVER_URL + PIPE + API_PEER_NOTES;
            JSONObject jsonBody= new JSONObject();
            jsonBody.put("peerGroupId", groupID);
            jsonBody.put("peerId", Singleton.getInstance().getUsername());
            jsonBody.put("notes", editTextNotes.getText());
            final String requestBody= jsonBody.toString();
            System.out.println(jsonBody);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, getQuestionnaireUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            System.out.println(response);
                            if(response.equalsIgnoreCase("Success")){
                                Toast.makeText(getApplicationContext(),"Success",Toast.LENGTH_SHORT).show();
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