package ru.yandex.money.android;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.yandex.money.model.common.params.ParamsP2P;
import com.yandex.money.model.common.params.ParamsPhone;
import com.yandex.money.model.cps.ProcessExternalPayment;
import com.yandex.money.model.cps.RequestExternalPayment;
import com.yandex.money.model.cps.misc.MoneySource;

import java.util.List;

import ru.yandex.money.android.database.DatabaseStorage;
import ru.yandex.money.android.fragments.CardsFragment;
import ru.yandex.money.android.fragments.CscFragment;
import ru.yandex.money.android.fragments.ErrorFragment;
import ru.yandex.money.android.fragments.SuccessFragment;
import ru.yandex.money.android.fragments.WebFragment;
import ru.yandex.money.android.parcelables.RequestExternalPaymentParcelable;
import ru.yandex.money.android.services.DataService;
import ru.yandex.money.android.services.DataServiceHelper;

/**
 * @author vyasevich
 */
public class PaymentActivity extends Activity {

    private static final String EXTRA_ARGUMENTS = "ru.yandex.money.android.extra.ARGUMENTS";
    private static final String EXTRA_REQ_ID = "ru.yandex.money.android.extra.REQ_ID";
    private static final String EXTRA_REQUEST_ID = "ru.yandex.money.android.extra.REQUEST_ID";
    private static final String EXTRA_TITLE = "ru.yandex.money.android.extra.TITLE";
    private static final String EXTRA_CONTRACT_AMOUNT = "ru.yandex.money.android.extra.CONTRACT_AMOUNT";
    private static final String EXTRA_SUCCESS = "ru.yandex.money.android.extra.SUCCESS";

    private final MultipleBroadcastReceiver receiver = buildReceiver();

    private PaymentArguments arguments;
    private DataServiceHelper dataServiceHelper;
    private List<MoneySource> cards;

    private String reqId;
    private String title;
    private String requestId;
    private double contractAmount;
    private boolean success = false;

    public static void startActivityForResult(Activity activity, String clientId,
                                              ParamsP2P params, int requestCode) {

        startActivity(activity, new PaymentArguments(clientId, ParamsP2P.PATTERN_ID,
                params.makeParams()), requestCode);
    }

    public static void startActivityForResult(Activity activity, String clientId,
                                              ParamsPhone params, int requestCode) {

        startActivity(activity, new PaymentArguments(clientId, ParamsPhone.PATTERN_ID,
                params.makeParams()), requestCode);
    }

    private static void startActivity(Activity activity, PaymentArguments arguments,
                                      int requestCode) {

        Intent intent = new Intent(activity, PaymentActivity.class);
        intent.putExtra(EXTRA_ARGUMENTS, arguments.toBundle());
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_activity);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        arguments = new PaymentArguments(getIntent().getBundleExtra(EXTRA_ARGUMENTS));
        dataServiceHelper = new DataServiceHelper(this, arguments.getClientId(), null);
        cards = new DatabaseStorage(this).selectMoneySources();

        registerReceiver(receiver, receiver.buildIntentFilter());
        if (savedInstanceState == null) {
            requestExternalPayment();
        } else {
            reqId = savedInstanceState.getString(EXTRA_REQ_ID);
            requestId = savedInstanceState.getString(EXTRA_REQUEST_ID);
            if (reqId == null && requestId == null) {
                requestExternalPayment();
            } else {
                title = savedInstanceState.getString(EXTRA_TITLE);
                contractAmount = savedInstanceState.getDouble(EXTRA_CONTRACT_AMOUNT);
                success = savedInstanceState.getBoolean(EXTRA_SUCCESS);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        applyResult();
        super.onBackPressed();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(EXTRA_REQ_ID, reqId);
        outState.putString(EXTRA_TITLE, title);
        outState.putString(EXTRA_REQUEST_ID, requestId);
        outState.putDouble(EXTRA_CONTRACT_AMOUNT, contractAmount);
        outState.putBoolean(EXTRA_SUCCESS, success);
    }

    public DataServiceHelper getDataServiceHelper() {
        return dataServiceHelper;
    }

    public List<MoneySource> getCards() {
        return cards;
    }

    public void showWeb() {
        replaceFragmentClearBackStack(WebFragment.newInstance(requestId));
    }

    public void showWeb(ProcessExternalPayment pep) {
        replaceFragmentAddingToBackStack(WebFragment.newInstance(requestId, pep));
    }

    public void showCards() {
        replaceFragmentClearBackStack(CardsFragment.newInstance(title, contractAmount));
    }

    public void showError(String error, String status) {
        replaceFragmentClearBackStack(ErrorFragment.newInstance(error, status));
    }

    public void showSuccess() {
        success = true;
        replaceFragmentClearBackStack(SuccessFragment.newInstance(requestId, contractAmount));
    }

    public void showSuccess(MoneySource moneySource) {
        success = true;
        replaceFragmentClearBackStack(SuccessFragment.newInstance(requestId, contractAmount, moneySource));
    }

    public void showCsc(MoneySource moneySource) {
        replaceFragmentAddingToBackStack(CscFragment.newInstance(requestId, moneySource));
    }

    public void requestExternalPayment() {
        reqId = dataServiceHelper.requestShop(arguments.getPatternId(), arguments.getParams());
    }

    private void onExternalPaymentReceived(RequestExternalPayment rep) {
        reqId = null;
        if (rep.isSuccess()) {
            title = rep.getTitle();
            requestId = rep.getRequestId();
            contractAmount = rep.getContractAmount().doubleValue();
            if (cards.size() == 0) {
                replaceFragmentClearBackStack(WebFragment.newInstance(requestId));
            } else {
                showCards();
            }
        } else {
            showError(rep.getError(), rep.getStatus());
        }
    }

    private void replaceFragmentClearBackStack(Fragment fragment) {
        if (fragment == null) {
            return;
        }

        getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    private void replaceFragmentAddingToBackStack(Fragment fragment) {
        if (fragment == null) {
            return;
        }

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(fragment.getTag())
                .commit();
    }

    private void applyResult() {
        setResult(success ? RESULT_OK : RESULT_CANCELED);
    }

    private MultipleBroadcastReceiver buildReceiver() {
        return new MultipleBroadcastReceiver()
                .addHandler(DataService.ACTION_EXCEPTION, new IntentHandler() {
                    @Override
                    public void handle(Intent intent) {
                        if (isManageableIntent(intent)) {
                            String error = intent.getStringExtra(DataService.EXTRA_EXCEPTION_ERROR);
                            String status = intent.getStringExtra(DataService.EXTRA_EXCEPTION_STATUS);
                            showError(error, status);
                        }
                    }
                })
                .addHandler(DataService.ACTION_REQUEST_EXTERNAL_PAYMENT, new IntentHandler() {
                    @Override
                    public void handle(Intent intent) {
                        if (isManageableIntent(intent)) {
                            RequestExternalPaymentParcelable parcelable = intent.getParcelableExtra(
                                    DataService.EXTRA_SUCCESS_PARCELABLE);
                            assert parcelable != null : "request extra is null";
                            onExternalPaymentReceived(parcelable.getRequestExternalPayment());
                        }
                    }
                });
    }

    private boolean isManageableIntent(Intent intent) {
        String requestId = intent.getStringExtra(DataService.EXTRA_REQUEST_ID);
        return requestId != null && requestId.equals(reqId);
    }
}
