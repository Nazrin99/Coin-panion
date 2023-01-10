package com.example.coin_panion.fragments.signup;

import android.content.Context;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.example.coin_panion.R;
import com.example.coin_panion.classes.utility.BaseViewModel;
import com.example.coin_panion.classes.utility.Line;
import com.example.coin_panion.classes.utility.SendSMS;
import com.example.coin_panion.classes.utility.ThreadStatic;
import com.example.coin_panion.classes.utility.Validifier;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignupFragment2#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignupFragment2 extends Fragment {
    private BaseViewModel signupViewModel;
    TextInputEditText firstName, lastName, email, password;
    TextInputLayout firstNameLayout, lastNameLayout, emailLayout, passwordLayout;
    Button nextButton;
    Thread dataThread;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SignupFragment2() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Signup_Fragment_2.
     */
    // TODO: Rename and change types and number of parameters
    public static SignupFragment2 newInstance(String param1, String param2) {
        SignupFragment2 fragment = new SignupFragment2();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        dataThread = new Thread();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_signup_2, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        signupViewModel = new ViewModelProvider(requireActivity()).get(BaseViewModel.class);

        // Binding views
        firstName = view.findViewById(R.id.signupFirstNameEditText);
        lastName = view.findViewById(R.id.signupLastNameEditText);
        email = view.findViewById(R.id.signupEmailEditText);
        password = view.findViewById(R.id.signupPasswordEditText);
        nextButton = view.findViewById(R.id.signupNextButton2);
        firstNameLayout = view.findViewById(R.id.signupFirstNameLayout);
        lastNameLayout = view.findViewById(R.id.signupLastNameLayout);
        emailLayout = view.findViewById(R.id.signupEmailLayout);
        passwordLayout = view.findViewById(R.id.signupPasswordLayout);

        requireActivity().runOnUiThread(() ->{nextButton.setEnabled(false);});

        // Set listeners for EditText
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!Validifier.isEmail(email.getText().toString())){
                    requireActivity().runOnUiThread(() -> {
                        emailLayout.setError(getResources().getString(R.string.email_format_error));
                        nextButton.setEnabled(false);
                    });
                }
                else{
                    requireActivity().runOnUiThread(() -> {emailLayout.setError(null);});
                    // Email format is correct, search email availability in database
                }
            }
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                requireActivity().runOnUiThread(() -> {
                    if(!Validifier.validPassword(password.getText().toString())){
                        // Password format is invalid
                        passwordLayout.setError(getResources().getString(R.string.password_hint));
                        nextButton.setEnabled(false);
                    }
                    else{
                        passwordLayout.setError(null);
                        // Password format is valid
                        if(Validifier.isEmail(email.getText().toString())){
                            nextButton.setEnabled(true);
                        }
                        else{
                            nextButton.setEnabled(false);
                        }
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() <= 0 ){
                    passwordLayout.setError(null);
                }
            }
        });

        nextButton.setOnClickListener(v -> {
            // Verify email existence
            requireActivity().runOnUiThread(() -> {
                InputMethodManager imm = (InputMethodManager) requireParentFragment().requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(requireView().getApplicationWindowToken(), 0);
            });
            AtomicBoolean emailExist = new AtomicBoolean(false);
            dataThread = new Thread(() -> {
                try{
                    Connection connection = Objects.requireNonNull(Line.getConnection());
                    PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM user WHERE email = ?");
                    preparedStatement.setString(1, email.getText().toString());
                    ResultSet resultSet = preparedStatement.executeQuery();

                    if(resultSet.next()){
                        // Email exists, throw error on EditText
                        emailExist.set(true);
                    }
                    else{
                        // Email does not exists, can proceed as normal
                        emailExist.set(false);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
            ThreadStatic.run(dataThread);
            if(emailExist.get()){
                requireActivity().runOnUiThread(() -> {
                    email.setError(getResources().getString(R.string.email_exists));
                    email.requestFocus();
                    email.getText().clear();
                    InputMethodManager imm = (InputMethodManager) requireParentFragment().requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(requireView(), 0);
                    nextButton.setEnabled(false);
                });
            }
            else{
                if(Validifier.validPassword(password.getText().toString())){
                    // All data is correct and in order, store into model
                    signupViewModel.put("firstName", firstName.getText().toString());
                    signupViewModel.put("lastName", lastName.getText().toString());
                    signupViewModel.put("email", email.getText().toString());
                    signupViewModel.put("password", password.getText().toString());
                    signupViewModel.put("otp", SendSMS.generateOTP());

                    // Proceed to email verification
                    Navigation.findNavController(v).navigate(R.id.action_signup_Fragment_2_to_signupFragment_3);
                }
                else{
                    nextButton.setEnabled(false);
                }
            }
        });
    }
}