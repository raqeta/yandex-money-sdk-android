package ru.yandex.money.android.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.yandex.money.api.model.ExternalCard;

import java.util.ArrayList;
import java.util.List;

/**
 * @author vyasevich
 */
public class DatabaseStorage {

    private static final String TAG = "DatabaseStorage";

    private final DatabaseHelper helper;

    public DatabaseStorage(Context context) {
        helper = DatabaseHelper.getInstance(context);
    }

    public List<ExternalCard> selectMoneySources() {
        SQLiteDatabase database = getReadableDatabase();

        Cursor cursor = database.rawQuery("SELECT * FROM " + MoneySourceTable.NAME, null);
        final int fundingSourceTypeIndex = cursor.getColumnIndex(MoneySourceTable.FUNDING_SOURCE_TYPE);
        final int typeIndex = cursor.getColumnIndex(MoneySourceTable.TYPE);
        final int panFragmentIndex = cursor.getColumnIndex(MoneySourceTable.PAN_FRAGMENT);
        final int tokenIndex = cursor.getColumnIndex(MoneySourceTable.TOKEN);

        List<ExternalCard> moneySources = new ArrayList<ExternalCard>();
        while (cursor.moveToNext()) {
            moneySources.add(new ExternalCard(cursor.getString(panFragmentIndex),
                    cursor.getString(typeIndex), cursor.getString(fundingSourceTypeIndex),
                    cursor.getString(tokenIndex)));
        }

        cursor.close();
        database.close();
        return moneySources;
    }

    public void insertMoneySource(ExternalCard moneySource) {
        if (moneySource == null) {
            Log.w(TAG, "trying to insert null money source");
            return;
        }

        ContentValues values = new ContentValues();
        values.put(MoneySourceTable.FUNDING_SOURCE_TYPE, moneySource.getFundingSourceType());
        values.put(MoneySourceTable.TYPE, moneySource.getType());
        values.put(MoneySourceTable.PAN_FRAGMENT, moneySource.getPanFragment());
        values.put(MoneySourceTable.TOKEN, moneySource.getMoneySourceToken());

        if (values.size() != 0) {
            SQLiteDatabase database = getWritableDatabase();
            database.insertOrThrow(MoneySourceTable.NAME, null, values);
            database.close();
        }
    }

    public void deleteMoneySource(ExternalCard moneySource) {
        if (moneySource == null) {
            Log.w(TAG, "trying to delete null money source");
            return;
        }

        SQLiteDatabase database = getWritableDatabase();
        database.execSQL("DELETE FROM " + MoneySourceTable.NAME +
                " WHERE " + MoneySourceTable.TOKEN + " = \"" +
                moneySource.getMoneySourceToken() + "\"");
        database.close();
    }

    private SQLiteDatabase getReadableDatabase() {
        SQLiteDatabase database = helper.getReadableDatabase();
        assert database != null : "cannot obtain readable database";
        return database;
    }

    private SQLiteDatabase getWritableDatabase() {
        SQLiteDatabase database = helper.getWritableDatabase();
        assert database != null : "cannot obtain writable database";
        return database;
    }
}
