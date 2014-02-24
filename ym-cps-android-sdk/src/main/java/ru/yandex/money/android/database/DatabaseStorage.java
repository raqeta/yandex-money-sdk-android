package ru.yandex.money.android.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.yandex.money.model.common.MoneySource;

/**
 * @author vyasevich
 */
public class DatabaseStorage {

    private final DatabaseHelper helper;

    public DatabaseStorage(Context context) {
        helper = DatabaseHelper.getInstance(context);
    }

    public void insertMoneySource(MoneySource moneySource) {
        MoneySource.WalletSource wallet = moneySource.getWallet();
        MoneySource.CardSource card = moneySource.getCard();

        ContentValues values = new ContentValues();
        if (wallet != null) {
            values.put(MoneySourceTable.WALLET_ALLOWED, wallet.isAllowed());
        }
        if (card != null) {
            values.put(MoneySourceTable.CARD_ALLOWED, card.isAllowed());
            values.put(MoneySourceTable.CSC_REQUIRED, card.isCscRequired());
            values.put(MoneySourceTable.PAN_FRAGMENT, card.getPanFragment());
            values.put(MoneySourceTable.TYPE, card.getType());
        }

        if (values.size() != 0) {
            SQLiteDatabase database = helper.getWritableDatabase();
            assert database != null : "cannot obtain database";
            database.insertOrThrow(MoneySourceTable.NAME, null, values);
        }
    }
}
