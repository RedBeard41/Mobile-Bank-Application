package com.bank.izbank.persistence.JSON;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.bank.izbank.UserInfo.User;
import com.bank.izbank.UserInfo.BankAccount;
import com.bank.izbank.UserInfo.CreditCard;
import com.bank.izbank.UserInfo.History;
import com.bank.izbank.Credit.Credit;
import com.bank.izbank.Bill.Bill;
import com.bank.izbank.Job.Job;
import com.bank.izbank.Job.Driver;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.UUID;

public class JsonStorage {
    private static final String TAG = "JsonStorage";
    private static JsonStorage instance;
    private static final Object LOCK = new Object();
    
    private static final String USERS_FILE = "users.json";
    private static final String BANK_ACCOUNTS_FILE = "bank_accounts.json";
    private static final String CREDIT_CARDS_FILE = "credit_cards.json";
    private static final String HISTORY_FILE = "history.json";
    private static final String CREDITS_FILE = "credits.json";
    private static final String BILLS_FILE = "bills.json";
    
    private final Context context;
    private final Gson gson;

    // Private constructor to prevent direct instantiation
    private JsonStorage(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Context cannot be null");
        }
        this.context = context.getApplicationContext();
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
                .excludeFieldsWithoutExposeAnnotation()
                .create();
                
        File directory = context.getFilesDir();
        if (!directory.exists() && !directory.mkdirs()) {
            throw new IllegalStateException("Unable to create app files directory");
        }
        
