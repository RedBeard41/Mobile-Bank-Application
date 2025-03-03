package com.bank.izbank.UserInfo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;

import com.bank.izbank.Bill.Bill;
import com.bank.izbank.Credit.Credit;
import com.bank.izbank.Job.Job;
import com.bank.izbank.R;
import com.bank.izbank.persistence.JSON.JsonStorage;
import com.bank.izbank.Sign.SignInActivity;
import com.bank.izbank.UserInfo.History;
import com.bank.izbank.Job.Job;
import com.bank.izbank.UserInfo.Address;
import com.bank.izbank.UserInfo.BankAccount;
import com.bank.izbank.UserInfo.CreditCard;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Stack;

import com.google.gson.annotations.Expose;

public class User implements  UserTypeState{
    private static JsonStorage storage;
    
    @Expose private String name;
    @Expose private String id;
    @Expose private String pass;
    @Expose private String phoneNumber;
    @Expose private Address address;
    private String addressSum;
    @Expose private ArrayList<CreditCard> creditcards;
    @Expose private ArrayList<BankAccount> bankAccounts;
    @Expose private ArrayList<Bill> userbills;
    @Expose private Job job;
    private Bitmap photo;
    @Expose private Stack<History> history;
    @Expose private ArrayList<Credit> credits;
    @Expose private boolean userType;

    /**
     * Initialize the storage system. This should only be called once at app startup.
     * @param context Application context
     */
    public static void initializeStorage(Context context) {
        if (storage == null) {
            storage = JsonStorage.initialize(context);
        }
    }

    /**
     * Internal method to get storage instance. Only entity classes should use this.
     */
    protected static JsonStorage getStorage() {
        if (storage == null) {
            throw new IllegalStateException("Storage not initialized. Call User.initializeStorage() first.");
        }
        return storage;
    }

    /**
     * Check if storage is initialized
     */
    public static boolean isStorageInitialized() {
        return storage != null;
    }

    /**
     * Get list of all user IDs in the system
     */
    public static List<String> getAllUserIds() {
        if (storage == null) {
            throw new IllegalStateException("Storage not initialized. Call User.initializeStorage() first.");
        }
        return storage.loadUserIds();
    }

    public User() {
        this.bankAccounts = new ArrayList<>();
        this.creditcards = new ArrayList<>();
        this.history = new Stack<>();
        this.credits = new ArrayList<>();
        this.userbills = new ArrayList<>();
    }

    public User(String name, String id,String pass,String phoneNumber, Address address, Job job) {
        this.name = name;
        this.id=id;
        this.pass=pass;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.job = job;
        this.creditcards = new ArrayList<>();
        this.bankAccounts = new ArrayList<>();
        this.history = new Stack<>();
        this.userbills = new ArrayList<>();
        this.credits = new ArrayList<>();
        this.userType=false;
    }

    public User(String name, String id,String pass,String phoneNumber, Address address) {
        this.name = name;
        this.id=id;
        this.pass=pass;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.history = new Stack<>();
        this.creditcards = new ArrayList<>();
        this.bankAccounts = new ArrayList<>();
        this.userbills = new ArrayList<>();
        this.credits = new ArrayList<>();
        this.userType=false;
    }

    public User(String name, String id,String phoneNumber, Address address, Job job) {
        this.name = name;
        this.id=id;
        this.pass=null;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.job = job;
        this.creditcards = new ArrayList<>();
        this.bankAccounts = new ArrayList<>();
        this.history = new Stack<>();
        this.userbills = new ArrayList<>();
        this.credits = new ArrayList<>();
        this.userType=false;
    }
    @Override
    public void TypeChange(User user) {
        this.userType=false;
    }
    public Stack<History> getHistory() {
        return history;
    }

