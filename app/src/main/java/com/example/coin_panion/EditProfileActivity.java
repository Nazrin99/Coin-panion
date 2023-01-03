package com.example.coin_panion;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Paint;
import android.os.Bundle;
import android.widget.TextView;

public class EditProfileActivity extends AppCompatActivity {

    private TextView textView, textView1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        textView = findViewById(R.id.activity_name);
        textView.setText("EDIT PROFILE");

        textView1 = findViewById(R.id.textView14);
        textView1.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
    }
}