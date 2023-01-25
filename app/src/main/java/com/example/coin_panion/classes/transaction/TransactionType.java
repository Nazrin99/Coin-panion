package com.example.coin_panion.classes.transaction;

public enum TransactionType{
    PAYMENT_ISSUE("PAYMENT_ISSUE"),
    PAYMENT_MADE("PAYMENT_MADE");

    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    TransactionType(String type) {
        this.type = type;
    }
}
