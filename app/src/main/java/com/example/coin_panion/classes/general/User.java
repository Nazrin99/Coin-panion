package com.example.coin_panion.classes.general;

import com.example.coin_panion.classes.utility.Line;
import com.example.coin_panion.classes.utility.ThreadStatic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.atomic.AtomicReference;
import java.util.zip.DataFormatException;

public class User{
    /*User info for account*/
    private Integer userID;
    private Integer phoneNumber;
    private String firstName;
    private String lastName;
    private String username;
    private String email;

    // Constructor to create User object for existing users from database. REQUIRE phoneNumber
    public User(Integer phoneNumber, Thread dataThread) {
        getUserData(phoneNumber, dataThread);

    }

    // Constructor to create User object for existing users from database. REQUIRE email
    public User(String email, Thread dataThread) {
        getUserData(email, dataThread);
    }

    // Constructor to create User object during runtime. Application data purpose
    public User(Integer userID, Integer phoneNumber, String firstName, String lastName, String username, String email) {
        this.userID = userID;
        this.phoneNumber = phoneNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
    }

    // Constructor to create new User object for database insertion
    public User(Integer phoneNumber, String firstName, String lastName, String username, String email) {
        this.phoneNumber = phoneNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
    }

    // INSERT: new User object into database. Returns a complete database object
    public User insertNewUser(User user, Thread dataThread){
        AtomicReference<User> userAtomicReference = new AtomicReference<>();
        dataThread = new Thread(() -> {
            try{
                Connection connection = Line.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO user(phone_number, email, first_name, last_name, username) VALUES(?, ?, ? ,? ,?)", Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setInt(1, user.getPhoneNumber());
                preparedStatement.setString(2, user.getEmail());
                preparedStatement.setString(3, user.getFirstName());
                preparedStatement.setString(4, user.getLastName());
                preparedStatement.setString(5, user.getUsername());
                preparedStatement.execute();
                ResultSet resultSet = preparedStatement.getGeneratedKeys();
                resultSet.next();
                user.setUserID(resultSet.getInt(1));
                userAtomicReference.set(user);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        ThreadStatic.run(dataThread);
        return userAtomicReference.get();
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
                else if(queryKey instanceof Integer){
                    preparedStatement.setString(1, "phone_number");
                    preparedStatement.setInt(2, (Integer) queryKey);
                }
                else{
                    throw new DataFormatException("Unexpected data type, not an instance of String or Integer");
                }
                ResultSet resultSet = preparedStatement.executeQuery();

                while(resultSet.next()){
                    this.userID = resultSet.getInt(1);
                    this.phoneNumber = resultSet.getInt(2);
                    this.email = resultSet.getString(3);
                    this.firstName = resultSet.getString(4);
                    this.lastName = resultSet.getString(5);
                    this.username = resultSet.getString(6);
                }
                resultSet.close();
            } catch (SQLException | DataFormatException e) {
                e.printStackTrace();
            }
        });
        ThreadStatic.run(dataThread);
    }

    // Getters and setters
    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public Integer getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(Integer phoneNumber) {
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
}
