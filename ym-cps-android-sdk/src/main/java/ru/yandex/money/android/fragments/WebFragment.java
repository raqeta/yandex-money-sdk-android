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

import java.util.Map;

import ru.yandex.money.android.R;
import ru.yandex.money.android.services.DataServiceHelper;

/**
 * @author vyasevich
 */
public class WebFragment extends PaymentFragment {

    private static final String EXTRA_LOADED = "ru.yandex.money.android.extra.LOADED";

    private ProgressBar progressBar;
    private WebView webView;

    private String requestId;
    private double contractAmount;

    public static WebFragment newInstance(String requestId, double contractAmount) {
        Bundle args = new Bundle();
        args.putString(EXTRA_REQUEST_ID, requestId);
        args.putDouble(EXTRA_CONTRACT_AMOUNT, contractAmount);

        WebFragment fragment = new WebFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        assert args != null : "specify proper args for WebFragment";

        requestId = args.getString(EXTRA_REQUEST_ID);
        contractAmount = args.getDouble(EXTRA_CONTRACT_AMOUNT);

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

        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean(EXTRA_LOADED)) {
                showWebView();
                webView.restoreState(savedInstanceState);
            }
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        processExternalPayment();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putDouble(EXTRA_CONTRACT_AMOUNT, contractAmount);

        boolean loaded = webView.getVisibility() == View.VISIBLE;
        outState.putBoolean(EXTRA_LOADED, loaded);
        if (loaded) {
            webView.saveState(outState);
        }
    }

    @Override
    protected void onExternalPaymentProcessed(ProcessExternalPayment pep) {
        super.onExternalPaymentProcessed(pep);
        if (pep.isExtAuthRequired()) {
            showWebView();
            String url = makeUrl(pep);
            webView.loadUrl(url);
        } else if (pep.isSuccess()) {
            getPaymentActivity().showSuccess(requestId, contractAmount);
        } else {
            getPaymentActivity().showError(pep.getError());
        }
    }

    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
        webView.setVisibility(View.GONE);
    }

    private void showWebView() {
        progressBar.setVisibility(View.GONE);
        webView.setVisibility(View.VISIBLE);
    }

    private void processExternalPayment() {
        reqId = getPaymentActivity().getDataServiceHelper().process(requestId, false);
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
            if (url.contains(DataServiceHelper.SUCCESS_URI)) {
                showProgressBar();
                processExternalPayment();
            } else if (url.contains(DataServiceHelper.FAIL_URI)) {
                getPaymentActivity().showError("authorization_reject");
            }
        }
    }
}
