package com.example.coin_panion.classes.general;

import com.example.coin_panion.classes.notification.Notification;
import com.example.coin_panion.classes.general.SettleUpAccount;
import com.example.coin_panion.classes.transaction.Transaction;
import com.example.coin_panion.classes.utility.Line;
import com.example.coin_panion.classes.utility.ThreadStatic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class Account{
    private String accountID;
    private User user;
    private String password;
    private String bio;
    private List<User> friends;
    private DebtLimit debtLimit;
    private List<Transaction> debts;
    private SettleUpAccount settleUpAccount;
    private List<Notification> notifications;

    /**
     * Retrieve a list of UNSETTLED DEBTS from the transaction table
     * @param accountID
     * @param dataThread
     * @return
     */
    public static List<Transaction> retrieveDebts(Integer accountID, Thread dataThread){
        AtomicReference<List<Transaction>> listAtomicReference = new AtomicReference<>();
        dataThread = new Thread(() -> {
            List<Transaction> debts = new ArrayList<>();
            try(
                    Connection connection = Line.getConnection();
                    PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM transaction WHERE epoch_settled IS NULL AND debtor = ?");
            ){
                preparedStatement.setInt(1, accountID);
                ResultSet resultSet = preparedStatement.executeQuery();
                while(resultSet.next()){
                    debts.add(new Transaction(resultSet.getInt(1), resultSet.getString(9), resultSet.getInt(2), resultSet.getInt(3), resultSet.getInt(4),resultSet.getString(5), resultSet.getDouble(6), resultSet.getLong(7), resultSet.getLong(8)));
                }
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            listAtomicReference.set(debts);
        });
        ThreadStatic.run(dataThread);
        return listAtomicReference.get();
    }

}