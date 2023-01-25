package com.example.coin_panion.classes.utility;

public enum PictureType {
    COVER("COVER"),
    PROFILE("PROFILE"),
    QR("QR"),
    PAYMENT("PAYMENT"),
    GENERAL("GENERAL");

    private String type;

    PictureType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
