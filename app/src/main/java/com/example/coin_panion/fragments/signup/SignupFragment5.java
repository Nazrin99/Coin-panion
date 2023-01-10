package com.example.coin_panion.fragments.signup;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.coin_panion.FriendsActivity;
import com.example.coin_panion.R;
import com.example.coin_panion.classes.general.Account;
import com.example.coin_panion.classes.general.User;
import com.example.coin_panion.classes.utility.BaseViewModel;
import com.example.coin_panion.classes.utility.Picture;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.FoldingCube;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignupFragment5#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignupFragment5 extends Fragment {
    BaseViewModel signupViewModel;
    ProgressBar progressBar;
    TextView progressTextView;
    Runnable textCarouselRunnable;
    String[] carouselItems = {
            "Contacting your friends...",
            "Polishing your account...",
            "Cleaning up the cobwebs...",
            "Bye, bye broke era!",
            "Creating your finance logs...",
            "Finishing up"
    };
    int index = 0;
    boolean finishingUp = false;

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
            if(finishingUp){

            }
            else{
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
            }

        };

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(textCarouselRunnable, 0, 5, TimeUnit.SECONDS);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Create User object and insert to database. Get complete User object upon execution of User.insertNewUser()
        User user = new User(Long.parseLong(signupViewModel.get("phoneNumber").toString()), signupViewModel.get("firstName").toString(), signupViewModel.get("lastName").toString(), signupViewModel.get("username").toString(), signupViewModel.get("email").toString());
        try{
            assert user != null;
        } catch (AssertionError e) {
            e.printStackTrace();
            System.out.println("User is null");
        }
        user.insertNewUser(new Thread());

        // Insert profile picture into database. Get a Picture object upon execution. Is a database execution
        Drawable profilePic = (Drawable) signupViewModel.get("picture");
        Picture accountPic;
        if(profilePic == null){
            accountPic = Picture.getPictureFromDB(2501);
        }
        else{
            accountPic = Picture.insertPicIntoDB(profilePic, new Thread());
        }
        try{
            assert accountPic != null;
        } catch (AssertionError e) {
            System.out.println("Account pic returns null");
        }

        // Get default cover photo from the database
        Picture accountCover = Picture.getPictureFromDB(2500);

        // Create Account object using userID, accountPicID, and accountCoverID as foreign keys. Return an Account object with updated accountID
        Account account = new Account(user, signupViewModel.get("password").toString(), signupViewModel.get("bio").toString(), accountPic, accountCover);
        account.insertNewAccount(new Thread());

        finishingUp = true;

        // Everything is in order, pack everything in a Bundle and send to HomeActivity
        Bundle bundle = new Bundle();
        bundle.putParcelable("account", account);
        bundle.putParcelable("user", user);

        Intent intent = new Intent(requireActivity(), FriendsActivity.class);
        intent.putExtras(bundle);

        Handler handler = new Handler();
        handler.postDelayed(() -> startActivity(intent), 3000);
    }
}