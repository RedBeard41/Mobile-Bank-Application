package com.bank.izbank.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bank.izbank.Credit.Credit;
import com.bank.izbank.R;
import com.bank.izbank.Sign.SignInActivity;
import com.bank.izbank.UserInfo.User;
import com.bank.izbank.persistence.JSON.JsonStorage;

import java.util.ArrayList;

public class CreditAdapter extends RecyclerView.Adapter<CreditAdapter.PostHolder> {
    private ArrayList<Credit> credits;
    private Context context;
    private User mainUser = SignInActivity.mainUser;
    
    public CreditAdapter(Context context, ArrayList<Credit> credits) {
        this.context = context;
        this.credits = credits != null ? credits : new ArrayList<>();
    }

    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.credit_cardview, parent, false);
        return new PostHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostHolder holder, int position) {
        if (credits.isEmpty()) {
            // Hide all views except amount
            holder.textViewInstallment.setVisibility(View.GONE);
            holder.textViewInterestRate.setVisibility(View.GONE);
            holder.itemView.findViewById(R.id.buttonCreditPay).setVisibility(View.GONE);
            // Style the empty message
            holder.textViewAmount.setText("No credits available");
            holder.textViewAmount.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            holder.textViewAmount.setTextSize(16); // slightly larger text
            holder.textViewAmount.setPadding(32, 64, 32, 64); // more vertical padding
        } else {
            Credit credit = credits.get(position);
            // Show all views
            holder.textViewInstallment.setVisibility(View.VISIBLE);
            holder.textViewInterestRate.setVisibility(View.VISIBLE);
            holder.itemView.findViewById(R.id.buttonCreditPay).setVisibility(View.VISIBLE);
            
            holder.textViewInstallment.setText(credit.getInstallment() + " months");
            holder.textViewInterestRate.setText("%" + credit.getInterestRate());
            holder.textViewAmount.setText(credit.getAmount() + " TL");
        }
    }

    @Override
    public int getItemCount() {
        return credits != null && !credits.isEmpty() ? credits.size() : 1; // Show at least one item for empty state
    }

    class PostHolder extends RecyclerView.ViewHolder {
        TextView textViewInstallment;
        TextView textViewInterestRate;
        TextView textViewAmount;

        public PostHolder(@NonNull View itemView) {
            super(itemView);
            textViewInstallment = itemView.findViewById(R.id.textView_installment);
            textViewInterestRate = itemView.findViewById(R.id.textView_interest_rate);
            textViewAmount = itemView.findViewById(R.id.textView_credit_amount);
        }
    }

    public void deleteCreditFromDatabase(Credit credit) {
        if (mainUser != null) {
            credits.remove(credit);
            notifyDataSetChanged();
            mainUser.setCredits(new ArrayList<>(credits));
            Toast.makeText(context, "Credit deleted", Toast.LENGTH_SHORT).show();
        }
    }
}
