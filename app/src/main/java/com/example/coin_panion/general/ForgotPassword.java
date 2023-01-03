package com.example.coin_panion.general;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;

import com.example.coin_panion.R;

public class ForgotPassword extends AppCompatActivity {

    EditText ETUserInfo;
    EditText PTFirstDigit, PTSecondDigit, PTThirdDigit, PTFourthDigit;
    EditText ETNewPass, ETConfirmPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);


    }

    public void UIElement(){
        ETUserInfo = findViewById(R.id.ETUserLogin);
        PTFirstDigit = findViewById(R.id.PTFirstDigit);
        PTSecondDigit = findViewById(R.id.PTSecondDigit);
        PTThirdDigit = findViewById(R.id.PTThirdDigit);
        PTFourthDigit = findViewById(R.id.PTFourthDigit);
        ETNewPass = findViewById(R.id.PTNewPass);
        ETConfirmPass = findViewById(R.id.PTConfirmPass);
    }

    public void setupListener(){
        
    }
}