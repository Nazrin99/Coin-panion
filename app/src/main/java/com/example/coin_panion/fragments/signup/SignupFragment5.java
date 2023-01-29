package com.example.coin_panion.fragments.signup;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.coin_panion.MainActivity;
import com.example.coin_panion.R;
import com.example.coin_panion.classes.general.Account;
import com.example.coin_panion.classes.general.User;
import com.example.coin_panion.classes.utility.BaseViewModel;
import com.example.coin_panion.classes.utility.Hashing;
import com.example.coin_panion.classes.utility.Picture;
import com.example.coin_panion.classes.utility.PictureType;
import com.example.coin_panion.classes.utility.Validifier;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.FoldingCube;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignupFragment5#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignupFragment5 extends Fragment {
    BaseViewModel signupViewModel;
    FirebaseFirestore firebaseFirestore;
    ProgressBar progressBar;
    TextView progressTextView;
    Runnable textCarouselRunnable;
    String[] carouselItems = {
            "Contacting your friends...",
            "Polishing your account...",
            "Cleaning up the cobwebs...",
            "Bye, bye broke era!",
            "Creating your finance logs...",
    };
    int index = 0;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SignupFragment5() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SignupFragment5.
     */
    // TODO: Rename and change types and number of parameters
    public static SignupFragment5 newInstance(String param1, String param2) {
        SignupFragment5 fragment = new SignupFragment5();
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
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_signup_5, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        signupViewModel = new ViewModelProvider(requireActivity()).get(BaseViewModel.class);

        progressTextView = view.findViewById(R.id.progressTextView);
        progressBar = view.findViewById(R.id.spin_kit);
        Sprite foldingCube = new FoldingCube();
        progressBar.setIndeterminateDrawable(foldingCube);
        progressBar.setIndeterminate(true);

        textCarouselRunnable = () -> {
            if(index == 5){
                index = 0;
                requireActivity().runOnUiThread(() -> progressTextView.setText(carouselItems[index]));
            }
            else{
                requireActivity().runOnUiThread(() -> {
                    progressTextView.setText(carouselItems[index]);
                    index++;
                });
            }
        };

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(textCarouselRunnable, 0, 3500, TimeUnit.MILLISECONDS);
    }

    @Override
    public void onResume() {
        super.onResume();

        // This is where we do new account creation and insertion. HOWEVER, take note to not to construct Account object and pass to MainActivity.class
        // as the fields contain BitmapDrawable objects which will throw NotSerializable error during runtime. Just insert the info into the database
        // and pass the userID to the next MainActivity.class

        // 1 : Get the necessary User variables from the view model
        String phoneNumber = signupViewModel.get("phoneNumber").toString();
        String email = signupViewModel.get("email").toString();
        String firstName = signupViewModel.get("firstName").toString();
        String lastName = signupViewModel.get("lastName").toString();
        String password = signupViewModel.get("password").toString();
        String username = signupViewModel.get("username").toString();

        // 2 : Get the necessary Account variables from the view model
        String bio = signupViewModel.get("bio").toString();
        Uri profilePicUri = (Uri) signupViewModel.get("picture");

        // 3 : Create User DocumentReference and store the userID
        FirebaseFirestore firebaseFirestore1 = FirebaseFirestore.getInstance();
        DocumentReference userDocumentReference = firebaseFirestore1.collection("user").document();
        String userID = userDocumentReference.getId();

        // 4 : Create Account document and store the accountID
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        DocumentReference accountReference = firebaseFirestore.collection("account").document();
        String accountID = accountReference.getId();

        // 5 : Create a map containing User details
        Map<String, Object> userDetails = new HashMap<>();
        userDetails.put("firstName", firstName);
        userDetails.put("lastName", lastName);
        userDetails.put("username", username);
        userDetails.put("email", email);
        userDetails.put("phoneNumber", phoneNumber);
        userDetails.put("password", Hashing.keccakHash(password));
        userDetails.put("userID", userID);

        // 6 : Create a map containing Account details
        Map<String, Object> accountDetails = new HashMap<>();
        accountDetails.put("accountID", accountID);
        accountDetails.put("accountCover", "default_COVER.png");
        accountDetails.put("bio", bio);
        accountDetails.put("debtLimitAmount", 0);
        accountDetails.put("debtLimitEndDate", Validifier.getProperDate(new Date()));
        accountDetails.put("settleUpAccountName", "General");
        accountDetails.put("settleUpAccountNumber", "00000000");
        accountDetails.put("qrImage", "default_QR.png");
        accountDetails.put("user", userDocumentReference);

        // 7 : Get the profile picture Uri
        if(profilePicUri == null){
            // No profile picture was selected, use default profile pic reference and put into accountDetails map
            accountDetails.put("accountPic", "default_PROFILE.png");
        }
        else{
            // Profile pic selected, insert profile pic reference into accountDetails map
            String profilePicReference = Picture.constructImageReference(accountID, PictureType.PROFILE);
            accountDetails.put("accountPic", profilePicReference);

            // Insert image into Firebase storage (Asynchronous)
            Executors.newSingleThreadExecutor().execute(() -> {
                InputStream inputStream = null;
                try {
                    inputStream = requireActivity().getContentResolver().openInputStream(profilePicUri);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }

                FirebaseStorage firebaseStorage = FirebaseStorage.getInstance(Picture.storageUrl);
                StorageReference storageReference = firebaseStorage.getReference().child(profilePicReference);
                StorageMetadata metadata = new StorageMetadata.Builder().setContentType("image/png").build();
                UploadTask uploadTask = storageReference.putStream(inputStream, metadata);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        System.out.println("Profile Pic Added");
                    }
                });
            });
        }

        // 8 : Insert the User document and Account document into Firebase (Asynchronous)
        insertNewUserAndAccount(userDocumentReference, userDetails, accountReference, accountDetails);

        // 9 : Send the userID to MainActivity.class
        Intent intent = new Intent(requireActivity(), MainActivity.class);
        intent.putExtra("userID", userID);

        Handler handler = new Handler();
        handler.postDelayed(() -> startActivity(intent), 3000);
    }

    private void insertNewUserAndAccount(DocumentReference userDocumentReference, Map<String, Object> userDetails, DocumentReference accountDocumentReference, Map<String, Object> accountDetails){
        Executors.newSingleThreadExecutor().execute(() -> {
            AtomicReference<DocumentReference> documentReferenceAtomicReference = new AtomicReference<>(accountDocumentReference);
            userDocumentReference.set(userDetails).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    System.out.println("User document inserted");

                    // 9 : Insert the Account document into Firebase (Asynchronous)
                    documentReferenceAtomicReference.get().set(accountDetails).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            System.out.println("Account document inserted");
                        }
                    });
                }
            });
        });
    }
}