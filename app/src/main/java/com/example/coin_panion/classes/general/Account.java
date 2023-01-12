package com.example.coin_panion.classes.general;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.example.coin_panion.classes.notification.Notification;
import com.example.coin_panion.classes.transaction.Transaction;
import com.example.coin_panion.classes.utility.Hashing;
import com.example.coin_panion.classes.utility.Line;
import com.example.coin_panion.classes.utility.Picture;
import com.example.coin_panion.classes.utility.ThreadStatic;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class Account implements Serializable {
    private Integer accountID;
    private User user;
    private String password;
    private String bio;
    private List<User> friends = new ArrayList<>();
    private DebtLimit debtLimit;
//    private List<Transaction> debts;
//    private List<Transaction> credits;
//    private List<PaymentApproval> paymentApprovalList;
    private SettleUpAccount settleUpAccount;
    private List<Notification> notifications = new ArrayList<>();
    private Picture accountPic;
    private Picture accountCover;

    /**
     * Constructor to create new account prior database insertion
     * @param user
     * @param password
     * @param bio
     * @param accountPic
     * @param accountCover
     */
    public Account(User user, String password, String bio, Picture accountPic, Picture accountCover) {
        this.user = user;
        this.password = password;
        this.bio = bio;
        this.accountPic = accountPic;
        this.accountCover = accountCover;
    }

    /**
     * Constructor to create complete Account object
     * @param accountID
     * @param user
     * @param password
     * @param bio
     * @param friends
     * @param debtLimit
     * @param settleUpAccount
     * @param notifications
     * @param accountPic
     * @param accountCover
     */
    public Account(Integer accountID, User user, String password, String bio, List<User> friends, DebtLimit debtLimit, SettleUpAccount settleUpAccount, List<Notification> notifications, Picture accountPic, Picture accountCover) {
        this.accountID = accountID;
        this.user = user;
        this.password = password;
        this.bio = bio;
        this.friends = friends;
        this.debtLimit = debtLimit;
        this.settleUpAccount = settleUpAccount;
        this.notifications = notifications;
        this.accountPic = accountPic;
        this.accountCover = accountCover;
    }

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

    /**
     * Inserts the details of the new Account in the database and updates this objects' accountID with the generated keys. Nulled fields remain for friends, debt limits, notifications, and settle up account
     * @param dataThread
     */
    public void insertNewAccount(Thread dataThread){
        dataThread = new Thread(() -> {
            try(
                    Connection connection = Line.getConnection();
                    PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO account(password, bio, account_pic, account_cover, user_id) VALUES(?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            ){
                preparedStatement.setString(1, Hashing.keccakHash(this.getPassword()));
                preparedStatement.setString(2,this.getBio());
                preparedStatement.setInt(3, this.getAccountPic().getPictureID());
                preparedStatement.setInt(4, this.getAccountCover().getPictureID());
                preparedStatement.setInt(5, this.getUser().getUserID());
                preparedStatement.execute();

                ResultSet resultSet = preparedStatement.getGeneratedKeys();
                if(resultSet.next()){
                    // Key exists, update Account object
                    this.setAccountID(resultSet.getInt(1));
                }
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        ThreadStatic.run(dataThread);
    }

    public static Account accountCredentialsValid( User user, String password, Thread dataThread){
        AtomicReference<Account> atomicReference = new AtomicReference<>();
        dataThread = new Thread(() -> {
            try{
                Connection connection = Line.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM account WHERE user_id = ? AND password = ?");
                preparedStatement.setInt(1, user.getUserID());
                preparedStatement.setString(2, Hashing.keccakHash(password));
                ResultSet resultSet = preparedStatement.executeQuery();

                if(resultSet.next()){
                    // TODO Account credentials match, proceed to construct account object from User and return Account object
                    Account loggedAccount = null;
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

    public Integer getAccountID() {
        return accountID;
    }

    public void setAccountID(Integer accountID) {
        this.accountID = accountID;
    }

    public Picture getAccountPic() {
        return accountPic;
    }

    public void setAccountPic(Picture accountPic) {
        this.accountPic = accountPic;
    }

    public Picture getAccountCover() {
        return accountCover;
    }

    public void setAccountCover(Picture accountCover) {
        this.accountCover = accountCover;
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

    public void setSettleUpAccounts(SettleUpAccount settleUpAccount) {
        this.settleUpAccount = settleUpAccount;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }
}