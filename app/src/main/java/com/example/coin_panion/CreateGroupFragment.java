package com.example.coin_panion;

import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.coin_panion.classes.friends.Contact;
import com.example.coin_panion.classes.general.Account;
import com.example.coin_panion.classes.general.User;
import com.example.coin_panion.classes.group.Group;
import com.example.coin_panion.classes.group.GroupMemberAddAdapter;
import com.example.coin_panion.classes.utility.BaseViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateGroupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateGroupFragment extends Fragment {
    BaseViewModel mainViewModel;
    ImageView createGroupBackButton, createGroupCoverImageView, createGroupPicImageView;
    AppCompatButton createGroupFinishButton;
    EditText createGroupNameEditText, createGroupDescEditText, createGroupTypeEditText;
    RecyclerView createGroupRecyclerView;


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

        Account account  = (Account) mainViewModel.get("account");
        User user = (User) mainViewModel.get("user");

        // View bindings
        createGroupBackButton = view.findViewById(R.id.createGroupBackButton);
        createGroupCoverImageView = view.findViewById(R.id.createGroupCoverImageView);
        createGroupPicImageView = view.findViewById(R.id.createGroupPicImageView);
        createGroupRecyclerView = view.findViewById(R.id.createGroupRecyclerView);
        createGroupDescEditText = view.findViewById(R.id.createGroupDescEditText);
        createGroupNameEditText = view.findViewById(R.id.createGroupNameEditText);
        createGroupFinishButton = view.findViewById(R.id.createGroupFinishButton);
        createGroupTypeEditText = view.findViewById(R.id.createGroupTypeEditText);

        // Get list of potential friends to add into group. Get from Account object
        List<User> friends = account.getFriends();
        List<Contact> contacts = new ArrayList<>();
        for(int i = 0 ; i < friends.size(); i++){
            contacts.add(new Contact(friends.get(i).getUsername(), friends.get(i).getPhoneNumber().toString()));
        }

        // Bind the list of potential friends to adapter
        GroupMemberAddAdapter groupMemberAddAdapter = new GroupMemberAddAdapter(contacts, getContext());
        createGroupRecyclerView.setAdapter(groupMemberAddAdapter);

        createGroupFinishButton.setOnClickListener(v -> {
            // 1 : Get the IDs of all members from the database using phone number
            List<Contact> selectedContacts = groupMemberAddAdapter.getSelectedContacts();
            List<Long> phoneNumbers = new ArrayList<>();
            for(int i = 0 ; i < selectedContacts.size(); i++){
                phoneNumbers.add(Long.parseLong(selectedContacts.get(i).getContactNumber()));
            }
            phoneNumbers.add(user.getPhoneNumber());
            List<Integer> memberIDs = User.getListOfIDs(phoneNumbers, new Thread());

            // 2 : Get the value from all the fields in the fragment
            String groupName = createGroupNameEditText.getText().toString();
            String groupType = createGroupTypeEditText.getText().toString();
            String groupDesc = createGroupDescEditText.getText().toString();
            Group newGroup = new Group(groupName, groupType, groupDesc, memberIDs);
            System.out.println(groupName);
            System.out.println(groupType);
            System.out.println(groupDesc);
            System.out.println(memberIDs.get(0));
            Group.insertNewGroupToDB(newGroup, new Thread());
        });

        createGroupBackButton.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.groupFirstFragment);
        });

    }
}