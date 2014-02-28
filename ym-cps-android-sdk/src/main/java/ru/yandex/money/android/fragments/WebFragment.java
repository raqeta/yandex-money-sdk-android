package ru.yandex.money.android.fragments;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.yandex.money.model.cps.Error;
import com.yandex.money.model.cps.ProcessExternalPayment;

import java.util.Map;

import ru.yandex.money.android.R;
import ru.yandex.money.android.parcelables.ProcessExternalPaymentParcelable;
import ru.yandex.money.android.services.DataServiceHelper;

/**
 * @author vyasevich
 */
public class WebFragment extends PaymentFragment {

    private static final String EXTRA_PROCESS_EXTERNAL_PAYMENT = "ru.yandex.money.android.extra.PROCESS_EXTERNAL_PAYMENT";

    private ProgressBar progressBar;
    private WebView webView;

    private String requestId;
    private ProcessExternalPayment pep;

    public static WebFragment newInstance(String requestId) {
        return newInstance(requestId, null);
    }

    public static WebFragment newInstance(String requestId, ProcessExternalPayment pep) {

        Bundle args = new Bundle();
        args.putString(EXTRA_REQUEST_ID, requestId);
        if (pep != null) {
            args.putParcelable(EXTRA_PROCESS_EXTERNAL_PAYMENT,
                    new ProcessExternalPaymentParcelable(pep));
        }

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
        ProcessExternalPaymentParcelable parcelable =
                args.getParcelable(EXTRA_PROCESS_EXTERNAL_PAYMENT);
        if (parcelable != null) {
            pep = parcelable.getProcessExternalPayment();
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

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (pep == null) {
            processExternalPayment();
        } else {
            loadPage(pep);
        }
    }

    @Override
    protected void onExternalPaymentProcessed(ProcessExternalPayment pep) {
        super.onExternalPaymentProcessed(pep);
        if (pep.isExtAuthRequired()) {
            loadPage(pep);
        } else if (pep.isSuccess()) {
            getPaymentActivity().showSuccess();
        } else {
            getPaymentActivity().showError(pep.getError(), pep.getStatus());
        }
    }

    private void loadPage(ProcessExternalPayment pep) {
        showWebView();
        webView.loadUrl(makeUrl(pep));
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
                getPaymentActivity().showError(Error.AUTHORIZATION_REJECT, null);
            }
        }

        // TODO remove on production
        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();
        }
    }
}
