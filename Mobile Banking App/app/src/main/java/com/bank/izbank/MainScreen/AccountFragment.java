package com.bank.izbank.MainScreen;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bank.izbank.Adapters.HistoryAdapter;
import com.bank.izbank.Adapters.MyBankAccountAdapter;
import com.bank.izbank.Adapters.MyCreditCardAdapter;
import com.bank.izbank.R;
import com.bank.izbank.UserInfo.BankAccount;
import com.bank.izbank.UserInfo.CreditCard;
import com.bank.izbank.UserInfo.History;
import com.bank.izbank.persistence.JSON.JsonStorage;
import com.bank.izbank.config.BankConfig;
import com.bank.izbank.Sign.SignInActivity;
import com.bank.izbank.UserInfo.User;
import android.widget.Button;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Stack;
import java.util.UUID;

import static com.parse.Parse.getApplicationContext;

public class AccountFragment extends Fragment {
    LinearLayout linear_layout_request_money,linear_layout_send_money, linear_layout_history;
    ImageView add_bank_account, add_credit_card;
    RecyclerView recyclerView;
    RecyclerView recyclerViewbankaccount, recyclerViewHistory;
    TextView text_view_name, date,text_view_total_money;
    ArrayList<CreditCard> myCreditCard;
    ArrayList<BankAccount> myBankAccount;
    BankAccount sendUser = null;
    String bankAccountAnother = null;
    String anotherUserid;
    private HistoryAdapter historyAdapter;
    private JsonStorage jsonStorage;
    private double total = 0.0;
    private User mainUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_1, container, false);
        
        // Initialize storage if needed
        if (!User.isStorageInitialized()) {
            User.initializeStorage(requireContext());
        }
        
        mainUser = SignInActivity.mainUser;
        
        // Initialize views
        text_view_name = view.findViewById(R.id.text_view_name);
        date = view.findViewById(R.id.text_view_date_main);
        recyclerView = view.findViewById(R.id.recyclerview_credit_card);
        recyclerViewbankaccount = view.findViewById(R.id.recyclerview_bank_account);
        add_bank_account = view.findViewById(R.id.image_view_add_bank_account);
        add_credit_card = view.findViewById(R.id.image_view_add_credit_card);
        linear_layout_request_money = view.findViewById(R.id.linear_layout_request_money);
        text_view_total_money = view.findViewById(R.id.text_view_total_money);
        linear_layout_send_money = view.findViewById(R.id.linear_layout_send_money);
        linear_layout_history = view.findViewById(R.id.linear_layout_history);
        
        // Initialize lists if null
        if (mainUser.getCreditcards() == null) {
            mainUser.setCreditcards(new ArrayList<>());
        }
        if (mainUser.getBankAccounts() == null) {
            mainUser.setBankAccounts(new ArrayList<>());
        }
        myCreditCard = mainUser.getCreditcards();
        myBankAccount = mainUser.getBankAccounts();
        
        // Set up UI
        click();
        calculateTotal();
        setDate();
        
        text_view_name.setText(String.format(BankConfig.HELLO_FORMAT, mainUser.getName().toUpperCase()));
        text_view_total_money.setText(String.format(BankConfig.MONEY_FORMAT, total, BankConfig.CURRENCY));

        // Set up RecyclerViews
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        recyclerViewbankaccount.setHasFixedSize(true);
        recyclerViewbankaccount.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Set up adapters
        MyCreditCardAdapter myCreditCardAdapter = new MyCreditCardAdapter(myCreditCard, getActivity(), myBankAccount, recyclerViewbankaccount);
        recyclerView.setAdapter(myCreditCardAdapter);

        MyBankAccountAdapter myBankAccountAdapter = new MyBankAccountAdapter(myBankAccount, getActivity());
        recyclerViewbankaccount.setAdapter(myBankAccountAdapter);

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!User.isStorageInitialized()) {
            User.initializeStorage(requireContext());
        }
        mainUser = SignInActivity.mainUser;
    }

    // This method is no longer needed as initialization is handled in onCreateView
    @Deprecated
    public void define() {
        // Deprecated - all initialization is now done in onCreateView
    }
    @SuppressLint("DefaultLocale")
    private void calculateTotal() {
        total = 0.0;
        for (BankAccount account : mainUser.getBankAccounts()) {
            total += account.getCash();
        }
        text_view_total_money.setText(String.format(BankConfig.MONEY_FORMAT, total, BankConfig.CURRENCY));
    }
    public void accountsToDatabase(BankAccount bankAc) {
        ArrayList<BankAccount> accounts = mainUser.getBankAccounts();
        accounts.add(bankAc);
        jsonStorage.saveBankAccounts(mainUser.getId(), accounts);
    }

    public void cardsToDatabase(CreditCard card) {
        ArrayList<CreditCard> cards = mainUser.getCreditcards();
        cards.add(card);
        jsonStorage.saveCreditCards(mainUser.getId(), cards);
    }

    public void historyToDatabase(History history) {
        Stack<History> histories = mainUser.getHistory();
        histories.push(history);
        jsonStorage.saveHistory(mainUser.getId(), histories);
    }

    public void updateBankAccount(BankAccount bankac) {
        ArrayList<BankAccount> accounts = mainUser.getBankAccounts();
        for (int i = 0; i < accounts.size(); i++) {
            if (accounts.get(i).getAccountno().equals(bankac.getAccountno())) {
                accounts.set(i, bankac);
                break;
            }
        }
        jsonStorage.saveBankAccounts(mainUser.getId(), accounts);
    }

    public void updateBankAccountAnotherUser(BankAccount bankac, String userId) {
        ArrayList<BankAccount> accounts = jsonStorage.loadBankAccounts(userId);
        for (int i = 0; i < accounts.size(); i++) {
            if (accounts.get(i).getAccountno().equals(bankac.getAccountno())) {
                accounts.set(i, bankac);
                break;
            }
        }
        jsonStorage.saveBankAccounts(userId, accounts);
    }

    public void accountsToDatabaseAnotherUser(BankAccount bankAc, String userId) {
        ArrayList<BankAccount> accounts = jsonStorage.loadBankAccounts(userId);
        accounts.add(bankAc);
        jsonStorage.saveBankAccounts(userId, accounts);
    }

    public void click(){
        linear_layout_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder history_popup=new AlertDialog.Builder(getContext());

                history_popup.setTitle("HISTORY");

                history_popup.setView(R.layout.history_popup);
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View dialogView= inflater.inflate(R.layout.history_popup, null);
                history_popup.setView(dialogView);
                recyclerViewHistory = dialogView.findViewById(R.id.history_recycler_view);
                recyclerViewHistory.setLayoutManager(new LinearLayoutManager(getActivity()));

                historyAdapter=new HistoryAdapter(stackToArrayList(mainUser.getHistory()),getActivity(),getContext());
                recyclerViewHistory.setAdapter(historyAdapter);
                historyAdapter.notifyDataSetChanged();
                history_popup.create().show();
            }
        });
        add_bank_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myBankAccount.size() >= BankConfig.MAX_ACCOUNTS) {
                    Toast.makeText(getContext(), BankConfig.MAX_ACCOUNTS_ERROR, Toast.LENGTH_SHORT).show();
                }
                else{
                    final EditText editText = new EditText(getContext());
                    editText.setHint("0");
                    editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    AlertDialog.Builder ad = new AlertDialog.Builder(getContext());
                    ad.setTitle(BankConfig.MONEY_AMOUNT_TITLE);
                    ad.setIcon(R.drawable.icon_save_money);
                    ad.setView(editText);
                    ad.setNegativeButton("ADD", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            try {

                                myBankAccount.add(new BankAccount(getContext()));
                                MyBankAccountAdapter myBankAccountAdapter = new MyBankAccountAdapter(myBankAccount,getActivity() );
                                recyclerViewbankaccount.setAdapter(myBankAccountAdapter);
                                calculateTotal();


                            }catch (NumberFormatException e){
                                myBankAccount.add(new BankAccount(getContext()));
                                MyBankAccountAdapter myBankAccountAdapter = new MyBankAccountAdapter(myBankAccount,getActivity() );
                                recyclerViewbankaccount.setAdapter(myBankAccountAdapter);
                                calculateTotal();

                            }
                            accountsToDatabase(myBankAccount.get(myBankAccount.size()-1));
                            History hs = new History(mainUser.getId(),"New Bank Account Added.",getDate() );
                            mainUser.getHistory().push(hs);
                            historyToDatabase(hs);






                        }
                    });
                    ad.create().show();

                }

            }
        });
        add_credit_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myCreditCard.size() >= BankConfig.MAX_CARDS) {
                    Toast.makeText(getContext(), BankConfig.MAX_CARDS_ERROR, BankConfig.TOAST_LONG).show();
                }
                else{


                    final EditText editText = new EditText(getContext());
                    editText.setHint("0");
                    editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    AlertDialog.Builder ad = new AlertDialog.Builder(getContext());
                    ad.setTitle(BankConfig.CARD_LIMIT_TITLE);
                    ad.setIcon(R.drawable.icon_credit_card);
                    ad.setView(editText);
                    ad.setNegativeButton("ADD", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            try {
                                myCreditCard.add(new CreditCard(getContext()));
                                MyCreditCardAdapter myCreditCardAdapter = new MyCreditCardAdapter(myCreditCard,getActivity(),myBankAccount ,recyclerViewbankaccount);
                                recyclerView.setAdapter(myCreditCardAdapter);


                            }catch (NumberFormatException e){
                                myCreditCard.add(new CreditCard(getContext()));
                                MyCreditCardAdapter myCreditCardAdapter = new MyCreditCardAdapter(myCreditCard,getActivity(),myBankAccount ,recyclerViewbankaccount);
                                recyclerView.setAdapter(myCreditCardAdapter);

                            }
                            cardsToDatabase(myCreditCard.get(myCreditCard.size()-1));
                            History hs = new History(mainUser.getId(),"New Credit Card Added.",getDate() );
                            mainUser.getHistory().push(hs);
                            historyToDatabase(hs);

                        }
                    });
                    ad.create().show();

                }


            }
        });
        linear_layout_request_money.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myBankAccount.size() == 0) {
                    AlertDialog.Builder ad = new AlertDialog.Builder(getContext());
                    ad.setTitle(BankConfig.NO_BANK_ACCOUNT_REQUEST);
                    ad.setNegativeButton("CLOSE", null);
                    ad.create().show();
                } else {
                    final EditText editText = new EditText(getContext());
                    editText.setHint("How Much Do You Want to Request?");
                    editText.setInputType(InputType.TYPE_CLASS_NUMBER);

                    AlertDialog.Builder ad = new AlertDialog.Builder(getContext());
                    ad.setTitle("Which Bank Account Do You Want to Request?");
                    ad.setIcon(R.drawable.icon_credit_card);
                    ad.setView(editText);
                    String[] items = new String[myBankAccount.size()];
                    for (int i =0; i<myBankAccount.size();i++){
                        String data= myBankAccount.get(i).getAccountno() + "  $" + myBankAccount.get(i).getCash();
                        items[i] = data;
                    }
                    final int[] checkedItem = {0};
                    ad.setSingleChoiceItems(items, checkedItem[0], new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            checkedItem[0] =i;
                        }
                    });
                    ad.setNegativeButton("Request", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            try {
                                int amount = Integer.parseInt(editText.getText().toString());
                                
                                if (amount <= 0) {
                                    Toast.makeText(getApplicationContext(), BankConfig.AMOUNT_POSITIVE_ERROR, BankConfig.TOAST_LONG).show();
                                    return;
                                }
                                
                                if (amount > BankConfig.MAX_REQUEST_AMOUNT) {
                                    Toast.makeText(getApplicationContext(), 
                                        "Maximum request amount is " + BankConfig.MAX_REQUEST_AMOUNT + " " + BankConfig.CURRENCY, 
                                        BankConfig.TOAST_LONG).show();
                                    return;
                                }
                                
                                i = checkedItem[0];
                                myBankAccount.get(i).setCash(myBankAccount.get(i).getCash() + amount);
                                updateBankAccount(myBankAccount.get(i));
                                MyBankAccountAdapter myBankAccountAdapter = new MyBankAccountAdapter(myBankAccount, getActivity());
                                recyclerViewbankaccount.setAdapter(myBankAccountAdapter);
                                calculateTotal();
                                
                                History hs = new History(mainUser.getId(), "Money Requested: $" + amount, getDate());
                                mainUser.getHistory().push(hs);
                                historyToDatabase(hs);
                            } catch (NumberFormatException e) {
                                Toast.makeText(getApplicationContext(), "Please enter a valid amount", BankConfig.TOAST_LONG).show();
                            }
                        }
                    });
                    ad.create().show();
                }
            }
        });

        linear_layout_send_money.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSendMoneyDialog();
            }
        });


    }

    public ArrayList<History> stackToArrayList(Stack<History> stack){
         ArrayList<History> arraylistHistory = new ArrayList<>();
         while (stack.size() !=0){
             arraylistHistory.add(stack.pop());
         }
         for (int i =arraylistHistory.size()-1;i>=0; i-- ) {
             mainUser.getHistory().push(arraylistHistory.get(i));
        }
         return arraylistHistory;
    }

    public void setDate(){
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        Date currentTime = Calendar.getInstance().getTime();
        date.setText(format.format(currentTime));

    }
    public Date getDate(){
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        Date currentTime = Calendar.getInstance().getTime();
        return currentTime;
    }

    private void showSendMoneyDialog() {
        final int[] from = new int[1];  // Initialize the array
        
        AlertDialog.Builder ad = new AlertDialog.Builder(getContext());
        ad.setTitle(BankConfig.SELECT_ACCOUNT_TITLE);
        ad.setIcon(R.drawable.icon_bank);

        String[] items = new String[mainUser.getBankAccounts().size()];
        for (int i = 0; i < items.length; i++) {
            items[i] = mainUser.getBankAccounts().get(i).getAccountno();
        }

        ad.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                from[0] = which;  // Store the selected index
            }
        });

        ad.setPositiveButton(BankConfig.CONTINUE_BUTTON, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (from[0] >= 0) {  // Check if an account was selected
                    sendMoneyToAnotherUser(from);
                } else {
                    Toast.makeText(getContext(), BankConfig.SELECT_ACCOUNT_ERROR, BankConfig.TOAST_SHORT).show();
                }
            }
        });

        ad.show();
    }

    private void sendMoneyToAnotherUser(final int[] from) {
        final EditText editText = new EditText(getContext());
        editText.setHint(BankConfig.ACCOUNT_NUMBER_HINT);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);

        AlertDialog.Builder typeAccountNo = new AlertDialog.Builder(getContext());
        typeAccountNo.setTitle(BankConfig.SEND_MONEY_TITLE);
        typeAccountNo.setView(editText);
        typeAccountNo.setNegativeButton(BankConfig.CONTINUE_BUTTON, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String accountNo = editText.getText().toString();
                List<String> userIds = jsonStorage.loadUserIds();
                // ... rest of the existing code ...
            }
        });
        // ... rest of the method ...
    }

    private void checkAccountLimit() {
        if (myBankAccount.size() >= BankConfig.MAX_ACCOUNTS) {  // Changed from MAX_BANK_ACCOUNTS
            Toast.makeText(getContext(), BankConfig.MAX_ACCOUNTS_ERROR, Toast.LENGTH_SHORT).show();
            return;
        }
        // ... rest of the code
    }

    private void loadAccounts() {
        String userId = jsonStorage.loadCurrentUserId();
        List<BankAccount> accounts = jsonStorage.loadBankAccounts(userId);
        myBankAccount.clear();
        myBankAccount.addAll(accounts);
        MyBankAccountAdapter myBankAccountAdapter = new MyBankAccountAdapter(myBankAccount, getActivity());
        recyclerViewbankaccount.setAdapter(myBankAccountAdapter);
    }
    
    private void saveAccount(BankAccount account) {
        String userId = jsonStorage.loadCurrentUserId();
        String accountId = UUID.randomUUID().toString();
        account.setId(accountId);
        
        jsonStorage.saveBankAccount(mainUser.getId(), account);
    }

    private void updateUserInfo(String changeColumnName, String changeItem) {
        mainUser.updateInfo(changeColumnName, changeItem);
    }

    private void loadUserIds() {
        List<String> userIds = jsonStorage.loadUserIds();
        // Process userIds as needed
    }

    private void updateLists() {
        // Convert List to ArrayList if needed
        if (mainUser.getCreditcards() instanceof ArrayList) {
            myCreditCard = (ArrayList<CreditCard>) mainUser.getCreditcards();
        } else {
            myCreditCard = new ArrayList<>(mainUser.getCreditcards());
        }

        if (mainUser.getBankAccounts() instanceof ArrayList) {
            myBankAccount = (ArrayList<BankAccount>) mainUser.getBankAccounts();
        } else {
            myBankAccount = new ArrayList<>(mainUser.getBankAccounts());
        }
    }
}
