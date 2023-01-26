package com.example.coin_panion.classes.transaction;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coin_panion.R;
import com.example.coin_panion.classes.utility.BaseViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DebtListAdapter extends RecyclerView.Adapter<DebtListAdapter.ViewHolder> {

    List<Transaction> debts = new ArrayList<>();
    Context context;
    BaseViewModel mainViewModel;

    public DebtListAdapter(List<Transaction> debts, Context context, BaseViewModel mainViewModel) {
        this.debts = debts;
        this.context = context;
        this.mainViewModel = mainViewModel;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.debt_friend_item, null);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Transaction debt = debts.get(position);

        holder.creditorUsernameTextView.setText(debt.getCreditorID().getUsername());
        holder.creditorAmountTextView.setText(String.format(Locale.ENGLISH, "%.2f", debt.getAmount()));
        holder.creditorSettleUpButton.setOnClickListener(v -> {
            mainViewModel.put("settleUpDebt", debt);
        });
    }

    @Override
    public int getItemCount() {
        return debts.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView creditorUsernameTextView, creditorAmountTextView;
        Button creditorSettleUpButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            creditorUsernameTextView = itemView.findViewById(R.id.creditorUsernameTextView);
            creditorAmountTextView = itemView.findViewById(R.id.creditorAmountTextView);
            creditorSettleUpButton = itemView.findViewById(R.id.creditorSettleUpButton);
        }
    }

    public List<Transaction> getDebts() {
        return debts;
    }

    public void setDebts(List<Transaction> debts) {
        this.debts = debts;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
