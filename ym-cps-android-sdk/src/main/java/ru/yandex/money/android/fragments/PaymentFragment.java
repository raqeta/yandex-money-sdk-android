package ru.yandex.money.android.fragments;

import android.app.Fragment;

import ru.yandex.money.android.PaymentActivity;

/**
 * @author vyasevich
 */
public abstract class PaymentFragment extends Fragment {

    protected PaymentActivity getPaymentActivity() {
        return (PaymentActivity) getActivity();
    }
}
