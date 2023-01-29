package com.example.coin_panion.fragments.friends;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.coin_panion.R;
import com.example.coin_panion.classes.general.Account;
import com.example.coin_panion.classes.utility.BaseViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FriendsDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendsDetailFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FriendsDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FriendsDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FriendsDetailFragment newInstance(String param1, String param2) {
        FriendsDetailFragment fragment = new FriendsDetailFragment();
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
        account = (Account) mainViewModel.get("account");
        friend = (Account) mainViewModel.get("selectedFriend");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friends_detail, container, false);
    }

    BaseViewModel mainViewModel;
    Account friend, account;
    AppCompatTextView friendsDetailsOweTextView, friendsDetailsTheyOweTextView;
    AppCompatButton friendsDetailSettleUpCompatButton, friendsDetailRemindCompatButton;
    TextView friendsDetailUsernameTextView, friendsDetailPhoneNumberTextView;
    ImageView friendsDetailCoverImageView, friendsDetailsProfileImageView;
    ImageButton friendDetailsBackImageButton, friendDetailsSettingsImageButton;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        friendsDetailsOweTextView = view.findViewById(R.id.friendsDetailsOweTextView);
        friendsDetailsTheyOweTextView = view.findViewById(R.id.friendsDetailsTheyOweTextView);
        friendsDetailSettleUpCompatButton = view.findViewById(R.id.friendsDetailSettleUpCompatButton);
        friendsDetailRemindCompatButton = view.findViewById(R.id.friendsDetailRemindCompatButton);
        friendsDetailUsernameTextView = view.findViewById(R.id.friendsDetailUsernameTextView);
        friendsDetailPhoneNumberTextView = view.findViewById(R.id.friendsDetailPhoneNumberTextView);
        friendsDetailCoverImageView = view.findViewById(R.id.settleUpCoverImageView);
        friendsDetailsProfileImageView = view.findViewById(R.id.settleUpProfileImageView);
        friendDetailsBackImageButton = view.findViewById(R.id.settleUpBackImageButton);
        friendDetailsSettingsImageButton = view.findViewById(R.id.friendDetailsSettingsImageButton);

        requireActivity().runOnUiThread(() -> {
            friendsDetailsProfileImageView.setImageDrawable(friend.getAccountPic().getPicture() == null ? ResourcesCompat.getDrawable(getResources(), R.mipmap.default_profile, null) : friend.getAccountPic().getPicture());
            friendsDetailCoverImageView.setImageDrawable(friend.getAccountCover().getPicture() == null ? ResourcesCompat.getDrawable(getResources(), R.mipmap.default_cover, null) : friend.getAccountCover().getPicture());
            friendsDetailUsernameTextView.setText(friend.getUser().getUsername());
            friendsDetailPhoneNumberTextView.setText(friend.getUser().getPhoneNumber());
        });

        friendDetailsSettingsImageButton.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.friendsSettingsFragment);
        });

        friendDetailsBackImageButton.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.friendsFragment);
        });
    }
}