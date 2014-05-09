package ru.yandex.money.android.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.yandex.money.model.cps.ProcessExternalPayment;
import com.yandex.money.model.cps.misc.MoneySource;

import ru.yandex.money.android.R;
import ru.yandex.money.android.database.DatabaseStorage;
import ru.yandex.money.android.formatters.MoneySourceFormatter;
import ru.yandex.money.android.parcelables.MoneySourceParcelable;
import ru.yandex.money.android.utils.CardType;
import ru.yandex.money.android.utils.Views;

/**
 * @author vyasevich
 */
public class SuccessFragment extends PaymentFragment {

    private static final String EXTRA_CONTRACT_AMOUNT = "ru.yandex.money.android.extra.CONTRACT_AMOUNT";
    private static final String EXTRA_STATE = "ru.yandex.money.android.extra.STATE";

    private String requestId;
    private State state = State.SUCCESS_SHOWED;
    private MoneySource moneySource;

    private View card;
    private Button saveCard;
    private View successMarker;
    private TextView description;

    public static SuccessFragment newInstance(String requestId, double contractAmount,
                                              MoneySource moneySource) {

        Bundle args = new Bundle();
        args.putString(EXTRA_REQUEST_ID, requestId);
        args.putDouble(EXTRA_CONTRACT_AMOUNT, contractAmount);
        if (moneySource != null) {
            args.putParcelable(EXTRA_MONEY_SOURCE, new MoneySourceParcelable(moneySource));
        }

        SuccessFragment frg = new SuccessFragment();
        frg.setArguments(args);
        return frg;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ym_success_fragment, container, false);
        assert view != null : "view is null";

        Bundle args = getArguments();
        assert args != null : "no arguments for SuccessFragment";

        requestId = args.getString(EXTRA_REQUEST_ID);
        Views.setText(view, R.id.ym_comment, getString(R.string.ym_success_comment,
                args.getDouble(EXTRA_CONTRACT_AMOUNT)));

        card = view.findViewById(R.id.ym_card);
        description = (TextView) view.findViewById(R.id.ym_description);
        successMarker = view.findViewById(R.id.ym_success_marker);
        saveCard = (Button) view.findViewById(R.id.ym_save_card);

        if (savedInstanceState == null) {
            moneySource = getMoneySourceFromBundle(args);
            if (moneySource != null) {
                state = State.CARD_EXISTS;
            }
        } else {
            state = (State) savedInstanceState.getSerializable(EXTRA_STATE);
            moneySource = getMoneySourceFromBundle(savedInstanceState);
        }

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        applyState();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(EXTRA_STATE, state);
        if (moneySource != null) {
            outState.putParcelable(EXTRA_MONEY_SOURCE, new MoneySourceParcelable(moneySource));
        }
    }

    @Override
    protected void onExternalPaymentProcessed(ProcessExternalPayment pep) {
        super.onExternalPaymentProcessed(pep);
        if (pep.isSuccess()) {
            moneySource = pep.getMoneySource();
            new DatabaseStorage(getPaymentActivity()).insertMoneySource(moneySource);
            state = State.SAVING_COMPLETED;
            onCardSaved();
        } else {
            showError(pep.getError(), pep.getStatus());
        }
    }

    private void applyState() {
        switch (state) {
            case SUCCESS_SHOWED:
                saveCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onSaveCardClicked();
                    }
                });
                break;
            case SAVING_INITIATED:
                onSaveCardClicked();
                break;
            case SAVING_COMPLETED:
                onCardSaved();
                break;
            case CARD_EXISTS:
                onCardExists();
                break;
        }
    }

    private void onSaveCardClicked() {
        card.setBackgroundResource(R.drawable.ym_card_process);
        saveCard.setEnabled(false);
        saveCard.setText(R.string.ym_success_saving_card);
        saveCard.setOnClickListener(null);
        description.setText(R.string.ym_success_saving_card_description);
        reqId = getPaymentActivity().getDataServiceHelper().process(requestId, true);
        state = State.SAVING_INITIATED;
    }

    private void onCardSaved() {
        Views.setImageResource(getView(), R.id.ym_payment_card_type,
                CardType.parseCardType(moneySource.getPaymentCardType()).getIcoResId());
        Views.setText(getView(), R.id.ym_pan_fragment,
                MoneySourceFormatter.formatPanFragment(moneySource.getPanFragment()));
        card.setBackgroundResource(R.drawable.ym_card_saved);
        saveCard.setVisibility(View.GONE);
        successMarker.setVisibility(View.VISIBLE);
        description.setText(getString(R.string.ym_success_card_saved_description,
                CardType.parseCardType(moneySource.getPaymentCardType()).getCscAbbr()));
    }

    private void onCardExists() {
        card.setVisibility(View.GONE);
        saveCard.setVisibility(View.GONE);
        successMarker.setVisibility(View.GONE);
        description.setVisibility(View.GONE);
        Views.setVisibility(getView(), R.id.ym_success, View.VISIBLE);
    }

    private MoneySource getMoneySourceFromBundle(Bundle bundle) {
        MoneySourceParcelable parcelable = bundle.getParcelable(EXTRA_MONEY_SOURCE);
        return parcelable == null ? null : parcelable.getMoneySource();
    }

    private enum State {
        SUCCESS_SHOWED,
        SAVING_INITIATED,
        SAVING_COMPLETED,
        CARD_EXISTS
    }
}
