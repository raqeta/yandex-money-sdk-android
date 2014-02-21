package ru.yandex.money.android;

import android.os.Bundle;

import java.util.Collections;
import java.util.Map;

import ru.yandex.money.android.utils.Bundles;

/**
 * @author vyasevich
 */
public class PaymentArguments {

    private static final String EXTRA_CLIENT_ID = "ru.yandex.money.android.extra.CLIENT_ID";
    private static final String EXTRA_PATTERN_ID = "ru.yandex.money.android.extra.PATTERN_ID";
    private static final String EXTRA_PARAMS = "ru.yandex.money.android.extra.PARAMS";

    private final String clientId;
    private final String patternId;
    private final Map<String, String> params;

    public PaymentArguments(String clientId, String patternId, Map<String, String> params) {
        this.clientId = clientId;
        this.patternId = patternId;
        this.params = params;
    }

    public PaymentArguments(Bundle bundle) {
        clientId = bundle.getString(EXTRA_CLIENT_ID);
        patternId = bundle.getString(EXTRA_PATTERN_ID);
        Bundle parameters = bundle.getBundle(EXTRA_PARAMS);
        params = Collections.unmodifiableMap(Bundles.readStringMapFromBundle(parameters));
    }

    public Bundle toBundle() {
        Bundle parameters = new Bundle();
        Bundles.writeStringMapToBundle(parameters, params);

        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_CLIENT_ID, clientId);
        bundle.putString(EXTRA_PATTERN_ID, patternId);
        bundle.putBundle(EXTRA_PARAMS, parameters);
        return bundle;
    }

    public String getClientId() {
        return clientId;
    }

    public String getPatternId() {
        return patternId;
    }

    public Map<String, String> getParams() {
        return params;
    }
}
