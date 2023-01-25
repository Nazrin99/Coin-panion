package com.example.coin_panion.fragments.friends;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;

import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.coin_panion.R;
import com.example.coin_panion.classes.friends.Contact;
import com.example.coin_panion.classes.friends.ContactAdapter;
import com.example.coin_panion.classes.general.Account;
import com.example.coin_panion.classes.general.User;
import com.example.coin_panion.classes.utility.BaseViewModel;
import com.example.coin_panion.classes.utility.Picture;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FriendsAddFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

@SuppressWarnings("deprecation")
public class FriendsAddFragment extends Fragment {
    BaseViewModel mainViewModel;
    RecyclerView contactsRecyclerView, selectedContactsRecyclerView;
    AppCompatButton saveContactButton;
    ImageButton backButton;
    Account account;
    ContactAdapter contactAdapter;
    ContactAdapter selectedContactAdapter;
    SearchView contactSearchView;
    ProgressDialog progressDialog;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FriendsAddFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddFriendsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FriendsAddFragment newInstance(String param1, String param2) {
        FriendsAddFragment fragment = new FriendsAddFragment();
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

        return inflater.inflate(R.layout.fragment_friends_from_contact, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainViewModel = new ViewModelProvider(requireActivity()).get(BaseViewModel.class);
        account = (Account) mainViewModel.get("account");

        // View bindings
        contactsRecyclerView = view.findViewById(R.id.contactsRecyclerView);
        selectedContactsRecyclerView = view.findViewById(R.id.selectedContactsRecyclerView);
        saveContactButton = view.findViewById(R.id.saveContactButton);
        backButton = view.findViewById(R.id.friendsSettingsBackButton);
        contactSearchView = view.findViewById(R.id.contactSearchView);

        contactSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                contactAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                contactAdapter.getFilter().filter(newText);
                return false;
            }
        });

        Executors.newSingleThreadExecutor().execute(() -> {
            requireActivity().runOnUiThread(() -> {
                progressDialog = ProgressDialog.show(requireActivity(), "", "Loading contacts");
            });
            List<Contact> selectedContacts = new ArrayList<>();
            List<Contact> listOfContacts = Contact.getAllContacts(requireActivity().getContentResolver(), account.getFriends(), account);
            System.out.println("Number of contacts : " + listOfContacts.size());
            mainViewModel.put("contacts", listOfContacts);

            contactAdapter = new ContactAdapter(listOfContacts, selectedContacts, requireActivity().getApplicationContext(), true);
            selectedContactAdapter = new ContactAdapter(selectedContacts, listOfContacts, contactAdapter, requireActivity().getApplicationContext(), false);
            contactAdapter.setOtherAdapter(selectedContactAdapter);
            requireActivity().runOnUiThread(() -> {
                contactsRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity().getApplicationContext()));
                contactsRecyclerView.setAdapter(contactAdapter);
                selectedContactsRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity().getApplicationContext()));
                selectedContactsRecyclerView.setAdapter(selectedContactAdapter);
                progressDialog.dismiss();
            });
        });

        backButton.setOnClickListener(v -> {
            mainViewModel.put("contacts", contactAdapter.getBackupContacts());
            if(account.getFriends().size() > 0){
                NavDirections navDirections = FriendsAddFragmentDirections.actionFriendsAddFragmentToFriendsFragment();
                Navigation.findNavController(v).navigate(navDirections);
            }
            else{
                NavDirections navDirections = FriendsAddFragmentDirections.actionFriendsAddFragmentToFriendsDefaultFragment();
                Navigation.findNavController(v).navigate(navDirections);
            }
        });

        saveContactButton.setOnClickListener(v -> {
            // Save all the contacts into database and close the window.

            // 1 : Get list of selected contact and get their phone numbers
            List<Contact> toSave = selectedContactAdapter.getContacts();
            Set<String> phoneNumber = new HashSet<>();
            for(int i = 0; i < toSave.size(); i++){
                phoneNumber.add(toSave.get(i).getContactNumber());
            }
            Iterator<String> phones = phoneNumber.iterator();

            // 2 : Query the database for all the User objects associated with the phone number
            List<Account> newFriendsAccounts = new ArrayList<>();
            Executors.newSingleThreadExecutor().execute(() -> {
                while(phones.hasNext()){
                    String phoneNumber1 = phones.next();
//                Account account = Account.getAccountFromPhoneNumber(listOfPhoneNumbers.get(i));
                    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                    Query query = firebaseFirestore.collection("user").whereEqualTo("phoneNumber", phoneNumber1);
                    AtomicReference<User> userAtomicReference = new AtomicReference<>(null);
                    AtomicReference<DocumentReference> documentReferenceAtomicReference = new AtomicReference<>();
                    AtomicReference<Account> atomicReference = new AtomicReference<>(null);
                    query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                            User user = new User(
                                    documentSnapshot.getString("userID"),
                                    documentSnapshot.getString("phoneNumber"),
                                    documentSnapshot.getString("firstName"),
                                    documentSnapshot.getString("lastName"),
                                    documentSnapshot.getString("username"),
                                    documentSnapshot.getString("email"),
                                    documentSnapshot.getString("password")
                            );
                            DocumentReference userDocumentReference = queryDocumentSnapshots.getDocuments().get(0).getReference();
                            Query query1 = firebaseFirestore.collection("account").whereEqualTo("user", userDocumentReference);
                            query1.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                                    Account account = new Account(
                                            documentSnapshot.getString("accountID"),
                                            user,
                                            documentSnapshot.getString("bio"),
                                            documentSnapshot.getDouble("debtLimitAmount"),
                                            documentSnapshot.getDate("debtLimitEndDate"),
                                            documentSnapshot.getString("settleUpAccountName"),
                                            documentSnapshot.getString("settleUpAccountNumber"),
                                            new Picture(documentSnapshot.getString("accountPic")),
                                            new Picture(documentSnapshot.getString("accountCover")),
                                            new Picture(documentSnapshot.getString("qrImage")));
                                    atomicReference.set(account);
                                    new Thread(() -> {
                                        account.getAccountPic().getPictureFromDatabase();
                                        account.getQrImage().getPictureFromDatabase();
                                        account.getAccountCover().getPictureFromDatabase();
                                    }).start();
                                }
                            });

                        }
                    });
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(atomicReference.get() != null){
                       newFriendsAccounts.add(atomicReference.get());
                    }
                }

                // 3 : Insert the list of new friends inside the friend table in the database, asynchronous
                Map<String, Object> toInsert = new HashMap<>();
                toInsert.put("accountID", account.getAccountID());
                toInsert.put("blocked", false);

                FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                CollectionReference collectionReference = firebaseFirestore.collection("friend");
                CollectionReference accountCollection = firebaseFirestore.collection("account");
                for(int i = 0 ; i < newFriendsAccounts.size(); i++){
                    toInsert.put("friendAccountID", accountCollection.document(newFriendsAccounts.get(i).getAccountID()));
                    collectionReference.add(toInsert).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            System.out.println("Friend document with id " + documentReference.getId() + " added!");
                        }
                    });
                    account.getFriends().add(newFriendsAccounts.get(i));
                    System.out.println("Updated list of new friends");
                }
            });

            // 4 : Append the list of new friends to the existing list of friends for the current account, update the data inside the viewmodel

            // 5 : Navigate to the original fragment
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                if(account.getFriends().size() == 0){
                    System.out.println("List of friends is empty");
                    NavDirections navDirections = FriendsAddFragmentDirections.actionFriendsAddFragmentToFriendsDefaultFragment();
                    Navigation.findNavController(view).navigate(navDirections);
                    progressDialog.dismiss();
                }
                else{
                    NavDirections navDirections = FriendsAddFragmentDirections.actionFriendsAddFragmentToFriendsFragment();
                    Bundle bundle = new Bundle();
                    Navigation.findNavController(view).navigate(navDirections);
                    progressDialog.dismiss();
                }
            }, 5000);

            requireActivity().runOnUiThread(() -> {
                progressDialog = ProgressDialog.show(requireActivity(), "", "Updating list of friends");

            });

        });
    }


}