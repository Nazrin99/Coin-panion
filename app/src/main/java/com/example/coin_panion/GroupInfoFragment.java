package com.example.coin_panion;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.coin_panion.classes.general.Account;
import com.example.coin_panion.classes.general.User;
import com.example.coin_panion.classes.group.Group;
import com.example.coin_panion.classes.utility.BaseViewModel;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GroupInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GroupInfoFragment extends Fragment {
    BaseViewModel mainViewModel;
    Account account;
    User user;
    ImageView groupInfoPicImageView, groupInfoCoverImageView, groupInfoSettingsImageView;
    TextView groupNameTextView, groupDescTextView;
    Group selected;
    RecyclerView groupActivityRecyclerView;
    Button createExpensesButton;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "groupID";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private Integer groupID;
    private String mParam2;

    public GroupInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GroupInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GroupInfoFragment newInstance(String param1, String param2) {
        GroupInfoFragment fragment = new GroupInfoFragment();
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
            groupID = getArguments().getInt(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);

        }
        mainViewModel = new ViewModelProvider(requireActivity()).get(BaseViewModel.class);

        selected = null;
        List<Group> groupList = (List<Group>) mainViewModel.get("groups");
        for(int i = 0; i < groupList.size(); i++){
            if(groupList.get(i).getGroupID() == groupID){
                selected = groupList.get(i);
            }
        }
        assert selected != null;


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_group_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        createExpensesButton = view.findViewById(R.id.createExpensesButton);
        groupInfoPicImageView = view.findViewById(R.id.groupInfoPicImageView);
        groupInfoCoverImageView = view.findViewById(R.id.groupInfoCoverImageView);
        groupInfoSettingsImageView = view.findViewById(R.id.groupInfoSettingsImageView);
        groupNameTextView = view.findViewById(R.id.groupNameTextView);
        groupDescTextView = view.findViewById(R.id.groupDescTextView);
        groupActivityRecyclerView = view.findViewById(R.id.groupActivityRecyclerView);

        requireActivity().runOnUiThread(() -> {
            groupNameTextView.setText(selected.getGroupName());
            groupDescTextView.setText(selected.getGroupDesc());
            groupInfoPicImageView.setImageDrawable(selected.getGroupPic().getPicture());
            groupInfoCoverImageView.setImageDrawable(selected.getGroupCover().getPicture());
        });

        groupInfoSettingsImageView.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.editGroupFragment2);
            mainViewModel.put("currentGroup", selected);
        });

        createExpensesButton.setOnClickListener(v -> {
            mainViewModel.put("currentGroup", selected);
            Navigation.findNavController(v).navigate(R.id.createExpensesButton);

        });


    }
}