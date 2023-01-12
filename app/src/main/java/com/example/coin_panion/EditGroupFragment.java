package com.example.coin_panion;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.coin_panion.classes.friends.ContactAdapter;
import com.example.coin_panion.classes.friends.RemoveFriendAdapter;
import com.example.coin_panion.classes.general.Account;
import com.example.coin_panion.classes.general.User;
import com.example.coin_panion.classes.group.Group;
import com.example.coin_panion.classes.utility.BaseViewModel;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditGroupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditGroupFragment extends Fragment {
    BaseViewModel mainViewModel;
    Group currentGroup;
    Account account;
    User user;

    private List<User> groupMembers;

    public EditGroupFragment(List<User> groupMembers) {
        this.groupMembers = groupMembers;
    }

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public EditGroupFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EditGroupFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditGroupFragment newInstance(String param1, String param2) {
        EditGroupFragment fragment = new EditGroupFragment();
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

    ImageButton IBUploadGroupBackground, IBUploadGroupProfilePic;
    EditText ETGroupName, ETGroupDescription;
    RecyclerView RVRemoveAddFriendGroup;
    Button BtnCreateGroup;
    RemoveFriendAdapter removeFriendAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_group, container, false);

        /*TODO function to take all the variable instance and upload it*/
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainViewModel = new ViewModelProvider(requireActivity()).get(BaseViewModel.class);
        currentGroup = (Group) mainViewModel.get("currentGroup");

        IBUploadGroupBackground = view.findViewById(R.id.IBUploadGroupBackground);
        IBUploadGroupProfilePic = view.findViewById(R.id.IBUploadGroupProfilePic);
        ETGroupName = view.findViewById(R.id.ETGroupName);
        ETGroupDescription = view.findViewById(R.id.ETGroupDescription);
        RVRemoveAddFriendGroup = view.findViewById(R.id.RVRemoveAddFriendGroup);
        BtnCreateGroup = view.findViewById(R.id.BtnCreateGroup);

        groupMembers = Group.retrieveGroupParticipants(currentGroup.getGroupID(), new Thread());

        /*Set layout manager*/
        RVRemoveAddFriendGroup.setLayoutManager(new LinearLayoutManager(getContext()));
        /*Initialize adapter*/
        removeFriendAdapter = new RemoveFriendAdapter(groupMembers,getContext());
        /*Set the adapter*/
        RVRemoveAddFriendGroup.setAdapter(removeFriendAdapter);
        /*Notify Changes in adapter*/
        BtnCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*TODO create an object instance of group and place in database*/
            }
        });

        ETGroupName.setText(currentGroup.getGroupName());
        ETGroupDescription.setText(currentGroup.getGroupDesc());
        IBUploadGroupProfilePic.setImageDrawable(currentGroup.getGroupPic().getPicture());
        IBUploadGroupBackground.setImageDrawable(currentGroup.getGroupCover().getPicture());


    }
}