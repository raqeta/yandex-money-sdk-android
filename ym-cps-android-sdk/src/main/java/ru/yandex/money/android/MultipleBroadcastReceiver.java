package ru.yandex.money.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author vyasevich
 */
public class MultipleBroadcastReceiver extends BroadcastReceiver {

    private final Map<String, IntentHandler> handlers = new HashMap<String, IntentHandler>();

    @Override
    public void onReceive(Context context, Intent intent) {
        IntentHandler handler = handlers.get(intent.getAction());
        if (handler != null) {
            handler.handle(intent);
        }
    }

    public MultipleBroadcastReceiver addHandler(String action, IntentHandler handler) {
        handlers.put(action, handler);
        return this;
    }

    public IntentFilter buildIntentFilter() {
        IntentFilter filter = new IntentFilter();
        Set<String> actions = handlers.keySet();
        for (String action : actions) {
            filter.addAction(action);
        }
        return filter;
    }
}
