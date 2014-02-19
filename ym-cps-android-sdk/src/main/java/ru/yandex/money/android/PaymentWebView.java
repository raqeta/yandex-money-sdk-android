package ru.yandex.money.android;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

/**
 * Created by dvmelnikov on 17/02/14.
 */
public class PaymentWebView extends WebView {

    public static final String TAG = PaymentWebViewClient.class.getName();

    public PaymentWebView(Context context) {
        super(context);
        setup();
    }

    public PaymentWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup();
    }

    private void setup() {
//        getSettings().setJavaScriptEnabled(true);
        setWebViewClient(new PaymentWebViewClient());
    }        

    private class PaymentWebViewClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            if (url.contains(YandexMoneyDroid.SUCCESS_URI)) {
                Toast.makeText(getContext(), "success", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            Log.d(TAG, "");
            Toast.makeText(getContext(), "error " + errorCode + ": description", Toast.LENGTH_LONG).show();
        }
    }
}
