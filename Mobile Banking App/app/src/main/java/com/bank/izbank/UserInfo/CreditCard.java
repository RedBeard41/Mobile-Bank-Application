package com.bank.izbank.UserInfo;

import java.util.Random;
import android.widget.Toast;
import com.bank.izbank.config.BankConfig;
import android.content.Context;
import java.util.List;
import java.util.ArrayList;

public class CreditCard {
    private int limit;
    private final String creditCardNo;
    private List<CreditCard> cards;
    private Context context;

    public CreditCard(Context context) {
        this.context = context;
        this.cards = new ArrayList<>();
        this.limit = 0;
        this.creditCardNo = setCreditCardNo();
    }
    public CreditCard(String no, int limit) {
        this.limit = limit;
        this.creditCardNo = no;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public String setCreditCardNo(){
        String no = "**** **** **** ";
        Random rnd = new Random();
        for (int i = 0; i <4; i++){
            int random = rnd.nextInt(10);
            no = no + random;
        }

        return no;

    }

    public String getCreditCardNo() {
        return creditCardNo;
    }

    private void validateCardLimit() {
        if (cards.size() >= BankConfig.MAX_CARDS) {  // Changed from MAX_CREDIT_CARDS
            Toast.makeText(context, BankConfig.MAX_CARDS_ERROR, Toast.LENGTH_SHORT).show();
            return;
        }
        // ... rest of the code
    }
}
