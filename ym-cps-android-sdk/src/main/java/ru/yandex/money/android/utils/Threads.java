package ru.yandex.money.android.utils;

/**
 * @author vyasevich
 */
public class Threads {
    public static void sleepSafely(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            // do nothing
        }
    }
}
