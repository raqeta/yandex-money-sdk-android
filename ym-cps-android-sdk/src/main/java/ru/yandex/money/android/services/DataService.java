package ru.yandex.money.android.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;

import com.yandex.money.api.YandexMoney;
import com.yandex.money.api.methods.BaseProcessPayment;
import com.yandex.money.api.methods.InstanceId;
import com.yandex.money.api.methods.ProcessExternalPayment;
import com.yandex.money.api.methods.RequestExternalPayment;
import com.yandex.money.api.model.Error;
import com.yandex.money.api.net.DefaultApiClient;

import java.util.Map;

import ru.yandex.money.android.Prefs;
import ru.yandex.money.android.parcelables.ExtendedCardParcelable;
import ru.yandex.money.android.parcelables.ProcessExternalPaymentParcelable;
import ru.yandex.money.android.parcelables.RequestExternalPaymentParcelable;
import ru.yandex.money.android.utils.Bundles;
import ru.yandex.money.android.utils.Threads;

/**
 * @author dvmelnikov
 */
public class DataService extends IntentService {

    public static final String ACTION_REQUEST_EXTERNAL_PAYMENT = "ru.yandex.money.android.action.REQUEST_EXTERNAL_PAYMENT";
    public static final String ACTION_PROCESS_EXTERNAL_PAYMENT = "ru.yandex.money.android.action.PROCESS_EXTERNAL_PAYMENT";
    public static final String ACTION_EXCEPTION = "ru.yandex.money.android.action.EXCEPTION";

    public static final String EXTRA_REQUEST_ID = "ru.yandex.money.android.extra.REQUEST_ID";
    static final String EXTRA_REQUEST_TYPE = "ru.yandex.money.android.extra.REQUEST_TYPE";

    public static final String EXTRA_EXCEPTION_ERROR = "ru.yandex.money.android.extra.EXCEPTION_ERROR";
    public static final String EXTRA_EXCEPTION_STATUS = "ru.yandex.money.android.extra.EXCEPTION_STATUS";

    static final String EXTRA_EXCEPTION_REQUEST_TYPE = "ru.yandex.money.android.extra.EXCEPTION_REQUEST_TYPE";

    public static final String EXTRA_SUCCESS_PARCELABLE = "ru.yandex.money.android.extra.SUCCESS_PARCELABLE";
    static final String EXTRA_REQUEST_PAYMENT_PARAMS = "ru.yandex.money.android.extra.REQUEST_PAYMENT_PARAMS";
    static final String EXTRA_REQUEST_PAYMENT_CLIENT_ID = "ru.yandex.money.android.extra.CLIENT_PAYMENT_ID";

    static final String EXTRA_REQUEST_PAYMENT_PATTERN_ID = "ru.yandex.money.android.extra.PATTERN_PAYMENT_ID";
    static final String EXTRA_PROCESS_PAYMENT_REQUEST_ID = "ru.yandex.money.android.extra.PROCESS_PAYMENT_REQUEST_ID";
    static final String EXTRA_PROCESS_PAYMENT_EXT_AUTH_SUCCESS_URI = "ru.yandex.money.android.extra.PROCESS_PAYMENT_EXT_AUTH_SUCCESS_URI";
    static final String EXTRA_PROCESS_PAYMENT_EXT_AUTH_FAIL_URI = "ru.yandex.money.android.extra.PROCESS_PAYMENT_EXT_AUTH_FAIL_URI";
    static final String EXTRA_PROCESS_PAYMENT_REQUEST_TOKEN = "ru.yandex.money.android.extra.PROCESS_PAYMENT_REQUEST_TOKEN";
    static final String EXTRA_PROCESS_PAYMENT_MONEY_SOURCE = "ru.yandex.money.android.extra.PROCESS_PAYMENT_MONEY_SOURCE";
    static final String EXTRA_PROCESS_PAYMENT_CSC = "ru.yandex.money.android.extra.PROCESS_PAYMENT_CSC";

    static final int REQUEST_TYPE_REQUEST_EXTERNAL_PAYMENT = 1;
    static final int REQUEST_TYPE_PROCESS_EXTERNAL_PAYMENT = 2;

    private static final String INSTANCE_ID_ERROR_MESSAGE = "Couldn't perform instanceId request: ";

    private YandexMoney ym;

    public DataService() {
        super(DataServiceHelper.class.getSimpleName());
        setupYm();
    }

    private void setupYm() {
        // we use stub instead of actual client id because we pass it as an extra when service is
        // started with a new intent
        ym = new YandexMoney(new DefaultApiClient("stub", false, "Android"));
        ym.setDebugLogging(true);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String reqId = intent.getStringExtra(EXTRA_REQUEST_ID);
        int type = intent.getIntExtra(EXTRA_REQUEST_TYPE, -1);
        String clientId = intent.getStringExtra(EXTRA_REQUEST_PAYMENT_CLIENT_ID);

        String instanceId = getInstanceIdOrSendFailBroadcast(reqId, clientId, type);
        if (TextUtils.isEmpty(instanceId))
            return;

        if (type == REQUEST_TYPE_REQUEST_EXTERNAL_PAYMENT) {
            RequestExternalPayment.Request req = parserRequestParams(intent, instanceId);
            requestPayment(reqId, req);
        } else if (type == REQUEST_TYPE_PROCESS_EXTERNAL_PAYMENT) {
            ProcessExternalPayment.Request req = parseProcessParams(intent, instanceId);
            processPayment(reqId, req);
        } else {
            throw new IllegalArgumentException("requestType parameter has invalid value");
        }
    }

