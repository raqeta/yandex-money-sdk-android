package ru.yandex.money.android.utils;

import android.os.Bundle;

import java.util.HashMap;
import java.util.Map;

/**
 * @author vyasevich
 */
public final class Bundles {

    public static void writeStringMapToBundle(Bundle bundle, Map<String, String> map) {
        if (bundle == null) {
            throw new NullPointerException("bundle is null");
        }
        if (map == null) {
            throw new NullPointerException("map is null");
        }
        if (map.isEmpty()) {
            return;
        }

        for (String key : map.keySet()) {
            bundle.putString(key, map.get(key));
        }
    }

    public static Map<String, String> readStringMapFromBundle(Bundle bundle) {
        if (bundle == null) {
            throw new NullPointerException("bundle is null");
        }

        Map<String, String> map = new HashMap<String, String>();
        for (String key : bundle.keySet()) {
            map.put(key, bundle.getString(key));
        }
        return map;
    }
}
