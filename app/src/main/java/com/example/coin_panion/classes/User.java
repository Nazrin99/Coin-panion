package com.example.coin_panion.classes;

import android.widget.Toast;

import java.sql.Blob;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class User{

    /*User info for account*/
    private static Integer account_ID;
    private String mobile_number;
    private String first_name;
    private String last_name;
    private String username;
    private String password;

    /*User additional info*/
    private Blob profile_pic;
    private String user_bio;

    private DebtLimit user_debtLimit;

    private SettleUp user_paymentMethod;

    /*User friend*/
    private List<Friend> friends;

    /*User notification & activity*/
    private List<Notification> notifications;

    /*Payment request*/
    private List<PaymentRequest> paymentRequests;


    /*Create new User object*/
    public User(String mobile_number, String first_name, String last_name, String username, String password) {
        this.mobile_number = mobile_number;
        this.first_name = first_name;
        this.last_name = last_name;
        this.username = username;
        this.password = password;
        /*Fetch all user friend*/
        friends = Friend.fetchFriends(account_ID);
    }

    /*Register new user*/
    /*TODO Insert new user into database based on this.variable*/
    public void registerUser(){

    }

    /*Additional funtion for new user*/
    /*TODO Update user info database based on this.variable*/
    public void updateUser(){

    }

    /*TODO Delete user info database based on this.variable*/
    public void deleteUser(){

    }

    private ResultSet fetchUserData(String phoneNumber){
        Integer userID = null;
        Thread dataThread = new Thread(() -> {
            // TODO QUERY USER INFO
        });
        dataThread.start();
        while(dataThread.isAlive()){

        }
        System.setProperty("userID", userID.toString());
        return null; //TODO CHANGE TO RESULTSET LATER
    }

    public void fetchGeneralNotification(){
        Notification notification = new Notification(account_ID);
        this.notifications = new ArrayList<>(notification.fetchGeneralNotification());
//        String[] notifications = new String[] {
//                "Notification 1",
//                "Notification 2",
//                "Notification 3"
//        };
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.list_item, notifications);
//        ListView listView = findViewById(R.id.list_view);
//        listView.setAdapter(adapter);

    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void fetchGeneralPaymentRequest(){
        PaymentRequest paymentRequest = new PaymentRequest(account_ID);
        this.paymentRequests = new ArrayList<>(paymentRequest.retrieveAllPaymentRequest());
    }

    public List<PaymentRequest> getPaymentRequests() {
        return paymentRequests;
    }

    public void setSettleUp(String accountName, long accountNumber, Blob qrCode){
        user_paymentMethod = new SettleUp(account_ID,accountName,accountNumber,qrCode);
        user_paymentMethod.updateSettleUpInfo();
    }

    public SettleUp getUser_paymentMethod() {
        return user_paymentMethod.fetchSettleUp(account_ID);
    }

}
