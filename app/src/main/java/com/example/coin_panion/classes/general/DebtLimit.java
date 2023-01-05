package com.example.coin_panion.classes.general;

import com.example.coin_panion.classes.User;

public class DebtLimit {

    // TODO generate ID for user's debtLimit
    private final Integer DebtLimitID;
    private final Integer DebtLimitUser;
    private String CurrencyType;
    private String DebtLimitTime;
    private Integer DebtLimitAmount;

    public DebtLimit(Integer debtLimitID, String currencyType, String debtLimitTime, Integer debtLimitAmount) {
        this.DebtLimitID = debtLimitID;
        this.DebtLimitUser = User.userID;
        this.DebtLimitTime = debtLimitTime;
        this.CurrencyType = currencyType;
        this.DebtLimitAmount = debtLimitAmount;
    }

    public void setCurrencyType(String currencyType) {
        CurrencyType = currencyType;
    }

    public void setDebtLimitTime(String debtLimitTime) {
        DebtLimitTime = debtLimitTime;
    }

    public void setDebtLimitAmount(Integer debtLimitAmount) {
        DebtLimitAmount = debtLimitAmount;
    }

    public Integer getDebtLimitID() {
        return DebtLimitID;
    }

    public Integer getDebtLimitUser() {
        return DebtLimitUser;
    }

    public String getCurrencyType() {
        return CurrencyType;
    }

    public String getDebtLimitTime() {
        return DebtLimitTime;
    }

    public Integer getDebtLimitAmount() {
        return DebtLimitAmount;
    }
}
