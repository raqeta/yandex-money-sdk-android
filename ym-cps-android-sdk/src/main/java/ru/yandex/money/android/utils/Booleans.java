package ru.yandex.money.android.utils;

/**
 * @author vyasevich
 */
public class Booleans {

    public static byte toByte(boolean b) {
        return (byte) (b ? 1 : 0);
    }

    public static boolean toBoolean(byte b) {
        return b != 0;
    }
}
