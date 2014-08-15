package ru.yandex.money.android.sample;

import android.app.ActionBar;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yandex.money.api.methods.params.P2pParams;
import com.yandex.money.api.methods.params.PhoneParams;

import java.math.BigDecimal;
import java.util.List;

import ru.yandex.money.android.PaymentActivity;
import ru.yandex.money.android.sample.storage.DatabaseHelper;
import ru.yandex.money.android.utils.Views;

/**
 * @author vyasevich
 */
public class PayActivity extends ListActivity {

    private static final String CLIENT_ID = "your_client_id";

    private static final int REQUEST_CODE = 101;

    private static final String EXTRA_PAYMENT = "ru.yandex.money.android.sample.extra.PAYMENT";

    private Payment payment;
    private DatabaseHelper helper;

    private EditText paymentTo;
    private EditText amount;
    private TextView previous;

    public static void startP2P(Context context) {
        startActivity(context, Payment.P2P);
    }

    public static void startPhone(Context context) {
        startActivity(context, Payment.PHONE);
    }

    private static void startActivity(Context context, Payment payment) {
        Intent intent = new Intent(context, PayActivity.class);
        intent.putExtra(EXTRA_PAYMENT, payment);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        payment = (Payment) getIntent().getSerializableExtra(EXTRA_PAYMENT);
        helper = DatabaseHelper.getInstance(this);

        init();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK &&
                !loadValues().contains(getPaymentTo())) {
            switch (payment) {
                case P2P:
                    helper.saveAccountNumber(getPaymentTo());
                    break;
                case PHONE:
                    helper.savePhoneNumber(getPaymentTo());
                    break;
            }
            updatePrevious();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_pay, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.pay:
                pay();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        paymentTo.setText((String) l.getItemAtPosition(position));
    }

    private void init() {
        paymentTo = (EditText) findViewById(R.id.payment_to);
        switch (payment) {
            case P2P:
                paymentTo.setHint(R.string.activity_pay_account_hint);
                break;
            case PHONE:
                paymentTo.setHint(R.string.activity_pay_phone_hint);
                break;
        }

        amount = (EditText) findViewById(R.id.amount);

        previous = (TextView) findViewById(R.id.previous);
        updatePrevious();
    }

    private void updatePrevious() {
        List<String> values = prepareValues(loadValues());
        if (values.isEmpty()) {
            setListAdapter(null);
            getListView().setVisibility(View.GONE);
            previous.setVisibility(View.GONE);
        } else {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, values);
            setListAdapter(adapter);
            getListView().setVisibility(View.VISIBLE);
            previous.setVisibility(View.VISIBLE);
        }
    }

    private List<String> prepareValues(List<String> values) {
        if (payment == Payment.PHONE) {
            for (int i = 0; i < values.size(); ++i) {
                String number = PhoneNumberUtils.formatNumber("+" + values.get(i));
                values.remove(i);
                values.add(i, number);
            }
        }
        return values;
    }

    private List<String> loadValues() {
        switch (payment) {
            case P2P:
                return helper.getAccountNumbers();
            case PHONE:
                return helper.getPhoneNumber();
            default:
                throw new IllegalArgumentException();
        }
    }

    private void pay() {
        if (isValid()) {
            switch (payment) {
                case P2P:
                    PaymentActivity.startActivityForResult(this, CLIENT_ID,
                            new P2pParams(getPaymentTo(), getAmount()), REQUEST_CODE);
                    break;
                case PHONE:
                    PaymentActivity.startActivityForResult(this, CLIENT_ID,
                            new PhoneParams(getPaymentTo(), getAmount()), REQUEST_CODE);
                    break;
            }
        } else {
            Toast.makeText(this, R.string.activity_pay_toast, Toast.LENGTH_SHORT).show();
        }
    }

    private String getPaymentTo() {
        return Views.getTextSafely(paymentTo).replaceAll("\\D", "");
    }

    private BigDecimal getAmount() {
        return new BigDecimal(Views.getTextSafely(amount));
    }

    private boolean isValid() {
        return !TextUtils.isEmpty(Views.getTextSafely(paymentTo)) &&
                !TextUtils.isEmpty(Views.getTextSafely(amount)) && getAmount().doubleValue() > 0;
    }

    private enum Payment {
        P2P,
        PHONE
    }
}