    private ProcessExternalPayment.Request parseProcessParams(Intent intent, String instanceId) {
        String requestId = intent.getStringExtra(EXTRA_PROCESS_PAYMENT_REQUEST_ID);
        String extAuthSuccessUri = intent.getStringExtra(EXTRA_PROCESS_PAYMENT_EXT_AUTH_SUCCESS_URI);
        String extAuthFailUri = intent.getStringExtra(EXTRA_PROCESS_PAYMENT_EXT_AUTH_FAIL_URI);
        boolean requestToken = intent.getBooleanExtra(EXTRA_PROCESS_PAYMENT_REQUEST_TOKEN, false);
        ExtendedCardParcelable parcelable = intent.getParcelableExtra(EXTRA_PROCESS_PAYMENT_MONEY_SOURCE);
        String csc = intent.getStringExtra(EXTRA_PROCESS_PAYMENT_CSC);

        ProcessExternalPayment.Request req;
        if (parcelable == null) {
            req = new ProcessExternalPayment.Request(instanceId, requestId, extAuthSuccessUri,
                    extAuthFailUri, requestToken);
        } else {
            req = new ProcessExternalPayment.Request(instanceId, requestId, extAuthSuccessUri,
                    extAuthFailUri, parcelable.getExtendedCard(), csc);
        }
        return req;
    }

    private RequestExternalPayment.Request parserRequestParams(Intent intent, String instanceId) {
        String patternId = intent.getStringExtra(EXTRA_REQUEST_PAYMENT_PATTERN_ID);
        Bundle bundle = intent.getParcelableExtra(EXTRA_REQUEST_PAYMENT_PARAMS);
        Map<String, String> params = Bundles.readStringMapFromBundle(bundle);
        return RequestExternalPayment.Request.newInstance(instanceId, patternId, params);
    }

    private void processPayment(String reqId, ProcessExternalPayment.Request req) {
        try {
            ProcessExternalPayment resp = ym.execute(req);
            if (resp.getStatus() == BaseProcessPayment.Status.IN_PROGRESS) {
                Threads.sleepSafely(resp.getNextRetry());
                processPayment(reqId, req);
            } else {
                ProcessExternalPaymentParcelable parc = new ProcessExternalPaymentParcelable(resp);
                sendSuccessBroadcast(ACTION_PROCESS_EXTERNAL_PAYMENT, reqId, parc);
            }
        } catch (Exception e) {
            sendExceptionBroadcast(reqId, REQUEST_TYPE_PROCESS_EXTERNAL_PAYMENT, Error.UNKNOWN, null);
        }
    }

    private void requestPayment(String reqId, RequestExternalPayment.Request req) {
        try {
            RequestExternalPayment resp = ym.execute(req);
            RequestExternalPaymentParcelable parc = new RequestExternalPaymentParcelable(resp);
            sendSuccessBroadcast(ACTION_REQUEST_EXTERNAL_PAYMENT, reqId, parc);
        } catch (Exception e) {
            sendExceptionBroadcast(reqId, REQUEST_TYPE_REQUEST_EXTERNAL_PAYMENT, Error.UNKNOWN, null);
        }
    }

    private void sendExceptionBroadcast(String requestId, int requestType, Error error, String status) {
        Intent intent = new Intent(ACTION_EXCEPTION);
        intent.setPackage(getPackageName());
        intent.putExtra(EXTRA_EXCEPTION_ERROR, error);
        intent.putExtra(EXTRA_EXCEPTION_STATUS, status);
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
        String instanceId = new Prefs(getApplicationContext()).restoreInstanceId();
        if (TextUtils.isEmpty(instanceId)) {
            return receiveInstanceId(reqId, clientId, requestType);
        } else {
            return instanceId;
        }
    }

    private String receiveInstanceId(String reqId, String clientId, int requestType) {
        try {
            InstanceId resp = ym.execute(new InstanceId.Request(clientId));
            if (resp.isSuccess()) {
                String instanceId = resp.getInstanceId();
                new Prefs(getApplicationContext()).storeInstanceId(instanceId);
                return instanceId;
            } else {
                sendExceptionBroadcast(reqId, requestType, resp.getError(),
                        resp.getStatus().toString());
                return null;
            }
        } catch (Exception e) {
            String message = INSTANCE_ID_ERROR_MESSAGE + e.getMessage();
            sendExceptionBroadcast(reqId, requestType, Error.UNKNOWN, null);
            return null;
        }
    }
}
