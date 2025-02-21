package com.bank.izbank.Sign;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bank.izbank.Job.Contractor;
import com.bank.izbank.Job.Doctor;
import com.bank.izbank.Job.Driver;
import com.bank.izbank.Job.Engineer;
import com.bank.izbank.Job.Entrepreneur;
import com.bank.izbank.Job.Farmer;
import com.bank.izbank.Job.Job;
import com.bank.izbank.Job.Police;
import com.bank.izbank.Job.Soldier;
import com.bank.izbank.Job.Sportsman;
import com.bank.izbank.Job.Student;
import com.bank.izbank.Job.Teacher;
import com.bank.izbank.Job.Waiter;
import com.bank.izbank.Job.Worker;
import com.bank.izbank.MainScreen.MainScreenActivity;
import com.bank.izbank.R;
import com.bank.izbank.UserInfo.Address;
import com.bank.izbank.UserInfo.User;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.io.ByteArrayOutputStream;
import java.util.List;

import static com.bank.izbank.Sign.SignIn.mainUser;

public class SignUpActivity extends AppCompatActivity {

    private EditText userNameText , userPassText ,userIdText,userPhoneText;

    private Spinner spinner;
    private ArrayAdapter<String> jobArrayAdapter;
    private String [] jobs;
    private Job [] defaultJobs;
    public Job tempJob ;
    public String job;
    private ImageView imageView;
    
    private Address newAddress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        userNameText=findViewById(R.id.edittext_id_name_sign_up);
        userPassText=findViewById(R.id.edittext_user_password_sign_up);
        userIdText=findViewById(R.id.edittext_id_number_sign_up);
        userPhoneText=findViewById(R.id.edittext_phone_sign_up);
        spinner = findViewById(R.id.jobSpinner);
        imageView=findViewById(R.id.fragment5_ImageView);

        defineJobSpinner();

    }

    public void defineJobSpinner(){

        defaultJobs = new Job[]{new Contractor(),new Doctor(),new Driver(),new Engineer(),new Entrepreneur(),
        new Farmer(),new Police(),new Soldier(),new Sportsman(),new Student(),new Teacher(),new Waiter(),new Worker()};

        jobs = new String[] {"Contractor","Doctor","Driver","Engineer","Entrepreneur","Farmer","Police","Soldier",
                "Sportsman","Student","Teacher","Waiter","Worker"};

        jobArrayAdapter = new ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item,jobs);

        spinner.setAdapter(jobArrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {


                job = adapterView.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }

    public void signUp(View view) {
        if (!isValidSignup()) {
            return;
        }

        // Create user with basic required fields
        mainUser = new User(
            userNameText.getText().toString(),
            userIdText.getText().toString(),
            userPassText.getText().toString(),
            userPhoneText.getText().toString(),
            null  // Address is optional
        );

        // Create UserInfo object
        ParseObject userInfo = new ParseObject("UserInfo");
        populateUserInfo(userInfo);
        
        // Save locally
        try {
            userInfo.pin();
            Log.d("SignUpActivity", "User data saved locally");
            Toast.makeText(getApplicationContext(), 
                "User Created Successfully", 
                Toast.LENGTH_LONG).show();
            
            // Go to sign in
            Intent intent = new Intent(getApplicationContext(), SignIn.class);
            startActivity(intent);
            finish();
        } catch (ParseException e) {
            Log.e("SignUpActivity", "Error saving locally: " + e.getMessage());
            Toast.makeText(getApplicationContext(), 
                "Error creating user: " + e.getMessage(), 
                Toast.LENGTH_LONG).show();
        }
    }

    private void populateUserInfo(ParseObject userInfo) {
        // Set only required fields
        userInfo.put("username", mainUser.getId());
        userInfo.put("password", mainUser.getPass());
        userInfo.put("userRealName", mainUser.getName());
        userInfo.put("phone", mainUser.getPhoneNumber());
        
        // Only add address if it exists
        if (newAddress != null) {
            userInfo.put("address", mainUser.addressWrite());
        }

        // Set job info if selected
        if (job != null) {
            for (Job x : defaultJobs) {
                if (x.getName().equals(job)) {
                    tempJob = x;
                    break;
                }
            }
            if (tempJob != null) {
                mainUser.setJob(tempJob);
                userInfo.put("job", tempJob.getName());
                userInfo.put("maxCreditAmount", tempJob.getMaxCreditAmount());
                userInfo.put("maxCreditInstallment", tempJob.getMaxCreditInstallment());
                userInfo.put("interestRate", tempJob.getInterestRate());
            }
        }
    }

    private boolean isValidSignup() {
        // Check only required fields
        if (userNameText.getText().toString().isEmpty() ||
            userIdText.getText().toString().isEmpty() ||
            userPassText.getText().toString().isEmpty() ||
            userPhoneText.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), 
                "Please fill all required fields", 
                Toast.LENGTH_LONG).show();
            return false;
        }

        // Check password length
        if (userPassText.getText().toString().length() < 6) {
            Toast.makeText(getApplicationContext(), 
                "Password must be at least 6 characters", 
                Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    public void createAddress(View v){

        AlertDialog.Builder ad = new AlertDialog.Builder(this);

        ad.setTitle("Address");
        ad.setIcon(R.drawable.ic_address);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView= inflater.inflate(R.layout.settings_address_popup, null);
        ad.setView(dialogView);
        ad.setPositiveButton("CONTINUE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                TextView street=dialogView.findViewById(R.id.setting_address_street);
                TextView blockNo=dialogView.findViewById(R.id.setting_address_block);
                TextView floor=dialogView.findViewById(R.id.setting_address_floor);
                TextView houseNo=dialogView.findViewById(R.id.setting_address_house);
                TextView country=dialogView.findViewById(R.id.setting_address_country);
                TextView neighborhood=dialogView.findViewById(R.id.setting_address_neigh);
                TextView town=dialogView.findViewById(R.id.setting_address_town);
                TextView state=dialogView.findViewById(R.id.setting_address_state);
                if(street.getText().toString() !=null &&neighborhood.getText().toString()!=null && blockNo.getText().toString()!=null&&floor.getText().toString()!=null &&houseNo.getText().toString()!=null&& town.getText().toString()!=null &&state.getText().toString()!=null&& country.getText().toString()!=null){
                    newAddress= new Address(street.getText().toString(),neighborhood.getText().toString(),Integer.parseInt(blockNo.getText().toString()),Integer.parseInt(floor.getText().toString()),Integer.parseInt(houseNo.getText().toString()),town.getText().toString(),state.getText().toString(),country.getText().toString());


                }else{
                    Toast.makeText(getApplicationContext(),"Please Fill the all field",Toast.LENGTH_SHORT).show();
                }
            }
        });
        ad.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(),"Canceled",Toast.LENGTH_SHORT).show();
            }
        });
        ad.create().show();


    }


}