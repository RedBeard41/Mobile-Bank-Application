package com.bank.izbank.MainScreen;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import static android.app.Activity.RESULT_OK;

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
import com.bank.izbank.R;
import com.bank.izbank.Sign.SignInActivity;
import com.bank.izbank.UserInfo.Address;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.LogOutCallback;
import com.parse.Parse;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.bank.izbank.UserInfo.User;
import com.bank.izbank.config.BankConfig;
import com.bank.izbank.persistence.JSON.JsonStorage;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.parse.Parse.getApplicationContext;
import com.bank.izbank.config.BankConfig;

public class SettingFragment extends Fragment {
    private TextView name,phone,userId,userAdress,prof,logOut;
    private Bitmap selectedImage;
    private ImageView imageView;
    private Spinner spinner;
    private ArrayAdapter<String> jobArrayAdapter;
    private  RelativeLayout relativeLayoutMobileRow,relativeLayoutNameRow,relativeLayoutAddressRow,relativeLayoutPassRow,relativeLayoutDeleteRow;
    private String newChangeItem ="";
    private Address newAddress;
    private String [] jobs;
    private Job [] defaultJobs;
    private  String job;
    private AlertDialog.Builder ad;
    private JsonStorage jsonStorage;
    private User mainUser;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!User.isStorageInitialized()) {
            User.initializeStorage(getContext());
        }
        mainUser = SignInActivity.mainUser;
        ad = new AlertDialog.Builder(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.activity_profile, container, false);
        name = rootView.findViewById(R.id.settings_userNameTextView);
        phone= rootView.findViewById(R.id.settings_phoneTextView);
        userId= rootView.findViewById(R.id.setting_userId_Big);
        userAdress= rootView.findViewById(R.id.settings_addressTextView);
        prof= rootView.findViewById(R.id.settings_profTextView);
        imageView=rootView.findViewById(R.id.fragment5_ImageView);
        relativeLayoutMobileRow=rootView.findViewById(R.id.account_mobile_row);
        relativeLayoutNameRow=rootView.findViewById(R.id.account_setting_name_row);
        relativeLayoutAddressRow=rootView.findViewById(R.id.settings_addres_row);
        relativeLayoutPassRow=rootView.findViewById(R.id.settings_change_pass_row);
        relativeLayoutDeleteRow=rootView.findViewById(R.id.settings_account_delete_row);
        logOut=rootView.findViewById(R.id.setting_logout_textView);

        spinner = rootView.findViewById(R.id.jobSpinner);
        name.setText(mainUser.getName());
        phone.setText(mainUser.getPhoneNumber());
        userId.setText(mainUser.getId());
        if (SignInActivity.mainUser != null) {
            String address = SignInActivity.mainUser.addressWrite();
            userAdress.setText(address);
        }
        if (mainUser != null && mainUser.getJob() != null) {
            prof.setText(mainUser.getJob().getName());
        } else {
            prof.setText(BankConfig.SETTINGS_NOT_SPECIFIED);
        }
        if(mainUser.getPhoto()!=null){
            imageView.setImageBitmap(mainUser.getPhoto());
        }
        defineJobSpinner();
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        relativeLayoutNameRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeName(v);
            }
        });
        relativeLayoutMobileRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePhone(v);
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage(v);
            }
        });
        relativeLayoutPassRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();
            }
        });

