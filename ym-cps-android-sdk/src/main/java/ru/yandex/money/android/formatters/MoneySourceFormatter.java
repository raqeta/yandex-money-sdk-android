package ru.yandex.money.android.formatters;

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
}
