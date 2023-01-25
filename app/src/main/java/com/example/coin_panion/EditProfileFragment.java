package com.example.coin_panion;

import static android.app.Activity.RESULT_OK;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coin_panion.classes.general.Account;
import com.example.coin_panion.classes.utility.BaseViewModel;
import com.example.coin_panion.classes.utility.Picture;
import com.example.coin_panion.classes.utility.PictureType;
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

public class EditProfileFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        return view;
    }

    AppCompatButton editProfileCancelButton, editProfileSaveButton;
    Account account;
    BaseViewModel mainViewModel;
    EditText editProfileUsernameEditText, editProfileFirstNameEditText,
            editProfileLastNameEditText, editProfileBioEditText,
            editProfilePhoneNumberEditText, editProfileEmailEditText,
            editProfileAccountNumberEditText, editProfileAccountNameEditText, editProfileDebtLimitEditText;
    CalendarView editProfileDebtLimitCalendarView;
    TextView editProfileEditPictureTextView;
    ImageView editProfilePicImageView, editProfileQRImageView;
    Date debtLimitEndDate;
    Uri profilePic, qRImage;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Binding views
        editProfileCancelButton = view.findViewById(R.id.editProfileCancelButton);
        editProfileSaveButton = view.findViewById(R.id.editProfileSaveButton);
        editProfileUsernameEditText = view.findViewById(R.id.editProfileUsernameEditText);
        editProfileFirstNameEditText = view.findViewById(R.id.editProfileFirstNameEditText);
        editProfileLastNameEditText = view.findViewById(R.id.editProfileLastNameEditText);
        editProfileBioEditText = view.findViewById(R.id.editProfileBioEditText);
        editProfilePhoneNumberEditText = view.findViewById(R.id.editProfilePhoneNumberEditText);
        editProfileEmailEditText = view.findViewById(R.id.editProfileEmailEditText);
        editProfileAccountNumberEditText = view.findViewById(R.id.editProfileAccountNumberEditText);
        editProfileEditPictureTextView = view.findViewById(R.id.editProfileEditPictureTextView);
        editProfilePicImageView = view.findViewById(R.id.editProfilePicImageView);
        editProfileQRImageView = view.findViewById(R.id.editProfileQRImageView);
        editProfileAccountNameEditText = view.findViewById(R.id.editProfileAccountNameEditText);
        editProfileDebtLimitCalendarView = view.findViewById(R.id.editProfileDebtLimitCalendarView);
        editProfileDebtLimitEditText = view.findViewById(R.id.editProfileDebtLimitEditText);


        // Bind view model and get account
        mainViewModel = new ViewModelProvider(requireActivity()).get(BaseViewModel.class);
        account = (Account) mainViewModel.get("account");


        // Values to fields
        editProfileUsernameEditText.setText(account.getUser().getUsername());
        editProfileFirstNameEditText.setText(account.getUser().getFirstName());
        editProfileLastNameEditText.setText(account.getUser().getLastName());
        editProfileBioEditText.setText(account.getBio());
        editProfileEmailEditText.setText(account.getUser().getEmail());
        editProfileDebtLimitEditText.setText(Double.toString(account.getDebtLimitAmount()));
        editProfileAccountNameEditText.setText(account.getSettleUpAccountName());
        editProfilePhoneNumberEditText.setText(account.getUser().getPhoneNumber());
        editProfileAccountNumberEditText.setText(account.getSettleUpAccountNumber());
        editProfilePicImageView.setImageDrawable(account.getAccountPic().getPicture() != null ? Picture.cropToSquareAndRound(account.getAccountPic().getPicture(), getResources()) : Picture.cropToSquareAndRound(ResourcesCompat.getDrawable(getResources(), R.mipmap.default_profile, null), getResources()));
        editProfileQRImageView.setImageDrawable(account.getQrImage().getPicture() != null ? account.getQrImage().getPicture() : ResourcesCompat.getDrawable(getResources(), R.mipmap.default_qr, null));
        editProfileDebtLimitEditText.setText(Double.toString(account.getDebtLimitAmount()));
        editProfileDebtLimitCalendarView.setDate(account.getDebtLimitEndDate() == null ? System.currentTimeMillis() : account.getDebtLimitEndDate().getTime());
        debtLimitEndDate = account.getDebtLimitEndDate() != null ? account.getDebtLimitEndDate() : new Date(System.currentTimeMillis());

        editProfilePhoneNumberEditText.setEnabled(false);

        // Bind onClicks and listeners
        editProfileEditPictureTextView.setOnClickListener(v -> {
            imageChooser(1);
        });

        editProfileQRImageView.setOnClickListener(v -> {
            imageChooser(2);
        });

        editProfileCancelButton.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.accountFirstActivity2);
            Toast.makeText(requireActivity(), "Changes made are not saved", Toast.LENGTH_SHORT).show();
        });

        editProfileDebtLimitCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                Date date = new Date(year-1900, month, dayOfMonth);
                if(date.getTime() < System.currentTimeMillis()){
                    Toast.makeText(requireActivity(), "Debt limit end date cannot be earlier than current time", Toast.LENGTH_SHORT);
                }
                else{
                    debtLimitEndDate = date;
                    System.out.println(date);
                }
            }
        });

        editProfileSaveButton.setOnClickListener(v -> {
            // Create a map of all the values in the fragment
            Map<String, Object> userDetails = new HashMap<>();
            userDetails.put("firstName", editProfileFirstNameEditText.getText().toString());
            userDetails.put("lastName", editProfileLastNameEditText.getText().toString());
            userDetails.put("email", editProfileEmailEditText.getText().toString());
            userDetails.put("username", editProfileUsernameEditText.getText().toString());

            Map<String, Object> accountDetails = new HashMap<>();
            accountDetails.put("bio", editProfileBioEditText.getText().toString());
            accountDetails.put("settleUpAccountNumber", editProfileAccountNumberEditText.getText().toString());
            accountDetails.put("settleUpAccountName", editProfileAccountNameEditText.getText().toString());
            accountDetails.put("debtLimitAmount", Double.parseDouble(editProfileDebtLimitEditText.getText().toString()));
            accountDetails.put("debtLimitEndDate", debtLimitEndDate);
            if(profilePic != null){
                String profilePicReference = Picture.constructImageReference(account.getAccountID(), PictureType.PROFILE);
                accountDetails.put("accountPic", profilePicReference);

                Executors.newSingleThreadExecutor().execute(() -> {
                    InputStream inputStream = null;

                    try {
                        inputStream = requireActivity().getContentResolver().openInputStream(profilePic);
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }

                    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance(Picture.storageUrl);
                    StorageReference imageRef = firebaseStorage.getReference().child(profilePicReference);
                    StorageMetadata metadata = new StorageMetadata.Builder().setContentType("image/png").build();
                    UploadTask uploadTask = imageRef.putStream(inputStream, metadata);
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            System.out.println("Profile added");
                        }
                    });
                });

                account.setAccountPic(new Picture(Picture.constructDrawableFromUri(profilePic, requireActivity().getContentResolver()), Picture.constructImageReference(account.getAccountID(), PictureType.PROFILE), PictureType.PROFILE));
            }
            if(qRImage != null){
                String qrReference = Picture.constructImageReference(account.getAccountID(), PictureType.QR);
                accountDetails.put("qrImage", qrReference);

                Executors.newSingleThreadExecutor().execute(() -> {
                    InputStream inputStream = null;

                    try {
                        inputStream = requireActivity().getContentResolver().openInputStream(qRImage);
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }

                    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance(Picture.storageUrl);
                    StorageReference imageRef = firebaseStorage.getReference().child(qrReference);
                    StorageMetadata metadata = new StorageMetadata.Builder().setContentType("image/png").build();
                    UploadTask uploadTask = imageRef.putStream(inputStream, metadata);
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            System.out.println("QR added");
                        }
                    });
                });

                account.setQrImage(new Picture(Picture.constructDrawableFromUri(qRImage, requireActivity().getContentResolver()), Picture.constructImageReference(account.getAccountID(), PictureType.QR), PictureType.QR));
            }

            // Update user details for this account
            account.getUser().setUsername(editProfileUsernameEditText.getText().toString());
            account.getUser().setEmail(editProfileEmailEditText.getText().toString());
            account.getUser().setLastName(editProfileLastNameEditText.getText().toString());
            account.getUser().setFirstName(editProfileFirstNameEditText.getText().toString());

            // Update account details for this account
            account.setBio(editProfileBioEditText.getText().toString());
            account.setSettleUpAccountName(editProfileAccountNameEditText.getText().toString());
            account.setSettleUpAccountNumber(editProfileAccountNumberEditText.getText().toString());
            account.setDebtLimitAmount(Double.parseDouble(editProfileDebtLimitEditText.getText().toString()));
            account.setDebtLimitEndDate(debtLimitEndDate == null ? new Date(Instant.now().getEpochSecond()) : debtLimitEndDate);
            System.out.println(account.getDebtLimitEndDate());

            // Update user details in cloud asynchronously
            Executors.newSingleThreadExecutor().execute(() -> {
                FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

                // 1 : Get DocumentReference for this User object
                DocumentReference userDocumentReference = firebaseFirestore.collection("user").document(account.getUser().getUserID());

                // 2 : Update User object
                userDocumentReference.update(userDetails).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        System.out.println("User fields updated!");
                    }
                });

                // 3 : Get DocumentReference for this Account object
                DocumentReference accountDocumentReference = firebaseFirestore.collection("account").document(account.getAccountID());

                // 4 : Update Account object
                accountDocumentReference.update(accountDetails).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        System.out.println("Account fields updated!");
                    }
                });
            });

            Navigation.findNavController(v).navigate(R.id.accountFirstActivity2);
            Toast.makeText(requireActivity(), "Account Details Updated", Toast.LENGTH_SHORT).show();
        });
    }

    public void imageChooser(int requestCode){
        Executors.newSingleThreadExecutor().execute(() -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select your new profile picture"), requestCode);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data != null){
            if(data.getData() != null){
                Uri imageUri = data.getData();
                if(requestCode == 1){
                    profilePic = imageUri;
                    System.out.println(profilePic);
                    Drawable drawable = Picture.constructDrawableFromUri(profilePic, requireActivity().getContentResolver());
                    requireActivity().runOnUiThread(() -> {
                        editProfilePicImageView.setImageDrawable(Picture.cropToSquareAndRound(drawable, getResources()));
                    });
                }
                else if(requestCode == 2){
                    qRImage = imageUri;
                    Drawable drawable = Picture.constructDrawableFromUri(qRImage, requireActivity().getContentResolver());
                    requireActivity().runOnUiThread(() -> {
                        editProfileQRImageView.setImageDrawable(drawable);
                    });
                }
            }
        }
    }
}