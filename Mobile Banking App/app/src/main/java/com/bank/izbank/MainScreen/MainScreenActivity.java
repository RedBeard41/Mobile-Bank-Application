package com.bank.izbank.MainScreen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.os.Bundle;
import com.bank.izbank.R;
import com.bank.izbank.Sign.SignInActivity;
import com.bank.izbank.config.BankConfig;
import com.bank.izbank.UserInfo.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainScreenActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private User lastSavedState;

    // Initialize fragments
    Fragment fragment1 = new AccountFragment();
    final Fragment fragment2 = new CreditFragment();
    final Fragment fragment4 = new BillFragment();
    final Fragment fragment5 = new SettingFragment();
    private Fragment tempFragment = fragment1;
    final FragmentManager fm = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        // Storage already initialized in SignInActivity
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        lastSavedState = SignInActivity.mainUser.deepCopy();

        initializeFragments();
        setupNavigation();
    }

    private void initializeFragments() {
        fm.beginTransaction().add(R.id.fragment_container, fragment5, BankConfig.FRAGMENT_TAG_SETTINGS).hide(fragment5).commit();
        fm.beginTransaction().add(R.id.fragment_container, fragment4, BankConfig.FRAGMENT_TAG_BILL).hide(fragment4).commit();
        fm.beginTransaction().add(R.id.fragment_container, fragment2, BankConfig.FRAGMENT_TAG_CREDIT).hide(fragment2).commit();
        fm.beginTransaction().add(R.id.fragment_container, fragment1, BankConfig.FRAGMENT_TAG_ACCOUNT).commit();
    }

    private void setupNavigation() {
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.menu1:
                    fm.beginTransaction().hide(tempFragment).show(fragment1).commit();
                    tempFragment = fragment1;
                    return true;
                case R.id.menu2:
                    fm.beginTransaction().hide(tempFragment).show(fragment2).commit();
                    tempFragment = fragment2;
                    return true;
                case R.id.menu4:
                    fm.beginTransaction().hide(tempFragment).show(fragment4).commit();
                    tempFragment = fragment4;
                    return true;
                case R.id.menu5:
                    fm.beginTransaction().hide(tempFragment).show(fragment5).commit();
                    tempFragment = fragment5;
                    return true;
            }
            return false;
        });
    }

    private boolean hasUserDataChanged() {
        return !SignInActivity.mainUser.equals(lastSavedState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (SignInActivity.mainUser != null && hasUserDataChanged()) {
            SignInActivity.mainUser.save();  // No JsonStorage needed
            lastSavedState = SignInActivity.mainUser.deepCopy();
        }
    }
}