package ru.yandex.money.android.formatters;

import ru.yandex.money.android.R;
import ru.yandex.money.android.utils.CardType;
import ru.yandex.money.android.utils.Strings;

/**
 * @author vyasevich
 */
public class MoneySourceFormatter {

    public static String formatPanFragment(String panFragment) {
        String[] fragments = panFragment.split("\\s");
        panFragment = Strings.concatenate(fragments, "");
        fragments = Strings.split(panFragment, 4);
        return Strings.concatenate(fragments, " ");
    }

    public static int getCscNumberType(CardType cardType) {
        return cardType == CardType.AMERICAN_EXPRESS ? R.string.csc_four : R.string.csc_three;
    }

    public static int getCscNumberLocation(CardType cardType) {
        return cardType == CardType.AMERICAN_EXPRESS ? R.string.csc_front : R.string.csc_back;
    }
}
