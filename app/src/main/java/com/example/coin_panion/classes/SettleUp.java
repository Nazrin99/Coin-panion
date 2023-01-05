package com.example.coin_panion.classes;

import java.sql.Blob;

public class SettleUp {

    private Integer accountID;
    private String settleup_account_name;
    private long accountNumber;
    private Blob QRCode;

    // Create new settle up object
    public SettleUp(Integer userID, String settleup_account_name, long accountNumber, Blob QRCode) {
        this.accountID = userID;
        this.accountNumber = accountNumber;
        this.QRCode = QRCode;
        this.settleup_account_name = settleup_account_name;
    }

    public long getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(long accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getSettleup_account_name() {
        return settleup_account_name;
    }

    public void setSettleup_account_name(String settleup_account_name) {
        this.settleup_account_name = settleup_account_name;
    }

    public Blob getQRCode() {
        return QRCode;
    }

    public void setQRCode(Blob QRCode) {
        this.QRCode = QRCode;
    }

    public void deleteQRCode()  {
        this.QRCode = null;
    }

    // Return N number of payment account from user

    //TODO query to pull user account number and image
    public SettleUp(Integer accountID) {
        /*Create a settle up object based on accountID*/
        String query = "SELECT * FROM SettleUpAccount WHERE accountID = " + accountID;
        this.accountID = accountID;
        this.accountNumber = accountNumber;
        this.QRCode = QRCode;
        this.settleup_account_name = settleup_account_name;
    }

    public void updateSettleUpInformation(String settleup_account_name, long accountNumber, Blob QRCode) {
        this.settleup_account_name = settleup_account_name;
        this.accountNumber = accountNumber;
        this.QRCode = QRCode;
        /*Update the information in the database*/
        String query = "UPDATE SettleUpAccount SET settleup_account_name = ?, accountNumber = ?, QRCode = ? WHERE accountID = ?";
    }
}
