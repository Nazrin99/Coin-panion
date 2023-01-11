package com.example.coin_panion.fragments.friends;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.coin_panion.R;
import com.example.coin_panion.classes.friends.Contact;
import com.example.coin_panion.classes.friends.ContactAdapter;
import com.example.coin_panion.classes.general.Account;
import com.example.coin_panion.classes.general.User;
import com.example.coin_panion.classes.utility.BaseViewModel;
import com.example.coin_panion.classes.utility.Line;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FriendsAddFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendsAddFragment extends Fragment {
    BaseViewModel friendsViewModel;
    SearchView searchViewContactFriend;
    RecyclerView contactsRecyclerView;
    AppCompatButton saveContactButton;
    ImageButton backButton;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FriendsAddFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddFriendsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FriendsAddFragment newInstance(String param1, String param2) {
        FriendsAddFragment fragment = new FriendsAddFragment();
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

        return inflater.inflate(R.layout.fragment_friends_from_contact, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        friendsViewModel = new ViewModelProvider(requireActivity()).get(BaseViewModel.class);
        Account currentAccount = ((Account)friendsViewModel.get("account"));

        // View bindings
        searchViewContactFriend = view.findViewById(R.id.searchViewContactFriend);
        contactsRecyclerView = view.findViewById(R.id.contactsRecyclerView);
        saveContactButton = view.findViewById(R.id.saveContactButton);
        backButton = view.findViewById(R.id.backButton);

        ArrayList<Contact> listOfContacts = Contact.getAllContacts(requireActivity().getContentResolver());

        ContactAdapter contactAdapter = new ContactAdapter(listOfContacts);
        contactsRecyclerView.setAdapter(contactAdapter);

        backButton.setOnClickListener(v -> {
            if(currentAccount.getFriends().size() == 0){
                NavDirections navDirections = FriendsAddFragmentDirections.actionFriendsAddFragmentToFriendsFragment();
                Navigation.findNavController(view).navigate(navDirections);
            }
            else{
                NavDirections navDirections = FriendsAddFragmentDirections.actionFriendsAddFragmentToFriendsDefaultFragment();
                Navigation.findNavController(view).navigate(navDirections);
            }
        });

        saveContactButton.setOnClickListener(v -> {
            // Save all the contacts into database and close the window.

            // 1 : Get list of selected contact and get their phone numbers
            List<Contact> toSave = contactAdapter.getSelectedContacts();
            List<Long> listOfPhoneNumbers = new ArrayList<>();
            for(Contact c : toSave){
                listOfPhoneNumbers.add(Long.parseLong(c.getContactNumber()));
            }

            // 2 : Query the database for all the User objects associated with the phone number
            List<User> newFriends = new ArrayList<>();
            for(int i = 0 ; i < listOfPhoneNumbers.size(); i++){
                newFriends.add(User.verifyUserLogin(listOfPhoneNumbers.get(i), new Thread()));
            }
            // 3 : Insert the list of new friends inside the friend table in the database
            try(
                    Connection connection = Line.getConnection();
                    PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO friend VALUES(?, ?, ?)")
                    ){
                for(int i = 0 ; i < newFriends.size(); i++){
                    preparedStatement.setInt(1, currentAccount.getAccountID());
                    preparedStatement.setInt(2, newFriends.get(i).getUserID());
                    preparedStatement.setBoolean(3, false);
                    preparedStatement.addBatch();
                }
                preparedStatement.executeBatch();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            // 4 : Append the list of new friends to the existing list of friends for the current account update the data inside the viewmodel
            for(int i = 0; i < newFriends.size(); i++){
                currentAccount.getFriends().add(newFriends.get(i));
            }
            friendsViewModel.put("account", currentAccount);

            // 5 : Navigate to the original fragment
            if(currentAccount.getFriends().size() == 0){
                NavDirections navDirections = FriendsAddFragmentDirections.actionFriendsAddFragmentToFriendsDefaultFragment();
                Navigation.findNavController(view).navigate(navDirections);
            }
            else{
                NavDirections navDirections = FriendsAddFragmentDirections.actionFriendsAddFragmentToFriendsFragment();
                Navigation.findNavController(view).navigate(navDirections);
            }

        });
    }


}