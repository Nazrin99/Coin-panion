package com.example.coin_panion.classes.transaction;

public enum TransactionType{
    PAYMENT_ISSUE("PAYMENT_ISSUE"),
    PAYMENT_MADE("PAYMENT_MADE"),

    GENERAL("GENERAL");

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

    public static TransactionType getTransType(String prompt){
        if(prompt.equalsIgnoreCase(TransactionType.PAYMENT_ISSUE.getType())){
            return TransactionType.PAYMENT_ISSUE;
        }
        else if(prompt.equalsIgnoreCase(TransactionType.PAYMENT_MADE.getType())){
            return TransactionType.PAYMENT_MADE;
        }
        return TransactionType.GENERAL;
    }
}
