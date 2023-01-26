package com.example.coin_panion;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
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
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.coin_panion.classes.general.Account;
import com.example.coin_panion.classes.general.User;
import com.example.coin_panion.classes.group.Group;
import com.example.coin_panion.classes.settleUp.PaymentRequest;
import com.example.coin_panion.classes.transaction.Transaction;
import com.example.coin_panion.classes.utility.BaseViewModel;
import com.example.coin_panion.classes.utility.Picture;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

@SuppressWarnings("deprecation")
public class MainActivity extends AppCompatActivity {
    Toolbar mainToolbar;
    BaseViewModel mainViewModel;
    Account account;
    BottomNavigationView mainBottomNavigationView;
    NavHostFragment mainNavHostFragment;
    NavController navController;
    TextView activityName;
    ProgressDialog progressDialog;

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainBottomNavigationView = findViewById(R.id.mainBottomNavigationView);
        mainNavHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.mainNavHostFragment);
        navController = mainNavHostFragment.getNavController();
        mainToolbar = findViewById(R.id.mainToolbar);
        activityName = findViewById(R.id.activityName);

        runOnUiThread(() -> {
            progressDialog = ProgressDialog.show(this, "", "Loading Your Account");
        });
        checkPermission();

        String userID;

        mainViewModel = new ViewModelProvider(this).get(BaseViewModel.class);

        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");

        Executors.newSingleThreadExecutor().execute(() -> {
            // 1 : Get User object from using userID
            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
            AtomicReference<User> userAtomicReference = new AtomicReference<>(null);

            // 1.1 : Get User Document
            Query query = firebaseFirestore.collection("user").whereEqualTo("userID",userID);
            System.out.println(userID);
            query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    System.out.println("Query successful");
                    if(queryDocumentSnapshots != null){
                        if(!queryDocumentSnapshots.isEmpty()){
                            DocumentSnapshot userDocumentSnapshot = queryDocumentSnapshots.getDocuments().get(0);

                            String phoneNumber = userDocumentSnapshot.getString("phoneNumber");
                            String firstName = userDocumentSnapshot.getString("firstName");
                            String lastName = userDocumentSnapshot.getString("lastName");
                            String username = userDocumentSnapshot.getString("username");
                            String email = userDocumentSnapshot.getString("email");
                            String password = userDocumentSnapshot.getString("password");

                            User user = new User(userID, phoneNumber, firstName, lastName, username, email, password);
                            userAtomicReference.set(user);
                            System.out.println("User object created");
                        }
                        else{
                            System.out.println("User snapshot is empty");
                        }
                    }
                    else{
                        System.out.println("User snapshot is null");
                    }
                }
            });
            while(userAtomicReference.get() == null){

            }

            FirebaseFirestore firebaseFirestore1 = FirebaseFirestore.getInstance();

            System.out.println(userAtomicReference.get());
            // 2.1 : Create User DocumentReference
            DocumentReference userDocumentReference = firebaseFirestore1.collection("user").document(userAtomicReference.get().getUserID());

            // 2.2 : Query Account DocumentReference using User DocumentReference
            Query query1 = firebaseFirestore1.collection("account").whereEqualTo("user", userDocumentReference);

            // 2.3 : Get Account object
            query1.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    System.out.println("Account document successfully accessed");
                    if(queryDocumentSnapshots != null){
                        if(!queryDocumentSnapshots.isEmpty()){
                            DocumentSnapshot documentSnapshot1 = queryDocumentSnapshots.getDocuments().get(0);

                            // 2.2.1 : Get all Account details
                            String accountID = documentSnapshot1.getString("accountID");
                            String bio = documentSnapshot1.getString("bio");
                            Double debtLimitAmount = documentSnapshot1.getDouble("debtLimitAmount");
                            Date debtLimitEndDate = documentSnapshot1.getDate("debtLimitEndDate");
                            String accountPicReference = documentSnapshot1.getString("accountPic");
                            String accountCoverReference = documentSnapshot1.getString("accountCover");
                            String qrImageReference = documentSnapshot1.getString("qrImage");
                            String settleUpAccountNumber = documentSnapshot1.getString("settleUpAccountNumber");
                            String settleUpAccountName = documentSnapshot1.getString("settleUpAccountName");
                            Picture accountPic = new Picture(accountPicReference);
                            Picture accountCover = new Picture(accountCoverReference);
                            Picture qrImage = new Picture(qrImageReference);

                            // 2.2.2 : Create Account object
                            account = new Account(accountID, userAtomicReference.get(), bio, debtLimitAmount, debtLimitEndDate, settleUpAccountName ,settleUpAccountNumber, accountPic, accountCover, qrImage);
                        }
                        else{
                            System.out.println("Account document is empty!");
                        }
                    }
                    else{
                        System.out.println("Query returns null");
                    }
                }
            });
            while(account == null){

            }

            // 2.4 : Get Account pictures from Firebase Storage (Asynchronous)
            Executors.newSingleThreadExecutor().execute(() -> {
                account.getAccountPic().getPictureFromDatabase();
                account.getAccountCover().getPictureFromDatabase();
                account.getQrImage().getPictureFromDatabase();
            });

            // 2.5 : Get the list of friends for the Account
            List<Account> friends = Account.getListOfFriends(account.getAccountID());
            account.setFriends(friends);

            // 2.6 : Get blocked contacts
            List<Account> blockedContacts = Account.getBlockedFriends(account.getAccountID());
            account.setBlockedContacts(blockedContacts);

            // 2.7 : Get list of group & group members
            List<Group> groups = Group.getGroups(account.getAccountID());
            for(int i = 0; i < groups.size(); i++){
                groups.get(i).getGroupMembersFromDatabase();
            }
            account.setGroups(groups);

            // 2.8 : Get list of transactions for a group TODO
            System.out.println("Passed");

            account.setDebts(new ArrayList<>());
            System.out.println("Got the debts");

            mainViewModel.put("account", account);

            if(account.getFriends().size() > 0){
                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    navController.navigate(R.id.friendsFragment);
                });
            }
            else{
                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    navController.navigate(R.id.friendsDefaultFragment);
                });
            }

        });

        mainBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
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
                        activityName.setText("GROUPS");
                        if(account.getGroups().size() > 0){
                            navController.navigate(R.id.groupBalanceFragment);
                        }
                        else{
                            navController.navigate(R.id.groupFirstFragment);
                        }
                        break;
                    case R.id.ITprofile:
                        navController.navigate(R.id.accountFirstActivity2);
                        activityName.setText("PROFILE");
                        break;

                }
                return true;
            }
        });
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