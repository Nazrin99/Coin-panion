package com.example.coin_panion.classes.general;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.PerformanceHintManager;

import androidx.annotation.NonNull;

import com.example.coin_panion.classes.utility.Line;
import com.example.coin_panion.classes.utility.ThreadStatic;
import com.sun.mail.imap.protocol.ID;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.zip.DataFormatException;

public class User implements Serializable {
    /*User info for account*/
    private Integer userID;
    private Long phoneNumber;
    private String firstName;
    private String lastName;
    private String username;
    private String email;

    protected User(Parcel in) {
        if (in.readByte() == 0) {
            userID = null;
        } else {
            userID = in.readInt();
        }
        if (in.readByte() == 0) {
            phoneNumber = null;
        } else {
            phoneNumber = in.readLong();
        }
        firstName = in.readString();
        lastName = in.readString();
        username = in.readString();
        email = in.readString();
    }

    // Constructor to create User object for existing users from database. REQUIRE phoneNumber
    public User(Object queryKey, Thread dataThread) {
        getUserData(queryKey, dataThread);

    }

    // Constructor to create User object for existing users from database. REQUIRE email
    public User(String email, Thread dataThread) {
        getUserData(email, dataThread);
    }

    // Constructor to create complete User object
    public User(Integer userID, Long phoneNumber, String firstName, String lastName, String username, String email) {
        this.userID = userID;
        this.phoneNumber = phoneNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
    }

    // Constructor to create new User object for database insertion
    public User(Long phoneNumber, String firstName, String lastName, String username, String email) {
        this.phoneNumber = phoneNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
    }

