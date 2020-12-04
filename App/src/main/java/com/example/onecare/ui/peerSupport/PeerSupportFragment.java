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

import com.example.onecare.R;
import com.example.onecare.ui.InitAuthSDKActivity;

public class PeerSupportFragment extends Fragment {
    Button b;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        final TextView textView = root.findViewById(R.id.text_gallery);
        Intent intent = new Intent(getActivity(), InitAuthSDKActivity.class);
        b = (Button) root.findViewById(R.id.button4);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent);
            }
        });
        textView.setText("Peer Support");
        return root;
    }
}