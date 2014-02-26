package ru.yandex.money.android.utils;

/**
 * @author vyasevich
 */
public class Strings {
    public static String concatenate(String[] array, String splitter) {
        if (array == null) {
            throw new NullPointerException("array is null");
        }
        if (splitter == null) {
            throw new NullPointerException("splitter is null");
        }
        if (array.length == 0) {
            return "";
        }

        StringBuilder sb = new StringBuilder(array[0]);
        for (int i = 1; i < array.length; ++i) {
            sb.append(splitter).append(array[i]);
        }
        return sb.toString();
    }

    public static String[] split(String str, int n) {
        if (str == null) {
            throw new NullPointerException("str is null");
        }
        if (n <= 0) {
            throw new IllegalArgumentException("n should be greater than 0");
        }

        final int length = str.length();
        String[] result = new String[length / n + (length % n == 0 ? 0 : 1)];
        for (int i = 0; i < result.length; ++i) {
            result[i] = str.substring(i * n, (i + 1) * n);
        }
        return result;
    }
}