        try {
            initializeWithSampleData();
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    Log.d(TAG, "Found file: " + file.getName());
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error during initialization", e);
            throw new IllegalStateException("Failed to initialize storage: " + e.getMessage(), e);
        }
    }

    // Public static initializer - but only User class should call this
    public static JsonStorage initialize(Context context) {
        synchronized (LOCK) {
            if (instance == null) {
                instance = new JsonStorage(context);
            }
            return instance;
        }
    }

    // Public static accessor - but only User class should call this
    public static JsonStorage getInstance() {
        synchronized (LOCK) {
            if (instance == null) {
                throw new IllegalStateException("JsonStorage not initialized. Must call initialize() first.");
            }
            return instance;
        }
    }

    private void initializeWithSampleData() {
        try {
            if (loadUsers().isEmpty()) {
                User defaultUser = new User();
                defaultUser.setId("12345");
                defaultUser.setName("Test User 1");
                defaultUser.setPass("pwd111");
                defaultUser.setPhoneNumber("1234567890");
                
                // Initialize other required fields
                defaultUser.setBankAccounts(new ArrayList<>());
                defaultUser.setCreditcards(new ArrayList<>());
                defaultUser.setHistory(new Stack<>());
                defaultUser.setCredits(new ArrayList<>());
                
                // Set job as Driver - all values are preset in Driver class
                defaultUser.setJob(new Driver());
                
                // Initialize empty collections for the user
                ArrayList<User> users = new ArrayList<>();
                users.add(defaultUser);
                saveUsers(users);
                
                saveBankAccounts(defaultUser.getId(), new ArrayList<>());
                saveCreditCards(defaultUser.getId(), new ArrayList<>());
                saveHistory(defaultUser.getId(), new Stack<>());
                saveCredits(defaultUser.getId(), new ArrayList<>());
                saveBills(defaultUser.getId(), new ArrayList<>());
                Log.d(TAG, "Sample data initialized with user id: " + defaultUser.getId());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error initializing sample data", e);
            throw new IllegalStateException("Failed to initialize sample data: " + e.getMessage(), e);
        }
    }

    // User methods
    public ArrayList<User> loadUsers() {
        File file = new File(context.getFilesDir(), USERS_FILE);
        if (!file.exists()) {
            return new ArrayList<>();
        }

        try (FileReader reader = new FileReader(file)) {
            Type userListType = new TypeToken<ArrayList<User>>(){}.getType();
            ArrayList<User> users = gson.fromJson(reader, userListType);
            return users != null ? users : new ArrayList<>();
        } catch (IOException e) {
            Log.e(TAG, "Error loading users", e);
            return new ArrayList<>();
        }
    }

    public void saveUsers(List<User> users) {
        File file = new File(context.getFilesDir(), USERS_FILE);
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(users, writer);
            Log.d(TAG, "Users saved successfully");
        } catch (IOException e) {
            Log.e(TAG, "Error saving users", e);
        }
    }

    public User findUserById(String userId) {
        List<User> users = loadUsers();
        return users.stream()
                .filter(user -> user.getId().equals(userId))
                .findFirst()
                .orElse(null);
    }

    public boolean authenticateUser(String userId, String password) {
        User user = findUserById(userId);
        return user != null && user.getPass().equals(password);
    }

    // Bank Account methods
    public ArrayList<BankAccount> loadBankAccounts(String userId) {
        File file = new File(context.getFilesDir(), BANK_ACCOUNTS_FILE);
        if (!file.exists()) {
            return new ArrayList<>();
        }

        try (FileReader reader = new FileReader(file)) {
            Type type = new TypeToken<Map<String, ArrayList<BankAccount>>>(){}.getType();
            Map<String, ArrayList<BankAccount>> allAccounts = gson.fromJson(reader, type);
            return allAccounts != null && allAccounts.containsKey(userId) 
                ? allAccounts.get(userId) 
                : new ArrayList<>();
        } catch (IOException e) {
            Log.e(TAG, "Error loading bank accounts", e);
            return new ArrayList<>();
        }
    }

    public void saveBankAccounts(String userId, List<BankAccount> accounts) {
        File file = new File(context.getFilesDir(), BANK_ACCOUNTS_FILE);
        Map<String, List<BankAccount>> allAccounts = new HashMap<>();
        
        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                Type type = new TypeToken<Map<String, List<BankAccount>>>(){}.getType();
                Map<String, List<BankAccount>> existing = gson.fromJson(reader, type);
                if (existing != null) {
                    allAccounts = existing;
                }
            } catch (IOException e) {
                Log.e(TAG, "Error reading existing bank accounts", e);
            }
        }
        
        allAccounts.put(userId, accounts);
        
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(allAccounts, writer);
            Log.d(TAG, "Bank accounts saved for user: " + userId);
        } catch (IOException e) {
            Log.e(TAG, "Error saving bank accounts", e);
        }
    }

    // Credit Card methods
    public ArrayList<CreditCard> loadCreditCards(String userId) {
        File file = new File(context.getFilesDir(), CREDIT_CARDS_FILE);
        if (!file.exists()) {
            return new ArrayList<>();
        }

        try (FileReader reader = new FileReader(file)) {
            Type type = new TypeToken<Map<String, ArrayList<CreditCard>>>(){}.getType();
            Map<String, ArrayList<CreditCard>> allCards = gson.fromJson(reader, type);
            return allCards != null && allCards.containsKey(userId) 
                ? allCards.get(userId) 
                : new ArrayList<>();
        } catch (IOException e) {
            Log.e(TAG, "Error loading credit cards", e);
            return new ArrayList<>();
        }
    }

    public void saveCreditCards(String userId, List<CreditCard> cards) {
        File file = new File(context.getFilesDir(), CREDIT_CARDS_FILE);
        Map<String, List<CreditCard>> allCards = new HashMap<>();
        
        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                Type type = new TypeToken<Map<String, List<CreditCard>>>(){}.getType();
                Map<String, List<CreditCard>> existing = gson.fromJson(reader, type);
                if (existing != null) {
                    allCards = existing;
                }
            } catch (IOException e) {
                Log.e(TAG, "Error reading existing credit cards", e);
            }
        }
        
        allCards.put(userId, cards);
        
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(allCards, writer);
            Log.d(TAG, "Credit cards saved for user: " + userId);
        } catch (IOException e) {
            Log.e(TAG, "Error saving credit cards", e);
        }
    }

    // History methods
    public Stack<History> loadHistory(String userId) {
        File file = new File(context.getFilesDir(), HISTORY_FILE);
        if (!file.exists()) {
            return new Stack<>();
        }

        try (FileReader reader = new FileReader(file)) {
            Type type = new TypeToken<Map<String, Stack<History>>>(){}.getType();
            Map<String, Stack<History>> allHistory = gson.fromJson(reader, type);
            return allHistory != null && allHistory.containsKey(userId) 
                ? allHistory.get(userId) 
                : new Stack<>();
        } catch (IOException e) {
            Log.e(TAG, "Error loading history", e);
            return new Stack<>();
        }
    }

    public void saveHistory(String userId, Stack<History> history) {
        File file = new File(context.getFilesDir(), HISTORY_FILE);
        Map<String, Stack<History>> allHistory = new HashMap<>();
        
        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                Type type = new TypeToken<Map<String, Stack<History>>>(){}.getType();
                Map<String, Stack<History>> existing = gson.fromJson(reader, type);
                if (existing != null) {
                    allHistory = existing;
                }
            } catch (IOException e) {
                Log.e(TAG, "Error reading existing history", e);
            }
        }
        
        allHistory.put(userId, history);
        
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(allHistory, writer);
            Log.d(TAG, "History saved for user: " + userId);
        } catch (IOException e) {
            Log.e(TAG, "Error saving history", e);
        }
    }

    // Credit methods
    public ArrayList<Credit> loadCredits(String userId) {
        File file = new File(context.getFilesDir(), CREDITS_FILE);
        if (!file.exists()) {
            return new ArrayList<>();
        }

        try (FileReader reader = new FileReader(file)) {
            Type type = new TypeToken<Map<String, ArrayList<Credit>>>(){}.getType();
            Map<String, ArrayList<Credit>> allCredits = gson.fromJson(reader, type);
            return allCredits != null && allCredits.containsKey(userId) 
                ? allCredits.get(userId) 
                : new ArrayList<>();
        } catch (IOException e) {
            Log.e(TAG, "Error loading credits", e);
            return new ArrayList<>();
        }
    }

    public void saveCredits(String userId, List<Credit> credits) {
        File file = new File(context.getFilesDir(), CREDITS_FILE);
        Map<String, List<Credit>> allCredits = new HashMap<>();
        
        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                Type type = new TypeToken<Map<String, List<Credit>>>(){}.getType();
                Map<String, List<Credit>> existing = gson.fromJson(reader, type);
                if (existing != null) {
                    allCredits = existing;
                }
            } catch (IOException e) {
                Log.e(TAG, "Error reading existing credits", e);
            }
        }
        
        allCredits.put(userId, credits);
        
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(allCredits, writer);
            Log.d(TAG, "Credits saved for user: " + userId);
        } catch (IOException e) {
            Log.e(TAG, "Error saving credits", e);
        }
    }

    // Bill methods
    public ArrayList<Bill> loadBills(String userId) {
        File file = new File(context.getFilesDir(), BILLS_FILE);
        if (!file.exists()) {
            return new ArrayList<>();
        }

        try (FileReader reader = new FileReader(file)) {
            Type type = new TypeToken<Map<String, ArrayList<Bill>>>(){}.getType();
            Map<String, ArrayList<Bill>> allBills = gson.fromJson(reader, type);
            return allBills != null && allBills.containsKey(userId) 
                ? allBills.get(userId) 
                : new ArrayList<>();
        } catch (IOException e) {
            Log.e(TAG, "Error loading bills", e);
            return new ArrayList<>();
        }
    }

    public void saveBills(String userId, List<Bill> bills) {
        File file = new File(context.getFilesDir(), BILLS_FILE);
        Map<String, List<Bill>> allBills = new HashMap<>();
        
        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                Type type = new TypeToken<Map<String, List<Bill>>>(){}.getType();
                Map<String, List<Bill>> existing = gson.fromJson(reader, type);
                if (existing != null) {
                    allBills = existing;
                }
            } catch (IOException e) {
                Log.e(TAG, "Error reading existing bills", e);
            }
        }
        
        allBills.put(userId, bills);
        
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(allBills, writer);
            Log.d(TAG, "Bills saved for user: " + userId);
        } catch (IOException e) {
            Log.e(TAG, "Error saving bills", e);
        }
    }

    public ArrayList<String> loadUserIds() {
        ArrayList<String> userIds = new ArrayList<>();
        File directory = context.getFilesDir();
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                String fileName = file.getName();
                if (fileName.endsWith("_bankaccounts.json")) {
                    userIds.add(fileName.replace("_bankaccounts.json", ""));
                }
            }
        }
        return userIds;
    }

    // Convenience methods for single-item operations
    public void saveBankAccount(String userId, BankAccount account) {
        List<BankAccount> accounts = loadBankAccounts(userId);
        accounts.add(account);
        saveBankAccounts(userId, accounts);
    }

    public void saveCreditCard(String userId, CreditCard card) {
        List<CreditCard> cards = loadCreditCards(userId);
        cards.add(card);
        saveCreditCards(userId, cards);
    }

    // New operations
    public void deleteUserData(String userId) {
        File directory = context.getFilesDir();
        String[] files = {USERS_FILE, BANK_ACCOUNTS_FILE, CREDIT_CARDS_FILE, 
                         HISTORY_FILE, CREDITS_FILE, BILLS_FILE};
        for (String filename : files) {
            new File(directory, filename).delete();
        }
    }

    public String loadCurrentUserId() {
        SharedPreferences prefs = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        return prefs.getString("currentUserId", null);
    }

    public void saveCredit(String userId, Credit credit) {
        List<Credit> credits = loadCredits(userId);
        credits.add(credit);
        saveCredits(userId, credits);
    }
}