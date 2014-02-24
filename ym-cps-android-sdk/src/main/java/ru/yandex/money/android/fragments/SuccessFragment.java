package ru.yandex.money.android.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.yandex.money.model.common.MoneySource;

import ru.yandex.money.android.R;
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
        args.putString(EXTRA_MONEY_SOURCE, MoneySource.toJson(moneySource).toString());

        SuccessFragment frg = new SuccessFragment();
        frg.setArguments(args);
        return frg;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.success_fragment, container, false);
        assert view != null : "view is null";

        Bundle arguments = getArguments();
        assert arguments != null : "no arguments for SuccessFragment";
        moneySource = MoneySource.parseJson(arguments.getString(EXTRA_MONEY_SOURCE));

        Views.setText(view, R.id.comment, getString(R.string.success_comment,
                arguments.getDouble(EXTRA_CONTRACT_AMOUNT)));

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

    }
}
