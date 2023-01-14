package com.example.coin_panion;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.coin_panion.classes.general.Account;
import com.example.coin_panion.classes.general.User;
import com.example.coin_panion.classes.utility.BaseViewModel;
import com.example.coin_panion.classes.utility.Hashing;
import com.example.coin_panion.classes.utility.SendEmail;
import com.example.coin_panion.classes.utility.SendSMS;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

public class ForgotPassword extends Fragment {

    String otpNum;
    String phoneNumber;
    EditText ETUserInfo;
    EditText PTFirstDigit, PTSecondDigit, PTThirdDigit, PTFourthDigit;
    EditText ETNewPass, ETConfirmPass;
    ImageButton IBSubmitResetPassword;
    Button BtnResetPassword;
    BaseViewModel mainViewModel;
    Account account;
    User user;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_forgot_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainViewModel = new ViewModelProvider(requireActivity()).get(BaseViewModel.class);
        account = (Account) mainViewModel.get("account");
        user = (User) mainViewModel.get("user");

        ETUserInfo = view.findViewById(R.id.loginLayout);
        PTFirstDigit = view.findViewById(R.id.PTFirstDigit);
        PTSecondDigit = view.findViewById(R.id.PTSecondDigit);
        PTThirdDigit = view.findViewById(R.id.PTThirdDigit);
        PTFourthDigit = view.findViewById(R.id.PTFourthDigit);
        ETNewPass = view.findViewById(R.id.PTNewPass);
        ETConfirmPass = view.findViewById(R.id.PTConfirmPass);
        IBSubmitResetPassword = view.findViewById(R.id.IBSubmitResetPassword);
        BtnResetPassword = view.findViewById(R.id.BtnResetPassword);


        IBSubmitResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otpNum = SendSMS.generate4DigitOTP();
                String email = user.getEmail();
                new Thread(() -> {
                    SendEmail.sendEmail(email, otpNum);
                }).start();
            }
        });

        BtnResetPassword.setOnClickListener(v -> {
            if(compileOTP().equalsIgnoreCase(otpNum)){
                if(ETNewPass.getText().toString().equals(ETConfirmPass.getText().toString())){
                    String password = ETNewPass.getText().toString();
                    account.setPassword(Hashing.keccakHash(password));
                    Account.changePassword(account.getAccountID(), Hashing.keccakHash(password), new Thread());

                    Navigation.findNavController(v).navigate(R.id.privacyActivity);
                }
                else{
                    Toast.makeText(getContext(), "Password does not match", Toast.LENGTH_SHORT).show();
                }
            }
            else{
                Toast.makeText(getContext(), "OTP INCORRECT", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    private String compileOTP(){
        StringBuilder builder = new StringBuilder();
        builder.append(PTFirstDigit.getText().toString());
        builder.append(PTSecondDigit.getText().toString());
        builder.append(PTThirdDigit.getText().toString());
        builder.append(PTFourthDigit.getText().toString());

        return builder.toString();

    }
}