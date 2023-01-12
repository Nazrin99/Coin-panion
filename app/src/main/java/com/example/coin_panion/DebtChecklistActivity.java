package com.example.coin_panion;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class DebtChecklistActivity extends AppCompatActivity {

    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debt_checklist);

        textView = findViewById(R.id.activityName);
        textView.setText("DEBT CHECKLIST");
    }
}