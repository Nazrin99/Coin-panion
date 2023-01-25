package com.example.coin_panion;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.coin_panion.classes.general.Account;
import com.example.coin_panion.classes.general.User;
import com.example.coin_panion.classes.utility.BaseViewModel;
import com.example.coin_panion.classes.utility.Picture;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AccountFirstActivity extends Fragment {
    BottomNavigationView bottomNavigationView;
    Account account;
    BaseViewModel mainViewModel;
    ImageView profileImageView, profileUsernameNextImageView, profilePrivacyNextImageView, profileDebtNextImageView, profileDebtLimitNextImageView;

    private TextView textView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_account_first, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainViewModel = new ViewModelProvider(requireActivity()).get(BaseViewModel.class);

        account = (Account) mainViewModel.get("account");
        System.out.println(account);

        textView = view.findViewById(R.id.profileUsernameTextView);
        profileImageView = view.findViewById(R.id.profileImageView);
        profileDebtLimitNextImageView = view.findViewById(R.id.profileDebtLimitNextImageView);
        profileDebtNextImageView = view.findViewById(R.id.profileDebtNextImageView);
        profilePrivacyNextImageView = view.findViewById(R.id.profilePrivacyNextImageView);
        profileUsernameNextImageView = view.findViewById(R.id.profileUsernameNextImageView);

        requireActivity().runOnUiThread(() -> {
            profileImageView.setImageDrawable(account.getAccountPic().getPicture() != null ? Picture.cropToSquareAndRound(account.getAccountPic().getPicture(), getResources()) : Picture.cropToSquareAndRound(ResourcesCompat.getDrawable(getResources(), R.mipmap.default_profile, null), getResources()));
            profileImageView.setAdjustViewBounds(true);
            profileImageView.setMaxHeight(10);
            profileImageView.setMaxWidth(10);
            textView.setText(account.getUser().getUsername());
        });

        profileDebtLimitNextImageView.setOnClickListener(v -> {

        });

        profileDebtNextImageView.setOnClickListener(v -> {

        });

        profilePrivacyNextImageView.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.privacyActivity);
        });

        profileUsernameNextImageView.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.editProfileFragment);
        });


    }
}