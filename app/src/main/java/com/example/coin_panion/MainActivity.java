package com.example.coin_panion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentContainerView;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.coin_panion.classes.general.Account;
import com.example.coin_panion.classes.general.User;
import com.example.coin_panion.classes.utility.BaseViewModel;
import com.example.coin_panion.classes.utility.Picture;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {
    Toolbar mainToolbar;
    BaseViewModel mainViewModel;
    Account account;
    User user;
    BottomNavigationView mainBottomNavigationView;
    NavHostFragment mainNavHostFragment;
    NavController navController;
    TextView activityName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainViewModel = new ViewModelProvider(this).get(BaseViewModel.class);

        Intent intent = getIntent();
        account = (Account) intent.getSerializableExtra("account");
        user = (User) intent.getSerializableExtra("user");
        System.out.println(account);
        System.out.println(user);

        mainBottomNavigationView = findViewById(R.id.mainBottomNavigationView);
        mainNavHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.mainNavHostFragment);
        navController = mainNavHostFragment.getNavController();
        mainToolbar = findViewById(R.id.mainToolbar);
        activityName = findViewById(R.id.activityName);

        mainViewModel.put("account", account);
        mainViewModel.put("user", user);

        mainBottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.ITfriends:
                        navController.navigate(R.id.friendsFragment);
                        activityName.setText("FRIENDS");
                        break;
                    case R.id.IThistory:
                        navController.navigate(R.id.historyDefaultFragment2);
                        activityName.setText("HISTORY");
                        break;
                    case R.id.ITgroups:
                        navController.navigate(R.id.groupBalanceFragment);
                        activityName.setText("GROUPS");
                        break;
                    case R.id.ITprofile:
                        navController.navigate(R.id.accountFirstActivity2);
                        activityName.setText("PROFILE");
                        break;

                }
                return false;
            }
        });
        navController.navigate(R.id.friendsFragment);

    }

    public void checkPermission() {
        // Check Condition
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS)
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

    public BaseViewModel getMainViewModel() {
        return mainViewModel;
    }

    public void setMainViewModel(BaseViewModel mainViewModel) {
        this.mainViewModel = mainViewModel;
    }
}