package com.example.coin_panion.classes.general;

import com.example.coin_panion.classes.utility.Line;
import com.example.coin_panion.classes.utility.ThreadStatic;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Instant;

@SuppressWarnings("ALL")
public class DebtLimit implements Serializable{
    private Double debtLimitAmount;
    private Long debtLimitEndDate;

    public DebtLimit(Double debtLimitAmount, Long debtLimitEndDate) {
        this.debtLimitAmount = debtLimitAmount;
        this.debtLimitEndDate = debtLimitEndDate;
    }

    // TODO CHECK IF DEBT IS OVERDUE
    public boolean isDebtLimitOverDue(){
        Long now = Instant.now().toEpochMilli();
        return this.debtLimitEndDate < now;
    }

    public void deleteUserDebtLimit(Integer accountID, Thread dataThread){
        updateUserDebtLimit(accountID, null, null, dataThread);
    }

    /**
     * Updates the debt limit of the user in the database the calling object
     * @param accountID
     * @param debtLimitAmount
     * @param debtLimitEndDate
     * @param dataThread
     */
    public void updateUserDebtLimit(Integer accountID, Double debtLimitAmount, Long debtLimitEndDate, Thread dataThread){
        this.debtLimitAmount = debtLimitAmount;
        dataThread = new Thread(() -> {
            try(Connection connection = Line.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE account SET debt_limit_amount = ?, debt_limit_end_date = ? WHERE account_id = ?")){
                preparedStatement.setDouble(1, debtLimitAmount);
                preparedStatement.setLong(2, debtLimitEndDate);
                preparedStatement.setInt(3, accountID);
                preparedStatement.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            this.setDebtLimitAmount(debtLimitAmount);
            this.setDebtLimitEndDate(debtLimitEndDate);
        });
        ThreadStatic.run(dataThread);
    }

    public Double getDebtLimitAmount() {
        return debtLimitAmount;
    }

    public void setDebtLimitAmount(Double debtLimitAmount) {
        this.debtLimitAmount = debtLimitAmount;
    }

    public Long getDebtLimitEndDate() {
        return debtLimitEndDate;
    }

    public void setDebtLimitEndDate(Long debtLimitEndDate) {
        this.debtLimitEndDate = debtLimitEndDate;
    }
}
