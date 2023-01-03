package com.example.coin_panion.SettleUp;

import com.example.coin_panion.general.Notification;
import com.example.coin_panion.general.PaymentRequest;

import java.sql.Blob;

public class SettleUpDebt {

    private Integer creditorID;
    private Integer debtorID;
    private String debtorName;
    private String creditorName;
    private Blob receipt;
    private Boolean settleUpStatus;

    public SettleUpDebt(Integer creditorID, Integer debtorID, Blob receipt) {
        this.creditorID = creditorID;
        this.debtorID = debtorID;
        this.receipt = receipt;
        this.settleUpStatus = false;
    }

    public Blob getReceipt() {
        return receipt;
    }

    public void setReceipt(Blob receipt) {
        this.receipt = receipt;
    }

    public Boolean getSettleUpStatus() {
        return settleUpStatus;
    }

    public void setSettleUpStatus(Boolean settleUpStatus) {
        this.settleUpStatus = settleUpStatus;
    }

    public String getDebtorName() {
        return debtorName;
    }

    public String getCreditorName() {
        return creditorName;
    }

    // TODO on click method
    public String settleUpNotification(){
        Notification notification = new Notification(4,debtorID,creditorID,"Hello " + getCreditorName() + ", " + getDebtorName() + "has sent you a payment request");
        return notification.getNotificationDesc();
    }

    // TODO store payment request
    public void createPaymentRequest(){
        PaymentRequest paymentRequest = new PaymentRequest(6,this.debtorID,this.debtorName,this.creditorName,getReceipt());

    }



}
