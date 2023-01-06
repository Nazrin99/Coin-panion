package com.example.coin_panion;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Terms_Privacy#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Terms_Privacy extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Terms_Privacy() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Terms_Privacy.
     */
    // TODO: Rename and change types and number of parameters
    public static Terms_Privacy newInstance(String param1, String param2) {
        Terms_Privacy fragment = new Terms_Privacy();
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

        /*Get and display the privacy policy of the app*/
        WebView webView = getView().findViewById(R.id.WVprivacyPolicy);
        webView.loadUrl("file:///assets/terms_and_privacy.html");

        /*Button to proceed to the login page*/
        Button btnToLogin = getView().findViewById(R.id.BtnToLogin);
        btnToLogin.setClickable(false);

        /*Check whether the check box is already check or not*/
        CheckBox checkBox = getView().findViewById(R.id.CBAgree);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    btnToLogin.setClickable(true);
                    Toast.makeText(getContext(),"Click on the Next Button to proceed with login",Toast.LENGTH_SHORT).show();
                    // User has agreed to the terms.
                    // TODO enable the next button or proceed with the app's login process.
                }
            }
        });

        /*Move on to the login page*/
        btnToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO go to login page after next button is clicked
                if(btnToLogin.isClickable()){
                    //TODO after button is clickable go to login page
                }else{
                    Toast.makeText(getContext(),"Please agree to the app privacy and policies to proceed",Toast.LENGTH_SHORT).show();
                }
            }
        });
        return inflater.inflate(R.layout.fragment_terms_privacy, container, false);
    }
}