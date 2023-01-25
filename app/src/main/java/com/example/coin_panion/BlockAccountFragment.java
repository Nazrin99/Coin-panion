package com.example.coin_panion;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coin_panion.classes.general.Account;
import com.example.coin_panion.classes.utility.BaseViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Document;

import java.io.FileReader;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BlockAccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BlockAccountFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public BlockAccountFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BlockAccountFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BlockAccountFragment newInstance(String param1, String param2) {
        BlockAccountFragment fragment = new BlockAccountFragment();
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
        return inflater.inflate(R.layout.fragment_block_account, container, false);
    }

    BaseViewModel mainViewModel;
    RecyclerView blockedContactRecyclerView;
    Button blockedContactSaveButton;
    ImageView blockedContactBackImageView;
    Account account;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView textView = requireActivity().findViewById(R.id.activityName);

        mainViewModel = new ViewModelProvider(requireActivity()).get(BaseViewModel.class);
        account = (Account) mainViewModel.get("account");

        blockedContactRecyclerView = view.findViewById(R.id.blockedContactRecyclerView);
        blockedContactBackImageView = view.findViewById(R.id.blockedContactBackImageView);
        blockedContactSaveButton = view.findViewById(R.id.blockedContactSaveButton);

        System.out.println(account.getBlockedContacts().size());
        BlockedAccountsAdapter blockedAccountsAdapter = new BlockedAccountsAdapter(account.getBlockedContacts(), getActivity().getApplicationContext());
        blockedContactRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity().getApplicationContext()));
        blockedContactRecyclerView.setAdapter(blockedAccountsAdapter);

        blockedContactBackImageView.setOnClickListener(v -> {
            requireActivity().runOnUiThread(() -> {
                Navigation.findNavController(v).navigate(R.id.privacyActivity);
                textView.setText("PRIVACY");
                if(account.getBlockedContacts().size() == 0){

                }
                else{
                    Toast.makeText(requireActivity(), "Changes were not saved", Toast.LENGTH_SHORT).show();
                }
            });
        });

        blockedContactSaveButton.setOnClickListener(v -> {
            // Get the list of contacts to be removed from blocked list first

            List<Account> accountFriends = account.getFriends();
            List<Account> blockedContacts = account.getBlockedContacts();
            List<Account> removeFromBlockList = blockedAccountsAdapter.getUnblockedContacts();
            System.out.println(blockedContacts.size());
            System.out.println(accountFriends.size());
            for(int i = 0; i < removeFromBlockList.size(); i++){
                blockedContacts.remove(removeFromBlockList.get(i));
                accountFriends.add(removeFromBlockList.get(i));
            }
            account.setBlockedContacts(blockedContacts);
            account.setFriends(accountFriends);
            System.out.println(blockedContacts.size());
            System.out.println(accountFriends.size());

            // Update the unblock status in firebase
            Executors.newSingleThreadExecutor().execute(() -> {
                FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                for(int i = 0; i < removeFromBlockList.size(); i++){
                    String friendAccountID = removeFromBlockList.get(i).getAccountID();
                    DocumentReference documentReference = firebaseFirestore.collection("account").document(friendAccountID);
                    firebaseFirestore.collection("friend").whereEqualTo("friendAccountID", documentReference).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            DocumentReference documentReference1 = queryDocumentSnapshots.getDocuments().get(0).getReference();
                            documentReference1.update("blocked", false).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    System.out.println("Unblocked " + documentReference1.getId());
                                }
                            });
                        }
                    });
                }
            });

            mainViewModel.put("account", account);

            requireActivity().runOnUiThread(() -> {
                textView.setText("PRIVACY");
                Navigation.findNavController(v).navigate(R.id.privacyActivity);
                Toast.makeText(requireActivity(), "Changes made successfully applied", Toast.LENGTH_SHORT).show();
            });
        });



    }
}