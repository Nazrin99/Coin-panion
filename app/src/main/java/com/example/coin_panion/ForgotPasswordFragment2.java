package com.example.coin_panion;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coin_panion.classes.utility.SendEmail;
import com.example.coin_panion.classes.utility.SendSMS;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ForgotPasswordFragment2#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ForgotPasswordFragment2 extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String phoneNumber;
    private String email;
    private String userID;

    public ForgotPasswordFragment2() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ForgotPasswordFragment2.
     */
    // TODO: Rename and change types and number of parameters
    public static ForgotPasswordFragment2 newInstance(String param1, String param2) {
        ForgotPasswordFragment2 fragment = new ForgotPasswordFragment2();
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
            phoneNumber = getArguments().getString("phoneNumber");
            email = getArguments().getString("email");
            userID = getArguments().getString("userID");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_signup_3, container, false);
    }

    TextView emailSentTextView;
    EditText[] codeEditTexts = new EditText[6];
    int[] editTextIDs = {
            R.id.codeEditText1,
            R.id.codeEditText2,
            R.id.codeEditText3,
            R.id.codeEditText4,
            R.id.codeEditText5,
            R.id.codeEditText6
    };
    String otp;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        otp = SendSMS.generateOTP();

        new Thread(new Runnable() {
            @Override
            public void run() {
                SendEmail.sendEmail(email, otp);
            }
        }).start();

        for(int i = 0; i < codeEditTexts.length; i++){
            codeEditTexts[i] = view.findViewById(editTextIDs[i]);
        }
        emailSentTextView = view.findViewById(R.id.emailSentTextView);

        requireActivity().runOnUiThread(() -> emailSentTextView.setText(String.format(getResources().getString(R.string.email_sent_to), email)));

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
                        codeEditTexts[value.get()].clearFocus();
                        InputMethodManager imm = (InputMethodManager) requireParentFragment().requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(requireView().getApplicationWindowToken(), 0);

                        if(verifyCode(otp)){
                            // Code is verified proceed to next fragment
                            Bundle bundle = new Bundle();
                            bundle.putString("userID", userID);
                            Navigation.findNavController(requireView()).navigate(R.id.forgotPasswordFragment3, bundle);
                        }
                        else{
                            // Display popup, wrong code entered
                            Toast.makeText(requireContext(), "Verification code incorrect", Toast.LENGTH_SHORT).show();
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

        // Bind editor listeners
        for(int i = 0; i < editTextIDs.length; i++){
            codeEditTexts[i].setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if(actionId == EditorInfo.IME_ACTION_DONE){
                        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        return true;
                    }
                    return false;
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
        return otp.equalsIgnoreCase(compileCode());
    }
}