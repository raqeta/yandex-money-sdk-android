package ru.yandex.money.android.database;

/**
* @author vyasevich
*/
public class MoneySourceTable {
    public static final String NAME = "MoneySources";

    public static final String ID = "id";
    public static final String WALLET_ALLOWED = "wallet_allowed";
    public static final String CARD_ALLOWED = "card_allowed";
    public static final String CSC_REQUIRED = "cscRequired";
    public static final String PAN_FRAGMENT = "panFragment";
    public static final String TYPE = "type";

    public static final String COMMAND_CREATE =
            "CREATE TABLE " + NAME + " (\n" +
                    ID + " INTEGER PRIMARY KEY,\n" +
                    WALLET_ALLOWED + " INTEGER,\n" +
                    CARD_ALLOWED + " INTEGER,\n" +
                    CSC_REQUIRED + " INTEGER,\n" +
                    PAN_FRAGMENT + " TEXT,\n" +
                    TYPE + " TEXT);";

    MoneySourceTable() {
        // forbid instance creation
    }
}