relativeLayoutAddressRow.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        changeAddress(v);
    }
});
relativeLayoutDeleteRow.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        deleteAccount(v);
    }
});
logOut.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        logOut();
    }
});
    }

    public void changeName(View v){

        final EditText editText = new EditText(getContext());
        editText.setHint(BankConfig.SETTINGS_CHANGE_NAME_HINT);

        ad.setTitle(BankConfig.SETTINGS_CHANGE_NAME_TITLE);
        ad.setIcon(R.drawable.ic_name);
        ad.setView(editText);
        ad.setPositiveButton("Change", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                newChangeItem=editText.getText().toString();
                change( newChangeItem,"userRealName");
                name.setText( newChangeItem);
                mainUser.setName( newChangeItem);
            }
        });
        ad.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getContext(), BankConfig.SETTINGS_CANCELED, BankConfig.TOAST_SHORT).show();
            }
        });
        ad.create().show();

    }
    public void changePassword(){

        ad.setTitle(BankConfig.SETTINGS_CHANGE_PASS_TITLE);
        ad.setMessage(BankConfig.SETTINGS_CHANGE_PASS_HINT);
        final EditText editText = new EditText(getContext());
        ad.setView(editText);
        ad.setPositiveButton("Change", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                newChangeItem=editText.getText().toString();
                ParseUser user=ParseUser.getCurrentUser();
               user.setPassword(newChangeItem);
               user.saveInBackground(new SaveCallback() {
                   @Override
                   public void done(ParseException e) {
                       if(e ==null ) {
                           Toast.makeText(getContext(), BankConfig.SETTINGS_PASS_CHANGED, BankConfig.TOAST_SHORT).show();
                       } else {
                           Toast.makeText(getContext(),e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                       }
                   }
               });
            }
        });
        ad.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getContext(), BankConfig.SETTINGS_CANCELED, BankConfig.TOAST_SHORT).show();
            }
        });
        ad.create().show();

    }
    public void changePhone(View v){

        final EditText editTextPhone = new EditText(getContext());
        editTextPhone.setHint(BankConfig.SETTINGS_CHANGE_PHONE_HINT);

        ad.setTitle(BankConfig.SETTINGS_CHANGE_PHONE_TITLE);
        ad.setIcon(R.drawable.ic_mobile);
        ad.setView(editTextPhone);
        ad.setPositiveButton("Change", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                newChangeItem =editTextPhone.getText().toString();
                change(newChangeItem,"phone");
                mainUser.setPhoneNumber(newChangeItem);
                phone.setText(newChangeItem);
            }
        });
        ad.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getContext(), BankConfig.SETTINGS_CANCELED, BankConfig.TOAST_SHORT).show();
            }
        });
        ad.create().show();


    }
    public void changeAddress(View v){

        ad.setTitle(BankConfig.SETTINGS_CHANGE_ADDRESS_TITLE);
        ad.setIcon(R.drawable.ic_address);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView= inflater.inflate(R.layout.settings_address_popup, null);
        ad.setView(dialogView);
        ad.setPositiveButton("Change", new DialogInterface.OnClickListener() {
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
                    mainUser.setAddress(newAddress);
                    userAdress.setText(mainUser.addressWrite());
                    change(mainUser.addressWrite(),"address");
                }else{
                    Toast.makeText(getContext(), BankConfig.SETTINGS_ADDRESS_ERROR, BankConfig.TOAST_SHORT).show();
                }


            }
        });
        ad.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getContext(), BankConfig.SETTINGS_CANCELED, BankConfig.TOAST_SHORT).show();
            }
        });
        ad.create().show();


    }


    private void deleteAccount(View v){
        jsonStorage.deleteUserData(mainUser.getId());
        Intent intent = new Intent(getActivity(), SignInActivity.class);
        startActivity(intent);
    }

    private void change(String changeItem, String changeColumnName) {
        mainUser.updateInfo(changeColumnName, changeItem);
    }

    public void selectImage(View view){
        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }
        else{
            Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent,2);
        }
    }

    public void defineJobSpinner() {
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
        
        jobArrayAdapter = new ArrayAdapter<>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            jobs
        );
        jobArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(jobArrayAdapter);
        
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                job = jobs[position];
                mainUser.setJob(defaultJobs[position]);
                mainUser.save();
                prof.setText(job);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }
    private void logOut() {
        // Clear only the in-memory user reference
        SignInActivity.mainUser = null;
        
        // Navigate to SignInActivity
        Intent intent = new Intent(getActivity(), SignInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        getActivity().finish();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            Uri dataUri = data.getData();
            try {
                if (Build.VERSION.SDK_INT >= 28) {
                    ImageDecoder.Source source = ImageDecoder.createSource(getActivity().getContentResolver(), dataUri);
                    selectedImage = ImageDecoder.decodeBitmap(source);
                    imageView.setImageBitmap(selectedImage);

                } else {
                    selectedImage = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), dataUri);
                    imageView.setImageBitmap(selectedImage);

                }
                mainUser.setPhoto(selectedImage);
                Bitmap smallerImage = makeSmallerImage(selectedImage, BankConfig.IMAGE_MAX_SIZE);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                smallerImage.compress(Bitmap.CompressFormat.PNG, BankConfig.IMAGE_QUALITY, byteArrayOutputStream);

                byte[] bytes = byteArrayOutputStream.toByteArray();

                ParseQuery<ParseObject> query = ParseQuery.getQuery("UserInfo");
                query.whereEqualTo("username", mainUser.getId());
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        if (e != null) {
                            e.printStackTrace();
                        } else {
                            if (objects.size() > 0) {
                                for (ParseObject object : objects) {
                                    ParseObject userInfo = objects.get(0);
                                        ParseFile file = new ParseFile(BankConfig.IMAGE_FORMAT, bytes);
                                        userInfo.put("images", file);
                                        object.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                if (e != null) {
                                                    Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(getContext(), BankConfig.SETTINGS_UPLOAD_SUCCESS, BankConfig.TOAST_SHORT).show();

                                                }

                                            }
                                        });



                                }
                            }
                        }

                    }
                });

            }catch (Exception e){
                Toast.makeText(getActivity().getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==1 && grantResults[0]==PackageManager.PERMISSION_GRANTED && permissions.length>0){
            Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent,2);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }





    private Bitmap makeSmallerImage(Bitmap image, int maximumSize) {

        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;

        if (bitmapRatio > 1) {
            width = maximumSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maximumSize;
            width = (int) (height * bitmapRatio);
        }

        return Bitmap.createScaledBitmap(image,width,height,true);
    }


}
