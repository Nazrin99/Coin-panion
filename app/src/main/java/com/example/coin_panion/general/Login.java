package com.example.coin_panion.general;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.coin_panion.R;
import com.example.coin_panion.utility.Validifier;

import java.util.regex.Pattern;

public class Login extends AppCompatActivity {

    EditText ETUserInfo;
    EditText ETUserPass;
    Button BtnLogin;
    Button BtnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        callUI();
        setupUIListeners();
    }

    public void callUI(){
        ETUserInfo = findViewById(R.id.ETUserInfo);
        ETUserPass = findViewById(R.id.ETUserPass);
        BtnLogin = findViewById(R.id.BtnLogin);
        BtnSignUp = findViewById(R.id.BtnSignUp);
    }

    public void setupUIListeners(){
        BtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateLogin();
                Log.i("Info","Button Clicked");
            }
        });

        BtnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                TO:DO add navigation to the sign up interface
//                Intent openSignUpInterface = new Intent(Login.this,SignUp.Class);
//                startActivity(openSignUpInterface);
            }
        });
    }

    public void validateLogin(){
        boolean validCredentials = true;

        if(ETUserInfo.getText().toString().equals(" ")){
            ETUserInfo.requestFocus();
            ETUserInfo.setError("You must enter your credentials to login!");
            validCredentials = false;
        }else{
            if(!Validifier.isEmail(ETUserInfo.getText().toString())){
                ETUserInfo.setError("Enter valid email!");
                validCredentials = false;
            }
        }

        if(isEmpty(ETUserPass)){
            ETUserPass.setError("You must enter your password to login!");
            validCredentials = false;
        }else{
            if(ETUserPass.getText().toString().length() < 5){
                ETUserPass.setError("password must be more than 5 character long!");
                validCredentials = false;
            }
        }

        //If the credentials entered by User is Valid
        // TO:DO need to validate with backend database
        if(validCredentials){

            String userInfo = ETUserInfo.getText().toString();
            String userPass = ETUserPass.getText().toString();
            // Code to validate the user Credentials with the database
            if(userInfo.equals("UserInfo") && userPass.equals("UserPass"));
            // After user is validated log to new interface and close this existing interface
//            Intent loginValid = new Intent(Login.this,FirstActivity.class);
//            startActivity(loginValid);
            this.finish();
            } else {
            Toast notAccurateCredentials = Toast.makeText(this,"Wrong information or password", Toast.LENGTH_SHORT);
            notAccurateCredentials.show();
        }
    }

    // Check if the editText is empty
    boolean isEmpty(EditText text){
        CharSequence string = text.getText().toString();
        return TextUtils.isEmpty(string);
    }

}