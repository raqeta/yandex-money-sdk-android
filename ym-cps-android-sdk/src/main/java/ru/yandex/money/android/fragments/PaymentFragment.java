package ru.yandex.money.android.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yandex.money.ParamsP2P;
import com.yandex.money.ParamsPhone;

import ru.yandex.money.android.R;

/**
 *
 */
public class PaymentFragment extends BasePaymentFragment {

    public static PaymentFragment newInstance(String clientId, ParamsP2P params) {
        PaymentFragment frg = new PaymentFragment();
        setArguments(frg, clientId, ParamsP2P.PATTERN_ID, params.makeParams());
        return frg;
    }

    public static PaymentFragment newInstance(String clientId, ParamsPhone params) {
        PaymentFragment frg = new PaymentFragment();
        setArguments(frg, clientId, ParamsPhone.PATTERN_ID, params.makeParams());
        return frg;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.payment_fragment, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        replaceFragment(WebFragment.newInstance(getClientId(), getPatternId(), getParams()));
    }

    public void showError(String error) {
        replaceFragment(ErrorFragment.newInstance(error));
    }

    public void showSuccess(double contractAmount) {
        replaceFragment(SuccessFragment.newInstance(contractAmount));
    }

    private void replaceFragment(Fragment fragment) {
        if (fragment == null) {
            return;
        }

        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }
}
