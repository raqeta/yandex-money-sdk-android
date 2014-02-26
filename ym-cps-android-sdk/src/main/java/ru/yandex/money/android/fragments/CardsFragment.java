package ru.yandex.money.android.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ru.yandex.money.android.R;

/**
 * @author vyasevich
 */
public class CardsFragment extends PaymentFragment {

    public static CardsFragment newInstance() {
        return new CardsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.cards_fragment, container, false);
        assert view != null : "view is null";

//        TextView paymentName = (TextView) view.findViewById(R.id.payment_name);
//        TextView paymentSum = (TextView) view.findViewById(R.id.payment_sum);

        return view;
    }
}
