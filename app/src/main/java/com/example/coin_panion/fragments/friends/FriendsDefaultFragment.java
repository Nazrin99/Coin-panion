package com.example.coin_panion.fragments.friends;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.coin_panion.R;
import com.example.coin_panion.classes.utility.BaseViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FriendsDefaultFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendsDefaultFragment extends Fragment {
    BaseViewModel friendsViewModel;
    AppCompatButton addFriendsDefaultButton;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FriendsDefaultFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FriendsDefaultFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FriendsDefaultFragment newInstance(String param1, String param2) {
        FriendsDefaultFragment fragment = new FriendsDefaultFragment();
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
        return inflater.inflate(R.layout.fragment_friends_default, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

            friendsViewModel = new ViewModelProvider(requireActivity()).get(BaseViewModel.class);
            addFriendsDefaultButton = view.findViewById(R.id.addFriendsDefaultButton);

            addFriendsDefaultButton.setOnClickListener(v -> {
                // Switch to contact view to add friends
                NavDirections navDirections = FriendsDefaultFragmentDirections.actionFriendsDefaultFragmentToFriendsAddFragment();
                Navigation.findNavController(view).navigate(navDirections);
            });

    }
}