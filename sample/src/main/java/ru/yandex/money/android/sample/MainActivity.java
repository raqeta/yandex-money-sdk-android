package ru.yandex.money.android.sample;

import android.app.Activity;
import android.os.Bundle;

import com.yandex.money.model.common.params.ParamsPhone;

import java.math.BigDecimal;

import ru.yandex.money.android.PaymentActivity;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            PaymentActivity.startActivityForResult(this, Consts.CLIENT_ID,
                    new ParamsPhone("79112611383", new BigDecimal(2)), 0);
        }
    }
}
