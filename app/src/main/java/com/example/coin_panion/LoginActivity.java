package com.example.coin_panion;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.coin_panion.classes.general.User;
import com.example.coin_panion.classes.utility.Line;
import static com.example.coin_panion.classes.utility.Validifier.isEmail;
import static com.example.coin_panion.classes.utility.Validifier.isPhoneNumber;

//import org.apache.commons.codec.digest.DigestUtils;

import org.apache.commons.codec.digest.DigestUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginActivity extends AppCompatActivity {

    EditText ETUserInfo;
    EditText ETUserPass;
    Button BtnLogin;
    Button BtnSignUp;
    Thread dataThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        callUI();
        setupUIListeners();

        dataThread = new Thread(() -> {});
    }

    public void
    callUI(){
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
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(intent);
            }
        });
    }

    public void validateLogin() {
        String gettingColumn = "";

        String userLogin = ETUserInfo.getText().toString();
        String userPass = ETUserPass.getText().toString();

        if(userLogin.isEmpty()){
            ETUserInfo.setError("Please enter your credentials");
            return;
        }
        if(userPass.isEmpty()){
            ETUserPass.setError("Please enter your password");
            return;
        }
        if(isEmail(userLogin)){
            gettingColumn = "email";
        }
        else if(isPhoneNumber(userLogin)){
            gettingColumn = "phone_number";
        }
        else{
            return;
        }
        String column = gettingColumn;
        while(dataThread.isAlive()){

        }
        dataThread = new Thread(new Runnable() {
            @Override
            public void run() {
                // Getting potential User from the database
                User user = new User( column.equalsIgnoreCase("email") ? userLogin : new Integer(userLogin), new Thread());

                if(user.getUserID() > 0){
                    // TODO User exists, User object is valid, proceed to validify account
                }
            }
        });
        dataThread.start();

    }



}