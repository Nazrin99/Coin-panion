package com.example.coin_panion.classes.settleUp;

public class DebtList {

    private Integer debtorID;
    private Integer creditorID;
    private Integer paymentAmount;
    private boolean paymentStatus;

    public DebtList(Integer debtorID, Integer creditorID, Integer paymentAmount, boolean paymentStatus) {
        this.debtorID = debtorID;
        this.creditorID = creditorID;
        this.paymentAmount = paymentAmount;
        this.paymentStatus = paymentStatus;
    }

    public Integer getPaymentAmount() {
        return paymentAmount;
    }

    public boolean isPaymentStatus() {
        return paymentStatus;
    }

    // TODO return friend with debt info based on query
    public Integer getDebtOwned(Integer friendID, Integer userID){
        //TODO query to get debt owned by friend based on friendID and UserID
        Integer amontFriendDebt = 0;
        return amontFriendDebt;
    }


}
