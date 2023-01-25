package com.example.coin_panion;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.example.coin_panion.classes.general.Account;
import com.example.coin_panion.classes.general.User;
import com.example.coin_panion.classes.group.FriendExpansesItemAdapter;
import com.example.coin_panion.classes.group.Group;
import com.example.coin_panion.classes.utility.BaseViewModel;
import com.example.coin_panion.classes.utility.Line;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateExpensesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateExpensesFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CreateExpensesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CreateExpensesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CreateExpensesFragment newInstance(String param1, String param2) {
        CreateExpensesFragment fragment = new CreateExpensesFragment();
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
        return inflater.inflate(R.layout.fragment_create_expenses, container, false);
    }

    BaseViewModel mainViewModel;
    Account account;
    Group selectedGroup;
    ImageView expensesBackButton;
    AppCompatButton expensesFinishButton;
    AppCompatEditText transactionNameEditText, amountEditText, creditorEditText;
    AppCompatSpinner splitSpinner;
    RecyclerView debtorRecyclerView;
    FriendExpansesItemAdapter friendExpansesItemAdapter;
    private static final DecimalFormat df = new DecimalFormat("0.00");


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainViewModel = new ViewModelProvider(requireActivity()).get(BaseViewModel.class);
        account = (Account) mainViewModel.get("account");
        selectedGroup = (Group) mainViewModel.get("selectedGroup");

        expensesBackButton = view.findViewById(R.id.expensesBackButton);
        expensesFinishButton = view.findViewById(R.id.expensesFinishButton);
        transactionNameEditText = view.findViewById(R.id.transactionNameEditText);
        amountEditText = view.findViewById(R.id.amountEditText);
        creditorEditText = view.findViewById(R.id.creditorEditText);
        splitSpinner = view.findViewById(R.id.splitSpinner);
        debtorRecyclerView = view.findViewById(R.id.debtorRecyclerView);
        amountEditText.setText("0");
        df.setRoundingMode(RoundingMode.UP);

        List<Account> groupMembers = selectedGroup.getGroupMembers();
        List<Account> potentialDebtors = new ArrayList<>(groupMembers);
        for(int i = 0; i < potentialDebtors.size(); i++){
            if(potentialDebtors.get(i).getAccountID().equalsIgnoreCase(account.getAccountID())){
                potentialDebtors.remove(i);
                break;
            }
        }

        System.out.println("groupMembers.size()" + groupMembers.size());
        creditorEditText.setEnabled(false);
        creditorEditText.setText(account.getUser().getUsername());
        friendExpansesItemAdapter = new FriendExpansesItemAdapter(requireActivity().getApplicationContext(), potentialDebtors);
        requireActivity().runOnUiThread(() -> {
            debtorRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
            debtorRecyclerView.setAdapter(friendExpansesItemAdapter);
        });

        amountEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() <= 0){
                    return;
                }
                else{
                    if(splitSpinner.getSelectedItem().toString().equalsIgnoreCase("Equally")){
                       Double amount = Double.parseDouble(amountEditText.getText().toString());
                       friendExpansesItemAdapter.setAmount(Double.parseDouble(df.format(amount/potentialDebtors.size())));
                       friendExpansesItemAdapter.notifyDataSetChanged();
                    }
                }
            }
        });

        splitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    friendExpansesItemAdapter.setAllSelected(true);
                    friendExpansesItemAdapter.setAmount(Double.parseDouble(amountEditText.getText().toString())/potentialDebtors.size());
                    friendExpansesItemAdapter.notifyDataSetChanged();
                }
                else if(position == 1){
                    friendExpansesItemAdapter.setAllSelected(false);
                    friendExpansesItemAdapter.notifyDataSetChanged();
                    friendExpansesItemAdapter.setAmount(-1);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        expensesBackButton.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.groupInfoFragment);
        });

        expensesFinishButton.setOnClickListener(v -> {
            Double amount = Double.parseDouble(amountEditText.getText().toString());
            String transName = transactionNameEditText.getText().toString();
            String creditor = creditorEditText.getText().toString();
            List<Account> accounts = friendExpansesItemAdapter.getAccounts();
            Map<String, Double> map = friendExpansesItemAdapter.getSelectedGroupMembers();


            if(splitSpinner.getSelectedItem().toString().equals("Equally")){
                DocumentReference creditorReference = FirebaseFirestore.getInstance().collection("account").document(account.getAccountID());
                Double netAmount = amount/accounts.size();
                String transType = "PAYMENT_ISSUE";
                Date date = new Date();

                Map<String, Object> toMap = new HashMap<>();
                toMap.put("transType", transType);
                toMap.put("transName", transName);
                toMap.put("amount", netAmount);
                toMap.put("creditor", creditorReference);
                toMap.put("groupID", selectedGroup.getGroupID());
                toMap.put("epochIssued", date);

                for(int i = 0 ; i < accounts.size(); i++){
                    DocumentReference documentReference = FirebaseFirestore.getInstance().collection("transaction").document();
                    DocumentReference debtorReference = FirebaseFirestore.getInstance().collection("account").document(accounts.get(i).getAccountID());
                    String transactionID = documentReference.getId();

                    toMap.put("transactionID", transactionID);
                    toMap.put("debtor", debtorReference);

                    documentReference.set(toMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            System.out.println("Transaction added");
                        }
                    });
                }
                Navigation.findNavController(v).navigate(R.id.groupInfoFragment);
            }
            else{

            }

        });
    }
}