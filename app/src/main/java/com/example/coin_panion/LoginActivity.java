package com.example.coin_panion;

import static com.example.coin_panion.classes.utility.Validifier.isEmail;
import static com.example.coin_panion.classes.utility.Validifier.isPhoneNumber;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.coin_panion.classes.general.Account;
import com.example.coin_panion.classes.general.User;
import com.example.coin_panion.classes.utility.Hashing;
import com.example.coin_panion.classes.utility.Picture;
import com.example.coin_panion.classes.utility.Validifier;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import io.grpc.Context;


public class LoginActivity extends AppCompatActivity {
    TextInputLayout loginLayout, passwordLayout;
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
    }

    public void callUI(){
        loginLayout = findViewById(R.id.loginLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        BtnLogin = findViewById(R.id.loginButton);
        BtnSignUp = findViewById(R.id.signupButton);
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