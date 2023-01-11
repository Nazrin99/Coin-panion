package com.example.coin_panion.classes.notification;

public enum NotificationType {
    ADDED_INTO_GROUP("ADDED_INTO_GROUP"),
    PAYMENT_APPROVAL("PAYMENT_APPROVAL"),
    GENERAL("GENERAL");

    private String type;

    NotificationType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
