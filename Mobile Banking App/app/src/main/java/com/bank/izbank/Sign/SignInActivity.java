package com.bank.izbank.Sign;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bank.izbank.Bill.Bill;
import com.bank.izbank.Bill.Date;
import com.bank.izbank.Credit.Credit;
import com.bank.izbank.Job.Job;
import com.bank.izbank.MainScreen.AdminPanelActivity;
import com.bank.izbank.MainScreen.MainScreenActivity;
import com.bank.izbank.R;
import com.bank.izbank.UserInfo.Address;
import com.bank.izbank.UserInfo.Admin;
import com.bank.izbank.UserInfo.BankAccount;
import com.bank.izbank.UserInfo.CreditCard;
import com.bank.izbank.UserInfo.History;
import com.bank.izbank.UserInfo.User;
import com.bank.izbank.UserInfo.UserContext;
import com.bank.izbank.UserInfo.UserTypeState;
import com.bank.izbank.splashScreen.splashScreen;
import com.bank.izbank.persistence.JSON.JsonStorage;
import com.bank.izbank.config.BankConfig;

import java.util.ArrayList;
import java.util.Stack;

public class SignInActivity extends AppCompatActivity {
    private static final String TAG = SignInActivity.class.getSimpleName();
    
    public static User mainUser;
    public static ArrayList<User> allUsers;
    private String billType;
    private String billAmount;
    private String billDate;
    private ArrayList<Bill> bills;
    private ArrayList<BankAccount> bankAccounts;
    private Stack<History> history;
    private ArrayList<CreditCard> creditCards;
    private String bankCash,bankAccountNo;
    private String cardNo, cardLimit;
    private Intent intent ;
    private ArrayList<Credit> credits;
    private String creditAmount;
    private String creditInstallment;
    private String creditInterestRate;
    private String creditPayAmount;
    private JsonStorage jsonStorage;
    private EditText editTextUsername;
    private EditText editTextPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        
        try {
            User.initializeStorage(getApplicationContext());
            Log.d(TAG, "Storage initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing storage", e);
            Toast.makeText(this, "Error initializing storage: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        
        editTextUsername = findViewById(R.id.edit_text_username);
        editTextPassword = findViewById(R.id.edit_text_password);
        Button buttonSignIn = findViewById(R.id.button_sign_in);
        Button buttonSignUp = findViewById(R.id.button_sign_up);

        buttonSignIn.setOnClickListener(v -> signIn());
        buttonSignUp.setOnClickListener(v -> startActivity(new Intent(this, SignUpActivity.class)));
    }

    private void signIn() {
        String username = editTextUsername.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            mainUser = User.loadUser(username);
            if (mainUser != null && mainUser.getPass().equals(password)) {
                startActivity(new Intent(this, MainScreenActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error loading user data", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadUserData(User user) {
        // Initialize collections if null
        if (user.getBankAccounts() == null) user.setBankAccounts(new ArrayList<>());
        if (user.getCreditcards() == null) user.setCreditcards(new ArrayList<>());
        if (user.getHistory() == null) user.setHistory(new Stack<>());
        
        // No direct JsonStorage access - User class handles persistence
        user.save();
    }

    private void saveUserToPrefs(User user) {
        SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("userId", user.getId());
        editor.putString("userName", user.getName());
        editor.putString("userPhone", user.getPhoneNumber());
        editor.apply();
        Log.d(TAG, "User preferences saved for ID: " + user.getId());
    }

    private void showToast(String message) {
        // Create custom TextView for Toast
        TextView textView = new TextView(getApplicationContext());
        textView.setText(message);
        textView.setTextColor(getResources().getColor(R.color.black));
        textView.setBackgroundColor(getResources().getColor(R.color.white));
        textView.setPadding(32, 16, 32, 16);  // Left, Top, Right, Bottom padding
        
        // Create and show Toast with custom view
        Toast toast = new Toast(getApplicationContext());
        toast.setView(textView);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
    }
}