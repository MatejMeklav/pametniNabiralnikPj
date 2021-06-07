package com.example.programskijezikiprojekt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;


import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends AppCompatActivity {
    Button btn_login, btn_capture;
    EditText et_username, et_password;

    TextView tx1;
    int counter = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if(!Python.isStarted()){
            Python.start(new AndroidPlatform(getBaseContext()));
        }

        btn_login = (Button)findViewById(R.id.btn_login);
        et_username = (EditText)findViewById(R.id.et_username);
        et_password = (EditText)findViewById(R.id.et_password);

        btn_capture = (Button)findViewById(R.id.btn_capture);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String username=et_username.getText().toString();
                String password=et_password.getText().toString();
                String url = "https://192.168.1.11/index.php";
                HttpsTrustManager.allowAllSSL();
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        response -> {
                            //Toast.makeText(LoginActivity.this, response.trim(), Toast.LENGTH_SHORT).show();
                            System.out.println("SUCESSSSSS"+response);
                            System.out.println("SUCESSSSSS"+response.contains("Warning"));

                            if(response.contains("Warning")){
                                //error, uporabniško ime ali geslo ne obstaja
                                Toast.makeText(LoginActivity.this, "Username or password is invalid.", Toast.LENGTH_SHORT).show();
                            }else{
                                //ne vrne errorja, poizvedba uspešna
                                Intent i = new Intent(getBaseContext(), MainActivity.class);
                                startActivity(i);

                            }



                        }, error -> {
                    Toast.makeText(LoginActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    System.out.println("DELA error"+error.toString());

                }){
                    @Override
                    protected Map<String, String> getParams(){
                        Map<String,String> params = new HashMap<String, String>();
                        params.put("username_sent",username);
                        params.put("password_sent",password);
                        return params;
                    }
                };
                RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
                stringRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                requestQueue.add(stringRequest);


            }



        });

        btn_capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}