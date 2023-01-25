package com.example.coin_panion.fragments.friends;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.coin_panion.R;
import com.example.coin_panion.classes.friends.Contact;
import com.example.coin_panion.classes.friends.FriendAdapter;
import com.example.coin_panion.classes.general.Account;
import com.example.coin_panion.classes.general.User;
import com.example.coin_panion.classes.utility.BaseViewModel;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FriendsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendsFragment extends Fragment {
    BaseViewModel mainViewModel;
    Account account;
    RecyclerView friendsRecyclerView;
    Button BtnAddMoreFriend;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FriendsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FriendsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FriendsFragment newInstance(String param1, String param2) {
        FriendsFragment fragment = new FriendsFragment();
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
            mParam1 = getArguments().getString("extraString");
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friends, container, false);
    }
    FriendAdapter friendAdapter;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(friendAdapter != null){
            friendAdapter.notifyDataSetChanged();
        }

        mainViewModel = new ViewModelProvider(requireActivity()).get(BaseViewModel.class);
        account = (Account) mainViewModel.get("account");

        // Binding views
        friendsRecyclerView = view.findViewById(R.id.friendsRecyclerView);
        BtnAddMoreFriend = view.findViewById(R.id.BtnAddMoreFriend);

        BtnAddMoreFriend.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.friendsAddFragment);
        });

        List<Account> friends;
        if(account == null){
            friends = new ArrayList<>();
        }
        else{
            friends = account.getFriends();
        }
        System.out.println(friends.size());

        friendAdapter = new FriendAdapter(friends, mainViewModel, getActivity().getApplicationContext());
        friendsRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity().getApplicationContext()));
        friendsRecyclerView.setAdapter(friendAdapter);

    }


}