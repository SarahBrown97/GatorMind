package com.example.onecare.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.onecare.R;
import com.example.onecare.reporting.Reporting;

public class MainActivity extends AppCompatActivity {

    private EditText userName;
    private EditText password;
    private TextView info;
    private Button login;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userName = (EditText) findViewById(R.id.userName);
        password = (EditText) findViewById(R.id.password);
        login = (Button) findViewById(R.id.btnLogin);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate(userName.getText().toString(),password.getText().toString());
            }
        });

    }

    private void validate(String userName, String passWord ){
        if((passWord.equals("1234"))){
            Toast.makeText(getApplicationContext(),"Success",Toast.LENGTH_SHORT).show();
           Intent intent= new Intent(MainActivity.this, Reporting.class);
           Singleton.getInstance().setUsername(userName);
            startActivity(intent);
        }
        else{
            Toast.makeText(getApplicationContext(),"Invalid Credentials",Toast.LENGTH_SHORT).show();
        }
    }
}