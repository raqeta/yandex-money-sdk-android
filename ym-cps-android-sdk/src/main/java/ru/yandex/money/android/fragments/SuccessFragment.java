package ru.yandex.money.android.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.yandex.money.model.cps.ProcessExternalPayment;
import com.yandex.money.model.cps.misc.MoneySource;

import java.io.IOException;

import ru.yandex.money.android.PaymentActivity;
import ru.yandex.money.android.PaymentArguments;
import ru.yandex.money.android.Prefs;
import ru.yandex.money.android.R;
import ru.yandex.money.android.YandexMoneyDroid;
import ru.yandex.money.android.database.DatabaseStorage;
import ru.yandex.money.android.parcelables.MoneySourceParcelable;
import ru.yandex.money.android.utils.CardType;
import ru.yandex.money.android.utils.Views;

/**
 * @author vyasevich
 */
public class SuccessFragment extends PaymentFragment {

    private static final String EXTRA_REQUEST_ID = "ru.yandex.money.android.extra.REQUEST_ID";
    private static final String EXTRA_CONTRACT_AMOUNT = "ru.yandex.money.android.extra.CONTRACT_AMOUNT";
    private static final String EXTRA_STATE = "ru.yandex.money.android.extra.STATE";
    private static final String EXTRA_MONEY_SOURCE = "ru.yandex.money.android.extra.MONEY_SOURCE";

    private String requestId;
    private State state = State.SUCCESS_SHOWED;
    private MoneySource moneySource;

    private View card;
    private Button saveCard;
    private View successMarker;
    private TextView description;

    public static SuccessFragment newInstance(String requestId, double contractAmount) {
        Bundle args = new Bundle();
        args.putString(EXTRA_REQUEST_ID, requestId);
        args.putDouble(EXTRA_CONTRACT_AMOUNT, contractAmount);

        SuccessFragment frg = new SuccessFragment();
        frg.setArguments(args);
        return frg;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.success_fragment, container, false);
        assert view != null : "view is null";

        Bundle args = getArguments();
        assert args != null : "no arguments for SuccessFragment";

        requestId = args.getString(EXTRA_REQUEST_ID);
        Views.setText(view, R.id.comment, getString(R.string.success_comment,
                args.getDouble(EXTRA_CONTRACT_AMOUNT)));

        card = view.findViewById(R.id.card);
        description = (TextView) view.findViewById(R.id.description);
        successMarker = view.findViewById(R.id.success_marker);
        saveCard = (Button) view.findViewById(R.id.save_card);

        if (savedInstanceState != null) {
            MoneySourceParcelable moneySourceParcelable =
                    savedInstanceState.getParcelable(EXTRA_MONEY_SOURCE);
            assert moneySourceParcelable != null : "no money source specified for SuccessFragment";
            moneySource = moneySourceParcelable.getMoneySource();
            state = (State) savedInstanceState.getSerializable(EXTRA_STATE);
        }
        applyState();

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(EXTRA_STATE, state);
        outState.putParcelable(EXTRA_MONEY_SOURCE, new MoneySourceParcelable(moneySource));
    }

    private void applyState() {
        switch (state) {
            case SUCCESS_SHOWED:
                saveCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onSaveCardClicked.execute();
                    }
                });
                break;
            case SAVING_INITIATED:
                onSaveCardClicked.execute();
                break;
            case SAVING_COMPLETED:
                onCardSaved(moneySource);
                break;
        }
    }

    private void onCardSaved(MoneySource moneySource) {
        this.moneySource = moneySource;
        Views.setText(getView(), R.id.payment_card_type, moneySource.getPaymentCardType());
        Views.setText(getView(), R.id.pan_fragment, moneySource.getPanFragment());
        card.setBackgroundResource(R.drawable.card_saved);
        saveCard.setVisibility(View.GONE);
        successMarker.setVisibility(View.VISIBLE);
        description.setText(getString(R.string.success_card_saved_description,
                CardType.parseCardType(moneySource.getPaymentCardType()).getCscAbbr()));
    }

    private enum State {
        SUCCESS_SHOWED,
        SAVING_INITIATED,
        SAVING_COMPLETED
    }

    private final AsyncTask<Void, Void, MoneySource> onSaveCardClicked =
            new AsyncTask<Void, Void, MoneySource>() {

        @Override
        protected void onPreExecute() {
            card.setBackgroundResource(R.drawable.card_process);
            saveCard.setEnabled(false);
            saveCard.setText(R.string.success_saving_card);
            saveCard.setOnClickListener(null);
            description.setText(R.string.success_saving_card_description);
            state = State.SAVING_INITIATED;
        }

        @Override
        protected MoneySource doInBackground(Void... params) {
            PaymentActivity activity = getPaymentActivity();
            PaymentArguments arguments = activity.getArguments();
            YandexMoneyDroid ymd = new YandexMoneyDroid(arguments.getClientId(),
                    new Prefs(activity));
            MoneySource moneySource = processExternalPayment(ymd, requestId);
            new DatabaseStorage(activity).insertMoneySource(moneySource);
            state = State.SAVING_COMPLETED;
            return moneySource;
        }

        @Override
        protected void onPostExecute(MoneySource moneySource) {
            onCardSaved(moneySource);
        }

        private MoneySource processExternalPayment(YandexMoneyDroid ymd, String requestId) {
            try {
                ProcessExternalPayment pep = ymd.process(requestId, true);
                if (pep.isInProgress()) {
                    return processExternalPayment(ymd, requestId);
                } else if (pep.isSuccess()) {
                    return pep.getMoneySource();
                } else {
                    // TODO catch exception properly
                    throw new RuntimeException("yeah yeah... we should catch exceptions...");
                }
            } catch (IOException e) {
                // TODO catch exception properly
                throw new RuntimeException("yeah yeah... we should catch exceptions...");
            }
        }
    };
}
