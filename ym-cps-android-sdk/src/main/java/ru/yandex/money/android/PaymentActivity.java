package ru.yandex.money.android;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;

import com.yandex.money.model.Error;
import com.yandex.money.model.common.params.P2pParams;
import com.yandex.money.model.common.params.PhoneParams;
import com.yandex.money.model.methods.BaseProcessPayment;
import com.yandex.money.model.methods.BaseRequestPayment;
import com.yandex.money.model.methods.ProcessExternalPayment;
import com.yandex.money.model.methods.RequestExternalPayment;
import com.yandex.money.model.methods.misc.MoneySourceExternal;

import java.util.List;
import java.util.Map;

import ru.yandex.money.android.database.DatabaseStorage;
import ru.yandex.money.android.fragments.CardsFragment;
import ru.yandex.money.android.fragments.CscFragment;
import ru.yandex.money.android.fragments.ErrorFragment;
import ru.yandex.money.android.fragments.SuccessFragment;
import ru.yandex.money.android.fragments.WebFragment;
import ru.yandex.money.android.parcelables.ProcessExternalPaymentParcelable;
import ru.yandex.money.android.parcelables.RequestExternalPaymentParcelable;
import ru.yandex.money.android.services.DataService;
import ru.yandex.money.android.services.DataServiceHelper;
import ru.yandex.money.android.utils.Keyboards;

/**
 * @author vyasevich
 */
public class PaymentActivity extends Activity {

    public static final String EXTRA_INVOICE_ID = "ru.yandex.money.android.extra.INVOICE_ID";

    private static final String EXTRA_ARGUMENTS = "ru.yandex.money.android.extra.ARGUMENTS";
    private static final String EXTRA_REQ_ID = "ru.yandex.money.android.extra.REQ_ID";
    private static final String EXTRA_REQUEST_ID = "ru.yandex.money.android.extra.REQUEST_ID";
    private static final String EXTRA_TITLE = "ru.yandex.money.android.extra.TITLE";
    private static final String EXTRA_CONTRACT_AMOUNT = "ru.yandex.money.android.extra.CONTRACT_AMOUNT";
    private static final String EXTRA_SUCCESS = "ru.yandex.money.android.extra.SUCCESS";

    private final MultipleBroadcastReceiver receiver = buildReceiver();

    private PaymentArguments arguments;
    private DataServiceHelper dataServiceHelper;
    private List<MoneySourceExternal> cards;

    private String reqId;
    private String title;
    private String requestId;
    private String invoiceId;
    private double contractAmount;
    private boolean success = false;

    public static void startActivityForResult(Activity activity, String clientId,
                                              P2pParams params, int requestCode) {

        startActivityForResult(activity, new PaymentArguments(clientId, P2pParams.PATTERN_ID,
                params.makeParams()), requestCode);
    }

    public static void startActivityForResult(Activity activity, String clientId,
                                              PhoneParams params, int requestCode) {

        startActivityForResult(activity, new PaymentArguments(clientId, PhoneParams.PATTERN_ID,
                params.makeParams()), requestCode);
    }

    public static void startActivityForResult(Activity activity, String clientId, String patternId,
                                              Map<String, String> params, int requestCode) {

        startActivityForResult(activity, new PaymentArguments(clientId, patternId, params),
                requestCode);
    }

