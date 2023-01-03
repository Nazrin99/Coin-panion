package com.example.coin_panion.SettleUp;

import java.sql.Blob;

public class SettleUp {

    private final Integer settleUpID;
    private Integer userID;
    private Integer accountNumber;
    private Blob QRCode;

    public SettleUp(Integer settleUpID, Integer userID, Integer accountNumber, Blob QRCode) {
        this.settleUpID = settleUpID;
        this.userID = userID;
        this.accountNumber = accountNumber;
        this.QRCode = QRCode;
    }

    public Integer getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(Integer accountNumber) {
        this.accountNumber = accountNumber;
    }

    public Blob getQRCode() {
        return QRCode;
    }

    public void setQRCode(Blob QRCode) {
        this.QRCode = QRCode;
    }

    public void deleteQRCode(){
        this.QRCode = null;
    }
}
