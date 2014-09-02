package ru.yandex.money.android;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by dvmelnikov on 12/02/14.
 */
public class Prefs {

    private static final String TAG = Prefs.class.getName();

    private static final String PREFS_NAME = "ru.yandex.money.android.preferences";
    private static final String PREF_INSTANCE_ID = "ru.yandex.money.android.instanceId";

    private final SharedPreferences prefs;

    public Prefs(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void storeInstanceId(String instanceId) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PREF_INSTANCE_ID, instanceId);
        editor.apply();
    }

    public String restoreInstanceId() {
        return prefs.getString(PREF_INSTANCE_ID, "");
    }
}
