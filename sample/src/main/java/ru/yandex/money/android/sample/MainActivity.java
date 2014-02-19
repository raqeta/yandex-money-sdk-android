package ru.yandex.money.android.sample;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.yandex.money.ParamsPhone;

import java.math.BigDecimal;

import ru.yandex.money.android.fragments.PaymentFragment;

public class MainActivity extends ActionBarActivity implements Consts {

    private static final String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            PaymentFragment paymentFragment = PaymentFragment
                    .newInstance(CLIENT_ID, new ParamsPhone("79112611383", new BigDecimal(2)));
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, paymentFragment)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
