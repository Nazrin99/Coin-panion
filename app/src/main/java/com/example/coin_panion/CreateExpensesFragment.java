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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
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
    User user;
    Group currentGroup;
    ImageView expensesBackButton;
    AppCompatButton expensesFinishButton;
    AppCompatEditText transactionNameEditText, amountEditText, creditorEditText;
    AppCompatSpinner splitSpinner;
    RecyclerView debtorRecyclerView;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainViewModel = new ViewModelProvider(requireActivity()).get(BaseViewModel.class);
        account = (Account) mainViewModel.get("account");
        user = (User) mainViewModel.get("user");
        currentGroup = (Group) mainViewModel.get("selected");

        expensesBackButton = view.findViewById(R.id.expensesBackButton);
        expensesFinishButton = view.findViewById(R.id.expensesFinishButton);
        transactionNameEditText = view.findViewById(R.id.transactionNameEditText);
        amountEditText = view.findViewById(R.id.amountEditText);
        creditorEditText = view.findViewById(R.id.creditorEditText);
        splitSpinner = view.findViewById(R.id.splitSpinner);
        debtorRecyclerView = view.findViewById(R.id.debtorRecyclerView);

        List<User> userIDs = Group.retrieveGroupParticipants(currentGroup.getGroupID(), new Thread());

        creditorEditText.setEnabled(false);
        FriendExpansesItemAdapter friendExpansesItemAdapter = new FriendExpansesItemAdapter(requireActivity(), userIDs);
        debtorRecyclerView.setAdapter(friendExpansesItemAdapter);
        debtorRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));

        expensesFinishButton.setOnClickListener(v -> {
            if(Double.parseDouble(Objects.requireNonNull(amountEditText.getText()).toString()) > 0){
                amountEditText.setError(null);
                splitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String selectedItem = (String) parent.getItemAtPosition(position);
                        String transactionName = transactionNameEditText.getText().toString();
                        Long epochIssued = System.currentTimeMillis();
                        if(selectedItem.equals("Equally")){
                            Double amount = Double.parseDouble(amountEditText.getText().toString());
                            Double netAmount = amount*1.0/userIDs.size();

                            try(
                                    Connection connection = Line.getConnection();
                                    PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO transaction(group_id, creditor, debtor, trans_name, amount, epoch_issued, trans_type) VALUES(?, ?, ?, ?, ?,?,?)")
                                    ){
                                preparedStatement.setInt(1, currentGroup.getGroupID());
                                preparedStatement.setInt(2, user.getUserID());
                                preparedStatement.setString(4, transactionName);
                                preparedStatement.setDouble(5, netAmount);
                                preparedStatement.setLong(6, epochIssued);
                                preparedStatement.setString(7, "PAYMENT_ISSUE");
                                for(int i = 0; i < userIDs.size(); i++){
                                    preparedStatement.setInt(3, userIDs.get(i).getUserID());
                                    preparedStatement.addBatch();
                                }
                                preparedStatement.executeBatch();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                            Navigation.findNavController(v).navigate(R.id.groupInfoFragment);
                        }
                        else{
                            HashMap<Integer, Double> unequalUsers = friendExpansesItemAdapter.getSelectedGroupMembers();
                            List<User> userObjects = friendExpansesItemAdapter.getUserObjects();
                            try(
                                    Connection connection = Line.getConnection();
                                    PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO transaction(group_id, creditor, debtor, trans_name, amount, epoch_issued, trans_type) VALUES(?, ?, ?, ?, ?,?,?)")
                                    ){
                                preparedStatement.setInt(1, currentGroup.getGroupID());
                                preparedStatement.setInt(2, user.getUserID());
                                preparedStatement.setString(4, transactionName);
                                preparedStatement.setLong(6, epochIssued);
                                preparedStatement.setString(7, "PAYMENT_ISSUE");
                                for(int i = 0 ; i  < userObjects.size(); i++){
                                    preparedStatement.setInt(3, userObjects.get(i).getUserID());
                                    preparedStatement.setDouble(5, unequalUsers.get(userObjects.get(i).getUserID()));
                                    preparedStatement.addBatch();
                                }
                                preparedStatement.executeBatch();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                            Navigation.findNavController(v).navigate(R.id.groupInfoFragment);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
            else{
                amountEditText.setError("Amount cannot be empty!");
            }
        });
    }
}