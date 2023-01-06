package com.example.coin_panion;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.coin_panion.classes.Notification;
import com.example.coin_panion.classes.NotificationAdapter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoryFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HistoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HistoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HistoryFragment newInstance(String param1, String param2) {
        HistoryFragment fragment = new HistoryFragment();
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

    String[] title = {"Pay", "Request", "Noti", "Roti"};
    String[] desc = {"Pay me ok", "Requesting for payment", "Requsting for noti", "I want roti"};
    List<Notification> noti = new ArrayList<>();

    TextView titleView;
    RecyclerView RVNotification;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        titleView = getView().findViewById(R.id.TVFragmentHistoryTitle);
        titleView.setText("Notifications");

        /*Dummy data into recycler view*/
        int i = 0;
        for(String str : desc){
            noti.add(new Notification(title[i++],desc[i++]));
        }

        //TODO pass user data to get notifications
        NotificationAdapter notificationAdapter = new NotificationAdapter(noti);
        RVNotification = getView().findViewById(R.id.RVNotification);
        RVNotification.setAdapter(notificationAdapter);

        return inflater.inflate(R.layout.fragment_history, container, false);
    }
}