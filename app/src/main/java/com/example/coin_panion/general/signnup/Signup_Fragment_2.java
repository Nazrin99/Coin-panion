package com.example.coin_panion.general.signnup;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.coin_panion.R;
import com.example.coin_panion.utility.Line;
import com.example.coin_panion.utility.Validifier;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Signup_Fragment_2#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Signup_Fragment_2 extends Fragment {
    private SignupViewModel signupViewModel;
    EditText firstName, lastName, email, password;
    Button nextButton;
    Thread dataThread;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Signup_Fragment_2() {
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
    public static Signup_Fragment_2 newInstance(String param1, String param2) {
        Signup_Fragment_2 fragment = new Signup_Fragment_2();
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

        signupViewModel = new ViewModelProvider(requireActivity()).get(SignupViewModel.class);

        firstName = view.findViewById(R.id.signupFirstNameEditText);
        lastName = view.findViewById(R.id.signupLastNameEditText);
        email = view.findViewById(R.id.signupEmailEditText);
        password = view.findViewById(R.id.signupPasswordEditText);
        nextButton = view.findViewById(R.id.signupNextButton2);

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
                        email.setError(getResources().getString(R.string.email_format_error));
                        nextButton.setEnabled(false);
                    });
                }
                else{
                    requireActivity().runOnUiThread(() -> {email.setError(getResources().getString(R.string.checking));});
                    // Email format is correct, search email availability in database
                    dataThread = new Thread(() -> {
                        try{
                            Connection connection = Line.getConnection();
                            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM account WHERE email = ?");
                            preparedStatement.setString(1, email.getText().toString());
                            ResultSet resultSet = preparedStatement.executeQuery();

                            if(resultSet.next()){
                                // Email exists, throw error on EditText
                                email.setError(getResources().getString(R.string.email_exists));
                            }
                            else{
                                // Email does not exists, can proceed as normal
                                email.setError("");
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    });
                    dataThread.start();
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
                        password.setError(getResources().getString(R.string.password_hint));
                    }
                    else{
                        password.setError("");
                        // Password format is valid
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        nextButton.setOnClickListener(v -> {
            // All data is correct and in order, proceed to set profile picture
        });


    }
}