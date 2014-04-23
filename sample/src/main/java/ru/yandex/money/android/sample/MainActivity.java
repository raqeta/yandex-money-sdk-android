package ru.yandex.money.android.sample;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.yandex.money.model.common.params.ParamsP2P;
import com.yandex.money.model.common.params.ParamsPhone;

import java.math.BigDecimal;

import ru.yandex.money.android.PaymentActivity;

public class MainActivity extends Activity {

    private static final int REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findButton(R.id.p2pTest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PaymentActivity.startActivityForResult(MainActivity.this, Consts.CLIENT_ID,
                        new ParamsP2P("41001901291751", new BigDecimal(2)), REQUEST_CODE);
            }
        });

        findButton(R.id.phoneTest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PaymentActivity.startActivityForResult(MainActivity.this, Consts.CLIENT_ID,
                        new ParamsPhone("79213020052", new BigDecimal(2)), REQUEST_CODE);
            }
        });
    }

    private Button findButton(int id) {
        return (Button) findViewById(id);
    }
}
