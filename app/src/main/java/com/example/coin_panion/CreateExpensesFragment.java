package com.example.coin_panion;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;

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

import com.example.coin_panion.classes.general.Account;
import com.example.coin_panion.classes.general.User;
import com.example.coin_panion.classes.group.FriendExpansesItemAdapter;
import com.example.coin_panion.classes.group.Group;
import com.example.coin_panion.classes.settleUp.PaymentRequestStatus;
import com.example.coin_panion.classes.transaction.Transaction;
import com.example.coin_panion.classes.transaction.TransactionStatus;
import com.example.coin_panion.classes.utility.BaseViewModel;
import com.example.coin_panion.classes.utility.Validifier;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

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
            double amount = Double.parseDouble(amountEditText.getText().toString());
            String transName = transactionNameEditText.getText().toString();
            String creditor = account.getAccountID();
            List<Account> accounts = friendExpansesItemAdapter.getAccounts();

            // 1 : Get DocumentReference for new Transaction document
            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

            // 2 : Get DocumentReference for creditor Account document
            DocumentReference creditorReference = firebaseFirestore.collection("user").document(account.getUser().getUserID());

            // 3 : Create a map of Transaction details & PaymentRequest details
            Map<String, Object> transDetails = new HashMap<>();
            transDetails.put("transName", transName);
            transDetails.put("epochIssued", Validifier.getProperDate(new Date()));
            transDetails.put("groupID", selectedGroup.getGroupID());
            transDetails.put("groupName", selectedGroup.getGroupName());
            transDetails.put("creditor", creditorReference);
            transDetails.put("payProof", null);
            transDetails.put("transStatus", TransactionStatus.PAYMENT_NOT_MADE.getType());

            if(splitSpinner.getSelectedItem().toString().equals("Equally")){
                transDetails.put("amount", amount/accounts.size());

                // 4 : For each of the member, append the member ID, transactionID, append to group object, and send to Firebase using separate Thread
                for(int i = 0; i < accounts.size(); i++){
                    // 4.1 : Create a Transaction document & PaymentRequest document
                    DocumentReference transReference = firebaseFirestore.collection("transaction").document();
                    transDetails.put("transID", transReference.getId());

                    // 4.2 : Get debtor Account document reference
                    DocumentReference debtorReference = firebaseFirestore.collection("user").document(accounts.get(i).getUser().getUserID());

                    // 4.3 : Append debtor to map
                    transDetails.put("debtor", debtorReference);

                    // 4.4 : Send the Document to the Firebase
                    Executors.newSingleThreadExecutor().execute(() -> {
                        transReference.set(transDetails).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                System.out.println("Transaction document added");
                            }
                        });
                    });

                    // 4.5 : Append Transaction object to existing group
                    Transaction transaction = new Transaction(transReference.getId(), transName, TransactionStatus.PAYMENT_NOT_MADE, selectedGroup.getGroupID(), selectedGroup.getGroupName(), account.getUser(), accounts.get(i).getUser(), amount/accounts.size(), (Date)transDetails.get("epochIssued"), null);
                    System.out.println(transDetails.get("epochIssued"));
                    selectedGroup.getGroupTransactions().add(transaction);
                }
                selectedGroup.getGroupTransactions().sort(new Comparator<Transaction>() {
                    @Override
                    public int compare(Transaction o1, Transaction o2) {
                        return o2.getEpochIssued().compareTo(o1.getEpochIssued());
                    }
                });


            }
            else{
                Map<User, Double> selectedAmounts = friendExpansesItemAdapter.getSelectedGroupMembers();
                List<Account> unequalAccounts = friendExpansesItemAdapter.getAccounts();

                System.out.println(selectedAmounts.size());
                // 4 : Get the total amount
                double totalAmount = 0;
                for(int i = 0; i < unequalAccounts.size(); i++){
                    totalAmount += selectedAmounts.get(unequalAccounts.get(i).getUser());
                }
                System.out.println(totalAmount);

                // 5 : Check if total amount entered equals the amount entered by creditor
                if(!(totalAmount == amount)){
                    Toast.makeText(requireActivity().getApplicationContext(), "Total amount across all debtors does not equal to entered amount", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 6 : For each member, append member ID, amount, transactionID, append to group object, and send to Firebase
                Iterator<Map.Entry<User, Double>> iterator = selectedAmounts.entrySet().iterator();
                while (iterator.hasNext()){
                    Map.Entry<User, Double> entry = iterator.next();
                    // 7 : Create a Transaction DocumentReference
                    DocumentReference transReference = firebaseFirestore.collection("transaction").document();
                    DocumentReference payReqReference = firebaseFirestore.collection("payment_request").document();
                    transDetails.put("transID", transReference.getId());

                    // 8 : Get debtor DocumentReference
                    DocumentReference debtorReference = firebaseFirestore.collection("user").document(entry.getKey().getUserID());
                    transDetails.put("debtor", debtorReference);

                    // 9 : Get debtor amount
                    transDetails.put("amount", entry.getValue());

                    // 10 : Send document to firebase
                    Executors.newSingleThreadExecutor().execute(() -> {
                        transReference.set(transDetails).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                System.out.println("Transaction document added!");
                            }
                        });
                    });

                    // 11 : Append transaction to group
                    Transaction transaction = new Transaction(transReference.getId(), transName, TransactionStatus.PAYMENT_NOT_MADE, selectedGroup.getGroupID(), selectedGroup.getGroupName(), account.getUser(), entry.getKey(), entry.getValue(), (Date)transDetails.get("epochIssued"), null);
                    System.out.println(transDetails.get("epochIssued"));
                    selectedGroup.getGroupTransactions().add(transaction);
                }

                selectedGroup.getGroupTransactions().sort(new Comparator<Transaction>() {
                    @Override
                    public int compare(Transaction o1, Transaction o2) {
                        return o2.getEpochIssued().compareTo(o1.getEpochIssued());
                    }
                });
            }

            requireActivity().runOnUiThread(() -> {
                Navigation.findNavController(v).navigate(R.id.groupInfoFragment);
            });

        });
    }

}