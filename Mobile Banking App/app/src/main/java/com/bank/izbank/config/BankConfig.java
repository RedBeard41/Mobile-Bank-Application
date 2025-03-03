package com.bank.izbank.config;

import android.widget.Toast;

public class BankConfig {
    // API and Network Constants
    // public static final String BASE_URL_CRYPTO = "https://api.nomics.com/v1/";
    
    // UI Component Constants
    public static final boolean RECYCLERVIEW_FIXED_SIZE = true;
    public static final int TOAST_SHORT = Toast.LENGTH_SHORT;
    public static final int TOAST_LONG = Toast.LENGTH_LONG;
    public static final int IMAGE_MAX_SIZE = 1024;
    public static final int IMAGE_QUALITY = 80;
    public static final String IMAGE_FORMAT = "image.png";
    
    // Currency and Amount Constants
    public static final String CURRENCY = "TL";
    public static final String AMOUNT_POSITIVE_ERROR = "Amount must be positive";
    public static final String MONEY_FORMAT = "%.2f %s";
    public static final String INSUFFICIENT_FUNDS_MSG = "You Don't Have Enough Money";
    public static final int MAX_REQUEST_AMOUNT = 10000;
    
    // Account Related Constants
    public static final int MAX_ACCOUNTS = 5;
    public static final String MAX_ACCOUNTS_ERROR = "Maximum account limit reached";
    public static final String ACCOUNT_NUMBER_HINT = "Enter account number";
    public static final String SEND_MONEY_TITLE = "Send Money";
    public static final String SELECT_ACCOUNT_TITLE = "Select Account";
    public static final String SELECT_ACCOUNT_ERROR = "Please select an account first";
    public static final String NO_BANK_ACCOUNT_MSG = "Don't Have Bank Account!";
    public static final String NO_BANK_ACCOUNT_REQUEST = "You don't have any bank account. Please add one before request.";
    
    // Credit Card Related Constants
    public static final int MAX_CARDS = 3;
    public static final String MAX_CARDS_ERROR = "Maximum card limit reached";
    public static final String CARD_LIMIT_TITLE = "How Much Credit Card Limit Do You Want?";
    
    // Credit Related Constants
    public static final String CREDIT_TITLE = "Credit Screen";
    public static final String CREDIT_SET_TITLE = "Set Credit";
    public static final String CREDIT_CHECK_TITLE = "Check Credit";
    public static final String CREDIT_RECEIVED_MSG = "Credit Received!";
    public static final String CREDIT_AMOUNT_LABEL = "Credit Amount: ";
    public static final String CREDIT_DURATION_LABEL = "Credit Duration: ";
    public static final String CREDIT_RECEIVED_FORMAT = "Credit Received.(%d %s)";
    
    // Bill Related Constants
    public static final String BILL_TITLE = "Bill Screen";
    public static final String BILL_CHOOSE_TITLE = "Choose bill which you want pay";
    public static final String BILL_AMOUNT_HINT = "type amount";
    public static final String BILL_PAID_MSG = "Bill Paid";
    public static final String BILL_PAID_FORMAT = "Bill Paid.(%s: %d %s)";
    
    // Settings Related Constants
    public static final String SETTINGS_NOT_SPECIFIED = "Not specified";
    public static final String SETTINGS_CHANGE_NAME_TITLE = "Change Name";
    public static final String SETTINGS_CHANGE_NAME_HINT = "Enter new Name";
    public static final String SETTINGS_CHANGE_PHONE_TITLE = "Change phone";
    public static final String SETTINGS_CHANGE_PHONE_HINT = "Enter new phone number";
    public static final String SETTINGS_CHANGE_PASS_TITLE = "Change password";  // Unified with CHANGE_PASSWORD_TITLE
    public static final String SETTINGS_CHANGE_PASS_HINT = "Enter new password";  // Unified with CHANGE_PASSWORD_HINT
    public static final String SETTINGS_PASS_CHANGED = "Password changed successfully";
    public static final String SETTINGS_CHANGE_ADDRESS_TITLE = "Change address";
    public static final String SETTINGS_ADDRESS_ERROR = "Please enter a valid address";
    public static final String SETTINGS_CANCELED = "Operation canceled";
    public static final String SETTINGS_DELETED = " deleted successfully";
    public static final String SETTINGS_NOT_DELETED = " could not be deleted";
    public static final String SETTINGS_UPLOAD_SUCCESS = "Upload successful";
    public static final String CHANGE_PASSWORD_TITLE = "Change Password";  // Duplicate of SETTINGS_CHANGE_PASS_TITLE
    public static final String CHANGE_PASSWORD_HINT = "Enter new password";  // Duplicate of SETTINGS_CHANGE_PASS_HINT
    
    // Sign In/Up Related Constants
    public static final String USERNAME_HINT = "Username";
    public static final String PASSWORD_HINT = "Password";
    public static final String SIGN_IN_BUTTON = "Sign In";
    public static final String SIGN_UP_BUTTON = "Sign Up";
    
    // Admin Related Constants
    public static final String ADMIN_PANEL_TITLE = "Admin Panel";
    public static final String ADMIN_HISTORY_TITLE = "Admin History";
    public static final String ADMIN_DATE_FORMAT = "dd/MM/yyyy";  // Unified with DATE_FORMAT
    
    // History Related Constants
    public static final String HISTORY_TITLE = "HISTORY";
    public static final String HISTORY_USER_FORMAT = "History User: %s - %s";
    
    // Common Constants
    public static final String DATE_FORMAT = "dd/MM/yyyy";
    public static final String HELLO_FORMAT = "HELLO, %s.";
    public static final String MONEY_AMOUNT_TITLE = "How Much Money Do You Want?";
    public static final String INVALID_VALUES_MSG = "Please enter valid values";
    public static final String CANCEL_BUTTON = "Cancel";
    public static final String CONTINUE_BUTTON = "Continue";
    public static final String OPERATION_CANCELED = "Operation canceled";
    
    // Database Field Constants
    public static final String DB_FIELD_IMAGES = "images";
    public static final String DB_FIELD_CASH = "cash";
    public static final String DB_FIELD_PROCESS = "process";
    
    // Fragment Tags
    public static final String FRAGMENT_TAG_SETTINGS = "settings";
    public static final String FRAGMENT_TAG_BILL = "bill";
    public static final String FRAGMENT_TAG_CREDIT = "credit";
    public static final String FRAGMENT_TAG_ACCOUNT = "account";
    
    // Localized Messages
    // public static final String CRYPTO_MONEY_DEDUCTED_TR = "crypto parası düşüldü";
    
    // These login-related messages are missing
    public static final String LOGIN_SUCCESS = "Login Successful";
    public static final String INVALID_CREDENTIALS = "Invalid Username or Password";
    public static final String FIELDS_REQUIRED = "All fields are required";
    
    // Add signup constants
    public static final String SIGNUP_SUCCESS = "Sign up successful!";
    public static final String SIGNUP_FAILED = "Sign up failed!";
    public static final String PASSWORD_TOO_SHORT = "Password must be at least 6 characters";
}