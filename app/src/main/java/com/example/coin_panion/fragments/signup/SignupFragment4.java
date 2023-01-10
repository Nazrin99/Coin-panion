package com.example.coin_panion.fragments.signup;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.coin_panion.R;
import com.example.coin_panion.classes.utility.BaseViewModel;
import com.example.coin_panion.classes.utility.Picture;
import com.google.android.material.textfield.TextInputLayout;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignupFragment4#newInstance} factory method to
 * create an instance of this fragment.
 */

@SuppressWarnings("deprecation")
public class SignupFragment4 extends Fragment {
    BaseViewModel signupViewModel;
    TextInputLayout usernameLayout, bioLayout;
    ImageView signupImageView;
    ImageButton signupUploadImageButton;
    AppCompatButton createProfileButton;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SignupFragment4() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SignupFragment_4.
     */
    // TODO: Rename and change types and number of parameters
    public static SignupFragment4 newInstance(String param1, String param2) {
        SignupFragment4 fragment = new SignupFragment4();
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
        return inflater.inflate(R.layout.fragment_signup_4, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        signupViewModel = new ViewModelProvider(requireActivity()).get(BaseViewModel.class);
        signupViewModel.put("picture", null);

        // Bind views
        bioLayout = view.findViewById(R.id.bioLayout);
        usernameLayout = view.findViewById(R.id.usernameLayout);
        signupImageView = view.findViewById(R.id.signupImageView);
        signupUploadImageButton = view.findViewById(R.id.signupUploadImageButton);
        createProfileButton = view.findViewById(R.id.createProfileButton);

        usernameLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() <= 0){
                    requireActivity().runOnUiThread(() -> usernameLayout.setError("Username can't be empty!"));
                }
                else{
                    requireActivity().runOnUiThread(() -> usernameLayout.setError(null));

                }
            }
        });

        bioLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() <= 0){
                    requireActivity().runOnUiThread(() -> bioLayout.setError("Bio can't be empty!"));
                }
                else{
                    requireActivity().runOnUiThread(() -> bioLayout.setError(null));
                }
            }
        });

        createProfileButton.setOnClickListener(v -> {
            if(bioLayout.getEditText().getText().toString().isEmpty()){
                requireActivity().runOnUiThread(() -> bioLayout.setError("Bio can't be empty!"));
                return;
            }
            if(usernameLayout.getEditText().getText().toString().isEmpty()){
                requireActivity().runOnUiThread(() -> usernameLayout.setError("Username can't be empty!"));
                return;
            }
            // Store username and bio and proceed to next fragment
            signupViewModel.put("username", usernameLayout.getEditText().getText().toString());
            signupViewModel.put("bio", bioLayout.getEditText().getText().toString());

            NavDirections navDirections = SignupFragment4Directions.actionSignupFragment4ToSignupFragment5();
            Navigation.findNavController(v).navigate(navDirections);
        });

        signupUploadImageButton.setOnClickListener(v -> imageChooser());
    }

    protected void imageChooser(){
        new Thread(() -> {
            Intent i = new Intent();
            i.setType("image/*");
            i.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(i, "Select Your Profile"), 1);
        }).start();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(data != null){
            Uri imageUri = data.getData();
            assert imageUri != null;

            Drawable drawable = Picture.constructDrawableFromUri(imageUri, getContext().getContentResolver());
            requireActivity().runOnUiThread(() -> {
                signupImageView.setImageDrawable(drawable);
                signupViewModel.put("picture", drawable);
            });
        }
    }
}