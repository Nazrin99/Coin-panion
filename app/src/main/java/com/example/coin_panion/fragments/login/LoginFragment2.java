package com.example.coin_panion.fragments.login;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coin_panion.FriendsActivity;
import com.example.coin_panion.R;
import com.example.coin_panion.classes.general.Account;
import com.example.coin_panion.classes.general.DebtLimit;
import com.example.coin_panion.classes.general.SettleUpAccount;
import com.example.coin_panion.classes.general.User;
import com.example.coin_panion.classes.notification.Notification;
import com.example.coin_panion.classes.utility.BaseViewModel;
import com.example.coin_panion.classes.utility.Hashing;
import com.example.coin_panion.classes.utility.Line;
import com.example.coin_panion.classes.utility.Picture;
import com.example.coin_panion.classes.utility.ThreadStatic;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.FoldingCube;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment2#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment2 extends Fragment {
    Bundle bundle = new Bundle();
    BaseViewModel loginViewModel;
    ProgressBar loginProgressBar;
    TextView loginProgressTextView;
    Runnable textCarouselRunnable;
    String[] carouselItems = {
            "Preparing your checklist",
            "Cleaning up the cobwebs...",
            "Hanging the decorations",
            "Creating your finance logs..."
    };
    int index = 0;
    Thread dataThread;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LoginFragment2() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment2.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment2 newInstance(String param1, String param2) {
        LoginFragment2 fragment = new LoginFragment2();
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
        return inflater.inflate(R.layout.fragment_login2, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loginViewModel = new ViewModelProvider(requireActivity()).get(BaseViewModel.class);

        loginProgressTextView = view.findViewById(R.id.loginProgressTextView);
        loginProgressBar = view.findViewById(R.id.loginSpinKit);
        requireActivity().runOnUiThread(() -> {
            Sprite foldingCube = new FoldingCube();
            loginProgressBar.setIndeterminateDrawable(foldingCube);
            loginProgressBar.setIndeterminate(true);
        });

        textCarouselRunnable = () -> {
            if(index == 3){
                System.out.println(index);
                index = 0;
                requireActivity().runOnUiThread(() -> loginProgressTextView.setText(carouselItems[index]));
                index++;
            }
            else{
                requireActivity().runOnUiThread(() -> {
                    loginProgressTextView.setText(carouselItems[index]);
                    index++;
                });
            }
        };

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(textCarouselRunnable, 0, 5, TimeUnit.SECONDS);

        AtomicReference<Object> objectAtomicReference = new AtomicReference<>(loginViewModel.get("data"));
        dataThread = new Thread(new Runnable() {
            AtomicReference<User> userAtomicReference = new AtomicReference<>();
            @Override
            public void run() {
                // Getting User from the database
                User user = User.verifyUserLogin(objectAtomicReference.get(), new Thread());

                if(user != null && user.getUserID() > 0){
                    // User exists, check for password validity
                    bundle.putParcelable("user", user);
                    boolean account_exists = false;
                    try(
                            Connection connection = Line.getConnection();
                            PreparedStatement preparedStatement = connection.prepareStatement("SELECT password FROM account WHERE user_id = ? ");
                            PreparedStatement preparedStatement1 = connection.prepareStatement("SELECT * FROM account WHERE user_id = ?");
                    ){
                        preparedStatement.setInt(1, user.getUserID());
                        ResultSet resultSet = preparedStatement.executeQuery();
                        if(resultSet.next()){
                            // Said account exists, verify password
                            if(resultSet.getString(1).equalsIgnoreCase(Hashing.keccakHash(loginViewModel.get("password").toString()))){
                                // Password if correct, construct account object pass into bundle
                                account_exists = true;
                            }
                            else{
                                // Password is incorrect, show Toast
                                requireActivity().runOnUiThread(() -> Toast.makeText(requireContext(), "Incorrect login credentials", Toast.LENGTH_SHORT).show());
                            }
                        }
                        else{
                            // Said account doesn't exists, show Toast
                            requireActivity().runOnUiThread(() -> Toast.makeText(requireContext(), "Account associated with the above credentials, does not exists!", Toast.LENGTH_SHORT).show());
                        }
                        if(account_exists){
                            System.out.println("Account exists");
                            preparedStatement1.setInt(1, user.getUserID());

                            ResultSet resultSet1 = preparedStatement1.executeQuery();
                            if(resultSet1.next()){
                                // Account info exists, create Account object

                                // 1 : Create debt limit object. Store bio string, accountID integer, picture id, and picture cover id
                                DebtLimit debtLimit = new DebtLimit(resultSet1.getDouble(4), resultSet1.getLong(5));
                                Integer accountID = resultSet1.getInt(1);
                                String password = resultSet1.getString(2);
                                String bio = resultSet1.getString(3);
                                Integer accountPicID = resultSet1.getInt(6);
                                Integer accountCoverID = resultSet1.getInt(7);

                                // 2 : Close the result set and query picture object
                                resultSet1.close();

                                Picture accountPic = Picture.getPictureFromDB(accountPicID);
                                Picture accountCover = Picture.getPictureFromDB(accountCoverID);

                                // 3 : Query account friends
                                List<User> friends = User.getFriends(accountID, new Thread());

                                // 4 : Query settle accounts
                                SettleUpAccount settleUpAccount = SettleUpAccount.retrieveSettleUpAccount(accountID, new Thread());

                                // 5 : Query notifications
                                List<Notification> notifications = Notification.getNotifications(accountID, new Thread());

                                // 6 : Create account object, and pass to bundle then switch activity
                                Account account = new Account(accountID, user, password, bio, friends, debtLimit, settleUpAccount, notifications, accountPic, accountCover);
                                bundle.putParcelable("account", account);

                                switchToLoginActivity();
                            }
                        }
                        resultSet.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    // User does exists, show Toast
                    requireActivity().runOnUiThread(() -> Toast.makeText(requireContext(), "Credentials does not exists", Toast.LENGTH_SHORT).show());
                }
            }
        });
        ThreadStatic.run(dataThread);

    }

    private void switchToLoginActivity(){
        Intent intent = new Intent(requireContext(), FriendsActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}