package ru.yandex.money.android.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yandex.money.model.cps.misc.MoneySource;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author vyasevich
 */
public class DatabaseStorage {

    private final DatabaseHelper helper;

    public DatabaseStorage(Context context) {
        helper = DatabaseHelper.getInstance(context);
    }

    public Collection<MoneySource> selectMoneySources() {
        SQLiteDatabase database = helper.getReadableDatabase();
        assert database != null : "cannot obtain readable database";

        Cursor cursor = database.rawQuery("SELECT * FROM " + MoneySourceTable.NAME, null);
        final int typeIndex = cursor.getColumnIndex(MoneySourceTable.TYPE);
        final int paymentCardTypeIndex = cursor.getColumnIndex(MoneySourceTable.PAYMENT_CARD_TYPE);
        final int panFragmentIndex = cursor.getColumnIndex(MoneySourceTable.PAN_FRAGMENT);
        final int tokenIndex = cursor.getColumnIndex(MoneySourceTable.TOKEN);

        Collection<MoneySource> moneySources = new ArrayList<MoneySource>();
        while (cursor.moveToNext()) {
            moneySources.add(new MoneySource(cursor.getString(typeIndex),
                    cursor.getString(paymentCardTypeIndex), cursor.getString(panFragmentIndex),
                    cursor.getString(tokenIndex)));
        }

        cursor.close();
        database.close();
        return moneySources;
    }

    public void insertMoneySource(MoneySource moneySource) {
        ContentValues values = new ContentValues();
        values.put(MoneySourceTable.TYPE, moneySource.getType());
        values.put(MoneySourceTable.PAYMENT_CARD_TYPE, moneySource.getPaymentCardType());
        values.put(MoneySourceTable.PAN_FRAGMENT, moneySource.getPanFragment());
        values.put(MoneySourceTable.TOKEN, moneySource.getMoneySourceToken());

        if (values.size() != 0) {
            SQLiteDatabase database = helper.getWritableDatabase();
            assert database != null : "cannot obtain writable database";
            database.insertOrThrow(MoneySourceTable.NAME, null, values);
            database.close();
        }
    }
}
