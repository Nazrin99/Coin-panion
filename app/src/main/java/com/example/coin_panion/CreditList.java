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
import android.widget.ImageView;

import com.example.coin_panion.classes.general.Account;
import com.example.coin_panion.classes.transaction.DebtListAdapter;
import com.example.coin_panion.classes.utility.BaseViewModel;

import org.checkerframework.checker.units.qual.A;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreditList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreditList extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CreditList() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CreditList.
     */
    // TODO: Rename and change types and number of parameters
    public static CreditList newInstance(String param1, String param2) {
        CreditList fragment = new CreditList();
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
        return inflater.inflate(R.layout.fragment_credit_list, container, false);
    }

    BaseViewModel mainViewModel;
    Account account;
    ImageView creditListBackImageView;
    RecyclerView creditListRecyclerView;
    DebtListAdapter debtListAdapter;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        account = (Account) mainViewModel.get("account");

        creditListBackImageView = view.findViewById(R.id.creditListBackImageView);
        creditListRecyclerView = view.findViewById(R.id.creditListRecyclerView);

        creditListBackImageView.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.accountFirstActivity2);
        });

        if(account.getCredits() != null){

        }
    }
}