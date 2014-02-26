package ru.yandex.money.android;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;

import com.yandex.money.YandexMoney;
import com.yandex.money.model.cps.InstanceId;
import com.yandex.money.model.cps.ProcessExternalPayment;
import com.yandex.money.model.cps.RequestExternalPayment;

import java.io.IOException;
import java.util.Map;

import ru.yandex.money.android.parcelables.ProcessExternalPaymentParcelable;
import ru.yandex.money.android.parcelables.RequestExternalPaymentParcelable;
import ru.yandex.money.android.utils.Bundles;

/**
 * Created by dvmelnikov on 24/02/14.
 */
public class DataService extends IntentService {

    public static final String ACTION_REQUEST_EXTERNAL_PAYMENT = "ru.yandex.money.android.ACTION_REQUEST_EXTERNAL_PAYMENT";
    public static final String ACTION_PROCESS_EXTERNAL_PAYMENT = "ru.yandex.money.android.ACTION_PROCESS_EXTERNAL_PAYMENT";
    public static final String ACTION_EXCEPTION = "ru.yandex.money.android.ACTION_PROCESS_EXTERNAL_PAYMENT";

    public static final String EXTRA_REQUEST_ID = "ru.yandex.money.android.extra.REQUEST_ID";
    public static final String EXTRA_REQUEST_TYPE = "ru.yandex.money.android.extra.REQUEST_TYPE";
    public static final String EXTRA_REQUEST_ACCESS_TOKEN = "ru.yandex.money.android.extra.REQUEST_ACCESS_TOKEN";

    public static final String EXTRA_EXCEPTION_MESSAGE = "ru.yandex.money.android.extra.EXCEPTION_MESSAGE";

    public static final String EXTRA_EXCEPTION_REQUEST_TYPE = "ru.yandex.money.android.extra.EXCEPTION_REQUEST_TYPE";

    public static final String EXTRA_SUCCESS_PARCELABLE = "ru.yandex.money.android.extra.SUCCESS_PARCELABLE";
    public static final String EXTRA_REQUEST_PAYMENT_PARAMS = "ru.yandex.money.android.extra.REQUEST_PAYMENT_PARAMS";
    public static final String EXTRA_REQUEST_PAYMENT_CLIENT_ID = "ru.yandex.money.android.extra.CLIENT_PAYMENT_ID";

    public static final String EXTRA_REQUEST_PAYMENT_PATTERN_ID = "ru.yandex.money.android.extra.PATTERN_PAYMENT_ID";
    public static final String EXTRA_PROCESS_PAYMENT_REQUEST_ID = "ru.yandex.money.android.extra.PROCESS_PAYMENT_REQUEST_ID";
    public static final String EXTRA_PROCESS_PAYMENT_EXT_AUTH_SUCCESS_URI = "ru.yandex.money.android.extra.PROCESS_PAYMENT_EXT_AUTH_SUCCESS_URI";
    public static final String EXTRA_PROCESS_PAYMENT_EXT_AUTH_FAIL_URI = "ru.yandex.money.android.extra.PROCESS_PAYMENT_EXT_AUTH_FAIL_URI";
    public static final String EXTRA_PROCESS_PAYMENT_REQUEST_TOKEN = "ru.yandex.money.android.extra.PROCESS_PAYMENT_REQUEST_TOKEN";
    public static final String EXTRA_PROCESS_PAYMENT_MONEY_SOURCE_TOKEN = "ru.yandex.money.android.extra.PROCESS_PAYMENT_MONEY_SOURCE_TOKEN";

    public static final String EXTRA_PROCESS_PAYMENT_CSC = "ru.yandex.money.android.extra.PROCESS_PAYMENT_CSC";
    public static final int REQUEST_TYPE_INSTANCE_ID = 0;
    public static final int REQUEST_TYPE_REQUEST_EXTERNAL_PAYMENT = 1;

    public static final int REQUEST_TYPE_PROCESS_EXTERNAL_PAYMENT = 2;
    private static final String INSTANCE_ID_ERROR_MESSAGE = "Couldn't perform instanceId request: ";

    private YandexMoney ym;
    private Prefs prefs;

    public DataService() {
        super(DataServiceHelper.class.getSimpleName());
        setupYm();
        prefs = new Prefs(this);
    }

    private void setupYm() {
        ym = new YandexMoney();
        ym.setDebugLogging(true); // set up logging (todo set false for production!)
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String reqId = intent.getStringExtra(EXTRA_REQUEST_ID);
        int type = intent.getIntExtra(EXTRA_REQUEST_TYPE, -1);
        if (!intent.hasExtra(EXTRA_REQUEST_TYPE)) {
            throw new RuntimeException("request must contain 'type' parameter");
        }
        String accessToken = intent.getStringExtra(EXTRA_REQUEST_ACCESS_TOKEN);
        accessToken = TextUtils.isEmpty(accessToken) ? null : accessToken;
        String clientId = intent.getStringExtra(EXTRA_REQUEST_PAYMENT_CLIENT_ID);

        String instanceId = getInstanceIdOrSendFailBroadcast(reqId, clientId, type);
        if (TextUtils.isEmpty(instanceId))
            return;

        if (type == REQUEST_TYPE_REQUEST_EXTERNAL_PAYMENT) {
            RequestExternalPayment.Request req = parserRequestParams(intent, accessToken, instanceId);
            requestPayment(reqId, req);
        }

        if (type == REQUEST_TYPE_PROCESS_EXTERNAL_PAYMENT) {
            ProcessExternalPayment.Request req = parseProcessParams(intent, accessToken, instanceId);
            processPayment(reqId, req);
        }
    }

