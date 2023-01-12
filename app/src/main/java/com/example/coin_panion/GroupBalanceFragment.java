package com.example.coin_panion;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.coin_panion.classes.general.Account;
import com.example.coin_panion.classes.general.User;
import com.example.coin_panion.classes.group.Group;
import com.example.coin_panion.classes.group.GroupAdapter;
import com.example.coin_panion.classes.utility.BaseViewModel;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GroupBalanceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GroupBalanceFragment extends Fragment {
    BaseViewModel mainViewModel;
    Account account;
    User user;
    RecyclerView groupListRecyclerView;
    Button createGroupButton;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public GroupBalanceFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GroupBalanceFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GroupBalanceFragment newInstance(String param1, String param2) {
        GroupBalanceFragment fragment = new GroupBalanceFragment();
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

        mainViewModel = new ViewModelProvider(requireActivity()).get(BaseViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_group_balance, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        account = (Account) mainViewModel.get("account");
        user = (User) mainViewModel.get("user");

        groupListRecyclerView = view.findViewById(R.id.groupListRecyclerView);
        createGroupButton = view.findViewById(R.id.createNewGroupButton);

        createGroupButton.setOnClickListener(v -> {
            requireActivity().runOnUiThread(() -> {
                NavDirections navDirections = GroupBalanceFragmentDirections.actionGroupBalanceFragmentToCreateGroupFragment();
                Navigation.findNavController(view).navigate(navDirections);
            });
        });

        List<Integer> groupIDs = Group.retrieveGroupIDsFromDB(account.getAccountID(), new Thread());
        System.out.println(groupIDs.size());
        List<Group> groups = Group.retrieveGroupFromDB(groupIDs, new Thread());

        mainViewModel.put("groups", groups);

        GroupAdapter groupAdapter = new GroupAdapter(getActivity(), groups);
        System.out.println("Adapter applied");

        requireActivity().runOnUiThread(() -> {
            groupListRecyclerView.setAdapter(groupAdapter);
            groupListRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        });

    }

    @Override
    public void onResume() {
        super.onResume();

        // Get list of group associated with this account
    }
}