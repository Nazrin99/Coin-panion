package com.example.coin_panion;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.widget.EditText;

import com.example.coin_panion.general.signnup.SignupViewModel;

public class SignupActivity extends AppCompatActivity {
    SignupViewModel signupViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        signupViewModel = new ViewModelProvider(this).get(SignupViewModel.class);
    }

}