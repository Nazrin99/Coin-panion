package com.example.coin_panion;

import static com.example.coin_panion.classes.utility.Validifier.isEmail;
import static com.example.coin_panion.classes.utility.Validifier.isPhoneNumber;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.coin_panion.classes.general.Account;
import com.example.coin_panion.classes.general.DebtLimit;
import com.example.coin_panion.classes.general.SettleUpAccount;
import com.example.coin_panion.classes.general.User;
import com.example.coin_panion.classes.notification.Notification;
import com.example.coin_panion.classes.utility.Hashing;
import com.example.coin_panion.classes.utility.Line;
import com.example.coin_panion.classes.utility.Picture;
import com.example.coin_panion.classes.utility.ThreadStatic;
import com.example.coin_panion.classes.utility.Validifier;
import com.google.android.material.textfield.TextInputLayout;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLOutput;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;


public class LoginActivity extends AppCompatActivity {
    TextInputLayout loginLayout, passwordLayout;
    Button BtnLogin;
    Button BtnSignUp;
    Runnable dataThread;
    Bundle bundle = new Bundle();
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        callUI();
        setupUIListeners();

        dataThread = new Thread(() -> {});
    }

    public void callUI(){
        loginLayout = findViewById(R.id.loginLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        BtnLogin = findViewById(R.id.loginButton);
        BtnSignUp = findViewById(R.id.signupButton);
    }

    public void setupUIListeners(){
        BtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateLogin();
            }
        });

        BtnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(intent);
            }
        });

        loginLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() <= 0){
                    runOnUiThread(() -> loginLayout.setError("Credential cannot be empty!"));
                }
                else{
                    runOnUiThread(() -> loginLayout.setError(null));
                }
            }
        });

        passwordLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(Validifier.isPhoneNumber(s.toString())){
                    runOnUiThread(() -> loginLayout.setError(null));
                }
                if(s.length() <= 0){
                    runOnUiThread(() -> passwordLayout.setError("Password field cannot be empty!"));
                }
                else{
                    runOnUiThread(() -> passwordLayout.setError(null));
                }
            }
        });
    }

    public void validateLogin() {

        String userLogin = loginLayout.getEditText().getText().toString();
        String userPass = passwordLayout.getEditText().getText().toString();

        if(userLogin.isEmpty()){
            runOnUiThread(() -> loginLayout.setError("Credential cannot be empty!"));
            return;
        }
        if(userPass.isEmpty()){
            runOnUiThread(() -> passwordLayout.setError("Password field cannot be empty!"));
            return;
        }
        Object data = null;
        if(isEmail(userLogin)){
            data = userLogin;
        }
        else if(isPhoneNumber(userLogin)){
            data = Long.parseLong(userLogin.substring(1));
        }
        else{
            // Not a phone number, not an email
            runOnUiThread(() -> loginLayout.setError("Make sure your phone number is in the following format (eg +60179916645)"));
            return;
        }
        System.out.println("Before");
        AtomicReference<Object> objectAtomicReference = new AtomicReference<>(data);
        dataThread = new Runnable() {
            AtomicReference<User> userAtomicReference = new AtomicReference<>();
            @Override
            public void run() {
                // Getting User from the database
                System.out.println("Entered thread");
                User user = User.verifyUserLogin(objectAtomicReference.get(), new Thread());
                System.out.println("Verifying");
                if(user != null && user.getUserID() > 0){
                    // User exists, check for password validity
                    boolean account_exists = false;
                    try(
                            Connection connection = Line.getConnection();
                            PreparedStatement preparedStatement = connection.prepareStatement("SELECT password FROM account WHERE user_id = ? ");
                            PreparedStatement preparedStatement1 = connection.prepareStatement("SELECT * FROM account WHERE user_id = ?");
                    ){
                        preparedStatement.setInt(1, user.getUserID());
                        ResultSet resultSet = preparedStatement.executeQuery();
                        if(resultSet.next()){
                            // Said account exists, verify password
                            if(resultSet.getString(1).equalsIgnoreCase(Hashing.keccakHash(userPass))){
                                // Password if correct, construct account object pass into bundle
                                account_exists = true;
                            }
                            else{
                                // Password is incorrect, show Toast
                                runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Incorrect login credentials", Toast.LENGTH_SHORT).show());
                            }
                        }
                        else{
                            // Said account doesn't exists, show Toast
                            runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Account associated with the above credentials, does not exists!", Toast.LENGTH_SHORT).show());
                        }
                        if(account_exists){
                            System.out.println("Account exists");
                            preparedStatement1.setInt(1, user.getUserID());

                            ResultSet resultSet1 = preparedStatement1.executeQuery();
                            if(resultSet1.next()){
                                // Account info exists, create Account object

                                // 1 : Create debt limit object. Store bio string, accountID integer, picture id, and picture cover id
                                DebtLimit debtLimit = new DebtLimit(resultSet1.getDouble(4), resultSet1.getLong(5));
                                Integer accountID = resultSet1.getInt(1);
                                String password = resultSet1.getString(2);
                                String bio = resultSet1.getString(3);
                                Integer accountPicID = resultSet1.getInt(6);
                                Integer accountCoverID = resultSet1.getInt(7);

                                // 2 : Close the result set and query picture object
                                resultSet1.close();

//                                Picture accountPic = Picture.getPictureFromDB(accountPicID);
//                                Picture accountCover = Picture.getPictureFromDB(accountCoverID);

                                Picture accountPic = null;
                                Picture accountCover = null;

                                // 3 : Query account friends
                                List<User> friends = User.getFriends(accountID, new Thread());

                                // 4 : Query settle accounts
                                SettleUpAccount settleUpAccount = SettleUpAccount.retrieveSettleUpAccount(accountID, new Thread());

                                // 5 : Query notifications
                                List<Notification> notifications = Notification.getNotifications(accountID, new Thread());

                                // 6 : Create account object, and pass to bundle then switch activity
                                Account account = new Account(accountID, user, password, bio, friends, debtLimit, settleUpAccount, notifications, accountPic, accountCover);
                                System.out.println(account == null);
                                System.out.println(account.getFriends().size());

                                switchToLoginActivity(account, user);
                            }
                        }
                        resultSet.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    // User does exists, show Toast
                    runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Credentials does not exists", Toast.LENGTH_SHORT).show());
                }
            }
        };
        handler.post(dataThread);
        System.out.println("Exited program");
    }

    private void switchToLoginActivity(Account account, User user){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("account", account);
        intent.putExtra("user", user);
        startActivity(intent);
    }


}