package com.example.pankkiapplikaatio;

import android.provider.BaseColumns;

//Includes the table and column names for all the database tables
public final class SQLTables {
    private SQLTables() {}

    public static class BankTable {
        public static final String TABLE_NAME = "banks";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_BIC = "bic";
    }

    public static class UserTable {
        public static final String TABLE_NAME = "users";
        public static final String COLUMN_USERNAME = "username";
        public static final String COLUMN_PASSWORD = "password";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_ADDRESS = "address";
        public static final String COLUMN_PHONE = "phone_number";
    }

    public static class AccountTable {
        public static final String TABLE_NAME = "accounts";
        public static final String COLUMN_ACCOUNT_NUMBER = "account_number";
        public static final String COLUMN_BANK = "bank";
        public static final String COLUMN_USERNAME = "username";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_BALANCE = "balance";
        public static final String COLUMN_LIMIT = "credit_limit";
        public static final String COLUMN_INTEREST = "interest_rate";
        public static final String COLUMN_ALLOW_PAYMENTS = "allow_payments";
    }

    public static class CardTable implements BaseColumns {
        public static final String TABLE_NAME = "cards";
        public static final String COLUMN_USERNAME = "username";
        public static final String COLUMN_ACCOUNT_NUMBER = "account_number";
        public static final String COLUMN_WITHDRAW_LIMIT = "withdraw_limit";
        public static final String COLUMN_PAY_LIMIT = "pay_limit";
        public static final String COLUMN_AREA = "area";
    }

    public static class TransactionTable implements BaseColumns {
        public static final String TABLE_NAME = "transactions";
        public static final String COLUMN_FROM_ACC = "account_from";
        public static final String COLUMN_FROM_BANK = "bank_from";
        public static final String COLUMN_FROM_BIC = "BIC_from";
        public static final String COLUMN_TO_ACC = "account_to";
        public static final String COLUMN_TO_BANK = "bank_to";
        public static final String COLUMN_TO_BIC = "BIC_to";
        public static final String COLUMN_AMOUNT = "amount";
        public static final String COLUMN_TIME = "time_of_transaction";
        public static final String COLUMN_DESCRIPTION = "description";
    }
}