    private static void startActivityForResult(Activity activity, PaymentArguments arguments,
                                               int requestCode) {

        Intent intent = new Intent(activity, PaymentActivity.class);
        intent.putExtra(EXTRA_ARGUMENTS, arguments.toBundle());
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.ym_payment_activity);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        arguments = new PaymentArguments(getIntent().getBundleExtra(EXTRA_ARGUMENTS));
        dataServiceHelper = new DataServiceHelper(this, arguments.getClientId());
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
                invoiceId = savedInstanceState.getString(EXTRA_INVOICE_ID);
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
        hideProgressBar();
        super.onBackPressed();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(EXTRA_REQ_ID, reqId);
        outState.putString(EXTRA_TITLE, title);
        outState.putString(EXTRA_REQUEST_ID, requestId);
        outState.putString(EXTRA_INVOICE_ID, invoiceId);
        outState.putDouble(EXTRA_CONTRACT_AMOUNT, contractAmount);
        outState.putBoolean(EXTRA_SUCCESS, success);
    }

    public DataServiceHelper getDataServiceHelper() {
        return dataServiceHelper;
    }

    public List<MoneySourceExternal> getCards() {
        return cards;
    }

    public void showWeb() {
        replaceFragmentClearBackStack(WebFragment.newInstance(requestId));
    }

    public void showWeb(ProcessExternalPayment pep, MoneySourceExternal moneySource) {
        replaceFragmentAddingToBackStack(WebFragment.newInstance(requestId, pep, moneySource));
    }

    public void showCards() {
        replaceFragmentClearBackStack(CardsFragment.newInstance(title, contractAmount));
    }

    public void showError(Error error, String status) {
        replaceFragmentClearBackStack(ErrorFragment.newInstance(error, status));
    }

    public void showSuccess(MoneySourceExternal moneySource) {
        replaceFragmentClearBackStack(SuccessFragment.newInstance(requestId, contractAmount, moneySource));
    }

    public void showCsc(MoneySourceExternal moneySource) {
        replaceFragmentAddingToBackStack(CscFragment.newInstance(requestId, moneySource));
    }

    public void showProgressBar() {
        setProgressBarIndeterminateVisibility(true);
    }

    public void hideProgressBar() {
        setProgressBarIndeterminateVisibility(false);
    }

    public void requestExternalPayment() {
        reqId = dataServiceHelper.requestShop(arguments.getPatternId(), arguments.getParams());
        showProgressBar();
    }

    private void onExternalPaymentReceived(RequestExternalPayment rep) {
        reqId = null;
        if (rep.getStatus() == BaseRequestPayment.Status.SUCCESS) {
            title = rep.getTitle();
            requestId = rep.getRequestId();
            contractAmount = rep.getContractAmount().doubleValue();
            if (cards.size() == 0) {
                replaceFragmentClearBackStack(WebFragment.newInstance(requestId));
            } else {
                showCards();
            }
        } else {
            showError(rep.getError(), rep.getStatus().toString());
        }
        hideProgressBar();
    }

    private void onExternalPaymentProcessed(ProcessExternalPayment pep) {
        if (pep.getStatus() == BaseProcessPayment.Status.SUCCESS) {
            success = true;
            invoiceId = pep.getInvoiceId();
        }
    }

    private void replaceFragmentClearBackStack(Fragment fragment) {
        if (fragment == null) {
            return;
        }

        hideProgressBar();
        getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.ym_container, fragment)
                .commit();
        hideKeyboard();
    }

    private void replaceFragmentAddingToBackStack(Fragment fragment) {
        if (fragment == null) {
            return;
        }

        hideProgressBar();
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.ym_container, fragment)
                .addToBackStack(fragment.getTag())
                .commit();
        hideKeyboard();
    }

    private void hideKeyboard() {
        Keyboards.hideKeyboard(this);
    }

    private void applyResult() {
        if (success) {
            Intent intent = new Intent();
            intent.putExtra(EXTRA_INVOICE_ID, invoiceId);
            setResult(RESULT_OK, intent);
        } else {
            setResult(RESULT_CANCELED);
        }
    }

    private MultipleBroadcastReceiver buildReceiver() {
        return new MultipleBroadcastReceiver()
                .addHandler(DataService.ACTION_EXCEPTION, new IntentHandler() {
                    @Override
                    public void handle(Intent intent) {
                        if (isManageableIntent(intent)) {
                            Error error = (Error) intent.getSerializableExtra(DataService.EXTRA_EXCEPTION_ERROR);
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
                            if (parcelable != null) {
                                onExternalPaymentReceived(parcelable.getRequestExternalPayment());
                            }
                        }
                    }
                })
                .addHandler(DataService.ACTION_PROCESS_EXTERNAL_PAYMENT, new IntentHandler() {
                    @Override
                    public void handle(Intent intent) {
                        ProcessExternalPaymentParcelable parcelable = intent.getParcelableExtra(
                                DataService.EXTRA_SUCCESS_PARCELABLE);
                        if (parcelable != null) {
                            onExternalPaymentProcessed(parcelable.getProcessExternalPayment());
                        }
                    }
                });
    }

    private boolean isManageableIntent(Intent intent) {
        String requestId = intent.getStringExtra(DataService.EXTRA_REQUEST_ID);
        return requestId != null && requestId.equals(reqId);
    }
}
