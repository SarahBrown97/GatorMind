package com.example.onecare.ui.peerSupport;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.onecare.R;
import com.example.onecare.utility.Singleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.example.onecare.utility.Constants.API_GET_PEER_GROUP_DETAILS;
import static com.example.onecare.utility.Constants.KEY_GROUP_ID;
import static com.example.onecare.utility.Constants.PIPE;
import static com.example.onecare.utility.Constants.SERVER_URL;

public class PeerSupportFragment extends Fragment {
    private TextView txtGroup;
    private TextView txtMembers;
    private Button btnMeet;

    private String groupID;
    private List<String> listMembers = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_peer_support, container, false);
        setupViews(root);
        getPeerGroupDetails();
        return root;
    }

    private void setupViews(View view){
        txtGroup = (TextView) view.findViewById(R.id.txtGroup);
        txtMembers = (TextView) view.findViewById(R.id.txtMembers);
        btnMeet = (Button) view.findViewById(R.id.btnMeet);
    }

    private void updatePeerDetails(){
        if (listMembers.size() > 0){
            txtGroup.setText("Group " + groupID);
            StringBuilder sbMembers = new StringBuilder("Peers: ");
            for(int index = 0; index < listMembers.size(); index++){
                if (index > 0){
                    sbMembers.append(", ");
                }
                sbMembers.append(listMembers.get(index));
            }
            txtMembers.setText(sbMembers.toString());
            btnMeet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), ZoomActivity.class);
                    intent.putExtra(KEY_GROUP_ID, groupID);
                    startActivity(intent);
                }
            });
        }
        else
        {
            txtGroup.setText(groupID);
            txtMembers.setText("");
            btnMeet.setVisibility(View.INVISIBLE);
        }

    }

    public void getPeerGroupDetails(){
        RequestQueue queue = Volley.newRequestQueue(this.getContext());
        String getPeerGroupDetails= SERVER_URL + PIPE + API_GET_PEER_GROUP_DETAILS + PIPE + Singleton.getInstance().getUsername();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, getPeerGroupDetails,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println(response);
                        try {
                            groupID = response.getString("peerGroupId");
                            JSONArray members= response.getJSONArray("peerIds");
                            for(int i=0; i<members.length();i++){
                                String peerID= members.getString(i);
                                listMembers.add(peerID);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        };

                        updatePeerDetails();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error.getLocalizedMessage());
            }
        });
        queue.add(jsonObjectRequest);
    }
}