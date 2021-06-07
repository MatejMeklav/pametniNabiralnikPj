package com.example.programskijezikiprojekt;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
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
import com.example.data.FirebaseDostop;
import com.example.data.FirebaseLogin;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
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
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {


                String username=et_username.getText().toString();
                String password=et_password.getText().toString();
                String url = "https://164.8.206.154/index.php";
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
                                String hashedPass="";
                                try {
                                    hashedPass= toSHA1(password.getBytes());
                                } catch (NoSuchAlgorithmException e) {
                                    e.printStackTrace();
                                }
                                FirebaseLogin obj = null;

                                    obj = new FirebaseLogin(username,hashedPass, LocalDateTime.now());
                                FirebaseDatabase database = FirebaseDatabase.getInstance("https://pametninabiralnikpj-default-rtdb.europe-west1.firebasedatabase.app/");
                                Task<Void> myRef = database.getReference("Login").child("LoginData").push().setValue(obj);;
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String toSHA1(byte[] convertme) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        return Base64.getEncoder().encodeToString(md.digest(convertme));
    }
}