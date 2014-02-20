package ru.yandex.money.android.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import java.util.Collections;
import java.util.Map;

import ru.yandex.money.android.utils.Bundles;

/**
 * @author vyasevich
 */
abstract class BasePaymentFragment extends Fragment {

    private static final String EXTRA_CLIENT_ID = "ru.yandex.money.android.extra.CLIENT_ID";
    private static final String EXTRA_PATTERN_ID = "ru.yandex.money.android.extra.PATTERN_ID";
    private static final String EXTRA_PARAMS = "ru.yandex.money.android.extra.PARAMS";

    private String clientId;
    private String patternId;
    private Map<String, String> params;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        readArguments();
    }

    protected static <T extends BasePaymentFragment> void setArguments(
            T fragment, String clientId, String patternId, Map<String, String> params) {

        Bundle parameters = new Bundle();
        Bundles.writeStringMapToBundle(parameters, params);

        Bundle args = new Bundle();
        args.putString(EXTRA_CLIENT_ID, clientId);
        args.putString(EXTRA_PATTERN_ID, patternId);
        args.putBundle(EXTRA_PARAMS, parameters);

        fragment.setArguments(args);
    }

    protected String getClientId() {
        return clientId;
    }

    protected String getPatternId() {
        return patternId;
    }

    protected Map<String, String> getParams() {
        return params;
    }

    private void readArguments() {
        Bundle args = getArguments();
        clientId = args.getString(EXTRA_CLIENT_ID);
        patternId = args.getString(EXTRA_PATTERN_ID);
        Bundle parameters = args.getBundle(EXTRA_PARAMS);
        params = Collections.unmodifiableMap(Bundles.readStringMapFromBundle(parameters));
    }
}
