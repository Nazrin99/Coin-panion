package com.example.coin_panion.fragments.signup;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

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
import android.widget.TextView;

import com.example.coin_panion.R;
import com.example.coin_panion.classes.utility.BaseViewModel;
import com.example.coin_panion.classes.utility.Line;
import com.example.coin_panion.classes.utility.ThreadStatic;
import com.example.coin_panion.classes.utility.Validifier;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLOutput;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class SignupFragment1 extends Fragment {
    private BaseViewModel signupViewModel;
    Button nextButton;
    TextInputEditText phoneNumberField;
    Thread dataThread;
    TextInputLayout layout;

    long delay = 2000; // 1 seconds after user stops typing
    long last_text_edit = 0;
    Handler handler = new Handler();
    ColorStateList RED = ColorStateList.valueOf(Color.RED);
    ColorStateList DARK_BLUE = ColorStateList.valueOf(getResources().getColor(R.color.dark_blue));


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
        signupViewModel = new ViewModelProvider(requireActivity()).get(BaseViewModel.class);

        nextButton = view.findViewById(R.id.signupNextButton);
        phoneNumberField = view.findViewById(R.id.signupPhoneNumberEditText);
        layout = view.findViewById(R.id.phoneNumberLayout);

        requireActivity().runOnUiThread(() -> {
            nextButton.setEnabled(false);
            layout.setHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.dark_blue)));
            layout.setBoxStrokeColor(getResources().getColor(R.color.dark_blue));
        });

        Drawable errorIcon = layout.getErrorIconDrawable();
        AtomicReference<Drawable> atomicIcon = new AtomicReference<>(errorIcon);

        phoneNumberField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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

        phoneNumberField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeCallbacks(input_finish_checker);
                handler.removeCallbacks(closeKeyboard);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(Objects.requireNonNull(phoneNumberField.getText()).toString().isEmpty()){
                    requireActivity().runOnUiThread(() -> {
                        layout.setError(null);
                        layout.setHintTextColor(DARK_BLUE);
                    });
                }
                else if(!Validifier.isPhoneNumber(phoneNumberField.getText().toString())){
                    requireActivity().runOnUiThread(() -> {
                        layout.setBoxStrokeErrorColor(RED);
                        layout.setErrorTextColor(RED);
                        layout.setHintTextColor(RED);
                        layout.setError(getResources().getString(R.string.signup_phone_wrong_format));
                        layout.setErrorIconDrawable(errorIcon);
                        nextButton.setEnabled(false);
                    });

                }
                else{
                    // Phone number is in valid format, check existence in database
                    last_text_edit = System.currentTimeMillis();
                    handler.postDelayed(closeKeyboard, 1000);
                    handler.postDelayed(input_finish_checker, delay);
                }
            }
        });

        nextButton.setOnClickListener(v ->{
            // Phone number is in order, proceed to enter next info
            signupViewModel.put("phoneNumber", Objects.requireNonNull(phoneNumberField.getText()).toString());
            NavDirections navDirections = SignupFragment1Directions.actionSignupFragment1ToSignupFragment2();
            Navigation.findNavController(v).navigate(navDirections);
        });
    }

    private boolean phoneNumberExists(){
        // Assuming phone number format is valid, we check database if phone number already exists
        AtomicReference<Boolean> phoneNumberExists = new AtomicReference<>();
        dataThread = new Thread(() -> {
            try{
                String string = phoneNumberField.getText().toString().substring(1);
                Long phoneNumber = Long.parseLong(string);
                System.out.println(phoneNumber);
                Connection connection = Line.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM user WHERE phone_number=?");
                preparedStatement.setLong(1, phoneNumber);
                ResultSet resultSet = preparedStatement.executeQuery();

                if(resultSet.next()){
                    // Phone number already exists
                    phoneNumberExists.set(true);
                }
                else{
                    // Phone number does not exist, proceed to next fragment
                    phoneNumberExists.set(false);
                }
                resultSet.close();
                preparedStatement.close();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        ThreadStatic.run(dataThread);
        return phoneNumberExists.get();
    }

    private final Runnable closeKeyboard = new Runnable() {
        @Override
        public void run() {
            InputMethodManager imm = (InputMethodManager) requireView().getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(requireView().getApplicationWindowToken(), 0);
            requireActivity().runOnUiThread(() -> {
                layout.setError(getResources().getString(R.string.checking));
            });
        }
    };

    private final Runnable input_finish_checker = new Runnable() {
        public void run() {
            if (System.currentTimeMillis() > (last_text_edit + delay - 500)) {
                boolean exists = phoneNumberExists();
                if(exists){
                    // Phone number already exists in database
                    requireActivity().runOnUiThread(() -> {
                        layout.setError(getResources().getString(R.string.signup_phone_already_exists));
                        layout.setErrorIconDrawable(null);
                    });
                }
                else{
                    // Phone number is not in database, can proceed to next fragment for verification
                    requireActivity().runOnUiThread(() -> {
                        layout.setError(getResources().getString(R.string.signup_phone_is_available));
                        layout.setErrorIconDrawable(0);
                        layout.setBoxStrokeErrorColor(ColorStateList.valueOf(getResources().getColor(R.color.dark_blue)));
                        layout.setErrorTextColor(ColorStateList.valueOf(getResources().getColor(R.color.dark_blue)));
                        layout.setHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.dark_blue)));
                        nextButton.setEnabled(true);
                    });
                }
                requireActivity().runOnUiThread(() -> {
                    InputMethodManager imm = (InputMethodManager) requireParentFragment().requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(requireView().getApplicationWindowToken(), 0);
                });
            }
        }
    };

}