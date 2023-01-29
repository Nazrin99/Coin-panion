package com.example.coin_panion;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;

import com.example.coin_panion.classes.general.Account;
import com.example.coin_panion.classes.utility.Hashing;
import com.example.coin_panion.classes.utility.Validifier;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;


public class LoginActivity extends AppCompatActivity {
    TextInputLayout loginLayout, passwordLayout;
    TextView loginForgotPasswordTextView;
    Button BtnLogin;
    Button BtnSignUp;
    Bundle bundle = new Bundle();
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        callUI();
        setupUIListeners();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        File file = new File(getApplicationContext().getFilesDir(), "login.txt");
        if(file.exists()){
            try{
                FileInputStream fileInputStream = new FileInputStream(file);
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String[] credentials = new String[2];
                String singleLine;
                int count = 0;

                while((singleLine = bufferedReader.readLine()) != null){
                    credentials[count++] = singleLine;
                }
                if(credentials[0].contains("LOGIN") && credentials[1].contains("PASSWORD")){
                    String userLogin = credentials[0].substring(6);
                    System.out.println(userLogin);
                    String userPass = credentials[1].substring(9);
                    System.out.println(userPass);

                    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                    Query query = null;
                    if(Validifier.isPhoneNumber(userLogin)){
                        query = firebaseFirestore.collection("user")
                                .whereEqualTo("phoneNumber", userLogin)
                                .whereEqualTo("password", Hashing.keccakHash(userPass));
                    }
                    else{
                        query = firebaseFirestore.collection("user")
                                .whereEqualTo("email", userLogin)
                                .whereEqualTo("password", Hashing.keccakHash(userPass));
                    }
                    query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if(queryDocumentSnapshots != null){
                                if(!queryDocumentSnapshots.isEmpty()){
                                    String userID = queryDocumentSnapshots.getDocuments().get(0).getString("userID");

                                    Executors.newSingleThreadExecutor().execute(() -> {
                                        switchToMainActivity(userID);
                                    });
                                }
                                else{
                                    System.out.println("No such account");
                                }
                            }
                            else{
                                System.out.println("Login snapshot is null");
                            }
                        }
                    });
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else{
            System.out.println("Files does not exists");
        }
    }

    public void callUI(){
        loginLayout = findViewById(R.id.loginLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        BtnLogin = findViewById(R.id.loginButton);
        BtnSignUp = findViewById(R.id.signupButton);
        loginForgotPasswordTextView = findViewById(R.id.loginForgotPasswordTextView);
    }

    public void setupUIListeners(){
        BtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateLogin();
            }
        });

        BtnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(intent);
            }
        });

        loginLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() <= 0){
                    runOnUiThread(() -> loginLayout.setError("Credential cannot be empty!"));
                }
                else{
                    runOnUiThread(() -> loginLayout.setError(null));
                }
            }
        });

        passwordLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(Validifier.isPhoneNumber(s.toString())){
                    runOnUiThread(() -> loginLayout.setError(null));
                }
                if(s.length() <= 0){
                    runOnUiThread(() -> passwordLayout.setError("Password field cannot be empty!"));
                }
                else{
                    runOnUiThread(() -> passwordLayout.setError(null));
                }
            }
        });

        loginForgotPasswordTextView.setOnClickListener(v -> {
            Intent intent = new Intent(this, ForgotPasswordActivity.class);
            startActivity(intent);
        });
    }

    public void validateLogin() {
        System.out.println("Entered validate login block");

        String userLogin = loginLayout.getEditText().getText().toString();
        String userPass = passwordLayout.getEditText().getText().toString();

        if(userLogin.isEmpty()){
            runOnUiThread(() -> loginLayout.setError("Credential cannot be empty!"));
            return;
        }
        if(userPass.isEmpty()){
            runOnUiThread(() -> passwordLayout.setError("Password field cannot be empty!"));
            return;
        }

        queryLoginInfo(userLogin, userPass);

    }

    private void queryLoginInfo(String userLogin, String userPass){
        Executors.newSingleThreadExecutor().execute(() -> {
            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
            CollectionReference user = firebaseFirestore.collection("user");

            Query query;
            AtomicReference<Account> atomicReference = new AtomicReference<>(null);
            if(Validifier.isEmail(userLogin)){
                query = user.whereEqualTo("email", userLogin);
            }
            else{
                query = user.whereEqualTo("phoneNumber", userLogin);
            }
            query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    System.out.println("Query successful");
                    assert queryDocumentSnapshots != null;
                    if(queryDocumentSnapshots.isEmpty()){
                        System.out.println("Email / phone number does not exists");
                    }
                    else if(queryDocumentSnapshots.getDocuments().size() == 1){
                        // Email / phone number exists, check password
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                        if(documentSnapshot.get("password", String.class).equals(Hashing.keccakHash(userPass))){
                            // All credentials are correct, send userID to MainActivity.class
                            String userID = documentSnapshot.getString("userID");

                            try{
                                File file = new File(getApplicationContext().getFilesDir(), "login.txt");
                                FileOutputStream fileOutputStream = new FileOutputStream(file);
                                fileOutputStream.write(("LOGIN:" + userLogin).getBytes(StandardCharsets.UTF_8));
                                fileOutputStream.write("\n".getBytes(StandardCharsets.UTF_8));
                                fileOutputStream.write(("PASSWORD:" + userPass).getBytes(StandardCharsets.UTF_8));
                                fileOutputStream.flush();
                                fileOutputStream.close();
                                System.out.println("login file created");
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }

                            switchToMainActivity(userID);
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "Credentials incorrect", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    System.out.println("Query unsuccessful");
                }
            });

        });
    }

    private void switchToMainActivity(String userID){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("userID", userID);
        startActivity(intent);
    }


}