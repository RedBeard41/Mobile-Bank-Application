package com.bank.izbank.UserInfo;

import java.util.Random;
import android.widget.Toast;
import com.bank.izbank.config.BankConfig;
import android.content.Context;
import java.util.List;
import java.util.ArrayList;

public class BankAccount {

    private int cash;
    private final String accountno;
    private List<BankAccount> accounts;
    private Context context;
    private String id;
    private String userId;
    private double balance;
    private String currency;

    public BankAccount(Context context) {
        this.context = context;
        this.accounts = new ArrayList<>();
        this.cash = 0;
        this.accountno = setBankAccountNo();
        this.balance = 0.0;
        this.currency = "USD";
    }

    public BankAccount(String no, int cash) {
        this.cash = cash;
        this.accountno = no;
        this.balance = 0.0;
        this.currency = "USD";
    }

    public BankAccount(String accountNo, String userId) {
        this.accountno = accountNo;
        this.userId = userId;
        this.cash = 0;
        this.balance = 0.0;
        this.currency = "USD";
    }

    public String setBankAccountNo() {
        String no = "";
        Random rnd = new Random();
        for (int i = 0; i < 10; i++) {
            int radnomint = rnd.nextInt(10);
            no = no + radnomint;
        }

        return no;
    }

    public String getAccountno() {
        return accountno;
    }

    public int getCash() {
        return cash;
    }

    public void setCash(int cash) {
        this.cash = cash;
    }

    private void validateAccountLimit() {
        if (accounts.size() >= BankConfig.MAX_ACCOUNTS) {
            Toast.makeText(context, BankConfig.MAX_ACCOUNTS_ERROR, Toast.LENGTH_SHORT).show();
            return;
        }
        // ... rest of the code
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public double getBalance() {
        return balance;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCurrency() {
        return currency;
    }
}
