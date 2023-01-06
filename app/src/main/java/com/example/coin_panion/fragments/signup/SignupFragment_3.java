package com.example.coin_panion.fragments.signup;

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
import android.widget.EditText;
import android.widget.TextView;

import com.example.coin_panion.R;
import com.example.coin_panion.classes.utility.BaseViewModel;
import com.example.coin_panion.classes.utility.SendEmail;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignupFragment_3#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignupFragment_3 extends Fragment {
    TextView emailSentTextView;
    EditText emailCodeEditText;
    BaseViewModel signupViewModel;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SignupFragment_3() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SignupFragment_1.
     */
    // TODO: Rename and change types and number of parameters
    public static SignupFragment_3 newInstance(String param1, String param2) {
        SignupFragment_3 fragment = new SignupFragment_3();
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
        return inflater.inflate(R.layout.fragment_signup_3, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        emailCodeEditText = view.findViewById(R.id.emailCodeEditText);
        emailSentTextView = view.findViewById(R.id.emailSentTextView);

        signupViewModel = new ViewModelProvider(requireActivity()).get(BaseViewModel.class);

        //Send verification email
        String otp = signupViewModel.get("otp").toString();
        new Thread(new Runnable() {
            @Override
            public void run() {
                SendEmail.sendEmail(signupViewModel.get("email").toString(), otp);
            }
        }).start();

        emailSentTextView.setText(String.format(getResources().getString(R.string.email_sent_to), signupViewModel.get("email").toString()));
        emailCodeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(emailCodeEditText.getText().toString().equals(otp)){
                    // Verification code is correct, move to next fragment
                    NavDirections toProfileFragment = SignupFragment_3Directions.actionSignupFragment3ToSignupFragment4();
                    Navigation.findNavController(view).navigate(toProfileFragment);
                }
                else{
                    emailCodeEditText.setError(getResources().getString(R.string.wrong_code));
                }
            }
        });



    }
}