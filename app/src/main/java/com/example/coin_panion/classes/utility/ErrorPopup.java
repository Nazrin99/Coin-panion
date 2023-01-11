package com.example.coin_panion.classes.utility;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.DialogFragment;

import com.example.coin_panion.R;

public class ErrorPopup extends DialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.wrong_code_popup, container, false);
        Button okButton = view.findViewById(R.id.BtnPopupOkButton);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the confirmation here
                dismiss();
            }
        });
        return view;
    }
}
/*TODO to call*/
//public class MainActivity extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        Button button = findViewById(R.id.button);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ConfirmationDialog confirmationDialog = new ConfirmationDialog();
//                confirmationDialog.show(getSupportFragmentManager(), "confirmation_dialog");
//            }
//        });
//    }
//}
