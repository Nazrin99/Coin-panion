package com.example.coin_panion.fragments.signup;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLOutput;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignupFragment1#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignupFragment1 extends Fragment {
    private BaseViewModel signupViewModel;
    Button nextButton;
    TextInputEditText phoneNumberField;
    Thread dataThread = new Thread();
    TextInputLayout layout;
    Handler handler = new Handler();
    long delay = 2000; // 2 seconds after user stops typing
    long last_text_edit = 0;
    ColorStateList RED;
    ColorStateList DARK_BLUE;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SignupFragment1() {
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
    public static SignupFragment1 newInstance(String param1, String param2) {
        SignupFragment1 fragment = new SignupFragment1();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

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

        ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.dark_blue));
        ColorStateList.valueOf(Color.RED);

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

    private void phoneNumberExists(){
        // Assuming phone number format is valid, we check database if phone number already exists
        String phoneNumber = phoneNumberField.getText().toString();
        Query query = FirebaseFirestore.getInstance().collection("user").whereEqualTo("phoneNumber", phoneNumber);
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots != null){
                    if(!queryDocumentSnapshots.isEmpty()){
                        requireActivity().runOnUiThread(() -> {
                            layout.setError(getResources().getString(R.string.signup_phone_already_exists));
                            layout.setErrorIconDrawable(null);
                        });
                    }
                    else{
                        requireActivity().runOnUiThread(() -> {
                            layout.setError(getResources().getString(R.string.signup_phone_is_available));
                            layout.setErrorIconDrawable(0);
                            layout.setBoxStrokeErrorColor(ColorStateList.valueOf(getResources().getColor(R.color.dark_blue)));
                            layout.setErrorTextColor(ColorStateList.valueOf(getResources().getColor(R.color.dark_blue)));
                            layout.setHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.dark_blue)));
                            nextButton.setEnabled(true);
                        });
                    }
                }
                else{
                    System.out.println("Document is null");
                }
            }
        });
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
                phoneNumberExists();
                requireActivity().runOnUiThread(() -> {
                    InputMethodManager imm = (InputMethodManager) requireParentFragment().requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(requireView().getApplicationWindowToken(), 0);
                });
            }
        }
    };

}