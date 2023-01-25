package com.example.coin_panion;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.coin_panion.classes.utility.BaseViewModel;

public class PrivacyActivity extends Fragment {
    BaseViewModel mainViewModel;
    TextView textView;
    ImageView IVNextResetPassword, imageView5, imageView4;

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return super.getLifecycle();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_privacy, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textView = requireActivity().findViewById(R.id.activityName);
        textView.setText("PRIVACY");
        imageView4 = view.findViewById(R.id.imageView4);
        imageView5 = view.findViewById(R.id.imageView5);
        IVNextResetPassword = view.findViewById(R.id.IVNextResetPassword);

        imageView4.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.accountFirstActivity2);
            textView.setText("PROFILE");
        });

        imageView5.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.blockAccountFragment);
            textView.setText("BLOCKED ACCOUNT");
        });

        IVNextResetPassword.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.forgotPassword);
            textView.setText("RESET PASSWORD");
        });

    }
}