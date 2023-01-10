package com.example.coin_panion.fragments.signup;

import android.app.Dialog;
import android.content.Context;
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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.coin_panion.R;
import com.example.coin_panion.classes.utility.BaseViewModel;
import com.example.coin_panion.classes.utility.SendEmail;

import java.util.Objects;
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
                        codeEditTexts[value.get()].clearFocus();
                        InputMethodManager imm = (InputMethodManager) requireParentFragment().requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(requireView().getApplicationWindowToken(), 0);

                        if(verifyCode(signupViewModel.get("otp").toString())){
                            // Code is verified proceed to next fragment
                            Navigation.findNavController(requireView()).navigate(R.id.action_signupFragment_3_to_signupFragment_4);
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

    private void createDialog(){
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
        fadeIn.setDuration(1000);

        Dialog dialog1 = new Dialog(requireContext());
        dialog1.getLayoutInflater().inflate(R.layout.wrong_code_popup, null);

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