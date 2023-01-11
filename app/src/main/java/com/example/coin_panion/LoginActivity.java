package com.example.coin_panion;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.text.Editable;
import android.text.PrecomputedText;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.coin_panion.classes.general.Account;
import com.example.coin_panion.classes.general.DebtLimit;
import com.example.coin_panion.classes.general.SettleUpAccount;
import com.example.coin_panion.classes.general.User;
import com.example.coin_panion.classes.notification.Notification;
import com.example.coin_panion.classes.utility.Hashing;
import com.example.coin_panion.classes.utility.Line;
import com.example.coin_panion.classes.utility.Picture;
import com.example.coin_panion.classes.utility.ThreadStatic;
import com.google.android.material.textfield.TextInputLayout;

import static com.example.coin_panion.classes.utility.Validifier.isEmail;
import static com.example.coin_panion.classes.utility.Validifier.isPhoneNumber;

import org.bouncycastle.util.io.pem.PemReader;
import org.checkerframework.checker.units.qual.A;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

//import org.apache.commons.codec.digest.DigestUtils;


public class LoginActivity extends AppCompatActivity {

    TextInputLayout ETUserInfo, ETUserPass;
    Button BtnLogin;
    Button BtnSignUp;
    Thread dataThread;
    Bundle bundle = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        callUI();
        setupUIListeners();

        dataThread = new Thread(() -> {});
    }

    public void
    callUI(){
        ETUserInfo = findViewById(R.id.loginLayout);
        ETUserPass = findViewById(R.id.passwordLayout);
        BtnLogin = findViewById(R.id.loginButton);
        BtnSignUp = findViewById(R.id.signupButton);
    }

    public void setupUIListeners(){
        BtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateLogin();
                Log.i("Info","Button Clicked");
            }
        });

        BtnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(intent);
            }
        });

        ETUserInfo.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() <= 0){
                    ETUserInfo.setError("Credential cannot be empty!");
                }
                else{
                    ETUserInfo.setError(null);
                }
            }
        });

        ETUserPass.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() <= 0){
                    ETUserPass.setError("Password field cannot be empty!");
                }
                else{
                    ETUserPass.setError(null);
                }
            }
        });
    }

    public void validateLogin() {

        String userLogin = ETUserInfo.getEditText().getText().toString();
        String userPass = ETUserPass.getEditText().getText().toString();

        if(userLogin.isEmpty()){
            ETUserInfo.setError("Credential cannot be empty!");
            return;
        }
        if(userPass.isEmpty()){
            ETUserPass.setError("Password field cannot be empty!");
            return;
        }
        Object data = null;
        if(isEmail(userLogin)){
            data = userLogin;
        }
        else if(isPhoneNumber(userLogin)){
            data = Long.parseLong(userLogin);
        }
        else{
            // Not a phone number, not an email
            return;
        }
        AtomicReference<Object> objectAtomicReference = new AtomicReference<>(data);
        dataThread = new Thread(new Runnable() {
            AtomicReference<User> userAtomicReference = new AtomicReference<>();
            @Override
            public void run() {
                // Getting User from the database
                User user = User.verifyUserLogin(objectAtomicReference.get(), new Thread());

                if(user != null && user.getUserID() > 0){
                    // User exists, check for password validity
                    bundle.putParcelable("user", user);
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
                            preparedStatement1.setInt(1, user.getUserID());

                            ResultSet resultSet1 = preparedStatement1.executeQuery();
                            if(resultSet1.next()){
                                // Account info exists, create Account object

                                // 1 : Create debt limit object. Store bio string, accountID integer, picture id, and picture cover id
                                DebtLimit debtLimit = new DebtLimit(resultSet.getDouble(4), resultSet.getLong(5));
                                Integer accountID = resultSet.getInt(1);
                                String password = resultSet.getString(2);
                                String bio = resultSet.getString(3);
                                Integer accountPicID = resultSet.getInt(6);
                                Integer accountCoverID = resultSet.getInt(7);

                                // 2 : Close the result set and query picture object
                                resultSet1.close();

                                Picture accountPic = Picture.getPictureFromDB(accountPicID);
                                Picture accountCover = Picture.getPictureFromDB(accountCoverID);

                                // 3 : Query account friends
                                List<User> friends = User.getFriends(accountID, new Thread());

                                // 4 : Query settle accounts
                                SettleUpAccount settleUpAccount = SettleUpAccount.retrieveSettleUpAccount(accountID, new Thread());

                                // 5 : Query notifications
                                List<Notification> notifications = Notification.getNotifications(accountID, new Thread());

                                // 6 : Create account object, and pass to bundle then switch activity
                                Account account = new Account(accountID, user, password, bio, friends, debtLimit, settleUpAccount, notifications, accountPic, accountCover);
                                bundle.putParcelable("account", account);

                                switchToLoginActivity();
                            }
                        }
                        else{
                            // Account does not exist, don't run anything
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
        });
        ThreadStatic.run(dataThread);
    }

    private void switchToLoginActivity(){
        Intent intent = new Intent(this, FriendsActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }



}