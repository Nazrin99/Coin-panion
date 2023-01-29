package com.example.coin_panion.classes.notification;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coin_panion.R;
import com.example.coin_panion.classes.general.Account;
import com.example.coin_panion.classes.transaction.Transaction;
import com.example.coin_panion.classes.transaction.TransactionStatus;

import org.checkerframework.checker.units.qual.A;

import java.text.DecimalFormat;
import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder>{
    private List<Transaction> transactionList;
    private Context context;
    private DecimalFormat decimalFormat = new DecimalFormat("0.00");

    public TransactionAdapter(List<Transaction> transactionList, Context context) {
        this.transactionList = transactionList;
        this.context = context;
    }

    @NonNull
    @Override
    public TransactionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.notification_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionAdapter.ViewHolder holder, int position) {
        Transaction transaction = transactionList.get(position);

        holder.TVNotiTitle.setText(transaction.getTransName());
        if(transaction.getTransStatus().getType().equalsIgnoreCase(TransactionStatus.PAYMENT_NOT_MADE.getType())){
            holder.TVNotiDesc.setText(transaction.getCreditorID().getUsername() + " requested payment from " + transaction.getDebtorID().getUsername() + " with an amount of " + decimalFormat.format(transaction.getAmount()) + " for " + transaction.getTransName());
            holder.notiDate.setText(transaction.getEpochIssued().toString());
        }
        else if(transaction.getTransStatus().getType().equalsIgnoreCase(TransactionStatus.PAYMENT_SETTLED.getType())){
            holder.TVNotiDesc.setText(transaction.getDebtorID().getUsername() + " has made payment to " + transaction.getCreditorID().getUsername() + " with an amount of " + decimalFormat.format(transaction.getAmount()) + " for " + transaction.getTransName());
            holder.notiDate.setText(transaction.getEpochIssued().toString());
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView TVNotiTitle, TVNotiDesc, notiDate;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            TVNotiTitle = itemView.findViewById(R.id.notiTitle);
            TVNotiDesc = itemView.findViewById(R.id.notiDesc);
            notiDate = itemView.findViewById(R.id.notiDate);
        }
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public List<Transaction> getTransactionList() {
        return transactionList;
    }

    public void setTransactionList(List<Transaction> transactionList) {
        this.transactionList = transactionList;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}

/*Code in fragment */
//NotificationAdapter adapter = new NotificationAdapter(data);
//    RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
//recyclerView.setAdapter(adapter);

