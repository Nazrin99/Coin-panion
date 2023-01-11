package com.example.coin_panion.classes.utility;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.example.coin_panion.R;

class ConfirmationDialog extends DialogFragment {

    private boolean isConfirmed;

    public ConfirmationDialog() {
        this.isConfirmed = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.confirm_perform_action_popup, container, false);
        Button confirmButton = view.findViewById(R.id.BtnConfirmAction);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the confirmation here
                confirmAction();
                dismiss();
            }
        });
        Button cancelButton = view.findViewById(R.id.BtnCancelAction);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the confirmation here
                dismiss();
            }
        });
        return view;
    }

    public void confirmAction(){
        this.isConfirmed = true;
    }

    public boolean isConfirmed() {
        return isConfirmed;
    }
}