    private ProcessExternalPayment.Request parseProcessParams(Intent intent, String accessToken, String instanceId) {
        String requestId = intent.getStringExtra(EXTRA_PROCESS_PAYMENT_REQUEST_ID);
        String extAuthSuccessUri = intent.getStringExtra(EXTRA_PROCESS_PAYMENT_EXT_AUTH_SUCCESS_URI);
        String extAuthFailUri = intent.getStringExtra(EXTRA_PROCESS_PAYMENT_EXT_AUTH_FAIL_URI);
        boolean requestToken = intent.getBooleanExtra(EXTRA_PROCESS_PAYMENT_REQUEST_TOKEN, false);
        String moneySourceToken = intent.getStringExtra(EXTRA_PROCESS_PAYMENT_MONEY_SOURCE_TOKEN);
        String csc = intent.getStringExtra(EXTRA_PROCESS_PAYMENT_CSC);

        ProcessExternalPayment.Request req;
        if (TextUtils.isEmpty(moneySourceToken)) {
            req = new ProcessExternalPayment.Request(null, instanceId, requestId,
                    extAuthSuccessUri, extAuthFailUri, requestToken);
        } else {
            req = new ProcessExternalPayment.Request(accessToken, instanceId, requestId,
                    extAuthSuccessUri, extAuthFailUri, moneySourceToken, csc);
        }
        return req;
    }

    private RequestExternalPayment.Request parserRequestParams(Intent intent, String accessToken, String instanceId) {
        String patternId = intent.getStringExtra(EXTRA_REQUEST_PAYMENT_PATTERN_ID);
        Bundle bundle = intent.getParcelableExtra(EXTRA_REQUEST_PAYMENT_PARAMS);
        Map<String, String> params = Bundles.readStringMapFromBundle(bundle);
        return RequestExternalPayment.Request.newInstance(accessToken,
                instanceId, patternId, params);
    }

    private void processPayment(String reqId, ProcessExternalPayment.Request req) {
        try {
            ProcessExternalPayment resp = ym.performRequest(req);
            ProcessExternalPaymentParcelable parc = new ProcessExternalPaymentParcelable(resp);
            sendSuccessBroadcast(ACTION_PROCESS_EXTERNAL_PAYMENT, reqId, parc);
        } catch (IOException e) {
            sendExceptionBroadcast(reqId, REQUEST_TYPE_REQUEST_EXTERNAL_PAYMENT, e.getMessage());
        }

    }

    private void requestPayment(String reqId, RequestExternalPayment.Request req) {
        try {
            RequestExternalPayment resp = ym.performRequest(req);
            RequestExternalPaymentParcelable parc = new RequestExternalPaymentParcelable(resp);
            sendSuccessBroadcast(ACTION_REQUEST_EXTERNAL_PAYMENT, reqId, parc);
        } catch (IOException e) {
            sendExceptionBroadcast(reqId, REQUEST_TYPE_REQUEST_EXTERNAL_PAYMENT, e.getMessage());
        }
    }

    private void sendExceptionBroadcast(String requestId, int requestType, String errorMessage) {
        Intent intent = new Intent(ACTION_EXCEPTION);
        intent.setPackage(getPackageName());
        intent.putExtra(EXTRA_EXCEPTION_MESSAGE, errorMessage);
        intent.putExtra(EXTRA_EXCEPTION_REQUEST_TYPE, requestType);
        intent.putExtra(EXTRA_REQUEST_ID, requestId);
        sendBroadcast(intent);
    }

    private void sendSuccessBroadcast(String action, String requestId, Parcelable parcelable) {
        Intent intent = new Intent(action);
        intent.setPackage(getPackageName());
        intent.putExtra(EXTRA_REQUEST_ID, requestId);
        intent.putExtra(EXTRA_SUCCESS_PARCELABLE, parcelable);
        sendBroadcast(intent);
    }

    private String getInstanceIdOrSendFailBroadcast(String reqId, String clientId, int requestType) {
        String instanceId = prefs.restoreInstanceId();
        if (TextUtils.isEmpty(instanceId)) {
            return receiveInstanceId(reqId, clientId, requestType);
        } else {
            return instanceId;
        }
    }

    private String receiveInstanceId(String reqId, String clientId, int requestType) {
        try {
            InstanceId resp = ym.performRequest(new InstanceId.Request(clientId));
            if (resp.isSuccess()) {
                String instanceId = resp.getInstanceId();
                prefs.storeInstanceId(instanceId);
                return instanceId;
            } else {
                String message = INSTANCE_ID_ERROR_MESSAGE + resp.getError();
                sendExceptionBroadcast(reqId, requestType, message);
                return null;
            }
        } catch (IOException e) {
            String message = INSTANCE_ID_ERROR_MESSAGE + e.getMessage();
            sendExceptionBroadcast(reqId, requestType, message);
            return null;
        }
    }
}
