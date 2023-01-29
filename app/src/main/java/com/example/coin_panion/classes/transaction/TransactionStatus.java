package com.example.coin_panion.classes.transaction;

public enum TransactionStatus {
    PAYMENT_SETTLED("PAYMENT_SETTLED"),
    PAYMENT_NOT_MADE("PAYMENT_NOT_MADE"),
    PAYMENT_REQUEST("PAYMENT_REQUEST"),
    PAYMENT_APPROVE("PAYMENT_APPROVE"),
    GENERAL("GENERAL");

    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    TransactionStatus(String type) {
        this.type = type;
    }

    public static TransactionStatus getTransType(String string){
        if(string.equalsIgnoreCase(TransactionStatus.PAYMENT_APPROVE.getType())){
            return TransactionStatus.PAYMENT_APPROVE;
        }
        else if(string.equalsIgnoreCase(TransactionStatus.PAYMENT_REQUEST.getType())){
            return TransactionStatus.PAYMENT_REQUEST;
        }
        else if(string.equalsIgnoreCase(TransactionStatus.PAYMENT_NOT_MADE.getType())){
            return TransactionStatus.PAYMENT_NOT_MADE;
        }
        else if(string.equalsIgnoreCase(TransactionStatus.PAYMENT_SETTLED.getType())){
            return TransactionStatus.PAYMENT_SETTLED;
        }
        return TransactionStatus.GENERAL;
    }
}
