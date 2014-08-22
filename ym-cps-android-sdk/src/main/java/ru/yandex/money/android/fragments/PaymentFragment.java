package ru.yandex.money.android.fragments;

import android.app.Fragment;
import android.content.Intent;

import com.yandex.money.api.methods.ProcessExternalPayment;
import com.yandex.money.api.model.Error;
import com.yandex.money.api.model.ExternalCard;

import ru.yandex.money.android.IntentHandler;
import ru.yandex.money.android.MultipleBroadcastReceiver;
import ru.yandex.money.android.PaymentActivity;
import ru.yandex.money.android.parcelables.ProcessExternalPaymentParcelable;
import ru.yandex.money.android.services.DataService;

/**
 * @author vyasevich
 */
public abstract class PaymentFragment extends Fragment {

    protected static final String EXTRA_REQUEST_ID = "ru.yandex.money.android.extra.REQUEST_ID";
    protected static final String EXTRA_MONEY_SOURCE = "ru.yandex.money.android.extra.MONEY_SOURCE";

    protected String reqId;

    private final MultipleBroadcastReceiver receiver = buildReceiver();

    @Override
    public void onResume() {
        super.onResume();
        getPaymentActivity().registerReceiver(receiver, receiver.buildIntentFilter());
    }

    @Override
    public void onPause() {
        super.onPause();
        getPaymentActivity().unregisterReceiver(receiver);
    }

    protected PaymentActivity getPaymentActivity() {
        return (PaymentActivity) getActivity();
    }

    protected void showWeb() {
        startActionSafely(new Action() {
            @Override
            public void start(PaymentActivity activity) {
                activity.showWeb();
            }
        });
    }

    protected void showWeb(final ProcessExternalPayment pep, final ExternalCard moneySource) {
        startActionSafely(new Action() {
            @Override
            public void start(PaymentActivity activity) {
                activity.showWeb(pep, moneySource);
            }
        });
    }

    protected void showCards() {
        startActionSafely(new Action() {
            @Override
            public void start(PaymentActivity activity) {
                activity.showCards();
            }
        });
    }

    protected void showError(final Error error, final String status) {
        startActionSafely(new Action() {
            @Override
            public void start(PaymentActivity activity) {
                activity.showError(error, status);
            }
        });
    }

    protected void showCsc(final ExternalCard moneySource) {
        startActionSafely(new Action() {
            @Override
            public void start(PaymentActivity activity) {
                activity.showCsc(moneySource);
            }
        });
    }

    protected void showSuccess(final ExternalCard moneySource) {
        startActionSafely(new Action() {
            @Override
            public void start(PaymentActivity activity) {
                activity.showSuccess(moneySource);
            }
        });
    }

    protected void showProgressBar() {
        startActionSafely(new Action() {
            @Override
            public void start(PaymentActivity activity) {
                activity.showProgressBar();
            }
        });
    }

    protected void hideProgressBar() {
        startActionSafely(new Action() {
            @Override
            public void start(PaymentActivity activity) {
                activity.hideProgressBar();
            }
        });
    }

    protected boolean isManageableIntent(Intent intent) {
        String requestId = intent.getStringExtra(DataService.EXTRA_REQUEST_ID);
        return requestId != null && requestId.equals(reqId);
    }

    protected void onExternalPaymentProcessed(ProcessExternalPayment pep) {
        reqId = null;
    }

    protected void startActionSafely(Action action) {
        PaymentActivity activity = getPaymentActivity();
        if (activity != null) {
            action.start(activity);
        }
    }

    private MultipleBroadcastReceiver buildReceiver() {
        return new MultipleBroadcastReceiver()
                .addHandler(DataService.ACTION_PROCESS_EXTERNAL_PAYMENT, new IntentHandler() {
                    @Override
                    public void handle(Intent intent) {
                        if (isManageableIntent(intent)) {
                            ProcessExternalPaymentParcelable parcelable = intent.getParcelableExtra(
                                    DataService.EXTRA_SUCCESS_PARCELABLE);
                            if (parcelable != null) {
                                onExternalPaymentProcessed(parcelable.getProcessExternalPayment());
                            }
                        }
                    }
                });
    }

    public interface Action {
        public void start(PaymentActivity activity);
    }
}
