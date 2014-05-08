package ru.yandex.money.android.sample;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findButton(R.id.send_p2p).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PayActivity.startP2P(MainActivity.this);
            }
        });

        findButton(R.id.top_up_phone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PayActivity.startPhone(MainActivity.this);
            }
        });
    }

    private Button findButton(int id) {
        return (Button) findViewById(id);
    }
}
