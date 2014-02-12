package ru.yandex.money.android;

import android.text.TextUtils;

import com.yandex.money.ParamsP2P;
import com.yandex.money.YandexMoney;
import com.yandex.money.model.RequestExternalPayment;
import com.yandex.money.net.IRequest;

/**
 * Created by dvmelnikov on 12/02/14.
 */
public class YandexMoneyDroid {

    private String clientId;
    private String accessToken;

    private YandexMoney ym;

    public YandexMoneyDroid(String clientId, String accessToken) {
        this.clientId = clientId;
        this.accessToken = accessToken;

        ym = new YandexMoney();
        ym.setDebugLogging(true);
    }

    public YandexMoneyDroid(String clientId) {
        this(clientId, null);
    }

    public void requestP2P(ParamsP2P params) {
        RequestExternalPayment.Request req = RequestExternalPayment.Request.newInstance(accessToken, instanceId, params);

        request(req);
    }

    private <T> void request(IRequest<T> request) {

    }
}
