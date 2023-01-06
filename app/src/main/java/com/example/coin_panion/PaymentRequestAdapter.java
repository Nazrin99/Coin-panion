package com.example.coin_panion;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coin_panion.classes.NotificationAdapter;
import com.example.coin_panion.classes.PaymentRequest;
import com.twilio.twiml.voice.Pay;

import java.util.List;

public class PaymentRequestAdapter extends RecyclerView.Adapter<PaymentRequestAdapter.ViewHolder> {
    private List<PaymentRequest> paymentRequestList;

    public PaymentRequestAdapter(List<PaymentRequest> paymentRequestList) {
        this.paymentRequestList = paymentRequestList;
    }

    @NonNull
    @Override
    public PaymentRequestAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.debt_list, parent, false);
        return new PaymentRequestAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentRequestAdapter.ViewHolder holder, int position) {
        PaymentRequest paymentRequest = paymentRequestList.get(position);
        holder.TVUser_name.setText(paymentRequest.debtorName());
    }

    @Override
    public int getItemCount() {
        return paymentRequestList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView TVUser_name;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            TVUser_name = itemView.findViewById(R.id.TVUser_name);
        }
    }
}
