package com.example.coin_panion;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coin_panion.classes.friends.RemoveFriendAdapter;
import com.example.coin_panion.classes.general.Account;
import com.example.coin_panion.classes.group.Group;
import com.example.coin_panion.classes.utility.BaseViewModel;
import com.example.coin_panion.classes.utility.Picture;
import com.example.coin_panion.classes.utility.PictureType;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.checkerframework.checker.units.qual.A;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditGroupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditGroupFragment extends Fragment {
    BaseViewModel mainViewModel;
    Group selectedGroup;
    Account account;
    List<Account> groupMembers;
    Uri groupPic, groupCover;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public EditGroupFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EditGroupFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditGroupFragment newInstance(String param1, String param2) {
        EditGroupFragment fragment = new EditGroupFragment();
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

    ImageView IBUploadGroupBackground, IBUploadGroupProfilePic, editGroupBackImageView;
    EditText ETGroupName, ETGroupDescription, editGroupTypeEditText;
    RecyclerView RVRemoveAddFriendGroup;
    AppCompatButton editGroupInfoConfirmCompatButton, editGroupInfoDeleteGroupButton;
    RemoveFriendAdapter removeFriendAdapter;
    boolean pictureChanged = false, coverChanged = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_group, container, false);

        /*TODO function to take all the variable instance and upload it*/
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView textView = requireActivity().findViewById(R.id.activityName);

        mainViewModel = new ViewModelProvider(requireActivity()).get(BaseViewModel.class);
        selectedGroup = (Group) mainViewModel.get("selectedGroup");
        account = (Account) mainViewModel.get("account");

        editGroupBackImageView = view.findViewById(R.id.editGroupBackImageView);
        IBUploadGroupBackground = view.findViewById(R.id.IBUploadGroupBackground);
        IBUploadGroupProfilePic = view.findViewById(R.id.IBUploadGroupProfilePic);
        ETGroupName = view.findViewById(R.id.ETGroupName);
        ETGroupDescription = view.findViewById(R.id.ETGroupDescription);
        RVRemoveAddFriendGroup = view.findViewById(R.id.RVRemoveAddFriendGroup);
        editGroupInfoConfirmCompatButton = view.findViewById(R.id.editGroupInfoConfirmCompatButton);
        editGroupTypeEditText = view.findViewById(R.id.editGroupTypeEditText);
        editGroupInfoDeleteGroupButton = view.findViewById(R.id.editGroupInfoDeleteGroupButton);

        groupMembers = selectedGroup.getGroupMembers();
        /*Initialize adapter*/
        removeFriendAdapter = new RemoveFriendAdapter(groupMembers ,requireActivity().getApplicationContext());

        ETGroupName.setText(selectedGroup.getGroupName());
        ETGroupDescription.setText(selectedGroup.getGroupDesc());
        IBUploadGroupProfilePic.setImageDrawable(selectedGroup.getGroupPic().getPicture() != null ? Picture.cropToSquareAndRound(selectedGroup.getGroupPic().getPicture(), getResources()) : ResourcesCompat.getDrawable(getResources(), R.mipmap.default_profile, null));
        IBUploadGroupBackground.setImageDrawable(selectedGroup.getGroupCover().getPicture() != null ? selectedGroup.getGroupCover().getPicture() : ResourcesCompat.getDrawable(getResources(), R.mipmap.default_cover, null));
        editGroupTypeEditText.setText(selectedGroup.getGroupType());

        requireActivity().runOnUiThread(() -> {
            /*Set the adapter*/
            RVRemoveAddFriendGroup.setAdapter(removeFriendAdapter);

            /*Set layout manager*/
            RVRemoveAddFriendGroup.setLayoutManager(new LinearLayoutManager(requireActivity()));
            removeFriendAdapter.notifyDataSetChanged();
        });
        /*Notify Changes in adapter*/
        editGroupInfoConfirmCompatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 1: Delete the group members from the group
                List<Account> removedMembers = removeFriendAdapter.getRemovedMembers();
                for(int i = 0; i < removedMembers.size(); i++) {
                    for (int j = 0; j < groupMembers.size(); j++) {
                        if (removedMembers.get(i).getAccountID().equalsIgnoreCase(groupMembers.get(j).getAccountID())) {
                            groupMembers.remove(j);
                            break;
                        }
                    }
                }

                // 2 : Store the group details in a map
                Map<String, Object> groupDetails = new HashMap<>();
                groupDetails.put("groupName", ETGroupName.getText().toString());
                groupDetails.put("groupDesc", ETGroupDescription.getText().toString());
                groupDetails.put("groupType", editGroupTypeEditText.getText().toString());

                // 3 : Update group details for runtime
                selectedGroup.setGroupName(ETGroupName.getText().toString());
                selectedGroup.setGroupDesc(ETGroupDescription.getText().toString());
                selectedGroup.setGroupType(editGroupTypeEditText.getText().toString());
                if(groupPic != null){
                    String ref = Picture.constructImageReference(selectedGroup.getGroupID(), PictureType.PROFILE);
                    Picture groupPicture = new Picture(Picture.constructDrawableFromUri(groupPic, requireActivity().getContentResolver()), Picture.constructImageReference(selectedGroup.getGroupID(), PictureType.PROFILE), PictureType.PROFILE);
                    selectedGroup.setGroupPic(groupPicture);

                    Executors.newSingleThreadExecutor().execute(() -> {
                        InputStream inputStream = null;

                        try {
                            inputStream = Picture.compressInputStream(requireActivity().getContentResolver().openInputStream(groupPic));
                        } catch (FileNotFoundException e) {
                            throw new RuntimeException(e);
                        }

                        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance(Picture.storageUrl);
                        StorageReference imageRef = firebaseStorage.getReference().child(ref);
                        StorageMetadata metadata = new StorageMetadata.Builder().setContentType("image/png").build();
                        UploadTask uploadTask = imageRef.putStream(inputStream, metadata);
                        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                System.out.println("Group pic updated");
                            }
                        });
                    });
                    groupDetails.put("groupPic", ref);
                }
                if(groupCover != null){
                    String ref = Picture.constructImageReference(selectedGroup.getGroupID(), PictureType.COVER);
                    Picture groupCoverPicture = new Picture(Picture.constructDrawableFromUri(groupCover, requireActivity().getContentResolver()), Picture.constructImageReference(selectedGroup.getGroupID(), PictureType.COVER), PictureType.COVER);
                    selectedGroup.setGroupCover(groupCoverPicture);

                    Executors.newSingleThreadExecutor().execute(() -> {
                        InputStream inputStream = null;

                        try {
                            inputStream = Picture.compressInputStream(requireActivity().getContentResolver().openInputStream(groupCover));
                        } catch (FileNotFoundException e) {
                            throw new RuntimeException(e);
                        }

                        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance(Picture.storageUrl);
                        StorageReference imageRef = firebaseStorage.getReference().child(ref);
                        StorageMetadata metadata = new StorageMetadata.Builder().setContentType("image/png").build();
                        UploadTask uploadTask = imageRef.putStream(inputStream, metadata);
                        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                System.out.println("Group cover updated");
                            }
                        });
                    });
                    groupDetails.put("groupCover", ref);
                }

                // 4 : Update group details in Firebase (Asynchronous)
                Executors.newSingleThreadExecutor().execute(() -> {
                    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                    DocumentReference groupDocumentReference = firebaseFirestore.collection("group").document(selectedGroup.getGroupID());
                    groupDocumentReference.update(groupDetails).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(requireActivity(), "Group details updated successfully", Toast.LENGTH_SHORT).show();
                        }
                    });
                });

                // 5 : Update group member details in Firebase (Asynchronous)
                Executors.newSingleThreadExecutor().execute(() -> {
                    DocumentReference groupDocumentReference = FirebaseFirestore.getInstance().collection("group").document(selectedGroup.getGroupID());
                    for(int i = 0; i < removedMembers.size(); i++){
                        DocumentReference accountDocumentReference = FirebaseFirestore.getInstance().collection("account").document(removedMembers.get(i).getAccountID());
                        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                        Query query = firebaseFirestore.collection("account_group")
                                .whereEqualTo("groupID", groupDocumentReference)
                                .whereEqualTo("accountID", accountDocumentReference);
                        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                if(queryDocumentSnapshots != null){
                                    if(!queryDocumentSnapshots.isEmpty()){
                                        DocumentReference accountGroupReference = queryDocumentSnapshots.getDocuments().get(0).getReference();
                                        accountGroupReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                System.out.println("Group member removed");
                                            }
                                        });
                                    }
                                }
                            }
                        });

                    }
                });

                // 6 : Set activity name
                textView.setText("GROUPS");

                // 7 : Navigate to the group info page
                Navigation.findNavController(v).navigate(R.id.groupInfoFragment);
            }
        });

        editGroupBackImageView.setOnClickListener(v -> {
            List<Account> removedMembers = removeFriendAdapter.getRemovedMembers();
            groupMembers.addAll(removedMembers);
            System.out.println(groupMembers.size());

            Navigation.findNavController(v).navigate(R.id.groupInfoFragment);
        });

        IBUploadGroupBackground.setOnClickListener(v -> {
            imageChooser(2);
        });

        IBUploadGroupProfilePic.setOnClickListener(v -> {
            imageChooser(1);
        });

        editGroupInfoDeleteGroupButton.setOnClickListener(v -> {
            boolean confirm = false;

            LayoutInflater inflater = (LayoutInflater) requireActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
            View popupView = inflater.inflate(R.layout.wrong_code_popup, null);
            int width = ViewGroup.LayoutParams.WRAP_CONTENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            boolean focusable = false;
            final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

            popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);

            AppCompatButton yesButton, noButton;
            yesButton = popupView.findViewById(R.id.popupYesButton);
            noButton = popupView.findViewById(R.id.popupNoButton);

            yesButton.setOnClickListener(yes -> {
                requireActivity().runOnUiThread(() -> {
                    popupWindow.dismiss();
                });
                // 1 : Delete the group from the current Account
                List<Group> groups = account.getGroups();
                for(int i = 0; i < groups.size(); i++){
                    if(selectedGroup.getGroupID().equalsIgnoreCase(groups.get(i).getGroupID())){
                        groups.remove(i);
                    }
                }

                // 2 : Delete account_group entity from Firebase (Asynchronous)
                Executors.newSingleThreadExecutor().execute(() -> {
                    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

                    // 2.1 : Create Account DocumentReference
                    DocumentReference accountDocumentReference = firebaseFirestore.collection("account").document(account.getAccountID());

                    // 2.2 : Create Group DocumentReference
                    DocumentReference groupDocumentReference = firebaseFirestore.collection("group").document(selectedGroup.getGroupID());

                    // 2.3 : Query the account_group document
                    Query query = firebaseFirestore.collection("account_group")
                            .whereEqualTo("accountID", accountDocumentReference)
                            .whereEqualTo("groupID", groupDocumentReference);
                    query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if(queryDocumentSnapshots != null){
                                if(!queryDocumentSnapshots.isEmpty()){
                                    // 2.4 : Delete the account_group document
                                    DocumentReference accountGroupReference = queryDocumentSnapshots.getDocuments().get(0).getReference();
                                    accountGroupReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            System.out.println("Account deleted from group " + selectedGroup.getGroupName());
                                        }
                                    });
                                }
                            }
                        }
                    });

                    if(selectedGroup.getGroupMembers().size() <= 1){
                        // I am the last member of the group, therefore delete everything related to the group
                        // 2.4 : Delete group from Firebase
                        FirebaseFirestore firebaseFirestore1 = FirebaseFirestore.getInstance();

                        groupDocumentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                System.out.println("Group " + selectedGroup.getGroupName() + " deleted entirely");
                            }
                        });

                        // 2.5 : Delete any group related images from Firebase
                        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance(Picture.storageUrl);
                        StorageReference groupPicReference = firebaseStorage.getReference().child(Picture.constructImageReference(selectedGroup.getGroupID(), PictureType.PROFILE));
                        StorageReference groupCoverReference = firebaseStorage.getReference().child(Picture.constructImageReference(selectedGroup.getGroupID(), PictureType.COVER));

                        groupPicReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                System.out.println("Group " + selectedGroup.getGroupName() + " profile pic deleted");
                            }
                        });

                        groupCoverReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                System.out.println("Group " + selectedGroup.getGroupName() + " cover pic deleted");
                            }
                        });

                    }
                });

                // 2.6 : Navigate to list of group fragment
                requireActivity().runOnUiThread(() -> {
                    if(groups.size() <= 0){
                        Navigation.findNavController(v).navigate(R.id.groupFirstFragment);
                    }
                    else{
                        Navigation.findNavController(v).navigate(R.id.groupBalanceFragment);
                    }
                });
            });

            noButton.setOnClickListener(no -> {
               popupWindow.dismiss();
            });

        });

    }

    public void imageChooser(int requestCode){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        if(requestCode == 1){
            startActivityForResult(Intent.createChooser(intent, "Select group picture"), requestCode);
        }
        else if(requestCode == 2){
            startActivityForResult(Intent.createChooser(intent, "Select group cover"), requestCode);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK && data != null){
            Uri imageUri = data.getData();
            if(imageUri != null){
                if(requestCode == 1){
                    groupPic = imageUri;
                    Drawable drawable = Picture.constructDrawableFromUri(groupPic, requireActivity().getContentResolver());
                    requireActivity().runOnUiThread(() -> {
                        IBUploadGroupProfilePic.setImageDrawable(Picture.cropToSquareAndRound(drawable, getResources()));
                    });
                }
                else if(requestCode == 2){
                    groupCover = imageUri;
                    Drawable drawable = Picture.constructDrawableFromUri(groupCover, requireActivity().getContentResolver());
                    requireActivity().runOnUiThread(() -> {
                        IBUploadGroupBackground.setImageDrawable(drawable);
                    });
                }
            }
        }
    }
}
