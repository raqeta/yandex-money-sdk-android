package ru.yandex.money.android;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;

import com.yandex.money.model.common.MoneySource;
import com.yandex.money.model.common.params.ParamsP2P;
import com.yandex.money.model.common.params.ParamsPhone;

import ru.yandex.money.android.fragments.ErrorFragment;
import ru.yandex.money.android.fragments.SuccessFragment;
import ru.yandex.money.android.fragments.WebFragment;

/**
 * @author vyasevich
 */
public class PaymentActivity extends Activity {

    private static final String EXTRA_ARGUMENTS = "ru.yandex.money.android.extra.ARGUMENTS";

    private PaymentArguments arguments;

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
        setContentView(R.layout.payment_fragment);
        arguments = new PaymentArguments(getIntent().getBundleExtra(EXTRA_ARGUMENTS));

        if (savedInstanceState == null) {
            replaceFragment(WebFragment.newInstance(arguments));
        }
    }

    public void showError(String error) {
        replaceFragment(ErrorFragment.newInstance(error));
    }

    public void showSuccess(double contractAmount, MoneySource moneySource) {
        replaceFragment(SuccessFragment.newInstance(contractAmount, moneySource));
    }

    private void replaceFragment(Fragment fragment) {
        if (fragment == null) {
            return;
        }

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }
}
