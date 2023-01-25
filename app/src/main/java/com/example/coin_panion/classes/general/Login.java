package com.example.coin_panion.classes.general;

import androidx.annotation.NonNull;

import com.example.coin_panion.classes.utility.Validifier;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.concurrent.atomic.AtomicReference;

public class Login {
    public static boolean loginValid(String credentials, String password){
        AtomicReference<Boolean> atomicReference = new AtomicReference<>(false);
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        CollectionReference users = firebaseFirestore.collection("users");

        Query query;
        if(Validifier.isEmail(credentials)){
            query = users.whereEqualTo("email", credentials);
        }
        else{
            query = users.whereEqualTo("phoneNumber", credentials);
        }
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                assert queryDocumentSnapshots != null;
                if(queryDocumentSnapshots.isEmpty()){
                    System.out.println("Email / phone number does not exists");
                }
                else if(queryDocumentSnapshots.getDocuments().size() == 1){
                    // Email / phone number exists, check password
                    DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                    if(documentSnapshot.get("password", String.class).equals(password)){
                        System.out.println("Password correct");
                        atomicReference.set(true);
                    }
                    else{
                        System.out.println("Incorrect password");
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        return false;
    }
}
