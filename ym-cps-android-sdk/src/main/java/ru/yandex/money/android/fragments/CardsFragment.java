package ru.yandex.money.android.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.yandex.money.api.model.ExternalCard;

import java.util.List;

import ru.yandex.money.android.R;
import ru.yandex.money.android.database.DatabaseStorage;
import ru.yandex.money.android.formatters.MoneySourceFormatter;
import ru.yandex.money.android.utils.CardType;
import ru.yandex.money.android.utils.Views;

/**
 * @author vyasevich
 */
public class CardsFragment extends PaymentFragment implements AdapterView.OnItemClickListener {

    private static final String EXTRA_TITLE = "ru.yandex.money.android.extra.TITLE";
    private static final String EXTRA_CONTRACT_AMOUNT = "ru.yandex.money.android.extra.CONTRACT_AMOUNT";

    public static CardsFragment newInstance(String title, double contractAmount) {
        Bundle args = new Bundle();
        args.putString(EXTRA_TITLE, title);
        args.putDouble(EXTRA_CONTRACT_AMOUNT, contractAmount);

        CardsFragment fragment = new CardsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.ym_cards_fragment, container, false);
        assert view != null : "view is null";

        Bundle args = getArguments();
        assert args != null : "specify proper arguments for CardsFragment";

        Views.setText(view, R.id.ym_payment_name, args.getString(EXTRA_TITLE));
        Views.setText(view, R.id.ym_payment_sum, getString(R.string.ym_cards_payment_sum_value,
                args.getDouble(EXTRA_CONTRACT_AMOUNT)));

        ListView list = (ListView) view.findViewById(android.R.id.list);
        list.setAdapter(new CardsAdapter());
        list.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ExternalCard moneySource = (ExternalCard) parent.getItemAtPosition(position);
        if (moneySource == null) {
            showWeb();
        } else {
            showCsc(moneySource);
        }
    }

    private class CardsAdapter extends BaseAdapter {

        private final LayoutInflater inflater;
        private final DatabaseStorage databaseStorage;

        public CardsAdapter() {
            inflater = LayoutInflater.from(getPaymentActivity());
            databaseStorage = new DatabaseStorage(getPaymentActivity());
        }

        @Override
        public int getCount() {
            return getSize() + 1;
        }

        @Override
        public Object getItem(int position) {
            return getCardAtPosition(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // this is a hack to keep footer height unchanged
            return position == getSize() ? getFooterView(parent) : getCardView(position, parent);
        }

        private View getCardView(int position, ViewGroup parent) {

            View root = inflater.inflate(R.layout.ym_card_item, parent, false);
            assert root != null : "unable to inflate layout in CardsAdapter";

            final ExternalCard moneySource = getCardAtPosition(position);
            final TextView panFragment = (TextView) root.findViewById(R.id.ym_pan_fragment);
            panFragment.setText(MoneySourceFormatter.formatPanFragment(moneySource.getPanFragment()));
            panFragment.setCompoundDrawablesWithIntrinsicBounds(CardType.parseCardType(
                    moneySource.getType()).getCardResId(), 0, 0, 0);

            ImageButton button = (ImageButton) root.findViewById(R.id.ym_actions);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPopup(v, moneySource);
                }
            });

            return root;
        }

        private View getFooterView(ViewGroup parent) {
            return inflater.inflate(R.layout.ym_cards_footer, parent, false);
        }

        private List<ExternalCard> getCards() {
            return getPaymentActivity().getCards();
        }

        private int getSize() {
            return getCards().size();
        }

        private ExternalCard getCardAtPosition(int position) {
            List<ExternalCard> cards = getCards();
            return position == cards.size() ? null : cards.get(position);
        }

        private void showPopup(View v, ExternalCard moneySource) {
            PopupMenu menu = new PopupMenu(getPaymentActivity(), v);
            MenuInflater inflater = menu.getMenuInflater();
            inflater.inflate(R.menu.ym_card_actions, menu.getMenu());
            menu.setOnMenuItemClickListener(new MenuItemClickListener(moneySource));
            menu.show();
        }

        private void deleteCard(ExternalCard moneySource) {
            databaseStorage.deleteMoneySource(moneySource);
            getCards().remove(moneySource);
            notifyDataSetChanged();
        }

        private class MenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

            private final ExternalCard moneySource;

            public MenuItemClickListener(ExternalCard moneySource) {
                this.moneySource = moneySource;
            }

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.ym_delete) {
                    deleteCard(moneySource);
                    return true;
                }
                return false;
            }
        }
    }
}
