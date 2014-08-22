package ru.yandex.money.android.database;

/**
* @author vyasevich
*/
public class MoneySourceTable {
    public static final String NAME = "MoneySources";

    public static final String FUNDING_SOURCE_TYPE = "funding_source_type";
    public static final String TYPE = "type";
    public static final String PAN_FRAGMENT = "pan_fragment";
    public static final String TOKEN = "token";

    public static final String COMMAND_CREATE =
            "CREATE TABLE " + NAME + " (\n" +
                    TOKEN + " TEXT PRIMARY KEY,\n" +
                    FUNDING_SOURCE_TYPE + " TEXT NOT NULL,\n" +
                    TYPE + " TEXT NOT NULL,\n" +
                    PAN_FRAGMENT + " TEXT NOT NULL);";

    MoneySourceTable() {
        // forbid instance creation
    }
}
