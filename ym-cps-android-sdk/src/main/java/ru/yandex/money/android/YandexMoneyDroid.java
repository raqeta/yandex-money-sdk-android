package ru.yandex.money.android;

import android.text.TextUtils;

import com.yandex.money.YandexMoney;
import com.yandex.money.model.common.params.ParamsP2P;
import com.yandex.money.model.cps.InstanceId;
import com.yandex.money.model.cps.ProcessExternalPayment;
import com.yandex.money.model.cps.RequestExternalPayment;
import com.yandex.money.net.IRequest;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by dvmelnikov on 12/02/14.
 */
public class YandexMoneyDroid {

    public final static String SUCCESS_URI = "ym-cps-android-sdk://ext_auth_success";
    public final static String FAIL_URI = "ym-cps-android-sdk://ext_auth_fail";

    private String clientId;
    private Prefs prefs;
    private String accessToken;

    private YandexMoney ym;
    private ExecutorService executor;

    public YandexMoneyDroid(String clientId, Prefs prefs, String accessToken) {
        this.clientId = clientId;
        this.prefs = prefs;
        this.accessToken = accessToken;

        executor = Executors.newSingleThreadExecutor();
        setupYm();
    }

    public YandexMoneyDroid(String clientId, Prefs prefs) {
        this(clientId, prefs, null);
    }

    private void setupYm() {
        ym = new YandexMoney();
        ym.setDebugLogging(true); // set up logging (todo set false for production!)
    }

    public <T> Future<T> execute(final IRequest<T> request) {
        return executor.submit(new Callable<T>() {
            @Override
            public T call() throws Exception {
                return ym.performRequest(request);
            }
        });
    }

    public RequestExternalPayment requestP2P(ParamsP2P params) throws IOException {
        String instanceId = getInstanceId();
        RequestExternalPayment.Request req =
                RequestExternalPayment.Request.newInstance(accessToken, instanceId, params);
        return request(req);
    }

    private String getInstanceId() throws IOException {
        String instanceId = prefs.restoreInstanceId();
        if (TextUtils.isEmpty(instanceId)) {
            Future<InstanceId> futureInstanceId = execute(new InstanceId.Request(clientId));
            try {
                InstanceId resp = futureInstanceId.get();
                if (resp.isSuccess()) {
                    instanceId = resp.getInstanceId();
                    prefs.storeInstanceId(instanceId);
                    return instanceId;
                } else {
                    throw new IOException(resp.getError());
                }
            } catch (InterruptedException e) {
                throw new IllegalStateException("InstanceId request interrupted");
            } catch (ExecutionException e) {
                throw new IOException(e.getMessage());
            }
        } else {
            return instanceId;
        }
    }

    public RequestExternalPayment requestShop(String patternId, Map<String, String> params) throws IOException {
        String instanceId = getInstanceId();
        RequestExternalPayment.Request req =
                RequestExternalPayment.Request.newInstance(accessToken, instanceId, patternId, params);
        return request(req);

    }

    private RequestExternalPayment request(RequestExternalPayment.Request req) throws IOException {
        Future<RequestExternalPayment> futureResp = execute(req);
        try {
            return futureResp.get();
        } catch (InterruptedException e) {
            throw new IllegalStateException("RequestExternalPayment request interrupted");
        } catch (ExecutionException e) {
            throw new IOException("RequestExternalPayment failed, see cause");
        }
    }

    public ProcessExternalPayment process(String requestId, boolean requestToken) throws IOException {
        String instanceId = getInstanceId();
        ProcessExternalPayment.Request req = new ProcessExternalPayment.Request(accessToken, instanceId, requestId, SUCCESS_URI, FAIL_URI, requestToken);
        Future<ProcessExternalPayment> futureResp = execute(req);
        try {
            return futureResp.get();
        } catch (InterruptedException e) {
            throw new IllegalStateException("ProcessExternalPayment request interrupted");
        } catch (ExecutionException e) {
            throw new IOException("ProcessExternalPayment failed, see cause");
        }
    }
}
