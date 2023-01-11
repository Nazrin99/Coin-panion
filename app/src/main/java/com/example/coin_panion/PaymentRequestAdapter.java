package com.example.coin_panion;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coin_panion.classes.settleUp.PaymentApproval;

import java.util.List;

public class PaymentRequestAdapter extends RecyclerView.Adapter<PaymentRequestAdapter.ViewHolder> {
    private List<PaymentApproval> paymentApprovalList;

    public PaymentRequestAdapter(List<PaymentApproval> paymentApprovalList) {
        this.paymentApprovalList = paymentApprovalList;
    }

    @NonNull
    @Override
    public PaymentRequestAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.debt_list_sec, parent, false);
        return new PaymentRequestAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentRequestAdapter.ViewHolder holder, int position) {
        PaymentApproval paymentApproval = paymentApprovalList.get(position);
//        holder.TVUser_name.setText(paymentApproval.debtorName());
    }

    @Override
    public int getItemCount() {
        return paymentApprovalList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView TVUser_name;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            TVUser_name = itemView.findViewById(R.id.TVUserName);
        }
    }
}
