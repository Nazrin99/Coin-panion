package com.example.coin_panion.classes.general;

import com.example.coin_panion.classes.notification.Notification;
import com.example.coin_panion.classes.general.SettleUpAccount;
import com.example.coin_panion.classes.settleUp.PaymentApproval;
import com.example.coin_panion.classes.transaction.Transaction;
import com.example.coin_panion.classes.utility.Line;
import com.example.coin_panion.classes.utility.ThreadStatic;

import org.apache.commons.codec.digest.DigestUtils;

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
//    private List<Transaction> debts;
//    private List<Transaction> credits;
//    private List<PaymentApproval> paymentApprovalList;
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

    public static Account accountCredentialsValid( User user, String password, Thread dataThread){
        AtomicReference<Account> atomicReference = new AtomicReference<>();
        dataThread = new Thread(() -> {
            try{
                Connection connection = Line.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM account WHERE user_id = ? AND password = ?");
                preparedStatement.setInt(1, user.getUserID());
                preparedStatement.setString(2, new DigestUtils("'SHA3-256").digestAsHex(password));
                ResultSet resultSet = preparedStatement.executeQuery();

                if(resultSet.next()){
                    // TODO Account credentials match, proceed to construct account object from User and return Account object
                    Account loggedAccount = new Account();
                    loggedAccount.setUser(user);
                    atomicReference.set(loggedAccount);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        ThreadStatic.run(dataThread);
        return atomicReference.get();
    }

    public String getAccountID() {
        return accountID;
    }

    public void setAccountID(String accountID) {
        this.accountID = accountID;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public List<User> getFriends() {
        return friends;
    }

    public void setFriends(List<User> friends) {
        this.friends = friends;
    }

    public DebtLimit getDebtLimit() {
        return debtLimit;
    }

    public void setDebtLimit(DebtLimit debtLimit) {
        this.debtLimit = debtLimit;
    }

    public SettleUpAccount getSettleUpAccount() {
        return settleUpAccount;
    }

    public void setSettleUpAccount(SettleUpAccount settleUpAccount) {
        this.settleUpAccount = settleUpAccount;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }
}