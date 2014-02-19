package ru.yandex.money.android.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.yandex.money.android.R;
import ru.yandex.money.android.utils.Views;

/**
 * @author vyasevich
 */
public class ErrorFragment extends Fragment {

    private static final String EXTRA_ERROR = "ru.yandex.money.android.extra.ERROR";

    public static ErrorFragment newInstance(String error) {
        Bundle args = new Bundle();
        args.putString(EXTRA_ERROR, error);

        ErrorFragment frg = new ErrorFragment();
        frg.setArguments(args);
        return frg;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.error_fragment, container, false);
        assert view != null : "view is null";
        Views.setText(view, R.id.message, getMessage(getArguments().getString(EXTRA_ERROR)));
        return view;
    }

    private String getMessage(String error) {
        if ("payee_not_found".equals(error)) {
            return getString(R.string.error_payee_not_found);
        } else if ("payment_refused".equals(error)) {
            return getString(R.string.error_payment_refused);
        } else {
            return getString(R.string.error_unknown, error);
        }
    }
}
