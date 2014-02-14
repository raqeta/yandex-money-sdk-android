package ru.yandex.money.android.sample;

import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.yandex.money.IdentifierType;
import com.yandex.money.ParamsP2P;
import com.yandex.money.model.InstanceId;

import java.math.BigDecimal;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import ru.yandex.money.android.PaymentFragment;

public class MainActivity extends ActionBarActivity implements Consts {

    private static final String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            ParamsP2P params = new ParamsP2P("41001901291751", IdentifierType.ACCOUNT, new BigDecimal("2.00"), "test");

            PaymentFragment paymentFragment = PaymentFragment.newInstance(CLIENT_ID, params);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, paymentFragment)
                    .commit();
        }

    }

    private void onResult(String asd2) {
        Toast.makeText(this, asd2, Toast.LENGTH_LONG).show();
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
