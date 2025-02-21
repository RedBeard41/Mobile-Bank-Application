package com.bank.izbank.splashScreen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bank.izbank.Bill.Bill;
import com.bank.izbank.MainScreen.AdminPanelActivity;
import com.bank.izbank.MainScreen.MainScreenActivity;
import com.bank.izbank.R;
import com.bank.izbank.Sign.SignIn;
import com.bank.izbank.UserInfo.Admin;
import com.bank.izbank.UserInfo.BankAccount;
import com.bank.izbank.UserInfo.User;
import com.bank.izbank.UserInfo.UserContext;
import com.bank.izbank.UserInfo.UserTypeState;
import com.felipecsl.gifimageview.library.GifImageView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.bank.izbank.Sign.SignIn.mainUser;

public class splashScreen extends AppCompatActivity {

    private GifImageView gifImageView;
    private ProgressBar progressBar;
    private UserContext userContext;
    private UserTypeState normalUser;
    public static UserTypeState adminUser;

    private User loadUserFromPrefs() {
        SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
        String userId = prefs.getString("userId", null);
        if (userId == null) return null;

        User user = new User();
        user.setId(userId);
        user.setName(prefs.getString("userName", ""));
        user.setPass(prefs.getString("userPass", ""));
        user.setPhoneNumber(prefs.getString("userPhone", ""));
        // Load other user fields as needed
        return user;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        gifImageView = findViewById(R.id.GifImageView);
        progressBar = findViewById(R.id.progress_barr);
        progressBar.setVisibility(View.VISIBLE);
        userContext=new UserContext();
        normalUser=new User();
        adminUser=new Admin();
        //Gif create
        try {
            InputStream inputStream = getAssets().open("deneme2.gif");
            byte[] bytes = IOUtils.toByteArray(inputStream);
            gifImageView.setBytes(bytes);
            gifImageView.startAnimation();
        }
        catch (IOException ex){

        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Try to load user data if mainUser is null
                if (mainUser == null) {
                    mainUser = loadUserFromPrefs();
                }

                if (mainUser == null) {
                    Log.e("splashScreen", "No user data found, redirecting to SignIn");
                    Intent intent = new Intent(splashScreen.this, SignIn.class);
                    startActivity(intent);
                    finish();
                    return;
                }

                try {
                    if (mainUser.getId().equals("9999") && mainUser.getName().equals("admin")) {
                        userContext.setState(adminUser);
                        userContext.TypeChange(mainUser);
                        Intent intent = new Intent(splashScreen.this, AdminPanelActivity.class);
                        startActivity(intent);
                    } else {
                        userContext.setState(mainUser);
                        Intent splashIntent = new Intent(splashScreen.this, MainScreenActivity.class);
                        startActivity(splashIntent);
                    }
                    finish();
                } catch (Exception e) {
                    Log.e("splashScreen", "Error in navigation: " + e.getMessage());
                    Intent intent = new Intent(splashScreen.this, SignIn.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, 5000);
    }
}