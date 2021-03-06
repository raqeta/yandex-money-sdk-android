package ru.yandex.money.android.services;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.yandex.money.api.model.ExternalCard;

import java.util.Map;
import java.util.UUID;

import ru.yandex.money.android.parcelables.ExtendedCardParcelable;
import ru.yandex.money.android.utils.Bundles;

/**
 * Created by dvmelnikov on 12/02/14.
 */
public class DataServiceHelper {

    public final static String SUCCESS_URI = "ym-cps-android-sdk://ext_auth_success";
    public final static String FAIL_URI = "ym-cps-android-sdk://ext_auth_fail";

    private Context context;
    private String clientId;

    public DataServiceHelper(Context context, String clientId) {
        this.context = context;
        this.clientId = clientId;
    }

    public String requestShop(String patternId, Map<String, String> params) {
        return requestExternalPayment(patternId, params);
    }

    public String process(String requestId, boolean requestToken) {
        return processExternalPayment(requestId, requestToken, null, null);
    }

    public String process(String requestId, ExternalCard moneySource, String csc) {
        return processExternalPayment(requestId, false, moneySource, csc);
    }

    private String processExternalPayment(String requestId, boolean requestToken,
                                          ExternalCard moneySource, String csc) {

        String reqId = genRequestId();
        Intent intent = makeIntent(context, reqId, DataService.REQUEST_TYPE_PROCESS_EXTERNAL_PAYMENT);

        intent.putExtra(DataService.EXTRA_PROCESS_PAYMENT_REQUEST_ID, requestId);
        intent.putExtra(DataService.EXTRA_PROCESS_PAYMENT_EXT_AUTH_SUCCESS_URI, SUCCESS_URI);
        intent.putExtra(DataService.EXTRA_PROCESS_PAYMENT_EXT_AUTH_FAIL_URI, FAIL_URI);
        intent.putExtra(DataService.EXTRA_PROCESS_PAYMENT_REQUEST_TOKEN, requestToken);
        if (moneySource != null) {
            intent.putExtra(DataService.EXTRA_PROCESS_PAYMENT_MONEY_SOURCE,
                    new ExtendedCardParcelable(moneySource));
        }
        intent.putExtra(DataService.EXTRA_PROCESS_PAYMENT_CSC, csc);

        context.startService(intent);
        return reqId;
    }

    private String requestExternalPayment(String patternId, Map<String, String> params) {
        String requestId = genRequestId();
        Intent intent = makeIntent(context, requestId, DataService.REQUEST_TYPE_REQUEST_EXTERNAL_PAYMENT);

        Bundle bundleParams = new Bundle();
        Bundles.writeStringMapToBundle(bundleParams, params);

        intent.putExtra(DataService.EXTRA_REQUEST_PAYMENT_CLIENT_ID, clientId);
        intent.putExtra(DataService.EXTRA_REQUEST_PAYMENT_PARAMS, bundleParams);
        intent.putExtra(DataService.EXTRA_REQUEST_PAYMENT_PATTERN_ID, patternId);

        context.startService(intent);
        return requestId;
    }

    private Intent makeIntent(Context context, String requestId, int requestType) {
        Intent intent = new Intent(context, DataService.class);
        intent.putExtra(DataService.EXTRA_REQUEST_ID, requestId);
        intent.putExtra(DataService.EXTRA_REQUEST_TYPE, requestType);
        return intent;
    }

    private String genRequestId() {
        return UUID.randomUUID().toString();
    }
}
