package com.bank.izbank.Sign;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class SignIn extends AppCompatActivity {
    private EditText userName,userPass;
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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


           setContentView(R.layout.activity_sign_in);//load screen
           userName=findViewById(R.id.edittext_id_number_sign_in);
           userPass=findViewById(R.id.edittext_user_password_sign_in);



    }
    public void signUp(View view){
        Intent signUp=new Intent(SignIn.this, SignUpActivity.class);
        startActivity(signUp);

    }


    public void getUserBills(){
        ParseQuery<ParseObject> queryBill=ParseQuery.getQuery("Bill");
        queryBill.whereEqualTo("username",SignIn.mainUser.getId());
        queryBill.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e!=null){
                    e.printStackTrace();
                }else{
                    bills = new ArrayList<>();
                    if(objects.size()>0){
                        for(ParseObject object:objects){

                            billType=object.getString("type");
                            billAmount=object.getString("amount");
                            billDate=object.getString("date");

                            String [] date = billDate.split("/");

                            Date tempdate = new Date(date[0],date[1],date[2]);

                            Bill tempBill = new Bill(billType,Integer.parseInt(billAmount),tempdate);

                            bills.add(tempBill);


                        }


                    }
                    SignIn.mainUser.setUserbills(bills);

                }

            }
        });



    }

    public void getUserCredits(){
        ParseQuery<ParseObject> queryBill=ParseQuery.getQuery("Credit");
        queryBill.whereEqualTo("username",SignIn.mainUser.getId());
        queryBill.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e!=null){
                    e.printStackTrace();
                }else{
                    credits = new ArrayList<>();
                    if(objects.size()>0){
                        for(ParseObject object:objects){

                            creditAmount = object.getString("amount");
                            creditInstallment = object.getString("installment");
                            creditInterestRate = object.getString("interestRate");
                            creditPayAmount = object.getString("payAmount");

                            Credit tempCredit = new Credit(Integer.parseInt(creditAmount),Integer.parseInt(creditInstallment),
                                    Integer.parseInt(creditInterestRate),Integer.parseInt(creditPayAmount));

                            credits.add(tempCredit);


                        }


                    }
                    SignIn.mainUser.setCredits(credits);

                }

            }
        });



    }


    public void getCreditCards(User user){
        ParseQuery<ParseObject> queryBankAccount=ParseQuery.getQuery("CreditCard");
        queryBankAccount.whereEqualTo("userId", user.getId());
        queryBankAccount.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e!=null){
                    e.printStackTrace();
                }else{
                    creditCards = new ArrayList<>();
                    if(objects.size()>0){
                        for(ParseObject object:objects){

                            cardNo=object.getString("creditCardNo");
                            cardLimit=object.getString("limit");

                            creditCards.add(new CreditCard(cardNo,Integer.parseInt(cardLimit)));


                        }


                    }
                    user.setCreditcards(creditCards);
                }


            }
        });
    }




    public void getBankAccounts(User user) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("BankAccount");
        query.whereEqualTo("userId", user.getId());
        // Try local first
        query.fromLocalDatastore();
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (objects != null && objects.size() > 0) {
                    // Found local data
                    handleBankAccounts(objects, user);
                } else {
                    // Try online
                    ParseQuery<ParseObject> onlineQuery = ParseQuery.getQuery("BankAccount");
                    onlineQuery.whereEqualTo("userId", user.getId());
                    onlineQuery.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> objects, ParseException e) {
                            if (e == null && objects.size() > 0) {
                                // Pin data for offline access
                                ParseObject.pinAllInBackground("BankAccounts", objects, 
                                    new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e == null) {
                                                handleBankAccounts(objects, user);
                                            }
                                        }
                                });
                            }
                        }
                    });
                }
            }
        });
    }

    private void handleBankAccounts(List<ParseObject> objects, User user) {
        bankAccounts = new ArrayList<>();
        for (ParseObject object : objects) {
            bankAccountNo = object.getString("accountNo");
            bankCash = object.getString("cash");
            bankAccounts.add(new BankAccount(bankAccountNo, Integer.parseInt(bankCash)));
        }
        user.setBankAccounts(bankAccounts);
    }

    public void getHistory(){
        ParseQuery<ParseObject> queryBankAccount=ParseQuery.getQuery("History");
        if (!mainUser.getId().equals("9999")){
            queryBankAccount.whereEqualTo("userId", SignIn.mainUser.getId());
        }
        else{
            getAllUsers();
        }
        queryBankAccount.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e!=null){
                    e.printStackTrace();
                }else{
                    history = new Stack<>();
                    if(objects.size()>0){
                        for(ParseObject object:objects){

                            String id = object.getString("userId");
                            String process = object.getString("process");
                            java.util.Date date = object.getDate("date");
                            history.push(new History(id,process,date));

                        }

                    }
                    SignIn.mainUser.setHistory(history);

                }


            }
        });
    }
    public void getAllUsers(){
        ParseQuery<ParseObject> queryBankAccount=ParseQuery.getQuery("UserInfo");
        queryBankAccount.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e!=null){
                    e.printStackTrace();
                }else{
                    allUsers = new ArrayList<>();
                    if(objects.size()>0){
                        for(ParseObject object:objects){


                            String name=object.getString("userRealName");
                            String phone=object.getString("phone");
                            String userId=object.getString("username");
                            String address_string= object.getString("address");
                            String[] str = address_string.split(" ");
                            Address address = new Address(str[0],str[1],Integer.parseInt(str[2]),Integer.parseInt(str[3]),Integer.parseInt(str[4]),str[5],str[6],str[7]);
                            String jobName = object.getString("job");
                            String maxCreditAmount = object.getString("maxCreditAmount");
                            String interestRate = object.getString("interestRate");
                            String maxCreditInstallment = object.getString("maxCreditInstallment");
                            Job tempJob = new Job(jobName,maxCreditAmount,maxCreditInstallment,interestRate);
                            User tempUser = new User(name,userId, phone,address,tempJob);
                            getBankAccounts(tempUser);
                            getCreditCards(tempUser);
                            ParseFile parseFile=(ParseFile)object.get("images");
                            if( parseFile!=null){
                                parseFile.getDataInBackground(new GetDataCallback() {
                                    @Override
                                    public void done(byte[] data, ParseException e) {
                                        if(data!=null && e==null){
                                            Bitmap downloadedImage= BitmapFactory.decodeByteArray(data,0,data.length);
                                            tempUser.setPhoto(downloadedImage);

                                        }
                                    }
                                });
                            }
                            allUsers.add(tempUser);


                        }


                    }


                }


            }
        });
    }

    private void saveUserToPrefs(User user) {
        SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("userId", user.getId());
        editor.putString("userName", user.getName());
        editor.putString("userPass", user.getPass());
        editor.putString("userPhone", user.getPhoneNumber());
        // Add other user fields as needed
        editor.apply();
    }

    public void signIn(View view) {
        String inputUsername = userName.getText().toString();
        String inputPassword = userPass.getText().toString();

        if (inputUsername.isEmpty() || inputPassword.isEmpty()) {
            Toast.makeText(getApplicationContext(),
                "Please enter username and password",
                Toast.LENGTH_LONG).show();
            return;
        }

        // Check local storage
        ParseQuery<ParseObject> localQuery = ParseQuery.getQuery("UserInfo");
        localQuery.fromLocalDatastore();
        localQuery.whereEqualTo("username", inputUsername);
        
        try {
            List<ParseObject> localUsers = localQuery.find();
            if (localUsers != null && !localUsers.isEmpty()) {
                ParseObject userObject = localUsers.get(0);
                if (inputPassword.equals(userObject.getString("password"))) {
                    handleUserObject(userObject);
                    
                    Log.d("SignIn", "Login successful, starting splash screen");
                    Intent intent = new Intent(getApplicationContext(), splashScreen.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), 
                        "Invalid password", 
                        Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), 
                    "User not found", 
                    Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.e("SignIn", "Error: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), 
                "Error: " + e.getMessage(), 
                Toast.LENGTH_LONG).show();
        }
    }

    private void handleUserObject(ParseObject object) {
        mainUser = new User();
        mainUser.setName(object.getString("userRealName"));
        mainUser.setId(object.getString("username"));
        mainUser.setPass(object.getString("password"));
        mainUser.setPhoneNumber(object.getString("phone"));
        
        // Only set address if it exists
        String addressStr = object.getString("address");
        if (addressStr != null && !addressStr.isEmpty()) {
            String[] addressParts = addressStr.split(" ");
            if (addressParts.length >= 8) {
                Address address = new Address(
                    addressParts[0],                    // street
                    addressParts[1],                    // neighborhood
                    Integer.parseInt(addressParts[2]),  // apartmentNumber
                    Integer.parseInt(addressParts[3]),  // floor
                    Integer.parseInt(addressParts[4]),  // homeNumber
                    addressParts[5],                    // city
                    addressParts[6],                    // province
                    addressParts[7]                     // country
                );
                mainUser.setAddress(address);
            }
        }
        
        // Save user data to SharedPreferences
        saveUserToPrefs(mainUser);
        
        // Create and set job
        if (object.getString("job") != null) {
            Job job = new Job(
                object.getString("job"),
                String.valueOf(object.getDouble("maxCreditAmount")),
                String.valueOf(object.getDouble("maxCreditInstallment")),
                String.valueOf(object.getDouble("interestRate"))
            );
            mainUser.setJob(job);
        }

        getBankAccounts(mainUser);
        getCreditCards(mainUser);
        getUserBills();
        getUserCredits();

        intent = new Intent(SignIn.this, splashScreen.class);
        startActivity(intent);
    }
}