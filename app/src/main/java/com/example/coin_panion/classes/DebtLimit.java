package com.example.coin_panion.classes;

import java.time.LocalDate;

public class DebtLimit {

    private Integer accountID;
    private Integer debt_limit_amount;
    private LocalDate debtLimitEndDate;

    /*Set debt limit object for a user*/
    public DebtLimit(Integer accountID, Integer debt_limit_amount, String timeInterval) {
        this.accountID = accountID;
        this.debt_limit_amount = debt_limit_amount;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            if (timeInterval.equalsIgnoreCase("today")) {
                this.debtLimitEndDate = LocalDate.now();
            } else if (timeInterval.equalsIgnoreCase("week")) {
                this.debtLimitEndDate = LocalDate.now().plusWeeks(1);
            } else if (timeInterval.equalsIgnoreCase("month")) {
                this.debtLimitEndDate = LocalDate.now().plusMonths(1);
            }
        }
    }

    /*Fetch debt limit info of user based on accountID*/
    public static DebtLimit fetchDebtLimit(Integer accountID){
        return null; // new DebtLimit(accountID, debt_limit_amount, debtLimitEndDate);
    }

    /*Check if the debt limit already is due*/
    public boolean isDebtLimitOverDue(){
        LocalDate today;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            today = LocalDate.now();
             return today.isAfter(debtLimitEndDate);
        }
        return false;
    }

    /*Update DebtLimit based on accountID*/
    public void updateUserDebtLimit(){
        //TODO update user debt limit to database based on this.variable
    }

}
