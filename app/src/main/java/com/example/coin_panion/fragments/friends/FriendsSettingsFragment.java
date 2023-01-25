package com.example.coin_panion.fragments.friends;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coin_panion.R;
import com.example.coin_panion.classes.general.Account;
import com.example.coin_panion.classes.utility.BaseViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FriendsSettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendsSettingsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FriendsSettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FriendsSettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FriendsSettingsFragment newInstance(String param1, String param2) {
        FriendsSettingsFragment fragment = new FriendsSettingsFragment();
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
        return inflater.inflate(R.layout.fragment_friends_settings, container, false);
    }

    BaseViewModel mainViewModel;
    Account friend, account;
    ImageButton friendsSettingsBackButton;
    AppCompatButton friendsSettingsSaveButton;
    TextView friendsSettingsUsernameTextView, friendsSettingsNumberTextView;
    Switch removeFriendSwitch, notificationSwitch, blockFriendSwitch;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainViewModel = new ViewModelProvider(requireActivity()).get(BaseViewModel.class);
        friend = (Account) mainViewModel.get("selectedFriend");
        account = (Account) mainViewModel.get("account");

        // Binding views
        friendsSettingsBackButton = view.findViewById(R.id.friendsSettingsBackButton);
        friendsSettingsSaveButton = view.findViewById(R.id.friendsSettingsSaveButton);
        friendsSettingsUsernameTextView = view.findViewById(R.id.friendsSettingsUsernameTextView);
        friendsSettingsNumberTextView = view.findViewById(R.id.friendsSettingsNumberTextView);
        removeFriendSwitch = view.findViewById(R.id.removeFriendSwitch);
        notificationSwitch = view.findViewById(R.id.notificationSwitch);
        blockFriendSwitch = view.findViewById(R.id.blockFriendSwitch);

        // Assign text to TextView
        requireActivity().runOnUiThread(() -> {
            friendsSettingsUsernameTextView.setText(friend.getUser().getUsername());
            friendsSettingsNumberTextView.setText(friend.getUser().getPhoneNumber());
        });

        // Configuring back button navigation
        friendsSettingsBackButton.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.friendsDetailFragment);
            Toast.makeText(requireActivity(), "Changes are not saved", Toast.LENGTH_SHORT).show();
        });

        friendsSettingsSaveButton.setOnClickListener(v -> {
            // Check status for each switch and perform actions accordingly
            if(blockFriendSwitch.isChecked()){
                // Block this friend

                // Move the friend Account object to account's list of blocked accounts
                account.getFriends().remove(friend);
                account.getBlockedContacts().add(friend);

                // Asynchronous background friend blocked status update
                Executors.newSingleThreadExecutor().execute(() -> {
                    // 1 : Create DocumentReference to friend Account
                    DocumentReference friendAccountReference = FirebaseFirestore.getInstance().collection("account").document(friend.getAccountID());

                    // 2 : Query for friend Document using the related parameters
                    Query query = FirebaseFirestore.getInstance().collection("friend")
                            .whereEqualTo("accountID", account.getAccountID())
                            .whereEqualTo("friendAccountID", friendAccountReference);
                    query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if(queryDocumentSnapshots != null){
                                if(!queryDocumentSnapshots.isEmpty()){
                                    DocumentReference documentReference = queryDocumentSnapshots.getDocuments().get(0).getReference();

                                    // 3 : Update blocked status to true
                                    documentReference.update("blocked", true).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            System.out.println("Friend with ID " + friendAccountReference.getId() + " has been blocked");
                                        }
                                    });
                                }
                                else{
                                    System.out.println("Snapshot is empty");
                                }
                            }
                            else{
                                System.out.println("Snapshot is null");
                            }
                        }
                    });
                });
            }

            if(removeFriendSwitch.isChecked()){
                // Remove this friend

                account.getFriends().remove(friend);

                // Asynchronous background friend deletion
                Executors.newSingleThreadExecutor().execute(() -> {
                    // 1 : Get DocumentReference to friend Account
                    DocumentReference documentReference = FirebaseFirestore.getInstance().collection("account").document(friend.getAccountID());

                    // 2 : Query for friend Document using related parameters
                    Query query = FirebaseFirestore.getInstance().collection("friend")
                            .whereEqualTo("accountID", account.getAccountID())
                            .whereEqualTo("friendAccountID", documentReference);
                    query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if(queryDocumentSnapshots != null){
                                if(!queryDocumentSnapshots.isEmpty()){
                                    DocumentReference friendDocumentReference = queryDocumentSnapshots.getDocuments().get(0).getReference();

                                    // 3 : Delete the document
                                    friendDocumentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            System.out.println("Account: " + account.getUser().getUsername() + " has deleted friend: " + friend.getUser().getUsername());
                                        }
                                    });
                                }
                                else{
                                    System.out.println("Snapshot is empty");
                                }

                            }
                            else{
                                System.out.println("Snapshot is null");
                            }
                        }
                    });
                });
            }

            // Navigate to the list of friends
            if(account.getFriends().size() > 0){
                Navigation.findNavController(v).navigate(R.id.friendsFragment);
            }
            else{
                Navigation.findNavController(v).navigate(R.id.friendsDefaultFragment);
            }
        });

    }
}