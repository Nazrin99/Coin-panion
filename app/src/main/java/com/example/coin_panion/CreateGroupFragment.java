package com.example.coin_panion;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.coin_panion.classes.friends.Contact;
import com.example.coin_panion.classes.friends.ContactAdapter;
import com.example.coin_panion.classes.general.Account;
import com.example.coin_panion.classes.group.Group;
import com.example.coin_panion.classes.utility.BaseViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Executable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateGroupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateGroupFragment extends Fragment {
    BaseViewModel mainViewModel;
    ImageView createGroupBackButton;
    AppCompatButton createGroupFinishButton;
    EditText createGroupNameEditText, createGroupDescEditText, createGroupTypeEditText;
    RecyclerView createGroupAddRecyclerView, createGroupRemoveRecyclerView;
    ContactAdapter groupMemberAddAdapter, groupMemberRemoveAdapter;
    Account account;
    List<Group> groups;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CreateGroupFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DetailGroupFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CreateGroupFragment newInstance(String param1, String param2) {
        CreateGroupFragment fragment = new CreateGroupFragment();
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
        return inflater.inflate(R.layout.fragment_create_group, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainViewModel = new ViewModelProvider(requireActivity()).get(BaseViewModel.class);

        account  = (Account) mainViewModel.get("account");
        groups = (List<Group>) mainViewModel.get("groups");
        System.out.println("Number of groups: " + groups.size());

        // View bindings
        createGroupBackButton = view.findViewById(R.id.createGroupBackButton);
        createGroupAddRecyclerView = view.findViewById(R.id.createGroupAddRecyclerView);
        createGroupRemoveRecyclerView = view.findViewById(R.id.createGroupRemoveRecyclerView);
        createGroupDescEditText = view.findViewById(R.id.createGroupDescEditText);
        createGroupNameEditText = view.findViewById(R.id.createGroupNameEditText);
        createGroupFinishButton = view.findViewById(R.id.createGroupFinishButton);
        createGroupTypeEditText = view.findViewById(R.id.createGroupTypeEditText);

        // Get list of potential friends to add into group. Get from Account object
        List<Account> friends = account.getFriends();
        System.out.println("Number of friends: " + friends.size());
        List<Contact> contacts = new ArrayList<>();
        for(int i = 0 ; i < friends.size(); i++){
            contacts.add(new Contact(friends.get(i).getUser().getUsername(), friends.get(i).getUser().getPhoneNumber()));
        }
        List<Contact> selectedMembers = new ArrayList<>();

        // Bind the list of potential friends to adapter
        groupMemberAddAdapter = new ContactAdapter(contacts, selectedMembers, requireActivity().getApplicationContext(), true);
        groupMemberRemoveAdapter = new ContactAdapter(selectedMembers, contacts, groupMemberAddAdapter, requireActivity().getApplicationContext(), false);
        groupMemberAddAdapter.setOtherAdapter(groupMemberRemoveAdapter);
        requireActivity().runOnUiThread(() -> {
            createGroupAddRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity().getApplicationContext()));
            createGroupAddRecyclerView.setAdapter(groupMemberAddAdapter);
            createGroupRemoveRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity().getApplicationContext()));
            createGroupRemoveRecyclerView.setAdapter(groupMemberRemoveAdapter);
        });

        createGroupFinishButton.setOnClickListener(this::createGroupFinished);

        createGroupBackButton.setOnClickListener(v -> {
            if(groups.size() == 0){
                Navigation.findNavController(v).navigate(R.id.groupFirstFragment);
            }
            else{
                Navigation.findNavController(v).navigate(R.id.groupBalanceFragment);
            }
        });

    }

    private void createGroupFinished(View v){
        // 1 : Get all the details for the new group
        String groupName = createGroupNameEditText.getText().toString();
        String groupDesc = createGroupDescEditText.getText().toString();
        String groupType = createGroupTypeEditText.getText().toString();

        // 2 : Create a map to insert all the new group details
        Map<String, Object> groupDetails = new HashMap<>();
        groupDetails.put("groupName", groupName);
        groupDetails.put("groupDesc", groupDesc);
        groupDetails.put("groupType", groupType);
        groupDetails.put("groupPic", "default_PROFILE.png");
        groupDetails.put("groupCover", "default_COVER.png");

        // 3 : Create a new Firebase document in "group" collection
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        DocumentReference documentReference = firebaseFirestore.collection("group").document();

        // 4 : Insert document ID as groupID in group details
        groupDetails.put("groupID", documentReference.getId());

        // 5 : Create group object
        Group newGroup = new Group(documentReference.getId(), groupName, groupDesc, groupType, "default_PROFILE.png", "default_COVER.png");

        // 6 : Get list of group members for new group and append to the new group
        List<Account> newMembers = new ArrayList<>();
        List<Contact> finalSelectedContacts = groupMemberRemoveAdapter.getContacts();
        List<Account> listOfFriends = account.getFriends();
        for(int i = 0; i < finalSelectedContacts.size(); i++){
            for(int j = 0; j < listOfFriends.size(); j++){
                if(finalSelectedContacts.get(i).getContactNumber().equalsIgnoreCase(listOfFriends.get(j).getUser().getPhoneNumber())){
                    newMembers.add(listOfFriends.get(j));
                    break;
                }
            }
        }
        newMembers.add(account);

        // 7 : Append list of members to new group and append new group to list of existing groups
        newGroup.setGroupMembers(newMembers);
        groups.add(newGroup);

        // 8 : Get the group pic and group cover for new group (Asynchronous)
        Executors.newSingleThreadExecutor().execute(() -> {
            newGroup.getGroupPic().getPictureFromDatabase();
            newGroup.getGroupCover().getPictureFromDatabase();
        });

        // 9 : Insert the new group info inside Firebase (Asynchronous)
        Executors.newSingleThreadExecutor().execute(() -> {
            documentReference.set(groupDetails).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    System.out.println("New group with name: " + groupDetails.get("groupName") + " with ID: " + documentReference.getId() + " has been added");
                }
            });
        });

        // 10 : Insert the list of new group members inside Firebase (Asynchronous)
        Executors.newSingleThreadExecutor().execute(() -> {
            FirebaseFirestore firebaseFirestore1 = FirebaseFirestore.getInstance();

            // 10.1 : Create a map for account_group details
            Map<String, Object> accountGroupDetails = new HashMap<>();

            // 10.2 : Create DocumentReference to new group document and append to map
            DocumentReference newGroupReference = firebaseFirestore1.collection("group").document(documentReference.getId());
            accountGroupDetails.put("groupID", newGroupReference);

            // 10.3 : Loop through list of new members and insert data into Firebase
            for(int i = 0 ; i < newMembers.size(); i++){
                // 10.3.1 : Create DocumentReference for new account_group document
                DocumentReference accountGroupReference = firebaseFirestore1.collection("account_group").document();

                // 10.3.2 : Create DocumentReference for members' accounts
                DocumentReference accountReference = firebaseFirestore1.collection("account").document(newMembers.get(i).getAccountID());
                accountGroupDetails.put("accountID", accountReference);

                accountGroupReference.set(accountGroupDetails).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        System.out.println("Bridge entity in account group added!");
                    }
                });
            }

            // 10.4 : Navigate away from the fragment
            if(groups.size() > 0){
                requireActivity().runOnUiThread(() -> {
                    Navigation.findNavController(v).navigate(R.id.groupBalanceFragment);
                });
            }
            else{
                requireActivity().runOnUiThread(() -> {
                    Navigation.findNavController(v).navigate(R.id.groupFirstFragment);
                });
            }
        });
    }
}