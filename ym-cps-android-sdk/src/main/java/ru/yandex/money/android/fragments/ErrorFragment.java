package ru.yandex.money.android.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.yandex.money.api.model.Error;

import ru.yandex.money.android.R;
import ru.yandex.money.android.utils.Views;

/**
 * @author vyasevich
 */
public class ErrorFragment extends PaymentFragment {

    private static final String TAG = "ErrorFragment";

    private static final String EXTRA_ERROR = "ru.yandex.money.android.extra.ERROR";
    private static final String EXTRA_STATUS = "ru.yandex.money.android.extra.STATUS";

    public static ErrorFragment newInstance(Error error, String status) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_ERROR, error);
        args.putString(EXTRA_STATUS, status);

        ErrorFragment frg = new ErrorFragment();
        frg.setArguments(args);
        return frg;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ym_error_fragment, container, false);
        assert view != null : "view is null";

        Bundle args = getArguments();
        assert args != null : "you did not pass mandatory arguments for ErrorFragment";

        showError(view, (Error) args.getSerializable(EXTRA_ERROR), args.getString(EXTRA_STATUS));
        return view;
    }

    private void showError(View view, Error error, String status) {
        Log.e(TAG, String.format("error=%1$s,status=%2$s", error, status));

        final int notSpecified = -1;

        final int titleResId;
        final int messageResId;
        final int actionResId;

        if (Error.ILLEGAL_PARAM_CLIENT_ID == error) {
            titleResId = R.string.ym_error_illegal_param_client_id_title;
            messageResId = R.string.ym_error_illegal_param_client_id;
            actionResId = notSpecified;
        } else if (Error.ILLEGAL_PARAM_CSC == error) {
            titleResId = R.string.ym_error_oops_title;
            messageResId = R.string.ym_error_illegal_param_csc;
            actionResId = R.string.ym_error_action_try_again;
        } else if (Error.AUTHORIZATION_REJECT == error) {
            titleResId = R.string.ym_error_something_wrong_title;
            messageResId = R.string.ym_error_authorization_reject;
            actionResId = R.string.ym_error_action_try_another_card;
        } else if (Error.PAYEE_NOT_FOUND == error) {
            titleResId = R.string.ym_error_oops_title;
            messageResId = R.string.ym_error_payee_not_found;
            actionResId = notSpecified;
        } else if (Error.PAYMENT_REFUSED == error) {
            titleResId = R.string.ym_error_something_wrong_title;
            messageResId = R.string.ym_error_payment_refused;
            actionResId = R.string.ym_error_action_try_again;
        } else if ("REFUSED".equals(status)) {
            titleResId = R.string.ym_error_illegal_param_client_id_title;
            messageResId = R.string.ym_error_illegal_param_client_id;
            actionResId = notSpecified;
        } else {
            titleResId = R.string.ym_error_oops_title;
            messageResId = R.string.ym_error_unknown;
            actionResId = notSpecified;
        }

        Views.setText(view, R.id.ym_error_title, getString(titleResId));
        Views.setText(view, R.id.ym_error_message, getString(messageResId));

        Button action = (Button) view.findViewById(R.id.ym_error_action);
        if (actionResId == notSpecified) {
            action.setVisibility(View.GONE);
            action.setOnClickListener(null);
        } else {
            action.setText(getString(actionResId));
            action.setVisibility(View.VISIBLE);
            action.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getPaymentActivity().requestExternalPayment();
                }
            });
        }
    }
}
