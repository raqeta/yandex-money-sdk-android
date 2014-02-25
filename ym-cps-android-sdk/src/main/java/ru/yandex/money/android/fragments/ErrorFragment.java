package ru.yandex.money.android.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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

        Bundle args = getArguments();
        assert args != null : "you did not pass mandatory arguments for ErrorFragment";

        showError(view, args.getString(EXTRA_ERROR));
        return view;
    }

    private void showError(View view, String error) {
        final int notSpecified = -1;

        final int titleResId;
        final int messageResId;
        final int actionResId;
        if ("illegal_param_client_id".equals(error)) {
            titleResId = R.string.error_illegal_param_client_id_title;
            messageResId = R.string.error_illegal_param_client_id;
            actionResId = notSpecified;
        } else if ("illegal_param_csc".equals(error)) {
            titleResId = R.string.error_oops_title;
            messageResId = R.string.error_illegal_param_csc;
            actionResId = R.string.error_action_try_again;
        } else if ("authorization_reject".equals(error)) {
            titleResId = R.string.error_something_wrong_title;
            messageResId = R.string.error_authorization_reject;
            actionResId = R.string.error_action_try_another_card;
        } else if ("payee_not_found".equals(error)) {
            titleResId = R.string.error_oops_title;
            messageResId = R.string.error_payee_not_found;
            actionResId = notSpecified;
        } else if ("payment_refused".equals(error)) {
            titleResId = R.string.error_something_wrong_title;
            messageResId = R.string.error_payment_refused;
            actionResId = R.string.error_action_try_again;
        } else {
            titleResId = R.string.error_oops_title;
            messageResId = R.string.error_unknown;
            actionResId = R.string.error_action_try_again;
        }

        Views.setText(view, R.id.title, getString(titleResId));
        Views.setText(view, R.id.message, getString(messageResId));

        Button action = (Button) view.findViewById(R.id.action);
        if (actionResId == notSpecified) {
            action.setVisibility(View.GONE);
            action.setOnClickListener(null);
        } else {
            action.setText(getString(actionResId));
            action.setVisibility(View.VISIBLE);
            //action.setOnClickListener(); TODO implement
        }
    }
}
