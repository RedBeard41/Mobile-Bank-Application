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
import android.widget.Button;
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
import com.bank.izbank.persistence.JSON.JsonStorage;
import com.bank.izbank.config.BankConfig;
import com.bank.izbank.Sign.SignInActivity;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

public class SignUpActivity extends AppCompatActivity {

    private EditText editTextName, editTextId, editTextPass, editTextPhone;
    private Button buttonSignUp;
    private Address address;
    private Job job;
    private User user;
    private Spinner jobSpinner;
    private String[] jobs;
    private Job[] defaultJobs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Make sure storage is initialized
        if (!User.isStorageInitialized()) {
            User.initializeStorage(getApplicationContext());
        }

        // Initialize views
        editTextName = findViewById(R.id.edittext_id_name_sign_up);
        editTextId = findViewById(R.id.idNumberEditText);
        editTextPass = findViewById(R.id.edittext_user_password_sign_up);
        editTextPhone = findViewById(R.id.edittext_phone_sign_up);
        buttonSignUp = findViewById(R.id.button_sign_up);
        jobSpinner = findViewById(R.id.jobSpinner);

        // Initialize job arrays
        jobs = new String[]{
            "Doctor", "Engineer", "Teacher", "Police", "Soldier",
            "Driver", "Worker", "Farmer", "Student", "Waiter",
            "Contractor", "Entrepreneur", "Sportsman"
        };
        
        defaultJobs = new Job[]{
            new Doctor(), new Engineer(), new Teacher(), new Police(),
            new Soldier(), new Driver(), new Worker(), new Farmer(),
            new Student(), new Waiter(), new Contractor(),
            new Entrepreneur(), new Sportsman()
        };

        // Set up job spinner
        ArrayAdapter<String> jobAdapter = new ArrayAdapter<>(
            this,
            android.R.layout.simple_spinner_item,
            jobs
        );
        jobAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        jobSpinner.setAdapter(jobAdapter);
        
        // Set default selection to Contractor (index 10)
        jobSpinner.setSelection(10);
        job = defaultJobs[10];  // Default to Contractor

        jobSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                job = defaultJobs[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                job = new Contractor(); // Default to Contractor if nothing selected
            }
        });

        buttonSignUp.setOnClickListener(v -> signUp());
    }

    private void signUp() {
        String name = editTextName.getText().toString().trim();
        String id = editTextId.getText().toString().trim();
        String pass = editTextPass.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();

        if (name.isEmpty() || id.isEmpty() || pass.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Use existing address if it was created, otherwise use default address
        Address userAddress = address;  // use class field
        if (userAddress == null) {
            userAddress = new Address(
                "Not specified", // street
                "Not specified", // neighborhood
                0,              // apartmentNumber
                0,              // floor
                0,              // homeNumber
                "Not specified", // province
                "Not specified", // city
                "Not specified"  // country
            );
        }

        // Create and save new user with selected job
        user = new User(name, id, pass, phone, userAddress, job);
        try {
            user.save();
            Toast.makeText(this, "Sign up successful", Toast.LENGTH_SHORT).show();
            finish();
        } catch (Exception e) {
            Toast.makeText(this, "Error creating user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void createAddress(View v) {
        AlertDialog.Builder ad = new AlertDialog.Builder(this);
        ad.setTitle("Address");
        ad.setIcon(R.drawable.ic_address);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.settings_address_popup, null);
        ad.setView(dialogView);
        ad.setPositiveButton("CONTINUE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText street = dialogView.findViewById(R.id.setting_address_street);
                EditText blockNo = dialogView.findViewById(R.id.setting_address_block);
                EditText floor = dialogView.findViewById(R.id.setting_address_floor);
                EditText houseNo = dialogView.findViewById(R.id.setting_address_house);
                EditText country = dialogView.findViewById(R.id.setting_address_country);
                EditText neighborhood = dialogView.findViewById(R.id.setting_address_neigh);
                EditText town = dialogView.findViewById(R.id.setting_address_town);
                EditText state = dialogView.findViewById(R.id.setting_address_state);

                String streetText = street.getText().toString().trim();
                String neighborhoodText = neighborhood.getText().toString().trim();
                String blockNoText = blockNo.getText().toString().trim();
                String floorText = floor.getText().toString().trim();
                String houseNoText = houseNo.getText().toString().trim();
                String townText = town.getText().toString().trim();
                String stateText = state.getText().toString().trim();
                String countryText = country.getText().toString().trim();

                try {
                    // Validate string fields
                    if (streetText.isEmpty() || neighborhoodText.isEmpty() || 
                        townText.isEmpty() || stateText.isEmpty() || 
                        countryText.isEmpty()) {
                        throw new IllegalArgumentException("Please fill all text fields");
                    }

                    // Parse and validate numeric fields
                    int blockNumber = Integer.parseInt(blockNoText);
                    int floorNumber = Integer.parseInt(floorText);
                    int houseNumber = Integer.parseInt(houseNoText);

                    if (blockNumber < 0 || floorNumber < 0 || houseNumber < 0) {
                        throw new IllegalArgumentException("Numeric values cannot be negative");
                    }
                    
                    address = new Address(
                        streetText,
                        neighborhoodText,
                        blockNumber,
                        floorNumber,
                        houseNumber,
                        townText,
                        stateText,
                        countryText
                    );
                    Toast.makeText(getApplicationContext(), "Address saved successfully", Toast.LENGTH_SHORT).show();
                } catch (NumberFormatException e) {
                    Toast.makeText(getApplicationContext(), "Please enter valid numbers for block, floor, and house number", Toast.LENGTH_SHORT).show();
                } catch (IllegalArgumentException e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        ad.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "Canceled", Toast.LENGTH_SHORT).show();
            }
        });
        ad.create().show();
    }
}