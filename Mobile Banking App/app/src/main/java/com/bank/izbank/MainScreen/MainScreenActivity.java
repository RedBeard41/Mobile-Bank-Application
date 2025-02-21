package com.bank.izbank.MainScreen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.bank.izbank.MainScreen.FinanceScreen.CryptoModel;
import com.bank.izbank.MainScreen.FinanceScreen.FinanceFragment;
import com.bank.izbank.R;
import com.bank.izbank.service.ICryptoAPI;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;

public class MainScreenActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;



    Fragment fragment1 = new AccountFragment();
    final Fragment fragment2 = new CreditFragment();
    final Fragment fragment4 = new BillFragment();
    final Fragment fragment5 = new SettingFragment();

    private Fragment tempFragment=fragment1;

    final FragmentManager fm = getSupportFragmentManager();

    private ArrayList<CryptoModel> cryptoModels;
    private final String BASE_URL = "https://api.nomics.com/v1/";
    private Retrofit retrofit;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Initialize fragments first
        fm.beginTransaction().add(R.id.fragment_container,fragment5,"5").hide(fragment5).commit();
        fm.beginTransaction().add(R.id.fragment_container,fragment4,"4").hide(fragment4).commit();
        fm.beginTransaction().add(R.id.fragment_container,fragment2,"2").hide(fragment2).commit();
        fm.beginTransaction().add(R.id.fragment_container,fragment1,"1").commit();

        // Setup navigation
        setupNavigation();

        // Load crypto data after UI is ready
        initializeRetrofit();
        loadData();
    }

    private void initializeRetrofit() {
        Gson gson = new GsonBuilder().setLenient().create();
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
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
                case R.id.menu3:
                    if (cryptoModels != null) {
                        Fragment financeFragment = new FinanceFragment(cryptoModels);
                        fm.beginTransaction()
                            .hide(tempFragment)
                            .add(R.id.fragment_container, financeFragment)
                            .show(financeFragment)
                            .commit();
                        tempFragment = financeFragment;
                    } else {
                        Toast.makeText(this, "Loading financial data...", Toast.LENGTH_SHORT).show();
                    }
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

    private void loadData() {
        ICryptoAPI cryptoAPI = retrofit.create(ICryptoAPI.class);
        
        compositeDisposable.add(
            cryptoAPI.getData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(cryptoList -> {
                    // Success - cryptoList is already the List<CryptoModel>
                    if (cryptoList != null && !cryptoList.isEmpty()) {
                        ArrayList<CryptoModel> cryptoModels = new ArrayList<>(cryptoList);
                        // Load your fragments with the data
                        loadFragment(cryptoModels);
                    }
                }, throwable -> {
                    // Error handling
                    System.out.println("Error: " + throwable.getMessage());
                })
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }

    private void loadFragment(ArrayList<CryptoModel> cryptoModels) {
        // Initialize fragments as class fields instead of local variables
        final Fragment defaultFragment = new AccountFragment();
        final Fragment creditFragment = new CreditFragment();
        final Fragment billFragment = new BillFragment();
        final Fragment settingFragment = new SettingFragment();
        Fragment currentFragment = defaultFragment;  // Track current fragment

        // Set the default fragment
        fm.beginTransaction()
            .add(R.id.fragment_container, settingFragment, "5").hide(settingFragment).commit();
        fm.beginTransaction()
            .add(R.id.fragment_container, billFragment, "4").hide(billFragment).commit();
        fm.beginTransaction()
            .add(R.id.fragment_container, creditFragment, "2").hide(creditFragment).commit();
        fm.beginTransaction()
            .add(R.id.fragment_container, defaultFragment, "1").commit();

        // Setup bottom navigation using your existing menu IDs
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment newFragment;
            
            switch (item.getItemId()) {
                case R.id.menu1:
                    newFragment = new AccountFragment();
                    fm.beginTransaction()
                        .add(R.id.fragment_container, newFragment, "1")
                        .hide(tempFragment)
                        .show(newFragment)
                        .commit();
                    tempFragment = newFragment;
                    break;
                case R.id.menu2:
                    fm.beginTransaction()
                        .hide(tempFragment)
                        .show(creditFragment)
                        .commit();
                    tempFragment = creditFragment;
                    break;
                case R.id.menu3:
                    newFragment = new FinanceFragment(cryptoModels);
                    getSupportFragmentManager()
                        .beginTransaction()
                        .hide(tempFragment)
                        .add(R.id.fragment_container, newFragment)
                        .show(newFragment)
                        .commit();
                    tempFragment = newFragment;
                    break;
                case R.id.menu4:
                    fm.beginTransaction()
                        .hide(tempFragment)
                        .show(billFragment)
                        .commit();
                    tempFragment = billFragment;
                    break;
                case R.id.menu5:
                    fm.beginTransaction()
                        .hide(tempFragment)
                        .show(settingFragment)
                        .commit();
                    tempFragment = settingFragment;
                    break;
            }
            return true;
        });
    }





    /*
public void getUserInfo(){

    ParseQuery<ParseObject> query2 = ParseQuery.getQuery("UserInfo");
    query2.whereEqualTo("userName", SignIn.mainUser.getId());
    query2.findInBackground(new FindCallback<ParseObject>() {
        @Override
        public void done(List<ParseObject> objects, ParseException e) {
            if(e!=null){
                e.printStackTrace();
            }else{
                if(objects.size()>0){
                    for(ParseObject object:objects){
                        ParseObject objectsOne=objects.get(0);
                        currentMoney=objects.getString("");
                        currentMoney= String.valueOf( Integer.parseInt(currentMoney)-decMoney);
                        objectsOne.put("cash",currentMoney);

                        objectsOne.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if(e != null){
                                    Toast.makeText(getApplicationContext(),e.getLocalizedMessage().toString(),Toast.LENGTH_LONG).show();
                                }
                                else{
                                    Toast.makeText(getApplicationContext(),"crypto parası düşüldü",Toast.LENGTH_LONG).show();
                                    SignIn.mainUser.getBankAccounts().get(index).setCash(Integer.parseInt(currentMoney));
                                    cryptoDatabase();

                                }
                            }
                        });




                    }


                }

            }
        }
    });
}
*/
}