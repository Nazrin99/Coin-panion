package com.example.coin_panion.classes.notification;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coin_panion.R;
import com.example.coin_panion.classes.transaction.Transaction;
import com.example.coin_panion.classes.transaction.TransactionType;

import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder>{
    private List<Transaction> transactionList;
    private Context context;

    public TransactionAdapter(List<Transaction> transactionList, Context context) {
        this.transactionList = transactionList;
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView TVNotiTitle;
        TextView TVNotiDesc;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            TVNotiTitle = itemView.findViewById(R.id.notiTitle);
            TVNotiDesc = itemView.findViewById(R.id.notiDesc);
        }
    }

    @Override
    public TransactionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.notification_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionAdapter.ViewHolder holder, int position) {
        Transaction transaction = transactionList.get(position);

        holder.TVNotiTitle.setText(transaction.getTransactionName());
        if(transaction.getTransactionType().equalsIgnoreCase(TransactionType.PAYMENT_ISSUE.getType())){
            holder.TVNotiDesc.setText(transaction.getCreditorID() + " issued a payment to " + transaction.getDebtorID() + " with an amount of " + transaction.getAmount() + " for " + transaction.getTransactionName());
        }
        else if(transaction.getTransactionType().equalsIgnoreCase(TransactionType.PAYMENT_MADE.getType())){
            holder.TVNotiDesc.setText(transaction.getDebtorID() + " has made payment to " + transaction.getCreditorID() + " for an amount of " + transaction.getAmount() + " for " + transaction.getTransactionName() + " at " + transaction.getEpochSettled());
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