    // INSERT: new User object into database. Returns a complete database object
    public void insertNewUser(Thread dataThread){
        AtomicReference<User> userAtomicReference = new AtomicReference<>();
        dataThread = new Thread(() -> {
            try{
                Connection connection = Line.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO user(phone_number, email, first_name, last_name, username) VALUES(?, ?, ? ,? ,?)", Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setLong(1, this.getPhoneNumber());
                preparedStatement.setString(2, this.getEmail());
                preparedStatement.setString(3, this.getFirstName());
                preparedStatement.setString(4, this.getLastName());
                preparedStatement.setString(5, this.getUsername());
                preparedStatement.execute();
                ResultSet resultSet = preparedStatement.getGeneratedKeys();
                resultSet.next();
                this.setUserID(resultSet.getInt(1));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        ThreadStatic.run(dataThread);
    }

    // DELETE: User info from database, required parameter is userID
    public void deleteUser(Integer userID, Thread dataThread){
        dataThread = new Thread(() ->{
            try{
                Connection connection = Line.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM user WHERE user_id = ?");
                preparedStatement.setInt(1, userID);
                preparedStatement.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        ThreadStatic.run(dataThread);
    }

    // DELETE: User info from database, required parameter is email
    public void deleteUser(String email , Thread dataThread){
        dataThread = new Thread(() ->{
            try{
                Connection connection = Line.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM user WHERE email = ?");
                preparedStatement.setString(1, email);
                preparedStatement.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        ThreadStatic.run(dataThread);
    }

    /**
     * Query database for the existence of a user using their credentials, returns a User object
     * @param queryKey
     * @param dataThread
     * @return
     */
    public static User verifyUserLogin(Object queryKey, Thread dataThread){
        System.out.println("Verify login");
        AtomicReference<User> atomicReference = new AtomicReference<>(null);
        dataThread = new Thread(() -> {
            try(
                    Connection connection = Line.getConnection();
                    ){
                PreparedStatement preparedStatement;
                if(queryKey instanceof String){
                    preparedStatement = connection.prepareStatement("SELECT * FROM user WHERE email = ?");
                    preparedStatement.setString(1, (String) queryKey);
                }
                else if(queryKey instanceof Long){
                    preparedStatement = connection.prepareStatement("SELECT * FROM user WHERE phone_number = ?");
                    preparedStatement.setLong(1, (Long) queryKey);
                }
                else{
                    throw new DataFormatException("Unexpected data type, expected String or Long");
                }
                ResultSet resultSet = preparedStatement.executeQuery();
                if(resultSet.next()){
                    // User exists, return a User object using AtomicReference
                    atomicReference.set(new User(resultSet.getInt(1), resultSet.getLong(2), resultSet.getString(4), resultSet.getString(5), resultSet.getString(6), resultSet.getString(3)));
                }
                resultSet.close();
            } catch (SQLException | DataFormatException e) {
                e.printStackTrace();
            }
        });
        dataThread.start();
        while (dataThread.isAlive()){

        }
        System.out.println(atomicReference.get());
        return atomicReference.get();
    }

    // SELECT: Get existing user data from the database. Require parameter queryKey which can be email or phone number, will be checked in method
    private void getUserData(Object queryKey, Thread dataThread){
        dataThread = new Thread(() -> {
            try {
                Connection connection = Line.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM user WHERE ? = ?");
                if(queryKey instanceof String){
                    preparedStatement.setString(1, "email");
                    preparedStatement.setString(2, (String) queryKey);
                }
                else if(queryKey instanceof Long){
                    preparedStatement.setString(1, "phone_number");
                    preparedStatement.setLong(2, (Long) queryKey);
                }
                else{
                    throw new DataFormatException("Unexpected data type, not an instance of String or Long");
                }
                ResultSet resultSet = preparedStatement.executeQuery();

                if(resultSet.next()){
                    this.userID = resultSet.getInt(1);
                    this.phoneNumber = resultSet.getLong(2);
                    this.email = resultSet.getString(3);
                    this.firstName = resultSet.getString(4);
                    this.lastName = resultSet.getString(5);
                    this.username = resultSet.getString(6);
                }
                else{
                    this.userID = -1;
                }
                resultSet.close();
            } catch (SQLException | DataFormatException e) {
                e.printStackTrace();
            }
        });
        ThreadStatic.run(dataThread);
    }

    /**
     * Retrieves list of friend associated with the accountID. Returns a list of User objects as friends
     * @param accountID
     * @param dataThread
     * @return
     */
    public static List<User> getFriends(Integer accountID, Thread dataThread){
        AtomicReference<List<User>> listAtomicReference = new AtomicReference<>(new ArrayList<>());
        dataThread = new Thread(() -> {
            List<User> friends = new ArrayList<>();
            List<Integer> friendIDs = new ArrayList<>();
            try(
                    Connection connection = Line.getConnection();
                    PreparedStatement preparedStatement = connection.prepareStatement("SELECT friend_id FROM friend WHERE account_id = ?");
                    ){
                preparedStatement.setInt(1, accountID);
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()){
                    friendIDs.add(resultSet.getInt(1));
                }
                resultSet.close();

                if(friendIDs.size() == 0){
                    return;
                }

                PreparedStatement preparedStatement1 = connection.prepareStatement("SELECT * FROM user WHERE " + getQueryString("user_id", friendIDs.size()));
                for(int i = 0; i < friendIDs.size(); i++){
                    preparedStatement1.setInt((i+1), friendIDs.get(i));
                }
                ResultSet resultSet1 = preparedStatement1.executeQuery();

                while(resultSet1.next()){
                    friends.add(new User(resultSet1.getInt(1), resultSet1.getLong(2), resultSet1.getString(4), resultSet1.getString(5), resultSet1.getString(6), resultSet1.getString(3)));
                }
                resultSet1.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            listAtomicReference.set(friends);
        });
        ThreadStatic.run(dataThread);
        return listAtomicReference.get();
    }

    public static List<Integer> getListOfIDs(List<Long> phoneNumber, Thread dataThread){
        AtomicReference<List<Integer>> atomicReference = new AtomicReference<>(new ArrayList<>());
        dataThread = new Thread(() -> {
            List<Integer> IDs = new ArrayList<>();
            try(
                    Connection connection = Line.getConnection();
                    PreparedStatement preparedStatement = connection.prepareStatement("SELECT user_id FROM user WHERE " + getQueryString("phone_number", phoneNumber.size()))
                    ){
                for(int i = 0; i < phoneNumber.size() ;i++){
                    preparedStatement.setLong((i+1), phoneNumber.get(i));
                }
                ResultSet resultSet = preparedStatement.executeQuery();
                while(resultSet.next()){
                    IDs.add(resultSet.getInt(1));
                }
                resultSet.close();
                atomicReference.set(IDs);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        ThreadStatic.run(dataThread);
        return atomicReference.get();
    }

    // Getters and setters
    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public Long getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(Long phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public static String getQueryString(String columnName, Integer length){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(columnName + "= ? ");
        for(int i = 0 ; i < length-1; i++){
            stringBuilder.append("OR " + columnName + "= ?");
        }
        return stringBuilder.toString();
    }
}
