package ru.yandex.money.android;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.yandex.money.ParamsP2P;
import com.yandex.money.model.RequestExternalPayment;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class PaymentFragment extends Fragment {

    public static final String TAG = PaymentFragment.class.getName();

    private static String ARG_CLIENT_ID = "ru.yandex.money.android.arg_client_id";
    private static String ARG_PATTERN_ID = "ru.yandex.money.android.arg_pattern_id";

    private YandexMoneyDroid ymd;

    private String patternId;
    private Map<String, String> params = new HashMap<String, String>();
    private String clientId;
    private WebView webview;
    private ProgressBar progress;
    private Prefs prefs;
    private boolean isPaused;

    public static PaymentFragment newInstance(String clientId, ParamsP2P params) {
        PaymentFragment frg = new PaymentFragment();

        Bundle args = new Bundle();
        args.putString(ARG_CLIENT_ID, clientId);
        for (Map.Entry<String, String> entry : params.makeParams().entrySet()) {
            args.putString(entry.getKey(), entry.getValue());
        }
        frg.setArguments(args);
        return frg;
    }

    private PaymentFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        parseArgs();
        ymd = new YandexMoneyDroid(clientId, new Prefs(getActivity()));
        prefs = new Prefs(getActivity());
    }

    private void parseArgs() {
        for (String key : getArguments().keySet()) {
            String val = getArguments().getString(key);
            if (ARG_CLIENT_ID.equals(key)) {
                clientId = getArguments().getString(ARG_CLIENT_ID);
            } else if (ARG_PATTERN_ID.equals(key)) {
                patternId = getArguments().getString(ARG_PATTERN_ID);
            } else {
                params.put(key, val);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.payment_fragment, container, false);

        progress = (ProgressBar) view.findViewById(R.id.progress);
        webview = (WebView) view.findViewById(R.id.webview);

        String patternId = "phone-topup";
        Map<String, String> params = new HashMap<String, String>();
        params.put("amount", "23");
        params.put("phone-number", "79112611383");
        try {
            RequestExternalPayment requestExternalPayment = ymd.requestShop(patternId, params);
            progress.setVisibility(View.GONE);
            webview.setVisibility(View.VISIBLE);
            Toast.makeText(getActivity(), requestExternalPayment.getRequestId(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(getActivity(), "fukcup " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        isPaused = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        isPaused = false;
    }
}
