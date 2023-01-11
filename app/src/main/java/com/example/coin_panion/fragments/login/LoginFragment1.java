package com.example.coin_panion.fragments.login;

import static com.example.coin_panion.classes.utility.Validifier.isEmail;
import static com.example.coin_panion.classes.utility.Validifier.isPhoneNumber;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.coin_panion.FriendsActivity;
import com.example.coin_panion.R;
import com.example.coin_panion.SignupActivity;
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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment1#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment1 extends Fragment {
    TextInputLayout loginLayout, passwordLayout;
    Button BtnLogin;
    Button BtnSignUp;
    Thread dataThread;
    Bundle bundle = new Bundle();
    BaseViewModel loginViewModel;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LoginFragment1() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment1.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment1 newInstance(String param1, String param2) {
        LoginFragment1 fragment = new LoginFragment1();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login1, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loginViewModel = new ViewModelProvider(requireActivity()).get(BaseViewModel.class);

        callUI(view);
        setupUIListeners();

        dataThread = new Thread(() -> {});
    }

    public void callUI(View view){
        loginLayout = view.findViewById(R.id.loginLayout);
        passwordLayout = view.findViewById(R.id.passwordLayout);
        BtnLogin = view.findViewById(R.id.loginButton);
        BtnSignUp = view.findViewById(R.id.signupButton);
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
                Intent intent = new Intent(requireContext(), SignupActivity.class);
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
                    requireActivity().runOnUiThread(() -> loginLayout.setError("Credential cannot be empty!"));
                }
                else{
                    requireActivity().runOnUiThread(() -> loginLayout.setError(null));
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
                    requireActivity().runOnUiThread(() -> loginLayout.setError(null));
                }
                if(s.length() <= 0){
                    requireActivity().runOnUiThread(() -> passwordLayout.setError("Password field cannot be empty!"));
                }
                else{
                    requireActivity().runOnUiThread(() -> passwordLayout.setError(null));
                }
            }
        });
    }

    public void validateLogin() {

        String userLogin = loginLayout.getEditText().getText().toString();
        String userPass = passwordLayout.getEditText().getText().toString();

        if(userLogin.isEmpty()){
            requireActivity().runOnUiThread(() -> loginLayout.setError("Credential cannot be empty!"));
            return;
        }
        if(userPass.isEmpty()){
            requireActivity().runOnUiThread(() -> passwordLayout.setError("Password field cannot be empty!"));
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
            requireActivity().runOnUiThread(() -> loginLayout.setError("Make sure your phone number is in the following format (eg +60179916645)"));
            return;
        }
        loginViewModel.put("data", data);
        loginViewModel.put("password", passwordLayout.getEditText().getText().toString());

        // Switch fragment
        NavDirections navDirections = LoginFragment1Directions.actionLoginFragment1ToLoginFragment2();
        Navigation.findNavController(requireView()).navigate(navDirections);
    }
}