    public void setHistory(Stack<History> history) {
        this.history = history;
    }
    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }

    public ArrayList<BankAccount> getBankAccounts() {
        return bankAccounts;
    }

    public void setBankAccounts(ArrayList<BankAccount> bankAccounts) {
        this.bankAccounts = bankAccounts;
    }

    public void setCreditcards(ArrayList<CreditCard> creditcards) {
        this.creditcards = creditcards;
    }

    public ArrayList<CreditCard> getCreditcards() {
        return creditcards;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public String addressWrite() {
        if (address == null) {
            return "No address set";  // or return empty string ""
        }
        addressSum = address.getStreet() + " " +
                    address.getNeighborhood() + " " +
                    address.getApartmentNumber() + " " +
                    address.getFloor() + " " +
                    address.getHomeNumber() + " " +
                    address.getCity() + " " +
                    address.getProvince() + " " +
                    address.getCountry();
        return addressSum;
    }

    public ArrayList<Bill> getUserbills() {
        return userbills;
    }

    public void setUserbills(ArrayList<Bill> userbills) {
        this.userbills = userbills;
    }

    public ArrayList<Credit> getCredits() {
        return credits;
    }

    public void setCredits(ArrayList<Credit> credits) {
        this.credits = credits;
    }

    public boolean isUserType() {
        return userType;
    }

    public void setUserType(boolean userType) {
        this.userType = userType;
    }

    /**
     * Load a user by their ID. This is the preferred way to load user data.
     */
    public static User loadUser(String userId) {
        if (storage == null) {
            throw new IllegalStateException("Storage not initialized. Call User.initializeStorage() first.");
        }
        
        User user = storage.findUserById(userId);
        if (user != null) {
            // Load all related data
            user.bankAccounts = storage.loadBankAccounts(userId);
            user.creditcards = storage.loadCreditCards(userId);
            user.history = storage.loadHistory(userId);
            user.credits = storage.loadCredits(userId);
            user.userbills = storage.loadBills(userId);
        }
        return user;
    }

    /**
     * Save all user data
     */
    public void save() {
        if (storage == null) {
            throw new IllegalStateException("Storage not initialized. Call User.initializeStorage() first.");
        }
        
        List<User> users = storage.loadUsers();
        boolean found = false;
        
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId().equals(this.getId())) {
                users.set(i, this);
                found = true;
                break;
            }
        }
        
        if (!found) {
            users.add(this);
        }
        
        storage.saveUsers(users);
        storage.saveBankAccounts(this.id, this.bankAccounts);
        storage.saveCreditCards(this.id, this.creditcards);
        storage.saveHistory(this.id, this.history);
        storage.saveCredits(this.id, this.credits);
        storage.saveBills(this.id, this.userbills);
    }

    public void updateInfo(String field, String value) {
        switch (field) {
            case "name": 
                this.setName(value); 
                break;
            case "phone": 
                this.setPhoneNumber(value); 
                break;
            case "pass":
                this.setPass(value);
                break;
            case "userType":
                this.setUserType(Boolean.parseBoolean(value));
                break;
            default: 
                throw new IllegalArgumentException("Unknown or non-updateable field: " + field);
        }
        this.save();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) &&
               Objects.equals(name, user.name) &&
               Objects.equals(pass, user.pass) &&
               Objects.equals(phoneNumber, user.phoneNumber) &&
               Objects.equals(address, user.address) &&
               Objects.equals(job, user.job) &&
               Objects.equals(bankAccounts, user.bankAccounts) &&
               Objects.equals(creditcards, user.creditcards) &&
               Objects.equals(history, user.history) &&
               Objects.equals(userType, user.userType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, pass, phoneNumber, address, job,
                          bankAccounts, creditcards, history, userType);
    }

    public User deepCopy() {
        User copy = new User();
        copy.setId(this.id);
        copy.setName(this.name);
        copy.setPass(this.pass);
        copy.setPhoneNumber(this.phoneNumber);
        copy.setAddress(this.address);
        copy.setJob(this.job);
        copy.setBankAccounts(new ArrayList<>(this.bankAccounts));
        copy.setCreditcards(new ArrayList<>(this.creditcards));
        
        Stack<History> historyCopy = new Stack<>();
        if (this.history != null) {
            historyCopy.addAll(this.history);
        }
        copy.setHistory(historyCopy);
        
        copy.setUserType(this.userType);
        return copy;
    }
}
