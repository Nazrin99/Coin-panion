package com.example.coin_panion;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

public class ForgotPassword extends AppCompatActivity {

    int otpNum;
    String phoneNumber;
    EditText ETUserInfo;
    EditText PTFirstDigit, PTSecondDigit, PTThirdDigit, PTFourthDigit;
    EditText ETNewPass, ETConfirmPass;
    ImageButton IBSubmitResetPassword;
    Button BtnResetPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
    }

    public void UIElement(){
        ETUserInfo = findViewById(R.id.ETUserInfo);
        PTFirstDigit = findViewById(R.id.PTFirstDigit);
        PTSecondDigit = findViewById(R.id.PTSecondDigit);
        PTThirdDigit = findViewById(R.id.PTThirdDigit);
        PTFourthDigit = findViewById(R.id.PTFourthDigit);
        ETNewPass = findViewById(R.id.PTNewPass);
        ETConfirmPass = findViewById(R.id.PTConfirmPass);
        IBSubmitResetPassword = findViewById(R.id.IBSubmitResetPassword);
        BtnResetPassword = findViewById(R.id.BtnResetPassword);
    }

    public void setupListener(){
        IBSubmitResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyForOtpRequest(v);
            }
        });

        BtnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO validate user existing password with new password
                if(verifyOTP() && verifyConfirmPassword()){
                    successfulVerification();
                }
            }
        });


    }
    // TODO determine which either email or phone number to reset password
    public void verifyForOtpRequest(View view) {
        // Check if it a phone or email
        // check whether it is valid and exist in database
        if (isEmpty(ETUserInfo)) {
            ETUserInfo.setError("Please Fill in the required credentials");
            return;
        }
        //check if phone
        if (isPhoneNumber(ETUserInfo)) {
            this.phoneNumber = ETUserInfo.getText().toString();
            getOTPNO(view);
            initialOTP();
        }
    }

    public void initialOTP(){
        try {
            // Construct data
            String apiKey = "api key=" + "Replace with API-KEY";

            Random rand = new Random();
            otpNum = rand.nextInt(9999) + 1;
            String message = "&message=" + "This is your otp number" + otpNum;
            String sender = "&message=" + "sendername";
            String numbers = "&phoneNumber=" + phoneNumber;

            // Send data required
            HttpURLConnection conn = (HttpURLConnection) new URL("https://api.textlocal.in/send/?").openConnection();
            String data = apiKey + numbers + message + sender;
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Length", Integer.toString(data.length()));
            conn.getOutputStream().write(data.getBytes("UTF-8"));
            final BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            final StringBuffer stringBuffer = new StringBuffer();
            String line;
            while ((line = rd.readLine()) != null) {
                stringBuffer.append(line);
            }
            rd.close();
        }catch (Exception e){
            System.out.println("Error SMS" + e);
        }
    }

    public void getOTPNO(View view){
        //TODO initial OTP
        Toast.makeText(ForgotPassword.this,"OTP will be sent to you",Toast.LENGTH_SHORT).show();
        IBSubmitResetPassword.setVisibility(View.GONE);
    }

    public boolean verifyOTP(){
        // after the verify button is pressed
        Toast.makeText(ForgotPassword.this, "Verifiying...",Toast.LENGTH_LONG).show();
        String inputOTP = PTFirstDigit.getText().toString() + PTSecondDigit.getText().toString() + PTThirdDigit.getText().toString() + PTFourthDigit.getText().toString();
        if(inputOTP.equals(String.valueOf(otpNum))){
           return true;
        }
        else{
            Toast.makeText(ForgotPassword.this, "Invalid OTP, Please try again",Toast.LENGTH_LONG).show();
            IBSubmitResetPassword.setVisibility(View.GONE);
            return false;
        }
    }

    public boolean verifyConfirmPassword(){
        if(ETNewPass.getText().toString().equals(ETConfirmPass.getText().toString())){
            return true;
        }else{
            ETConfirmPass.setError("Password did not matches");
            return false;
        }
    }

    public void successfulVerification(){
        Toast.makeText(ForgotPassword.this,"Password changed successfully",Toast.LENGTH_LONG).show();
        this.finish();
        // Navigate to login page after password has been reset successfully
        Intent toLoginInt = new Intent(ForgotPassword.this, Login.class);
        startActivity(toLoginInt);
    }

    // Check if the editText is empty
    boolean isEmpty(EditText text){
        CharSequence string = text.getText().toString();
        return TextUtils.isEmpty(string);
    }

    // Check if it was a valid email
    boolean isEmail(EditText text){
        CharSequence email = text.getText().toString();
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }

    // Check if it was a valid phone Number
    boolean isPhoneNumber(EditText text){
        CharSequence phoneNumber = text.getText().toString();
        return (!TextUtils.isEmpty(phoneNumber) && Patterns.PHONE.matcher(phoneNumber).matches());
    }
}