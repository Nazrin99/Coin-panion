package com.example.coin_panion;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.coin_panion.classes.utility.Validifier;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ForgotPasswordFragment1#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ForgotPasswordFragment1 extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ForgotPasswordFragment1() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ForgotPasswordFragment1.
     */
    // TODO: Rename and change types and number of parameters
    public static ForgotPasswordFragment1 newInstance(String param1, String param2) {
        ForgotPasswordFragment1 fragment = new ForgotPasswordFragment1();
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
        return inflater.inflate(R.layout.fragment_forgot_password1, container, false);
    }

    TextInputLayout forgotPasswordPhoneNumberLayout;
    Button forgotPasswordNextButton;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        forgotPasswordPhoneNumberLayout = view.findViewById(R.id.forgotPasswordPhoneNumberLayout);
        forgotPasswordNextButton = view.findViewById(R.id.forgotPasswordNextButton);
        requireActivity().runOnUiThread(() -> forgotPasswordNextButton.setEnabled(false));

        forgotPasswordPhoneNumberLayout.getEditText().addTextChangedListener(new TextWatcher() {
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
                        forgotPasswordPhoneNumberLayout.setError(null);
                        forgotPasswordNextButton.setEnabled(false);
                    });
                }
                else{
                    if(Validifier.isPhoneNumber(forgotPasswordPhoneNumberLayout.getEditText().getText().toString())){
                        requireActivity().runOnUiThread(() -> {
                            forgotPasswordNextButton.setEnabled(true);
                            forgotPasswordPhoneNumberLayout.setError(null);
                        });
                    }
                    else{
                        requireActivity().runOnUiThread(() -> {
                            forgotPasswordPhoneNumberLayout.setError("Wrong phone number format!");
                            forgotPasswordNextButton.setEnabled(false);
                        });
                    }
                }
            }
        });

        forgotPasswordNextButton.setOnClickListener(v -> {
            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
            Query query = firebaseFirestore.collection("user")
                    .whereEqualTo("phoneNumber", forgotPasswordPhoneNumberLayout.getEditText().getText().toString());
            query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if(queryDocumentSnapshots != null){
                        if(!queryDocumentSnapshots.isEmpty()){
                            Bundle bundle = new Bundle();
                            bundle.putString("phoneNumber", forgotPasswordPhoneNumberLayout.getEditText().getText().toString());
                            bundle.putString("email", queryDocumentSnapshots.getDocuments().get(0).getString("email"));
                            bundle.putString("userID", queryDocumentSnapshots.getDocuments().get(0).getReference().getId());
                            Navigation.findNavController(requireView()).navigate(R.id.forgotPasswordFragment2, bundle);
                        }
                        else{
                            Toast.makeText(requireContext(), "No account associated with this phone number", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        });
    }
}