package com.bank.izbank.MainScreen;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bank.izbank.Credit.Credit;
import com.bank.izbank.Adapters.CreditAdapter;
import com.bank.izbank.R;
import com.bank.izbank.Sign.SignInActivity;
import com.bank.izbank.UserInfo.BankAccount;
import com.bank.izbank.UserInfo.History;
import com.bank.izbank.UserInfo.CreditCard;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.parse.Parse.getApplicationContext;
import com.bank.izbank.config.BankConfig;
import com.bank.izbank.persistence.JSON.JsonStorage;
import com.bank.izbank.UserInfo.User;

public class CreditFragment extends Fragment {

    private Toolbar toolbarCredit;
    private RecyclerView recyclerViewCredit;
    private ArrayList<Credit> list,searchList;
    private CreditAdapter creditAdapter;
    private FloatingActionButton floatingActionButtonCredit;
    private Credit credit;
    private AdapterView.OnItemClickListener mListener;
    private FloatingActionButton addCreditButton;
    private List<Credit> credits;
    private User mainUser;
    private EditText amountInput;
    private EditText durationInput;
    private ListView creditsListView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!User.isStorageInitialized()) {
            User.initializeStorage(getContext());
        }
        credits = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_2, container, false);
        
        mainUser = SignInActivity.mainUser;
        
        // Initialize RecyclerView
        recyclerViewCredit = view.findViewById(R.id.recyclerView_credit);
        recyclerViewCredit.setHasFixedSize(true);
        recyclerViewCredit.setLayoutManager(new LinearLayoutManager(getContext()));
        
        // Initialize list and adapter
        if (mainUser.getCredits() instanceof ArrayList) {
            list = (ArrayList<Credit>) mainUser.getCredits();
        } else {
            list = new ArrayList<>(mainUser.getCredits());
        }
        creditAdapter = new CreditAdapter(getContext(), list);
        recyclerViewCredit.setAdapter(creditAdapter);
        
        // Setup toolbar
        toolbarCredit = view.findViewById(R.id.toolbar_credit);
        toolbarCredit.setTitle(BankConfig.CREDIT_TITLE);
        toolbarCredit.setLogo(R.drawable.icon_credit);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbarCredit);
        
        // Setup FAB
        floatingActionButtonCredit = view.findViewById(R.id.floatingActionButton_credit);
        floatingActionButtonCredit.setOnClickListener(v -> showCreditDialog());
        
        return view;
    }

    private void showCreditDialog() {
        AlertDialog.Builder creditPopup = new AlertDialog.Builder(getContext());
        creditPopup.setTitle(BankConfig.CREDIT_SET_TITLE);
        
        // Inflate first step popup
        View dialogView = getLayoutInflater().inflate(R.layout.credit_screen_credit_first_step_popup, null);
        creditPopup.setView(dialogView);
        
        // Get references to popup views
        EditText creditAmount = dialogView.findViewById(R.id.editText_credit_amount);
        EditText installment = dialogView.findViewById(R.id.editText_credit_installment);
        TextView interestRate = dialogView.findViewById(R.id.textView_credit_interestRate);
        TextView staticMaxAmount = dialogView.findViewById(R.id.textView_static_credit_max_amount);
        TextView staticMaxInstallment = dialogView.findViewById(R.id.textView_static_credit_max_installment);
        
        // Set initial values
        interestRate.setText("Rate: %" + mainUser.getJob().getInterestRate());
        staticMaxAmount.setText(mainUser.getJob().getMaxCreditAmount());
        staticMaxInstallment.setText(mainUser.getJob().getMaxCreditInstallment());
        
        creditPopup.setNegativeButton("Set", (dialog, i) -> {
            if (!(creditAmount.getText().toString().isEmpty() || installment.getText().toString().isEmpty())) {
                int currentAmount = Integer.parseInt(creditAmount.getText().toString());
                int currentInstallment = Integer.parseInt(installment.getText().toString());
                int maxAmount = Integer.parseInt(mainUser.getJob().getMaxCreditAmount());
                int maxInstallment = Integer.parseInt(mainUser.getJob().getMaxCreditInstallment());
                
                if (maxAmount >= currentAmount && maxInstallment >= currentInstallment) {
                    showConfirmationDialog(currentAmount, currentInstallment);
                } else {
                    Toast.makeText(getContext(), BankConfig.INVALID_VALUES_MSG, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), BankConfig.INVALID_VALUES_MSG, Toast.LENGTH_SHORT).show();
            }
        });
        
        creditPopup.create().show();
    }

    private void showConfirmationDialog(int amount, int installment) {
        AlertDialog.Builder creditPopupSecond = new AlertDialog.Builder(getContext());
        creditPopupSecond.setTitle(BankConfig.CREDIT_CHECK_TITLE);
        
        View dialogViewSecond = getLayoutInflater().inflate(R.layout.credit_screen_credit_second_step_popup, null);
        creditPopupSecond.setView(dialogViewSecond);
        
        TextView amountSecond = dialogViewSecond.findViewById(R.id.textView_taken_amount);
        TextView payAmountSecond = dialogViewSecond.findViewById(R.id.textView_pay_amount);
        
        int payAmount = calculatePayAmount(amount, installment);
        amountSecond.setText(String.valueOf(amount));
        payAmountSecond.setText(String.valueOf(payAmount));
        
        creditPopupSecond.setNegativeButton("Confirm", (dialog, i) -> {
            Credit tempCredit = new Credit(amount, installment, 
                Integer.parseInt(mainUser.getJob().getInterestRate()), payAmount);
            receiveCredit(tempCredit);
        });
        
        creditPopupSecond.create().show();
    }

    public void updateBankAccount(BankAccount bankac){
        ParseQuery<ParseObject> queryBankAccount=ParseQuery.getQuery("BankAccount");
        queryBankAccount.whereEqualTo("accountNo", bankac.getAccountno());
        queryBankAccount.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e!=null){
                    e.printStackTrace();
                }else{

                    if(objects.size()>0){
                        for(ParseObject object:objects){
                            object.deleteInBackground();

                            accountsToDatabase(bankac);
                        }
                    }
                }
            }
        });
    }

    public void accountsToDatabase(BankAccount bankAc){
        ParseObject object=new ParseObject("BankAccount");
        object.put("accountNo",bankAc.getAccountno());
        object.put("userId", SignInActivity.mainUser.getId());

        object.put("cash",String.valueOf(bankAc.getCash()));


        object.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e != null){
                    //Toast.makeText(getApplicationContext(),e.getLocalizedMessage().toString(),Toast.LENGTH_LONG).show();
                }
                else{


                }
            }
        });
    }

    public void creditToDatabase(Credit tempCredit) {
        // Add credit to user's credits and save
        mainUser.getCredits().add(tempCredit);
        mainUser.save();
    }

    public java.util.Date getDateRealTime(){
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        Date currentTime = Calendar.getInstance().getTime();
        return currentTime;
    }

    public void historyToDatabase(History history) {
        // History is already added to user's history stack before this is called
        // Just need to save the user
        mainUser.save();
    }

    public void receiveCredit(Credit tempCredit){

        int max=Integer.MIN_VALUE;
        int index=-1;

        for(int i =0;i<SignInActivity.mainUser.getBankAccounts().size();i++){

            if(SignInActivity.mainUser.getBankAccounts().get(i).getCash()>max){
                max=SignInActivity.mainUser.getBankAccounts().get(i).getCash();
                index=i;
            }

        }

        if(SignInActivity.mainUser.getBankAccounts().size()>0 ){

            SignInActivity.mainUser.getBankAccounts().get(index).setCash(SignInActivity.mainUser.getBankAccounts().get(index).getCash()+tempCredit.getAmount());

            updateBankAccount(SignInActivity.mainUser.getBankAccounts().get(index));

            Toast.makeText(getApplicationContext(), BankConfig.CREDIT_RECEIVED_MSG, BankConfig.TOAST_LONG).show();

            creditToDatabase(tempCredit);
            list.add(tempCredit);
            creditAdapter = new CreditAdapter(getContext(),list);
            recyclerViewCredit.setAdapter(creditAdapter);


            recyclerViewCredit.setAdapter(creditAdapter);
            History hs = new History(mainUser.getId(),
                String.format("Credit Received.(%d %s)", tempCredit.getAmount(), BankConfig.CURRENCY),
                getDateRealTime());
            mainUser.getHistory().push(hs);
            historyToDatabase(hs);

        }
        else {

            Toast.makeText(getApplicationContext(), BankConfig.NO_BANK_ACCOUNT_MSG, BankConfig.TOAST_LONG).show();


        }


    }

    private void checkCardLimit() {
        if (credits.size() >= BankConfig.MAX_CARDS) {  // Changed from MAX_CREDIT_CARDS
            Toast.makeText(getContext(), BankConfig.MAX_CARDS_ERROR, Toast.LENGTH_SHORT).show();
            return;
        }
        // ... rest of the code
    }

    private void applyForCredit() {
        int amount = Integer.parseInt(amountInput.getText().toString());
        int duration = Integer.parseInt(durationInput.getText().toString());
        int interestRate = Integer.parseInt(mainUser.getJob().getInterestRate());
        Credit tempCredit = new Credit(amount, duration, interestRate, calculatePayAmount(amount, duration));
        creditToDatabase(tempCredit);
        Toast.makeText(getContext(), "Credit application submitted!", Toast.LENGTH_SHORT).show();
        updateCreditsList();
    }

    private void updateCreditsList() {
        if (mainUser.getCredits() instanceof ArrayList) {
            list = (ArrayList<Credit>) mainUser.getCredits();
        } else {
            list = new ArrayList<>(mainUser.getCredits());
        }
        creditAdapter = new CreditAdapter(getContext(), list);
        recyclerViewCredit.setAdapter(creditAdapter);
    }

    private int calculatePayAmount(int amount, int duration) {
        int interestRate = Integer.parseInt(mainUser.getJob().getInterestRate());
        return amount + ((amount * interestRate * duration) / 1200);
    }

    private void saveCredit(Credit credit) {
        mainUser.getCredits().add(credit);
        mainUser.save();
        
        list.add(credit);
        creditAdapter.notifyDataSetChanged();
    }

}
