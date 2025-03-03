package com.bank.izbank.MainScreen;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bank.izbank.Adapters.HistoryAdapter;
import com.bank.izbank.Adapters.MyBankAccountAdapter;
import com.bank.izbank.Adapters.UserAdapter;
import com.bank.izbank.R;
import com.bank.izbank.Sign.SignInActivity;
import com.bank.izbank.UserInfo.History;
import com.bank.izbank.UserInfo.User;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import com.bank.izbank.config.BankConfig;
import com.bank.izbank.persistence.JSON.JsonStorage;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Stack;
import java.util.ArrayList;
import java.util.List;
import android.widget.ArrayAdapter;

public class AdminPanelActivity extends AppCompatActivity {

    private LinearLayout linear_layout_history, linear_layout_logout;
    RecyclerView recyclerViewHistory;
    private HistoryAdapter historyAdapter;
    private TextView date;
    boolean flag = true;
    private RecyclerView recyclerViewUser;
    private ArrayList<User> users;
    private UserAdapter adapter;
    private JsonStorage jsonStorage;
    private ListView userListView;
    private ArrayList<String> userIds;
    private UserAdapter userAdapter;
    private User mainUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);
        
        if (!User.isStorageInitialized()) {
            User.initializeStorage(this);
        }
        
        mainUser = SignInActivity.mainUser;
        userListView = findViewById(R.id.recyclerview_user);
        
        userIds = new ArrayList<>(User.getAllUserIds());
        userAdapter = new UserAdapter(users, this);
        recyclerViewUser.setAdapter(userAdapter);
        
        userListView.setOnItemClickListener((parent, view, position, id) -> {
            String userId = userIds.get(position);
            loadUserDetails(userId);
        });

        define();
        recyclerViewUser.setHasFixedSize(BankConfig.RECYCLERVIEW_FIXED_SIZE);
        recyclerViewUser.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        users = new ArrayList<>();
        adapter = new UserAdapter(users, this);
        recyclerViewUser.setAdapter(adapter);

        setDate();
        click();
    }
    public void define(){
        linear_layout_history = findViewById(R.id.linear_layout_history);
        date = findViewById(R.id.text_view_date_admin);
        recyclerViewUser = findViewById(R.id.recyclerview_user);
    }
    public void click(){
        linear_layout_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder history_popup=new AlertDialog.Builder(AdminPanelActivity.this);

                history_popup.setTitle(BankConfig.ADMIN_HISTORY_TITLE);

                history_popup.setView(R.layout.history_popup);
                LayoutInflater inflater = getLayoutInflater();
                View dialogView= inflater.inflate(R.layout.history_popup, null);
                history_popup.setView(dialogView);
                recyclerViewHistory = dialogView.findViewById(R.id.history_recycler_view);
                recyclerViewHistory.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

                historyAdapter=new HistoryAdapter(stackToArrayList(mainUser.getHistory()),AdminPanelActivity.this,getApplicationContext());
                flag=false;

                recyclerViewHistory.setAdapter(historyAdapter);
                historyAdapter.notifyDataSetChanged();
                history_popup.create().show();
            }
        });

    }
    public ArrayList<History> stackToArrayList(Stack<History> stack){
        ArrayList<History> arraylistHistory = new ArrayList<>();
        while (stack.size() !=0){
            arraylistHistory.add(stack.pop());
        }
        for (int i =arraylistHistory.size()-1;i>=0; i-- ) {
            if (flag){
                History history = arraylistHistory.get(i);
                history.setProcess(String.format(BankConfig.HISTORY_USER_FORMAT, 
                    history.getUserId(), 
                    history.getProcess()));
            }

            mainUser.getHistory().push(arraylistHistory.get(i));
        }
        return arraylistHistory;
    }
    public void setDate(){
        SimpleDateFormat format = new SimpleDateFormat(BankConfig.DATE_FORMAT);
        Date currentTime = Calendar.getInstance().getTime();
        date.setText(format.format(currentTime));

    }

    public void logOut(View view){
        ParseUser.logOutInBackground(new LogOutCallback() {
            @Override
            public void done(ParseException e) {
                if(e !=null){
                    Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), BankConfig.TOAST_LONG).show();
                }else{
                    Intent intent=new Intent(getApplicationContext(), SignInActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    private String formatDate(String date) {
        SimpleDateFormat format = new SimpleDateFormat(BankConfig.DATE_FORMAT);
        try {
            Date parsedDate = format.parse(date);
            return format.format(parsedDate);
        } catch (Exception e) {
            return date; // Return original string if parsing fails
        }
    }

    private void loadUsers() {
        List<String> userIds = jsonStorage.loadUserIds();
        users.clear();
        for (String userId : userIds) {
            User user = jsonStorage.findUserById(userId);
            if (user != null) {
                users.add(user);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private Context getContext() {
        return this;
    }

    private void loadUserDetails(String userId) {
        User user = jsonStorage.findUserById(userId);
        if (user != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("User Details")
                   .setMessage("Name: " + user.getName() + "\n" +
                             "Phone: " + user.getPhoneNumber() + "\n" +
                             "Bank Accounts: " + user.getBankAccounts().size() + "\n" +
                             "Credit Cards: " + user.getCreditcards().size())
                   .setPositiveButton("OK", null)
                   .show();
        }
    }
}