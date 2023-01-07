package com.example.coin_panion.classes.general;

import android.app.Notification;
import com.example.coin_panion.classes.transaction.PaymentApproval;

import java.util.ArrayList;

public class History {

    private String HistoryID;
    private Integer userID;
    ArrayList<Notification> notificationList = new ArrayList<>();
    ArrayList<PaymentApproval> paymentApprovalList = new ArrayList<>();

    public History(ArrayList<Notification> notificationList, ArrayList<PaymentApproval> paymentApprovalList) {
        this.notificationList = notificationList;
        this.paymentApprovalList = paymentApprovalList;
//        this.userID = User.userID;
    }

    //TODO call Notification from database based on receiverID that is the UserID place everything into an arraylist
    public ArrayList<Notification> NotificationList(Integer userID){
        return new ArrayList<>();
    }

    //TODO call Payment request from database based on the CreditorID that is the UserID place everthing into an arraylist
    public ArrayList<PaymentApproval> DisplayPaymentRequestList(Integer userID){
        return new ArrayList<>();
    }

}
