package com.example.coin_panion.classes.transaction;

import java.sql.Blob;
import java.sql.Timestamp;
import java.util.List;

public class PaymentApproval {

   private Integer debtorID;
   private Integer creditorID;
   private Blob paymentEvidence;
   private Boolean paymentStatus;
   private Timestamp epoch_approv_issue;

   /*Create new payment request object*/
    public PaymentApproval(Integer debtorID, Integer creditorID, Blob paymentEvidence) {
        this.debtorID = debtorID;
        this.creditorID = creditorID;
        this.paymentEvidence = paymentEvidence;
        this.paymentStatus = false;
        this.epoch_approv_issue = null;
    }

    /*Create payment request object from existing data in database*/
    public PaymentApproval(Integer debtorID, Integer creditorID, Blob paymentEvidence, Boolean paymentStatus) {
        this.debtorID = debtorID;
        this.creditorID = creditorID;
        this.paymentEvidence = paymentEvidence;
        this.paymentStatus = paymentStatus;
        this.epoch_approv_issue = new Timestamp(System.currentTimeMillis());
    }

    /*Creditor Retrieve payment request from debtor*/
    public PaymentApproval(Integer creditorID) {
        this.creditorID = creditorID;
        /*TODO take the all payment request data based on creditorID where payment status is still false and epoch_approv_issue is null*/

    }

    /*Creditor retrieve specified payment request from debtor*/
    public PaymentApproval(Integer debtorID, Integer creditorID) {
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
    public List<PaymentApproval> retrieveAllPaymentRequest(){

        return null;
    }

    /*TODO get the debtorName based on the debtorID*/
    public String debtorName(){

        return null;
    }


}
