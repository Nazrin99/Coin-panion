package com.example.coin_panion;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
        if(ETUserInfo.getText().toString().isEmpty()){
            ETUserInfo.setError("Please enter your credentials");
            return;
        }
        if(isEmail(ETUserInfo.getText().toString())){
            gettingColumn = "email";
        }
        else if(isPhoneNumber(ETUserInfo.getText().toString())){
            gettingColumn = "phone_number";
        }
        else{
            return;
        }

        String userLogin = ETUserInfo.getText().toString();
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
                            // TODO Password valid, construct User and pass to Home Activity
                            Integer accountID = resultSet.getInt(1);
                            Integer phoneNumber = resultSet.getInt(2);
                            String email = resultSet.getString(3);
                            String firstName = resultSet.getString(4);
                            String lastName = resultSet.getString(5);
                            String username = resultSet.getString(6);
                            String password = resultSet.getString(7);
                            String bio = resultSet.getString(8);
                            Integer debtLimitDate = resultSet.getInt(9);
                            Integer debtLimitAmount = resultSet.getInt(10);
                            Integer pictureID = resultSet.getInt(11);
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



}