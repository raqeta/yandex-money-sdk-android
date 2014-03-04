package ru.yandex.money.android.fragments;

import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yandex.money.model.cps.ProcessExternalPayment;
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

    private String requestId;
    private MoneySource moneySource;
    private CardType cardType;
    private String csc;

    private LinearLayout error;
    private TextView errorTitle;
    private TextView errorMessage;
    private EditText cscEditText;
    private Button pay;

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
        assert args != null : "provide correct arguments for CscFragment";

        MoneySourceParcelable moneySourceParcelable = args.getParcelable(EXTRA_MONEY_SOURCE);
        assert moneySourceParcelable != null : "provide money source for CscFragment";

        requestId = args.getString(EXTRA_REQUEST_ID);
        moneySource = moneySourceParcelable.getMoneySource();
        cardType = CardType.parseCardType(moneySource.getPaymentCardType());

        View view = inflater.inflate(R.layout.csc_fragment, container, false);
        assert view != null : "unable to inflate view in CscFragment";

        error = (LinearLayout) view.findViewById(R.id.error);
        errorTitle = (TextView) view.findViewById(R.id.error_title);
        errorMessage = (TextView) view.findViewById(R.id.error_message);

        cscEditText = (EditText) view.findViewById(R.id.csc);
        cscEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(cardType.getDigits())});

        Views.setText(view, R.id.csc_code, getString(R.string.csc_code, cardType.getCscAbbr()));
        Views.setText(view, R.id.csc_hint, getString(R.string.csc_hint,
                getString(MoneySourceFormatter.getCscNumberType(cardType)),
                getString(MoneySourceFormatter.getCscNumberLocation(cardType))));

        Button cancel = (Button) view.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCancelClicked();
            }
        });

        pay = (Button) view.findViewById(R.id.pay);
        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPayClicked();
            }
        });

        return view;
    }

    @Override
    protected void onExternalPaymentProcessed(ProcessExternalPayment pep) {
        super.onExternalPaymentProcessed(pep);
        if (pep.isSuccess()) {
            showSuccess(moneySource);
        } else if (pep.isExtAuthRequired()) {
            showWeb(pep, moneySource);
        } else {
            showError(pep.getError(), pep.getStatus());
        }
    }

    private void setErrorGone() {
        setError(View.GONE, null, null);
    }

    private void setErrorVisible(String title, String message) {
        setError(View.VISIBLE, title, message);
    }

    private void setError(int visibility, String title, String message) {
        error.setVisibility(visibility);
        errorTitle.setText(title);
        errorMessage.setText(message);
    }

    private void onCancelClicked() {
        showCards();
    }

    private void onPayClicked() {
        if (valid()) {
            setErrorGone();
            pay.setEnabled(false);
            cscEditText.setEnabled(false);
            reqId = getPaymentActivity().getDataServiceHelper().process(requestId,
                    moneySource.getMoneySourceToken(), csc);
        } else {
            setErrorVisible(getString(R.string.error_oops_title),
                    getString(R.string.error_csc_invalid));
        }
    }

    private boolean valid() {
        csc = Views.getTextSafely(cscEditText);
        return csc != null && csc.length() == cardType.getDigits();
    }
}
