package com.example.coin_panion.general;

import java.sql.Blob;
import java.util.ArrayList;

public class PaymentRequest{

    private Integer paymentRequestID;
    private Integer debtorID;
    private final Integer creditorID;
    private String debtorName;
    private String creditorName;
    private Blob paymentEvidence;
    private Boolean paymentStatus ;

    // TODO generate notificationID
    public PaymentRequest(Integer paymentRequestID, Integer debtorID, String debtorName, String creditorName, Blob paymentEvidence) {
        this.paymentRequestID = paymentRequestID;
        this.debtorID = debtorID;
        this.paymentEvidence = paymentEvidence;
        this.debtorName = debtorName;
        this.creditorName = creditorName;
        this.creditorID = User.userID;
        this.paymentStatus = false;
    }

    public Blob getPaymentEvidence() {
        return paymentEvidence;
    }

    public void setPaymentEvidence(Blob paymentEvidence) {
        this.paymentEvidence = paymentEvidence;
    }

    public Boolean getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(Boolean paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    //TODO get friend Info
    //Tobe displayed in the interface



    //TODO get notification for approved payment request
    //UserName has approved your payment request FriendName
    public String approvePaymentRequestNotification(){
        Notification notification = new Notification(5,this.creditorID, this.debtorID,"Hello " + debtorName + ", " + creditorName + " has approved your payment request");
        return notification.getNotificationDesc();
    }

    //TODO function to add the payment to the database
    public void addPaymentRequest(){

    }


}
