package com.bank.izbank.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bank.izbank.R;
import com.bank.izbank.Bill.Bill;
import com.bank.izbank.Sign.SignInActivity;
import com.bank.izbank.UserInfo.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class BillAdapter extends RecyclerView.Adapter<BillAdapter.ViewHolder> {
    private ArrayList<Bill> bills;
    private Context context;
    private SimpleDateFormat dateFormat;
    private User mainUser = SignInActivity.mainUser;

    public BillAdapter(Context context, ArrayList<Bill> bills) {
        this.context = context;
        this.bills = bills != null ? bills : new ArrayList<>();
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bill_cardview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (bills.isEmpty()) {
            holder.textViewBillType.setVisibility(View.GONE);
            holder.textViewBillDate.setVisibility(View.GONE);
            holder.textViewBillAmount.setText("No bills available");
            holder.textViewBillAmount.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            holder.textViewBillAmount.setTextSize(16);
            holder.textViewBillAmount.setPadding(32, 64, 32, 64);
        } else {
            Bill bill = bills.get(position);
            // Show all views
            holder.textViewBillType.setVisibility(View.VISIBLE);
            holder.textViewBillDate.setVisibility(View.VISIBLE);
            holder.textViewBillAmount.setVisibility(View.VISIBLE);
            
            holder.textViewBillType.setText(bill.getType());
            // Format date using the custom Date class
            String formattedDate = String.format("%s/%s/%s", 
                bill.getDate().getDay(),
                bill.getDate().getMonth(),
                bill.getDate().getYear());
            holder.textViewBillDate.setText(formattedDate);
            holder.textViewBillAmount.setText(bill.getAmount() + " TL");
            holder.textViewBillAmount.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            holder.textViewBillAmount.setPadding(0, 0, 0, 0);
        }
    }

    @Override
    public int getItemCount() {
        return bills != null && !bills.isEmpty() ? bills.size() : 1;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewBillType;
        TextView textViewBillDate;
        TextView textViewBillAmount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewBillType = itemView.findViewById(R.id.textView_bill_type);
            textViewBillDate = itemView.findViewById(R.id.textView_bill_date);
            textViewBillAmount = itemView.findViewById(R.id.textView_bill_amount);
        }
    }
}
