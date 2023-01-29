package com.example.coin_panion;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.coin_panion.classes.utility.Hashing;
import com.example.coin_panion.classes.utility.Validifier;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ForgotPasswordFragment3#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ForgotPasswordFragment3 extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String userID;

    public ForgotPasswordFragment3() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ForgotPasswordFragment3.
     */
    // TODO: Rename and change types and number of parameters
    public static ForgotPasswordFragment3 newInstance(String param1, String param2) {
        ForgotPasswordFragment3 fragment = new ForgotPasswordFragment3();
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
            userID = getArguments().getString("userID");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forgot_password3, container, false);
    }

    TextInputLayout forgotPasswordNewLayout, forgotPasswordConfirmLayout;
    Button forgotPasswordResetButton;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        forgotPasswordConfirmLayout = view.findViewById(R.id.forgotPasswordConfirmLayout);
        forgotPasswordNewLayout = view.findViewById(R.id.forgotPasswordNewLayout);
        forgotPasswordResetButton = view.findViewById(R.id.forgotPasswordResetButton);

        forgotPasswordResetButton.setEnabled(false);

        forgotPasswordNewLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() <= 0){
                    requireActivity().runOnUiThread(() -> {
                        forgotPasswordNewLayout.setError(null);
                        forgotPasswordResetButton.setEnabled(false);
                    });
                }
                else{
                    if(Validifier.validPassword(forgotPasswordNewLayout.getEditText().getText().toString())){
                        if(forgotPasswordNewLayout.getEditText().getText().toString().equals(forgotPasswordConfirmLayout.getEditText().getText().toString())){
                            requireActivity().runOnUiThread(() -> {
                                forgotPasswordNewLayout.setError(null);
                                forgotPasswordResetButton.setEnabled(true);
                            });
                        }
                        else{
                            requireActivity().runOnUiThread(() -> {
                                forgotPasswordNewLayout.setError(null);
                            });
                        }
                    }
                }
            }
        });

        forgotPasswordConfirmLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() <= 0){
                    requireActivity().runOnUiThread(() -> {
                        forgotPasswordConfirmLayout.setError(null);
                        forgotPasswordResetButton.setEnabled(false);
                    });
                }
                else{
                    if(forgotPasswordConfirmLayout.getEditText().getText().toString().equals(forgotPasswordNewLayout.getEditText().getText().toString())){
                        if(Validifier.validPassword(forgotPasswordNewLayout.getEditText().getText().toString())){
                            requireActivity().runOnUiThread(() -> {
                                forgotPasswordConfirmLayout.setError(null);
                                forgotPasswordResetButton.setEnabled(true);
                            });
                        }
                        else{
                            requireActivity().runOnUiThread(() -> forgotPasswordConfirmLayout.setError(null));
                        }
                    }
                }
            }
        });

        forgotPasswordResetButton.setOnClickListener(v -> {
            Executors.newSingleThreadExecutor().execute(() -> {
                FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                System.out.println(userID);
                DocumentReference documentReference = firebaseFirestore.collection("user").document(userID);
                documentReference.update("password", Hashing.keccakHash(forgotPasswordConfirmLayout.getEditText().getText().toString())).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        System.out.println("Password has been reset");
                        Intent intent = new Intent(requireActivity(), LoginActivity.class);
                        startActivity(intent);
                    }
                });

            });
        });

    }
}