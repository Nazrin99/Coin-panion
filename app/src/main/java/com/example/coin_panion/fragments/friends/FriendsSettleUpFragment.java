package com.example.coin_panion.fragments.friends;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.coin_panion.R;
import com.example.coin_panion.classes.general.Account;
import com.example.coin_panion.classes.transaction.Transaction;
import com.example.coin_panion.classes.transaction.TransactionStatus;
import com.example.coin_panion.classes.utility.BaseViewModel;
import com.example.coin_panion.classes.utility.Picture;
import com.example.coin_panion.classes.utility.PictureType;
import com.example.coin_panion.classes.utility.Validifier;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FriendsSettleUpFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendsSettleUpFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FriendsSettleUpFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FriendsSettleUpFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FriendsSettleUpFragment newInstance(String param1, String param2) {
        FriendsSettleUpFragment fragment = new FriendsSettleUpFragment();
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
        debt = (Transaction) mainViewModel.get("debt");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friends_settle_up, container, false);
    }

    BaseViewModel mainViewModel;
    Transaction debt;
    Account account, creditorAccount;
    AppCompatButton settleUpSaveAppCompatButton, settleUpUploadButton;
    ImageButton settleUpBackImageButton;
    ImageView settleUpCoverImageView, settleUpProfileImageView, settleUpQRImageView;
    TextView settleUpUsernameTextView, settleUpPhoneNumberTextView, settleUpAmountTextView,
            settleUpTransName, settleUpGroupName, settleUpAccountNumberTextView;
    Uri paymentProof;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Bind views
        settleUpSaveAppCompatButton = view.findViewById(R.id.settleUpSaveAppCompatButton);
        settleUpUploadButton = view.findViewById(R.id.settleUpUploadButton);
        settleUpBackImageButton = view.findViewById(R.id.settleUpBackImageButton);
        settleUpCoverImageView = view.findViewById(R.id.settleUpCoverImageView);
        settleUpProfileImageView = view.findViewById(R.id.settleUpProfileImageView);
        settleUpQRImageView = view.findViewById(R.id.settleUpQRImageView);
        settleUpUsernameTextView = view.findViewById(R.id.settleUpUsernameTextView);
        settleUpPhoneNumberTextView = view.findViewById(R.id.settleUpPhoneNumberTextView);
        settleUpAmountTextView = view.findViewById(R.id.settleUpAmountTextView);
        settleUpTransName = view.findViewById(R.id.settleUpTransName);
        settleUpGroupName = view.findViewById(R.id.settleUpGroupName);
        settleUpAccountNumberTextView = view.findViewById(R.id.settleUpAccountNumberTextView);

        creditorAccount = getCreditorAccount(debt.getCreditorID().getUserID());

        // Bind values
        requireActivity().runOnUiThread(() -> {
            settleUpCoverImageView.setImageDrawable(creditorAccount.getAccountCover().getPicture() != null ? creditorAccount.getAccountCover().getPicture() : ResourcesCompat.getDrawable(getResources(), R.mipmap.default_cover, null));
            settleUpProfileImageView.setImageDrawable(creditorAccount.getAccountPic().getPicture() != null ? Picture.cropToSquareAndRound(creditorAccount.getAccountPic().getPicture(), getResources()) : ResourcesCompat.getDrawable(getResources(), R.mipmap.default_profile, null));
            settleUpQRImageView.setImageDrawable(creditorAccount.getQrImage().getPicture() != null ? creditorAccount.getQrImage().getPicture() : ResourcesCompat.getDrawable(getResources(), R.mipmap.default_qr, null));
            settleUpUsernameTextView.setText(creditorAccount.getUser().getUsername());
            settleUpPhoneNumberTextView.setText(creditorAccount.getUser().getPhoneNumber());
            settleUpAmountTextView.setText(String.format(Locale.ENGLISH, "%.2f", debt.getAmount()));
            settleUpTransName.setText(debt.getTransName());
            settleUpGroupName.setText(debt.getGroupName());
            settleUpAccountNumberTextView.setText(creditorAccount.getSettleUpAccountNumber());
        });

        // Bind listener
        settleUpBackImageButton.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.debtList);
        });

        settleUpUploadButton.setOnClickListener(v -> {
            imageChooser();
        });

        settleUpSaveAppCompatButton.setOnClickListener(v -> {
            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
            // Create Transaction document
            DocumentReference transactionReference = firebaseFirestore.collection("transaction").document();

            // Create a map of Transaction details
            Map<String, Object> transactionMap = new HashMap<>();
            transactionMap.put("amount", debt.getAmount());
            transactionMap.put("creditor", firebaseFirestore.collection("user").document(debt.getCreditorID().getUserID()));
            transactionMap.put("debtor", firebaseFirestore.collection("user").document(debt.getDebtorID().getUserID()));
            transactionMap.put("epochIssued", Validifier.getProperDate(new Date()));
            transactionMap.put("groupID", debt.getGroupID());
            transactionMap.put("groupName", debt.getGroupName());
            transactionMap.put("transID", transactionReference.getId());
            transactionMap.put("transName", debt.getTransName());
            transactionMap.put("transStatus", TransactionStatus.PAYMENT_REQUEST.getType());
            transactionMap.put("payProof", Picture.constructImageReference(transactionReference.getId(), PictureType.PAYMENT));

            // Add the document to Firebase
            Executors.newSingleThreadExecutor().execute(() -> {
                transactionReference.set(transactionMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        System.out.println("Payment request transaction document added!");
                    }
                });
            });

            // Construct Drawable & pass to newly created Picture object
            Drawable drawable = Picture.constructDrawableFromUri(paymentProof, requireActivity().getContentResolver());
            Picture paymentProofPic = new Picture(drawable, Picture.constructImageReference(transactionReference.getId(), PictureType.PAYMENT), PictureType.PAYMENT);

            // Create a new transaction object and pass
        });

    }

    void imageChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Upload your payment receipt"), 50);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == 50 && data != null){
            if(data.getData() != null){
                paymentProof = data.getData();
                settleUpUploadButton.setText("Uploaded. Click to Reupload");
            }
        }
    }

    private Account getCreditorAccount(String userID){
        Account creditorAccount = (Account) mainViewModel.getAccountWithUserID(userID);
        if(creditorAccount == null){
            // Query database for values
            DocumentReference userReference = FirebaseFirestore.getInstance().collection("user").document(userID);
            creditorAccount = Account.getAccount(userReference);
        }
        return creditorAccount;
    }
}