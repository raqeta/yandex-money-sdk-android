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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;

import com.yandex.money.model.cps.misc.MoneySource;

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

        View view = inflater.inflate(R.layout.cards_fragment, container, false);
        assert view != null : "view is null";

        Bundle args = getArguments();
        assert args != null : "specify proper arguments for CardsFragment";

        Views.setText(view, R.id.payment_name, args.getString(EXTRA_TITLE));
        Views.setText(view, R.id.payment_sum, getString(R.string.cards_payment_sum_value,
                args.getDouble(EXTRA_CONTRACT_AMOUNT)));

        ListView list = (ListView) view.findViewById(android.R.id.list);
        list.setAdapter(new CardsAdapter());
        list.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MoneySource moneySource = (MoneySource) parent.getItemAtPosition(position);
        getPaymentActivity().showCsc(moneySource);
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
            return getCards().size();
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

            View root = convertView == null ? inflater.inflate(R.layout.card_item, parent,false) :
                    convertView;
            assert root != null : "unable to inflate layout in CardsAdapter";

            final MoneySource moneySource = getCardAtPosition(position);
            ImageView card = (ImageView) root.findViewById(R.id.card);
            card.setImageResource(CardType.parseCardType(moneySource.getPaymentCardType())
                    .getCardResId());
            Views.setText(root, R.id.pan_fragment, MoneySourceFormatter.formatPanFragment(
                    moneySource.getPanFragment()));

            ImageButton button = (ImageButton) root.findViewById(R.id.actions);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPopup(v, moneySource);
                }
            });

            return root;
        }

        private List<MoneySource> getCards() {
            return getPaymentActivity().getCards();
        }

        private MoneySource getCardAtPosition(int position) {
            return getCards().get(position);
        }

        private void showPopup(View v, MoneySource moneySource) {
            PopupMenu menu = new PopupMenu(getPaymentActivity(), v);
            MenuInflater inflater = menu.getMenuInflater();
            inflater.inflate(R.menu.card_actions, menu.getMenu());
            menu.setOnMenuItemClickListener(new MenuItemClickListener(moneySource));
            menu.show();
        }

        private void deleteCard(MoneySource moneySource) {
            databaseStorage.deleteMoneySource(moneySource);
            getCards().remove(moneySource);
            notifyDataSetChanged();
        }

        private class MenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

            private final MoneySource moneySource;

            public MenuItemClickListener(MoneySource moneySource) {
                this.moneySource = moneySource;
            }

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.delete) {
                    deleteCard(moneySource);
                    return true;
                }
                return false;
            }
        }
    }
}
