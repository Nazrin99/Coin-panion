package com.example.coin_panion.classes.settleUp;

public enum PaymentRequestStatus {
    PAYMENT_PENDING_APPROVAL("PAYMENT_PENDING_APPROVAL"),
    PAYMENT_APPROVED("PAYMENT_APPROVED"),
    APPROVAL_GIVEN("APPROVAL_GIVEN"),
    APPROVAL_REQUESTED("APPROVAL_REQUESTED"),

    PAYMENT_NOT_MADE("PAYMENT_NOT_MADE"),

    GENERAL("GENERAL");

    String type;

    PaymentRequestStatus(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public static PaymentRequestStatus getPaymentRequestStatus(String string){
        if(string.equalsIgnoreCase(PaymentRequestStatus.APPROVAL_REQUESTED.getType())){
            return PaymentRequestStatus.APPROVAL_REQUESTED;
        }
        else if(string.equalsIgnoreCase(PaymentRequestStatus.APPROVAL_GIVEN.getType())){
            return PaymentRequestStatus.APPROVAL_GIVEN;
        }
        else if(string.equalsIgnoreCase(PaymentRequestStatus.PAYMENT_APPROVED.getType())){
            return PaymentRequestStatus.PAYMENT_APPROVED;
        }
        else if(string.equalsIgnoreCase(PaymentRequestStatus.PAYMENT_NOT_MADE.getType())){
            return PaymentRequestStatus.PAYMENT_NOT_MADE;
        }
        else if(string.equalsIgnoreCase(PaymentRequestStatus.PAYMENT_PENDING_APPROVAL.getType())){
            return PaymentRequestStatus.PAYMENT_PENDING_APPROVAL;
        }
        else{
            return PaymentRequestStatus.GENERAL;
        }
    }
}
