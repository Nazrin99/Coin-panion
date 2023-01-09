package com.example.coin_panion.fragments.signup;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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

import java.util.concurrent.atomic.AtomicInteger;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignupFragment3#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignupFragment3 extends Fragment {
    AlertDialog.Builder dialogBuilder;
    AlertDialog dialog;
    TextView emailSentTextView, popupTextView;
    EditText[] codeEditTexts = new EditText[6];
    Button popupOkButton;
    int[] editTextIDs = {
            R.id.codeEditText1,
            R.id.codeEditText2,
            R.id.codeEditText3,
            R.id.codeEditText4,
            R.id.codeEditText5,
            R.id.codeEditText6
    };
    BaseViewModel signupViewModel;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SignupFragment3() {
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
    public static SignupFragment3 newInstance(String param1, String param2) {
        SignupFragment3 fragment = new SignupFragment3();
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

        signupViewModel = new ViewModelProvider(requireActivity()).get(BaseViewModel.class);

        //Send verification email
        String otp = signupViewModel.get("otp").toString();
        new Thread(new Runnable() {
            @Override
            public void run() {
                SendEmail.sendEmail(signupViewModel.get("email").toString(), otp);
            }
        }).start();

        // Bind views
        for(int i = 0; i < codeEditTexts.length; i++){
            codeEditTexts[i] = view.findViewById(editTextIDs[i]);
        }
        emailSentTextView = view.findViewById(R.id.emailSentTextView);

        requireActivity().runOnUiThread(() -> emailSentTextView.setText(String.format(getResources().getString(R.string.email_sent_to), signupViewModel.get("email").toString())));

        // Bind listeners up until before the last edit text
        for(int i = 0 ; i < editTextIDs.length; i++){
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
                    if(value.get() >= editTextIDs.length-1){
                        System.out.println("Block entered");
                        codeEditTexts[value.get()].clearFocus();

                        if(verifyCode(compileCode())){
                            // Code is verified proceed to next fragment
                        }
                        else{
                            // Display popup, wrong code entered
                            System.out.println("Wrong code");
                            createDialog();
                        }
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

        // Set first edittext to be focused by default
        codeEditTexts[0].setFocusedByDefault(true);
    }

    private String compileCode(){
        StringBuilder stringBuilder = new StringBuilder();
        for (EditText codeEditText : codeEditTexts) {
            stringBuilder.append(codeEditText.getText().toString().trim());
        }
        return stringBuilder.toString();
    }

    private boolean verifyCode(String otp){
        return otp.equals(compileCode());
    }

    private void createDialog(){
        dialogBuilder = new AlertDialog.Builder(requireActivity());
        final View popup = getLayoutInflater().inflate(R.layout.wrong_code_popup, null);
        popupTextView = popup.findViewById(R.id.popupTextView);
        popupOkButton = popup.findViewById(R.id.popupOkButton);

        dialogBuilder.setView(popup);
        dialog = dialogBuilder.create();
        dialog.show();

        popupOkButton.setOnClickListener(v -> {
            dialog.dismiss();
        });
    }
}