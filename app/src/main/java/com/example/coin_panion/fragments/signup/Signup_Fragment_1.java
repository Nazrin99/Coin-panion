package com.example.coin_panion.fragments.signup;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.coin_panion.R;
import com.example.coin_panion.classes.utility.Line;
import com.example.coin_panion.utility.Validifier;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Signup_Fragment_1 extends Fragment {
    private SignupViewModel signupViewModel;
    Button nextButton;
    EditText phoneNumberField;
    Thread dataThread;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_signup_1, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        signupViewModel = new ViewModelProvider(requireActivity()).get(SignupViewModel.class);

        nextButton = view.findViewById(R.id.signupNextButton);
        phoneNumberField = view.findViewById(R.id.signupPhoneNumberEditText);

        nextButton.setEnabled(false);

        phoneNumberField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                requireActivity().runOnUiThread(() -> {
                    if(!Validifier.isPhoneNumber(phoneNumberField.getText().toString())){
                        phoneNumberField.setError(getResources().getString(R.string.signup_phone_wrong_format));
                        nextButton.setEnabled(false);
                    }
                    else{
                        // Phone number is in valid format, check existence in database
                        phoneNumberField.setError(getResources().getString(R.string.checking));
                        if(phoneNumberExists()){
                            // Phone number already exists in database
                            phoneNumberField.setError(getResources().getString(R.string.signup_phone_already_exists));
                        }
                        else{
                            // Phone number is not in database, can proceed to next fragment for verification
                            phoneNumberField.setError(getResources().getString(R.string.signup_phone_is_available));
                            nextButton.setEnabled(true);
                        }
                    }
                });
            }
        });

        nextButton.setOnClickListener(v ->{
            // Phone number is in order, proceed to enter next info
            Navigation.findNavController(v).navigate(R.id.action_signup_Fragment_1_to_signup_Fragment_2);
        });
    }

    public boolean phoneNumberExists(){
        // Assuming phone number format is valid, we check database if phone number already exists
        final boolean[] phoneNumberExists = {false};
        dataThread = new Thread(() -> {
            try{
                Connection connection = Line.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM account WHERE phone_number=?");
                preparedStatement.setInt(1, Integer.parseInt(phoneNumberField.getText().toString()));
                ResultSet resultSet = preparedStatement.executeQuery();

                if(resultSet.next()){
                    // Phone number already exists
                    requireActivity().runOnUiThread(() -> {phoneNumberField.setError("Phone number already exists!");});
                }
                else{
                    // Phone number does not exist, verify phone number using OTP
                    phoneNumberExists[0] = false;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        while(dataThread.isAlive()){

        }
        return phoneNumberExists[0];
    }
}