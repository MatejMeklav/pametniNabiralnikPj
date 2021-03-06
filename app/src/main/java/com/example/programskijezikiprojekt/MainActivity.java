package com.example.programskijezikiprojekt;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.data.FirebaseDostop;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;


import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class MainActivity extends AppCompatActivity  {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static String ID_BOX ="";
    TextView nameVal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nameVal=(TextView)findViewById(R.id.textViewNameVal);
        String text = "Scan QR code first!";
        nameVal.setText(text);

    }




    public void codeScan(View view) {
        Intent i = new Intent(getBaseContext(), ScannerActivity.class);
        startActivityForResult(i, ScannerActivity.ACTIVITY_ID_SCANNER_ACTIVITY);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ScannerActivity.ACTIVITY_ID_SCANNER_ACTIVITY) {
            if (resultCode == RESULT_OK) {
               // Log.i(TAG, "QR CODE: " + data.getExtras().get(scannerActivity.QR_CODE));
                ID_BOX = data.getExtras().get(ScannerActivity.QR_CODE).toString();
                nameVal.setText(ID_BOX);
            }
        }
    }




    public void openBox(View view) {
        String url ="https://api-test.direct4.me/Sandbox/PublicAccess/V1/api/access/OpenBox?boxID="+ID_BOX+"&tokenFormat=2";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            try {
                System.out.println("response: "+response);
                JSONObject jsonObject = new JSONObject(response);
                String Token = jsonObject.getString("Data");
                System.out.println("data: "+Token);

                jsonToZip(Token);
                extractZip(getFilesDir().toString(),"token.zip");

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> System.out.println("Error: " + error));
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);

        final MediaPlayer mPlayer = MediaPlayer.create(this, Uri.parse(getFilesDir()+"/token.wav") );
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        });
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mPlayer.start();

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        GPSTracking gps = new GPSTracking(getBaseContext());

                        FirebaseDostop obj = new FirebaseDostop(ID_BOX, gps.getLat(), gps.getLng(),LocalDateTime.now());
                        System.out.println("datedate"+LocalDateTime.now().toString());
                            FirebaseDatabase database = FirebaseDatabase.getInstance("https://pametninabiralnikpj-default-rtdb.europe-west1.firebasedatabase.app/");
                            Task<Void> myRef = database.getReference("Dostop").child("dostopi").push().setValue(obj);;
                       //
                        String url = "https://164.8.206.154/index.php";
                        HttpsTrustManager.allowAllSSL();
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                                response -> {
                                    Toast.makeText(MainActivity.this, response.trim(), Toast.LENGTH_SHORT).show();
                                    System.out.println("SUCESSSSSS"+response);


                                }, error -> {
                                    Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                                    System.out.println("DELA error"+error.toString());

                                }){
                            @Override
                            protected Map<String, String> getParams(){
                                Map<String,String> params = new HashMap<String, String>();
                                params.put("data",ID_BOX);
                                return params;
                            }
                        };
                        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
                        stringRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                        requestQueue.add(stringRequest);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        mPlayer.start();
                        System.out.println("DELA NEGATIVE");
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Ste uspe??no odprli nabiralnik?").setPositiveButton("Da", dialogClickListener)
                .setNegativeButton("Ne", dialogClickListener).show();

    }

    private boolean extractZip(String path, String zipPath)
    {
        InputStream is;
        ZipInputStream zis;
        try
        {
            String filename;
            is = new FileInputStream(zipPath);
            zis = new ZipInputStream(new BufferedInputStream(is));
            ZipEntry ze;
            byte[] buffer = new byte[1024];
            int count;

            while ((ze = zis.getNextEntry()) != null)
            {
                filename = ze.getName();
                System.out.println(filename);
                if (ze.isDirectory()) {
                    File fmd = new File(path +"/"+ filename);
                    fmd.mkdirs();
                    continue;
                }

                FileOutputStream fout = new FileOutputStream(path +"/"+ filename);

                while ((count = zis.read(buffer)) != -1)
                {
                    fout.write(buffer, 0, count);
                }

                fout.close();
                zis.closeEntry();
            }

            zis.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private void jsonToZip(String data){

        byte[] outputBytes;
        outputBytes = Base64.decode(data, Base64.DEFAULT);
        try (FileOutputStream fos = new FileOutputStream("token.zip")) {
            fos.write(outputBytes);

        } catch (IOException e) {
            Log.d("Napaka", e.toString());
        }
    }

}