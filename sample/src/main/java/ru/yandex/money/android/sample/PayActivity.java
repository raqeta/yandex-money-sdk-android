package ru.yandex.money.android.sample;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.yandex.money.model.common.params.ParamsP2P;
import com.yandex.money.model.common.params.ParamsPhone;

import java.math.BigDecimal;

import ru.yandex.money.android.PaymentActivity;
import ru.yandex.money.android.utils.Views;

/**
 * @author vyasevich
 */
public class PayActivity extends Activity {

    private static final String CLIENT_ID = "[your_client_id]";

    private static final int REQUEST_CODE = 101;

    private static final String EXTRA_PAYMENT = "ru.yandex.money.android.sample.extra.PAYMENT";

    private Payment payment;

    private EditText paymentTo;
    private EditText amount;

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
        if (savedInstanceState == null) {
            init();
        }
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
