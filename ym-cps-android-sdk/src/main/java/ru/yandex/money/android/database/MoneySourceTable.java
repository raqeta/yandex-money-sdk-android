package ru.yandex.money.android.database;

/**
* @author vyasevich
*/
public class MoneySourceTable {
    public static final String NAME = "MoneySources";

    public static final String ID = "id";
    public static final String TYPE = "type";
    public static final String PAYMENT_CARD_TYPE = "payment_card_type";
    public static final String PAN_FRAGMENT = "pan_fragment";
    public static final String TOKEN = "token";

    public static final String COMMAND_CREATE =
            "CREATE TABLE " + NAME + " (\n" +
                    ID + " INTEGER PRIMARY KEY,\n" +
                    TYPE + " TEXT NOT NULL,\n" +
                    PAYMENT_CARD_TYPE + " TEXT,\n" +
                    PAN_FRAGMENT + " TEXT NOT NULL,\n" +
                    TOKEN + " TEXT);";

    MoneySourceTable() {
        // forbid instance creation
    }
}
