package com.example.coin_panion.classes.transaction;

import java.sql.Blob;

public class SettleUpAccount {
    private Integer accountId;
    private String accountName;
    private long accountNumber;
    private Blob qrCode;

    /*Create new settle Up object for a user*/
    public SettleUpAccount(Integer accountId, String accountName, long accountNumber, Blob qrCode) {
        this.accountId = accountId;
        this.accountName = accountName;
        this.accountNumber = accountNumber;
        this.qrCode = qrCode;
    }


    /*TODO fetch settleUp info from database based on accountID*/
    public SettleUpAccount fetchSettleUp(Integer accountId){

        return null;// fetch settleUp based info based on user accountId
    }

    /*TODO update settleUp info from database based on accountID*/
    public void updateSettleUpInfo(){

    }
}

