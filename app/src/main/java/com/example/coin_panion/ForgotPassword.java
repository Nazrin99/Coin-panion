package com.example.coin_panion;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.coin_panion.classes.general.Account;
import com.example.coin_panion.classes.utility.BaseViewModel;
import com.example.coin_panion.classes.utility.Hashing;
import com.example.coin_panion.classes.utility.SendEmail;
import com.example.coin_panion.classes.utility.SendSMS;
import com.example.coin_panion.classes.utility.Validifier;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firestore.v1.WriteResult;

import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class ForgotPassword extends Fragment {

    String otpNum;
    EditText ETUserInfo;
    EditText[] codeEditTexts = new EditText[4];
    EditText ETNewPass, ETConfirmPass;
    ImageButton IBSubmitResetPassword;
    Button BtnResetPassword;
    BaseViewModel mainViewModel;
    Account account;
    ImageView forgotPasswordBackImageView;

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

        TextView textView = requireActivity().findViewById(R.id.activityName);
        forgotPasswordBackImageView = view.findViewById(R.id.forgotPasswordBackImageView);
        ETUserInfo = view.findViewById(R.id.loginLayout);
        codeEditTexts[0] = view.findViewById(R.id.PTFirstDigit);
        codeEditTexts[1] = view.findViewById(R.id.PTSecondDigit);
        codeEditTexts[2] = view.findViewById(R.id.PTThirdDigit);
        codeEditTexts[3] = view.findViewById(R.id.PTFourthDigit);
        ETNewPass = view.findViewById(R.id.PTNewPass);
        ETConfirmPass = view.findViewById(R.id.PTConfirmPass);
        IBSubmitResetPassword = view.findViewById(R.id.IBSubmitResetPassword);
        BtnResetPassword = view.findViewById(R.id.BtnResetPassword);

        for(int i = 0 ; i < codeEditTexts.length; i++){
            AtomicInteger value = new AtomicInteger(i);
            codeEditTexts[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if(value.get() >= codeEditTexts.length-1){
                        codeEditTexts[value.get()].clearFocus();
                        InputMethodManager imm = (InputMethodManager) requireParentFragment().requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(requireView().getApplicationWindowToken(), 0);
                    }
                    else{
                        requireActivity().runOnUiThread(() -> {
                            codeEditTexts[value.get()+1].getText().clear();
                            codeEditTexts[value.get()+1].requestFocus();
                        });
                    }
                }
            });
        }

        IBSubmitResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otpNum = SendSMS.generate4DigitOTP();
                String email = account.getUser().getEmail();
                new Thread(() -> {
                    SendEmail.sendEmail(email, otpNum);
                }).start();
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(v.getContext(), "Verification code has been send to " + email, Toast.LENGTH_SHORT).show();
                });
            }
        });

        BtnResetPassword.setOnClickListener(v -> {
            if(compileOTP().equals(otpNum)){
                String newPassword = ETNewPass.getText().toString();
                String confirmPassword = ETConfirmPass.getText().toString();
                if(Validifier.validPassword(newPassword)){
                    if(newPassword.equals(confirmPassword)){
                        System.out.println("Password changed");
                        String password = ETNewPass.getText().toString();
                        String hashedPassword = Hashing.keccakHash(password);
                        System.out.println(hashedPassword);
                        account.getUser().setPassword(hashedPassword);

                        // Insert changed password into database
                        Executors.newSingleThreadExecutor().execute(() -> {
                            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                            DocumentReference documentReference = firebaseFirestore.collection("user").document(account.getUser().getUserID());
                            documentReference.update("password", hashedPassword).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    System.out.println("Password changed successfully");
                                }
                            });
                        });

                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(v.getContext(), "Password changed successfully", Toast.LENGTH_SHORT).show();
                            Navigation.findNavController(v).navigate(R.id.privacyActivity);
                            textView.setText("PRIVACY");
                        });
                    }
                    else{
                        Toast.makeText(v.getContext(), "Password does not match", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(v.getContext(), "Password format is incorrect", Toast.LENGTH_SHORT).show();
                }
            }
            else{
                Toast.makeText(getContext(), "OTP INCORRECT", Toast.LENGTH_SHORT).show();
            }
        });

        forgotPasswordBackImageView.setOnClickListener(v -> {
            requireActivity().runOnUiThread(() -> {
                Navigation.findNavController(v).navigate(R.id.privacyActivity);
                textView.setText("PRIVACY");
            });
        });
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    private String compileOTP(){
        StringBuilder builder = new StringBuilder();
        for(int i = 0 ; i < codeEditTexts.length; i++){
            builder.append(codeEditTexts[i].getText().toString());
        }

        return builder.toString();

    }
}