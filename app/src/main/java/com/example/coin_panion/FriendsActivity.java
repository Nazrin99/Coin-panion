package com.example.coin_panion;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class FriendsActivity extends AppCompatActivity {
    Toolbar toolbar;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        checkPermission();

        toolbar = findViewById(R.id.friendActivityAppBar);
        textView = findViewById(R.id.activity_name);
        textView.setText("Friends");

    }

    public void checkPermission() {
        // Check Condition
        if (ContextCompat.checkSelfPermission(FriendsActivity.this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            // When permission is not granted

            // Request permission from user
            ActivityCompat.requestPermissions(FriendsActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, 100);
        } else {

        }
    }
}

