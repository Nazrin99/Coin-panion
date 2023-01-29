package com.example.coin_panion;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coin_panion.classes.general.Account;
import com.example.coin_panion.classes.group.Group;
import com.example.coin_panion.classes.notification.TransactionAdapter;
import com.example.coin_panion.classes.transaction.Transaction;
import com.example.coin_panion.classes.utility.BaseViewModel;
import com.example.coin_panion.classes.utility.Picture;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GroupInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GroupInfoFragment extends Fragment {
    BaseViewModel mainViewModel;
    ImageView groupInfoPicImageView, groupInfoCoverImageView, groupInfoSettingsImageView, groupInfoBackImageView;
    TextView groupNameTextView, groupDescTextView;
    RecyclerView groupActivityRecyclerView;
    Button createExpensesButton;
    Account account;
    TransactionAdapter transactionAdapter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "groupID";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private Group selectedGroup;
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
        mainViewModel = new ViewModelProvider(requireActivity()).get(BaseViewModel.class);

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

        selectedGroup = (Group) mainViewModel.get("selectedGroup");
        account = (Account) mainViewModel.get("account");

        TextView textView = requireActivity().findViewById(R.id.activityName);
        createExpensesButton = view.findViewById(R.id.createExpensesButton);
        groupInfoPicImageView = view.findViewById(R.id.groupInfoPicImageView);
        groupInfoCoverImageView = view.findViewById(R.id.groupInfoCoverImageView);
        groupInfoSettingsImageView = view.findViewById(R.id.groupInfoSettingsImageView);
        groupNameTextView = view.findViewById(R.id.groupNameTextView);
        groupDescTextView = view.findViewById(R.id.groupDescTextView);
        groupActivityRecyclerView = view.findViewById(R.id.groupActivityRecyclerView);
        groupInfoBackImageView = view.findViewById(R.id.groupInfoBackImageView);

        requireActivity().runOnUiThread(() -> {
            textView.setText(selectedGroup.getGroupName().toUpperCase(Locale.ROOT));
            groupNameTextView.setText(selectedGroup.getGroupName());
            groupDescTextView.setText(selectedGroup.getGroupDesc());
            groupInfoPicImageView.setImageDrawable(selectedGroup.getGroupPic().getPicture() != null ? Picture.cropToSquareAndRound(selectedGroup.getGroupPic().getPicture(), getResources()) : ResourcesCompat.getDrawable(getResources(), R.mipmap.default_profile, null));
            groupInfoCoverImageView.setImageDrawable(selectedGroup.getGroupCover().getPicture() != null ? selectedGroup.getGroupCover().getPicture() : ResourcesCompat.getDrawable(getResources(), R.mipmap.default_cover, null));
        });

        TransactionAdapter transactionAdapter1 = new TransactionAdapter(selectedGroup.getGroupTransactions(), requireActivity().getApplicationContext());
        requireActivity().runOnUiThread(() -> {
            groupActivityRecyclerView.setAdapter(transactionAdapter1);
            groupActivityRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity().getApplicationContext()));
        });

        groupInfoSettingsImageView.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.editGroupFragment2);
        });

        createExpensesButton.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.createExpensesFragment);
        });

        groupInfoBackImageView.setOnClickListener(v -> {
            textView.setText("GROUP");
            Navigation.findNavController(v).navigate(R.id.groupBalanceFragment);
        });


    }
}