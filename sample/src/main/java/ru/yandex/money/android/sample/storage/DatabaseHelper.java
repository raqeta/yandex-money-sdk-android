package ru.yandex.money.android.sample.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * @author vyasevich
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String NAME = "sample.db";

    private static final int VERSION = 1;

    private static DatabaseHelper instance;

    private DatabaseHelper(Context context) {
        super(context, NAME, null, VERSION);
    }

    public static DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.beginTransaction();
        db.execSQL(createTable(TableAccounts.NAME, TableAccounts.COLUMN_ACCOUNT_NUMBER));
        db.execSQL(createTable(TablePhones.NAME, TablePhones.COLUMN_PHONE_NUMBER));
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // does nothing
    }

    public void saveAccountNumber(String number) {
        saveValue(TableAccounts.NAME, TableAccounts.COLUMN_ACCOUNT_NUMBER, number);
    }

    public void savePhoneNumber(String number) {
        saveValue(TablePhones.NAME, TablePhones.COLUMN_PHONE_NUMBER, number);
    }

    public List<String> getAccountNumbers() {
        return getValues(TableAccounts.NAME, TableAccounts.COLUMN_ACCOUNT_NUMBER);
    }

    public List<String> getPhoneNumber() {
        return getValues(TablePhones.NAME, TablePhones.COLUMN_PHONE_NUMBER);
    }

    private static String createTable(String name, String column) {
        return "CREATE TABLE " + name + " (" + column + " TEXT PRIMARY KEY)";
    }

    private void saveValue(String table, String column, String value) {
        ContentValues values = new ContentValues();
        values.put(column, value);
        getWritableDatabase().insertOrThrow(table, null, values);
    }

    private List<String> getValues(String table, String column) {
        List<String> values = new ArrayList<String>();
        Cursor cursor = getReadableDatabase().query(table, new String[]{column}, null, null, null,
                null, column);
        while (cursor.moveToNext()) {
            values.add(cursor.getString(0));
        }
        return values;
    }

    private static class TableAccounts {
        private static final String NAME = "Accounts";
        private static final String COLUMN_ACCOUNT_NUMBER = "id";
    }

    private static class TablePhones {
        private static final String NAME = "Phones";
        private static final String COLUMN_PHONE_NUMBER = "id";
    }
}
