package ru.yandex.money.android.fragments;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.yandex.money.model.cps.ProcessExternalPayment;
import com.yandex.money.model.cps.RequestExternalPayment;

import java.io.IOException;
import java.util.Map;

import ru.yandex.money.android.PaymentArguments;
import ru.yandex.money.android.Prefs;
import ru.yandex.money.android.R;
import ru.yandex.money.android.YandexMoneyDroid;
import ru.yandex.money.android.utils.Threads;

/**
 * @author vyasevich
 */
public class WebFragment extends PaymentFragment {

    private static final String EXTRA_REQUEST_ID = "ru.yandex.money.android.extra.REQUEST_ID";
    private static final String EXTRA_CONTRACT_AMOUNT = "ru.yandex.money.android.extra.CONTRACT_AMOUNT";
    private static final String EXTRA_LOADING = "ru.yandex.money.android.extra.LOADING";

    private PaymentArguments arguments;
    private YandexMoneyDroid ymd;

    private ProgressBar progressBar;
    private WebView webView;

    private String requestId;
    private double contractAmount;

    public static WebFragment newInstance() {
        return new WebFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        arguments = getPaymentActivity().getArguments();
        ymd = new YandexMoneyDroid(arguments.getClientId(), new Prefs(getActivity()));
        if (savedInstanceState != null) {
            requestId = savedInstanceState.getString(EXTRA_REQUEST_ID);
            contractAmount = savedInstanceState.getDouble(EXTRA_CONTRACT_AMOUNT);
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.web_fragment, container, false);
        assert view != null : "view is null";

        progressBar = (ProgressBar) view.findViewById(R.id.progress);
        webView = (WebView) view.findViewById(R.id.webview);
        webView.setWebViewClient(new Client());
        webView.getSettings().setJavaScriptEnabled(true);

        if (savedInstanceState == null || savedInstanceState.getBoolean(EXTRA_LOADING)) {
            playFragment();
        } else {
            showWebView();
            webView.restoreState(savedInstanceState);
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(EXTRA_REQUEST_ID, requestId);
        outState.putDouble(EXTRA_CONTRACT_AMOUNT, contractAmount);
        boolean loading = webView.getVisibility() != View.VISIBLE;
        outState.putBoolean(EXTRA_LOADING, loading);
        if (!loading) {
            webView.saveState(outState);
        }
    }

    private void showWebView() {
        progressBar.setVisibility(View.GONE);
        webView.setVisibility(View.VISIBLE);
    }

    private void playFragment() {
        try {
            RequestExternalPayment rep = ymd.requestShop(arguments.getPatternId(),
                    arguments.getParams());
            if (rep.isSuccess()) {
                requestId = rep.getRequestId();
                contractAmount = rep.getContractAmount().doubleValue();
                processExternalPayment(requestId);
            } else {
                getPaymentActivity().showError(rep.getError());
            }
        } catch (IOException e) {
            getPaymentActivity().showError(e.getMessage());
        }
    }

    private void processExternalPayment(String requestId) {
        try {
            ProcessExternalPayment pep = ymd.process(requestId, false);
            onExternalPaymentProcessed(pep);
        } catch (IOException e) {
            getPaymentActivity().showError(e.getMessage());
        }
    }

    private void onExternalPaymentProcessed(ProcessExternalPayment pep) {
        if (pep.isExtAuthRequired()) {
            showWebView();
            String url = makeUrl(pep);
            webView.loadUrl(url);
        } else if (pep.isSuccess()) {
            getPaymentActivity().showSuccess(requestId, contractAmount);
        } else if (pep.isInProgress()) {
            Threads.sleepSafely(pep.getNextRetry());
            processExternalPayment(requestId);
        } else {
            getPaymentActivity().showError(pep.getError());
        }
    }

    private String makeUrl(ProcessExternalPayment pep) {
        String url = pep.getAcsUri() + "?";
        for (Map.Entry<String, String> entry : pep.getAcsParams().entrySet()) {
            url = url + entry.getKey() + "=" + entry.getValue() + "&";
        }
        return url;
    }

    private class Client extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            if (url.contains(YandexMoneyDroid.SUCCESS_URI)) {
                processExternalPayment(requestId);
            } else if (url.contains(YandexMoneyDroid.FAIL_URI)) {
                getPaymentActivity().showError(null);
            }
        }
    }
}
