package ru.yandex.money.android.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * @author vyasevich
 */
public class Keyboards {

    private Keyboards() {
    }

    public static void hideKeyboard(Activity activity) {
        if (activity == null || activity.isFinishing()) {
            return;
        }
        View view = activity.getWindow().getCurrentFocus();
        if (view != null) {
            getInputMethodManager(activity).hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void showKeyboard(Activity activity, View view) {
        if (activity == null || activity.isFinishing()) {
            return;
        }
        getInputMethodManager(activity).showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }

    private static InputMethodManager getInputMethodManager(Context context) {
        return (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
    }
}
