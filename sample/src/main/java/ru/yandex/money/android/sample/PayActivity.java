package ru.yandex.money.android.sample;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yandex.money.model.common.params.ParamsP2P;
import com.yandex.money.model.common.params.ParamsPhone;

import java.math.BigDecimal;
import java.util.List;

import ru.yandex.money.android.PaymentActivity;
import ru.yandex.money.android.sample.storage.DatabaseHelper;
import ru.yandex.money.android.utils.Views;

/**
 * @author vyasevich
 */
public class PayActivity extends ListActivity {

    private static final String CLIENT_ID = "[your_client_id]";

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

        payment = (Payment) getIntent().getSerializableExtra(EXTRA_PAYMENT);
        helper = DatabaseHelper.getInstance(this);

        init();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
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

        Button proceed = (Button) findViewById(R.id.proceed);
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proceed();
            }
        });

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

    private void proceed() {
        if (isValid()) {
            switch (payment) {
                case P2P:
                    PaymentActivity.startActivityForResult(this, CLIENT_ID,
                            new ParamsP2P(getPaymentTo(), getAmount()), REQUEST_CODE);
                    break;
                case PHONE:
                    PaymentActivity.startActivityForResult(this, CLIENT_ID,
                            new ParamsPhone(getPaymentTo(), getAmount()), REQUEST_CODE);
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
