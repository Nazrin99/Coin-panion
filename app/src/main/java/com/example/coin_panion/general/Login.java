package com.example.coin_panion.general;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.coin_panion.R;
import com.example.coin_panion.utility.Line;

import org.apache.commons.codec.digest.DigestUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

public class Login extends AppCompatActivity {

    EditText ETUserLogin;
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
        ETUserLogin = findViewById(R.id.ETUserLogin);
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

    public void validateLogin() {
        String gettingColumn = "";

        if(ETUserPass.getText().toString().isEmpty()){
            ETUserPass.setError("Please enter a password");
            return;
        }
        if(ETUserLogin.getText().toString().isEmpty()){
            ETUserLogin.setError("Please enter your credentials");
            return;
        }
        if(isEmail(ETUserLogin)){
            gettingColumn = "email";
        }
        else if(isPhoneNumber(ETUserLogin)){
            gettingColumn = "phone_number";
        }
        else{
            gettingColumn = "username";
        }

        String userLogin = ETUserLogin.getText().toString();
        String userPass = ETUserPass.getText().toString();
        String column = gettingColumn;

        while(dataThread.isAlive()){

        }
        dataThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Connection connection = Line.getConnection();
                    PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM account WHERE ?=?");
                    preparedStatement.setString(1, column);
                    preparedStatement.setString(2, userLogin);
                    ResultSet resultSet = preparedStatement.executeQuery();

                    if(resultSet.next()){
                        // Credentials exists, verifying password
                        if(new DigestUtils("SHA3-256").digestAsHex(resultSet.getString("password")).equalsIgnoreCase(userPass)){
                            // TODO Password valid, go to Home Activity
                        }
                        else{
                            // Password is wrong
                            Toast.makeText(getApplicationContext(), "Invalid login credentials", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        // Credentials doesn't exist
                        Toast.makeText(getApplicationContext(), "Login credentials not found", Toast.LENGTH_SHORT).show();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
        dataThread.start();

    }
    // Check if the editText is empty
    boolean isEmpty(EditText text){
        CharSequence string = text.getText().toString();
        return TextUtils.isEmpty(string);
    }

    // Check if it was a valid email
    boolean isEmail(EditText text){
        CharSequence email = text.getText().toString();
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }

    // Check if it was a valid phone Number
    boolean isPhoneNumber(EditText text) {
        String regexPhone = "[0][1][0-9]{8}[0-9]?";

        return Pattern.compile(regexPhone).matcher(text.getText().toString()).matches();

    }



}