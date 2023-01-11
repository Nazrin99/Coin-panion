package com.example.coin_panion;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.example.coin_panion.classes.general.Account;
import com.example.coin_panion.fragments.friends.FriendsDefaultFragment;
import com.example.coin_panion.fragments.friends.FriendsDefaultFragmentDirections;

public class FriendsActivity extends AppCompatActivity {
    Toolbar toolbar;
    TextView textView;
    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        checkPermission();

        toolbar = findViewById(R.id.friendActivityAppBar);
        textView = findViewById(R.id.activity_name);
        textView.setText("Friends");

        bundle = getIntent().getExtras();
        Account account = (Account) bundle.getParcelable("account");
        if(account.getFriends().size() == 0){
            // User has no friends, display default screen
        }
        else{
            // User has friends, display friends screen
            NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.navHostFragment);
            NavController navController = navHostFragment.getNavController();

            NavDirections navDirections = FriendsDefaultFragmentDirections.actionFriendsDefaultFragmentToFriendsFragment();
            navController.navigate(navDirections);

        }

    }

    public void checkPermission() {
        // Check Condition
        if (ContextCompat.checkSelfPermission(FriendsActivity.this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            // When permission is not granted

            // Request permission from user
            ActivityCompat.requestPermissions(FriendsActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, 100);
        } else {

        }
    }
}

