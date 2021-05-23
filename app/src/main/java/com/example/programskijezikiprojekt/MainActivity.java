package com.example.programskijezikiprojekt;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void CodeScan(View view) {
        Intent i = new Intent(getBaseContext(), scannerActivity.class);
        startActivityForResult(i, scannerActivity.ACTIVITY_ID_SCANNER_ACTIVITY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == scannerActivity.ACTIVITY_ID_SCANNER_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                Log.i(TAG, "QR CODE: " + data.getExtras().get(scannerActivity.QR_CODE));
            }
        }
    }




}