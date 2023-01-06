package com.example.coin_panion.classes;

import java.sql.Blob;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PaymentRequest{

   private Integer debtorID;
   private Integer creditorID;
   private Blob paymentEvidence;
   private Boolean paymentStatus;
   private Timestamp epoch_approv_issue;

   /*Create new payment request object*/
    public PaymentRequest(Integer debtorID, Integer creditorID, Blob paymentEvidence) {
        this.debtorID = debtorID;
        this.creditorID = creditorID;
        this.paymentEvidence = paymentEvidence;
        this.paymentStatus = false;
        this.epoch_approv_issue = null;
    }

    /*Create payment request object from existing data in database*/
    public PaymentRequest(Integer debtorID, Integer creditorID, Blob paymentEvidence, Boolean paymentStatus) {
        this.debtorID = debtorID;
        this.creditorID = creditorID;
        this.paymentEvidence = paymentEvidence;
        this.paymentStatus = paymentStatus;
        this.epoch_approv_issue = new Timestamp(System.currentTimeMillis());
    }

    /*Creditor Retrieve payment request from debtor*/
    public PaymentRequest(Integer creditorID) {
        this.creditorID = creditorID;
        /*TODO take the all payment request data based on creditorID where payment status is still false and epoch_approv_issue is null*/

    }

    /*Creditor retrieve specified payment request from debtor*/
    public PaymentRequest(Integer debtorID, Integer creditorID) {
        this.debtorID = debtorID;
        this.creditorID = creditorID;
        /*TODO take the specified payment request data based on creditorID */
    }

    /*TODO add payment request to database based on this.variable*/
    /*TODO add notification after payment request is added*/
    public void addPaymentRequest(){

    }

    /*TODO update Payment request paymentStatus to true and fill in time epoch_approv_issue*/
    /*TODO add notification after payment request is approved*/
    public void updatePaymentRequest(){

    }

    /*Get all User's payment approval request*/
    public List<PaymentRequest> retrieveAllPaymentRequest(){

        return null;
    }



}
