package com.bank.izbank.MainScreen;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bank.izbank.Bill.Bill;
import com.bank.izbank.Adapters.BillAdapter;
import com.bank.izbank.Bill.ElectricBill;
import com.bank.izbank.Bill.GasBill;
import com.bank.izbank.Bill.InternetBill;
import com.bank.izbank.Bill.PhoneBill;
import com.bank.izbank.Bill.WaterBill;
import com.bank.izbank.R;
import com.bank.izbank.Sign.SignInActivity;
import com.bank.izbank.UserInfo.BankAccount;
import com.bank.izbank.UserInfo.History;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.bank.izbank.persistence.JSON.JsonStorage;
import com.bank.izbank.config.BankConfig;
import com.bank.izbank.UserInfo.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.parse.Parse.getApplicationContext;

public class BillFragment extends Fragment{

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private ArrayList<Bill> list;
    private BillAdapter billAdapter;
    private FloatingActionButton floatingActionButtonBill;
    private Bill bill;
    private User mainUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_4, container, false);
        
        // Make sure storage is initialized
        if (!User.isStorageInitialized()) {
            User.initializeStorage(getContext());
        }
        
        mainUser = SignInActivity.mainUser;
        
        recyclerView = view.findViewById(R.id.recyclerView_bill);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        floatingActionButtonBill = view.findViewById(R.id.floatingActionButton_bill);

        toolbar = view.findViewById(R.id.toolbar_bill);
        toolbar.setTitle(BankConfig.BILL_TITLE);
        toolbar.setLogo(R.drawable.icon_receipt);

        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        setHasOptionsMenu(true);
        
        // Convert List to ArrayList if needed
        if (mainUser.getUserbills() instanceof ArrayList) {
            list = (ArrayList<Bill>) mainUser.getUserbills();
        } else {
            list = new ArrayList<>(mainUser.getUserbills());
        }
        
        billAdapter = new BillAdapter(getContext(), list);
        recyclerView.setAdapter(billAdapter);

        floatingActionButtonBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final EditText editText = new EditText(getContext());
                editText.setHint(BankConfig.BILL_AMOUNT_HINT);
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);

                AlertDialog.Builder ad = new AlertDialog.Builder(getContext());

                ad.setTitle(BankConfig.BILL_CHOOSE_TITLE);
                ad.setIcon(R.drawable.icon_bill);
                ad.setView(editText);

                String[] items = {"Electric","Gas","Internet","Phone","Water"};

                final int[] checkedItem = {0};

                ad.setSingleChoiceItems(items, checkedItem[0], new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
                                checkedItem[0] =i;
                                break;
                            case 1:
                                checkedItem[0] =i;
                                break;
                            case 2:
                                checkedItem[0] =i;
                                break;
                            case 3:
                                checkedItem[0] =i;
                                break;
                            case 4:
                                checkedItem[0] =i;
                                break;
                        }
                    }
                });

                ad.setNegativeButton("Pay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        i= checkedItem[0];
                        switch (i) {
                            case 0:
                                try {
                                    bill=new ElectricBill();
                                }catch (NumberFormatException e){

                                }
                                break;
                            case 1:
                                try {
                                    bill=new GasBill();
                                }catch (NumberFormatException e){

                                }
                                break;
                            case 2:
                                try {
                                    bill=new InternetBill();
                                }catch (NumberFormatException e){

                                }
                                break;
                            case 3:
                                try {
                                    bill=new PhoneBill();
                                }catch (NumberFormatException e){

                                }
                                break;
                            case 4:
                                try {
                                    bill=new WaterBill();
                                }catch (NumberFormatException e){

                                }
                                break;
                        }

                        try {


                            bill.setAmount(Integer.parseInt(editText.getText().toString()));
                            setDate(bill);


                            payBill(bill);



                        }catch (NumberFormatException e){

                        }



                    }
                });

                ad.create().show();

            }
        });

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void payBill(Bill bill){

        int max=Integer.MIN_VALUE;
        int index=-1;

        for(int i =0;i<SignInActivity.mainUser.getBankAccounts().size();i++){

            if(SignInActivity.mainUser.getBankAccounts().get(i).getCash()>max){
                max=SignInActivity.mainUser.getBankAccounts().get(i).getCash();
                index=i;
            }

        }

        if(SignInActivity.mainUser.getBankAccounts().size()>0 &&
                bill.getAmount()<=SignInActivity.mainUser.getBankAccounts().get(index).getCash()){

            BankAccount account = SignInActivity.mainUser.getBankAccounts().get(index);
            account.setCash(account.getCash()-bill.getAmount());

            mainUser.save();

            Toast.makeText(getApplicationContext(), BankConfig.BILL_PAID_MSG, BankConfig.TOAST_LONG).show();

            list.add(bill);
            mainUser.save();
            billAdapter = new BillAdapter(getContext(),list);


            recyclerView.setAdapter(billAdapter);
            History hs = new History(mainUser.getId(), 
                String.format(BankConfig.BILL_PAID_FORMAT, bill.getType(), bill.getAmount(), BankConfig.CURRENCY), 
                getDateRealTime());
            mainUser.getHistory().push(hs);
            mainUser.save();

        }
        else {

            Toast.makeText(getApplicationContext(), BankConfig.INSUFFICIENT_FUNDS_MSG, BankConfig.TOAST_LONG).show();


        }


    }

    public void setDate(Bill newBill){
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        java.util.Date currentTime = Calendar.getInstance().getTime();
        String str = format.format(currentTime);

        String [] date = str.split("/");

        newBill.getDate().setDay(date[0]);
        newBill.getDate().setMonth(date[1]);
        newBill.getDate().setYear(date[2]);

    }







    public java.util.Date getDateRealTime(){
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        Date currentTime = Calendar.getInstance().getTime();
        return currentTime;
    }

    private void updateBillsList() {
        if (mainUser.getUserbills() instanceof ArrayList) {
            list = (ArrayList<Bill>) mainUser.getUserbills();
        } else {
            list = new ArrayList<>(mainUser.getUserbills());
        }
        billAdapter.notifyDataSetChanged();
    }

}
