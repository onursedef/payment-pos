package com.onursedef.paymentpos;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class Failed extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.failed);

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(Failed.this, MainActivity.class);
            startActivity(intent);
        }, 30 * 1000);
    }
}