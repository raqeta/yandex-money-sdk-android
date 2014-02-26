package ru.yandex.money.android.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yandex.money.model.cps.misc.MoneySource;

import ru.yandex.money.android.R;
import ru.yandex.money.android.formatters.MoneySourceFormatter;
import ru.yandex.money.android.parcelables.MoneySourceParcelable;
import ru.yandex.money.android.utils.CardType;
import ru.yandex.money.android.utils.Views;

/**
 * @author vyasevich
 */
public class CscFragment extends PaymentFragment {

    private static final String EXTRA_MONEY_SOURCE = "ru.yandex.money.android.extra.MONEY_SOURCE";

    private String requestId;
    private MoneySource moneySource;

    public static CscFragment newInstance(String requestId, MoneySource moneySource) {
        Bundle args = new Bundle();
        args.putString(EXTRA_REQUEST_ID, requestId);
        args.putParcelable(EXTRA_MONEY_SOURCE, new MoneySourceParcelable(moneySource));

        CscFragment fragment = new CscFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle args = getArguments();
        assert args == null : "provide correct arguments for CscFragment";

        MoneySourceParcelable moneySourceParcelable = args.getParcelable(EXTRA_MONEY_SOURCE);
        assert moneySourceParcelable != null : "provide money source for CscFragment";

        requestId = args.getString(EXTRA_REQUEST_ID);
        moneySource = moneySourceParcelable.getMoneySource();

        View view = inflater.inflate(R.layout.csc_fragment, container, false);

        CardType cardType = CardType.parseCardType(moneySource.getPaymentCardType());
        Views.setText(view, R.id.csc_code, getString(R.string.csc_code, cardType.getCscAbbr()));
        Views.setText(view, R.id.csc_hint, getString(R.string.csc_hint,
                getString(MoneySourceFormatter.getCscNumberType(cardType)),
                getString(MoneySourceFormatter.getCscNumberLocation(cardType))));

        return view;
    }
}
