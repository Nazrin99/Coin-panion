package com.example.coin_panion;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import com.example.coin_panion.classes.utility.BaseViewModel;

public class SignupActivity extends AppCompatActivity {
    BaseViewModel signupViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        signupViewModel = new ViewModelProvider(this).get(BaseViewModel.class);
    }

}