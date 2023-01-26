package com.example.coin_panion.classes.general;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.PerformanceHintManager;

import androidx.annotation.NonNull;

import com.example.coin_panion.classes.utility.Line;
import com.example.coin_panion.classes.utility.ThreadStatic;
import com.example.coin_panion.classes.utility.Validifier;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.sun.mail.imap.protocol.ID;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.zip.DataFormatException;

public class User implements Serializable {
    /*User info for account*/
    private String userID;
    private String phoneNumber;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String password;

    /**
     * Constructor to create complete User object
     * @param userID
     * @param phoneNumber
     * @param firstName
     * @param lastName
     * @param username
     * @param email
     * @param password
     */
    public User(String userID, String phoneNumber, String firstName, String lastName, String username, String email, String password) {
        this.userID = userID;
        this.phoneNumber = phoneNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public User() {
    }

    public User(String userID) {
        this.userID = userID;
    }

    /**
     * Constructor to create User object for database insertion
     * @param phoneNumber
     * @param firstName
     * @param lastName
     * @param username
     * @param email
     * @param password
     */
    public User(String phoneNumber, String firstName, String lastName, String username, String email, String password) {
        this.phoneNumber = phoneNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public static User getUser(DocumentReference userReference){
        AtomicReference<User> userAtomicReference = new AtomicReference<>(null);
        userReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = new User(
                        documentSnapshot.get("userID", String.class),
                        documentSnapshot.get("phoneNumber", String.class),
                        documentSnapshot.get("firstName", String.class),
                        documentSnapshot.get("lastName", String.class),
                        documentSnapshot.get("username", String.class),
                        documentSnapshot.get("email", String.class),
                        documentSnapshot.get("password", String.class)
                );
                userAtomicReference.set(user);
            }
        });
        while(userAtomicReference.get() == null){

        }
        return userAtomicReference.get();
    }

    public void getUser(){
        AtomicReference<User> userAtomicReference = new AtomicReference<>(this);
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        Query query = firebaseFirestore.collection("user").whereEqualTo("userID", this.getUserID());
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots != null){
                    if(!queryDocumentSnapshots.isEmpty()){
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                        userAtomicReference.get().setUsername(documentSnapshot.getString("username"));
                        userAtomicReference.get().setFirstName(documentSnapshot.getString("firstName"));
                        userAtomicReference.get().setLastName(documentSnapshot.getString("lastName"));
                        userAtomicReference.get().setEmail(documentSnapshot.getString("email"));
                        userAtomicReference.get().setPassword(documentSnapshot.getString("password"));
                        userAtomicReference.get().setPhoneNumber(documentSnapshot.getString("phoneNumber"));
                    }
                }
            }
        });
    }

    /**
     * Returns a user object based on their credentials (phone number/ email)
     * @param credentials
     * @return
     */
    public static User getUser(String credentials){
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        Query query = null;
        if(Validifier.isEmail(credentials)){
            query = firebaseFirestore.collection("user").whereEqualTo("email", credentials);
        }
        else{
            query = firebaseFirestore.collection("user").whereEqualTo("phoneNumber", credentials);
        }
        AtomicReference<User> userAtomicReference = new AtomicReference<>(null);
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots != null){
                    if(!queryDocumentSnapshots.isEmpty()){
                        DocumentSnapshot userDocument = queryDocumentSnapshots.getDocuments().get(0);
                        User user = new User(
                                userDocument.get("userID", String.class),
                                userDocument.get("phoneNumber", String.class),
                                userDocument.get("firstName", String.class),
                                userDocument.get("lastName", String.class),
                                userDocument.get("username", String.class),
                                userDocument.get("email", String.class),
                                userDocument.get("password", String.class)
                        );
                        userAtomicReference.set(user);
                    }
                }
                else{
                    System.out.println("User document is empty!");
                }
            }
        });
        while(userAtomicReference.get() == null){

        }
        return userAtomicReference.get();
    }

    public static List<Integer> getListOfIDs(List<Long> phoneNumber, Thread dataThread){
        AtomicReference<List<Integer>> atomicReference = new AtomicReference<>(new ArrayList<>());
        dataThread = new Thread(() -> {
            List<Integer> IDs = new ArrayList<>();
            try(
                    Connection connection = Line.getConnection();
                    PreparedStatement preparedStatement = connection.prepareStatement("SELECT user_id FROM user WHERE " + getQueryString("phone_number", phoneNumber.size()))
                    ){
                for(int i = 0; i < phoneNumber.size() ;i++){
                    preparedStatement.setLong((i+1), phoneNumber.get(i));
                }
                ResultSet resultSet = preparedStatement.executeQuery();
                while(resultSet.next()){
                    IDs.add(resultSet.getInt(1));
                }
                resultSet.close();
                atomicReference.set(IDs);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        ThreadStatic.run(dataThread);
        return atomicReference.get();
    }

    // Getters and setters


    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public static String getQueryString(String columnName, Integer length){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(columnName + "= ? ");
        for(int i = 0 ; i < length-1; i++){
            stringBuilder.append("OR " + columnName + "= ?");
        }
        return stringBuilder.toString();
    }
}
