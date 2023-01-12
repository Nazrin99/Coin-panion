package com.example.coin_panion;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;

import com.example.coin_panion.classes.general.Account;
import com.example.coin_panion.classes.general.User;
import com.example.coin_panion.classes.utility.BaseViewModel;
import com.example.coin_panion.fragments.friends.FriendsDefaultFragmentDirections;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class FriendsActivity extends AppCompatActivity {
    BaseViewModel friendsViewModel;
    Toolbar toolbar;
    TextView textView;
    Bundle bundle;
    BottomNavigationView bottomNavigationView;
    Account account;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        toolbar = findViewById(R.id.friendActivityAppBar);
        textView = toolbar.findViewById(R.id.activityName);
        textView.setText("Friends");
        bottomNavigationView = findViewById(R.id.friendsBottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.ITprofile:
                        Intent intent = new Intent(getApplicationContext(), AccountFirstActivity.class);
                        intent.putExtra("account", account);
                        intent.putExtra("user", user);
                        startActivity(intent);
                        break;
                    case R.id.IThistory:
                        Intent intent1 = new Intent(getApplicationContext(), HistoryActivity.class);
                        intent1.putExtra("account", account);
                        intent1.putExtra("user", user);
                        startActivity(intent1);
                        break;
                    case R.id.ITgroups:
                        Intent intent2 = new Intent(getApplicationContext(), GroupActivity.class);
                        intent2.putExtra("account", account);
                        intent2.putExtra("user", user);
                        startActivity(intent2);
                        break;
                }
                return false;
            }
        });

        friendsViewModel = new ViewModelProvider(this).get(BaseViewModel.class);

        Intent intent = getIntent();
        account = (Account) intent.getSerializableExtra("account");
        user = (User) intent.getSerializableExtra("user");

        friendsViewModel.put("account", account);
        friendsViewModel.put("user", user);

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
            requestPermission();
        } else {

        }


    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_CONTACTS)) {
            // show UI part if you want here to show some rationale !!!
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_CONTACTS},
                    79);
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_CONTACTS)) {
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_CONTACTS},
                    79);
        }
    }
}

