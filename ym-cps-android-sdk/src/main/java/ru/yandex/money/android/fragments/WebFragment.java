package ru.yandex.money.android.fragments;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.yandex.money.model.ProcessExternalPayment;
import com.yandex.money.model.RequestExternalPayment;

import java.io.IOException;
import java.util.Map;

import ru.yandex.money.android.Prefs;
import ru.yandex.money.android.R;
import ru.yandex.money.android.YandexMoneyDroid;

/**
 * @author vyasevich
 */
public class WebFragment extends BasePaymentFragment {

    private static final String EXTRA_LOADING = "ru.yandex.money.android.extra.LOADING";

    private YandexMoneyDroid ymd;
    private double contractAmount;

    private ProgressBar progressBar;
    private WebView webView;

    public static WebFragment newInstance(String clientId, String patternId,
                                          Map<String, String> params) {

        WebFragment frg = new WebFragment();
        setArguments(frg, clientId, patternId, params);
        return frg;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ymd = new YandexMoneyDroid(getClientId(), new Prefs(getActivity()));
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
            RequestExternalPayment requestExternalPayment =
                    ymd.requestShop(getPatternId(), getParams());
            if (requestExternalPayment.isSuccess()) {
                contractAmount = requestExternalPayment.getContractAmount().doubleValue();
                ProcessExternalPayment processExternalPayment =
                        ymd.process(requestExternalPayment.getRequestId(), false);

                if (processExternalPayment.isExtAuthRequired()) {
                    showWebView();
                    String url = makeUrl(processExternalPayment);
                    webView.loadUrl(url);
                } else if (processExternalPayment.isSuccess()) {
                    getPaymentFragment().showSuccess(contractAmount);
                }
            } else {
                getPaymentFragment().showError(requestExternalPayment.getError());
            }
        } catch (IOException e) {
            getPaymentFragment().showError(e.getMessage());
        }
    }

    private String makeUrl(ProcessExternalPayment processExternalPayment) {
        String res = processExternalPayment.getAcsUri() + "?";
        for (Map.Entry<String, String> entry : processExternalPayment.getAcsParams().entrySet()) {
            res = res + entry.getKey() + "=" + entry.getValue() + "&";
        }
        return res;
    }

    private PaymentFragment getPaymentFragment() {
        return (PaymentFragment) getParentFragment();
    }

    private class Client extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            if (url.contains(YandexMoneyDroid.SUCCESS_URI)) {
                getPaymentFragment().showSuccess(contractAmount);
            } else if (url.contains(YandexMoneyDroid.FAIL_URI)) {
                getPaymentFragment().showError("fail");
            }
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            System.out.println("!!!!: " + url);
            return true;
        }
    }
}
