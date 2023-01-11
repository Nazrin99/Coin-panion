package com.example.coin_panion;

import static com.example.coin_panion.classes.utility.Validifier.isEmail;
import static com.example.coin_panion.classes.utility.Validifier.isPhoneNumber;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.coin_panion.classes.general.Account;
import com.example.coin_panion.classes.general.DebtLimit;
import com.example.coin_panion.classes.general.SettleUpAccount;
import com.example.coin_panion.classes.general.User;
import com.example.coin_panion.classes.notification.Notification;
import com.example.coin_panion.classes.utility.BaseViewModel;
import com.example.coin_panion.classes.utility.Hashing;
import com.example.coin_panion.classes.utility.Line;
import com.example.coin_panion.classes.utility.Picture;
import com.example.coin_panion.classes.utility.ThreadStatic;
import com.example.coin_panion.classes.utility.Validifier;
import com.google.android.material.textfield.TextInputLayout;

import org.bouncycastle.jcajce.provider.symmetric.ARC4;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

//import org.apache.commons.codec.digest.DigestUtils;


public class LoginActivity extends AppCompatActivity {
    BaseViewModel loginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginViewModel = new ViewModelProvider(this).get(BaseViewModel.class);
    }


}