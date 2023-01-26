package com.example.coin_panion;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.coin_panion.classes.general.Account;
import com.example.coin_panion.classes.utility.BaseViewModel;
import com.example.coin_panion.classes.utility.Picture;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AccountFirstActivity extends Fragment {
    BottomNavigationView bottomNavigationView;
    Account account;
    BaseViewModel mainViewModel;
    ImageView profileImageView, profileUsernameNextImageView, profilePrivacyNextImageView, profileDebtNextImageView, profileCreditNextImageView;

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
        profileCreditNextImageView = view.findViewById(R.id.profileCreditNextImageView);
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

        profileCreditNextImageView.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.creditList);
        });

        profileDebtNextImageView.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.debtList);
        });

        profilePrivacyNextImageView.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.privacyActivity);
        });

        profileUsernameNextImageView.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.editProfileFragment);
        });


    }
}