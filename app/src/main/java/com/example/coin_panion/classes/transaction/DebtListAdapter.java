package com.example.coin_panion.classes.transaction;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coin_panion.R;
import com.example.coin_panion.classes.utility.BaseViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DebtListAdapter extends RecyclerView.Adapter<DebtListAdapter.DebtListHolder> {

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
    public DebtListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == 0){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.debt_list_item, null);
            return new DebtListHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull DebtListHolder holder, int position) {
        Transaction debt = debts.get(position);

        holder.debtorFromTextView.setText(debt.getTransName() + " from group " + debt.getGroupName());
        holder.debtorUsernameTextView.setText(debt.getCreditorID().getUsername());
        holder.debtorAmountTextView.setText("RM " + String.format(Locale.ENGLISH, "%.2f", debt.getAmount()));
        if(debt.getTransStatus().getType().equalsIgnoreCase(TransactionStatus.PAYMENT_REQUEST.getType())){
            holder.debtorSettleUpButton.setText("PENDING APPROVAL");
            holder.debtorSettleUpButton.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.grey)));
        }
        else{
            holder.debtorSettleUpButton.setOnClickListener(v -> {
                mainViewModel.put("debt", debt);
                Navigation.findNavController(v).navigate(R.id.friendsSettleUpFragment);
            });
            holder.debtorSettleUpButton.setText("SETTLE UP");
            holder.debtorSettleUpButton.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.dark_blue)));
        }
    }

    @Override
    public int getItemCount() {
        return debts.size();
    }

    @Override
    public long getItemId(int position) {
        Transaction debt = debts.get(position);

        if(debt.getTransStatus().getType().equalsIgnoreCase(TransactionStatus.PAYMENT_NOT_MADE.getType())){
            return 0;
        }
        return 1;
    }

    static class DebtListHolder extends RecyclerView.ViewHolder{
        TextView debtorUsernameTextView, debtorAmountTextView, debtorFromTextView;
        Button debtorSettleUpButton;

        public DebtListHolder(@NonNull View itemView) {
            super(itemView);

            debtorUsernameTextView = itemView.findViewById(R.id.debtorUsernameTextView);
            debtorAmountTextView = itemView.findViewById(R.id.debtorAmountTextView);
            debtorSettleUpButton = itemView.findViewById(R.id.debtorSettleUpButton);
            debtorFromTextView = itemView.findViewById(R.id.debtorFromTextView);
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
