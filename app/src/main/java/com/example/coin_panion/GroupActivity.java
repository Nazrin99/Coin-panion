package com.example.coin_panion;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class GroupActivity extends AppCompatActivity {

    private TextView textView2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newgroup_expenses);

//        TextView textView = (TextView)findViewById(R.id.textView2);
//        textView.setPaintFlags(textView.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);

        textView2 = findViewById(R.id.activityName);

        textView2.setText("GROUP");
    }


}