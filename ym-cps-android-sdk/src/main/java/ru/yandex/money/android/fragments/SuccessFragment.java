package ru.yandex.money.android.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.yandex.money.model.common.MoneySource;

import ru.yandex.money.android.R;
import ru.yandex.money.android.database.DatabaseStorage;
import ru.yandex.money.android.parcelables.MoneySourceParcelable;
import ru.yandex.money.android.utils.Threads;
import ru.yandex.money.android.utils.Views;

/**
 * @author vyasevich
 */
public class SuccessFragment extends Fragment {

    private static final String EXTRA_CONTRACT_AMOUNT = "ru.yandex.money.android.extra.CONTRACT_AMOUNT";
    private static final String EXTRA_MONEY_SOURCE = "ru.yandex.money.android.extra.MONEY_SOURCE";

    private MoneySource moneySource;

    public static SuccessFragment newInstance(double contractAmount, MoneySource moneySource) {
        Bundle args = new Bundle();
        args.putDouble(EXTRA_CONTRACT_AMOUNT, contractAmount);
        args.putParcelable(EXTRA_MONEY_SOURCE, new MoneySourceParcelable(moneySource));

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

        MoneySourceParcelable moneySourceParcelable = args.getParcelable(EXTRA_MONEY_SOURCE);
        assert moneySourceParcelable != null : "no money source specified for SuccessFragment";
        moneySource = moneySourceParcelable.getMoneySource();

        Views.setText(view, R.id.comment, getString(R.string.success_comment,
                args.getDouble(EXTRA_CONTRACT_AMOUNT)));

        Button button = (Button) view.findViewById(R.id.saveCard);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSaveCardClicked();
            }
        });

        return view;
    }

    private void onSaveCardClicked() {
        Threads.runOnBackground(new Runnable() {
            @Override
            public void run() {
                new DatabaseStorage(getActivity()).insertMoneySource(moneySource);
            }
        });
    }
}
