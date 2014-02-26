package ru.yandex.money.android.utils;

import ru.yandex.money.android.R;

/**
 * @author vyasevich
 */
public enum CardType {

    VISA("VISA", "CVV2", R.drawable.visa, R.drawable.visa_card),
    MASTER_CARD("MasterCard", "CVC2", R.drawable.mc, R.drawable.mc_card),
    AMERICAN_EXPRESS("AmericanExpress", "CID", R.drawable.ae, R.drawable.ae_card), // also cscAbbr = 4DBC
    JCB("JCB", "CAV2", R.drawable.default_card, R.drawable.default_card),
    UNKNOWN("UNKNOWN", "CSC", R.drawable.default_card, R.drawable.default_card);

    private final String name;
    private final String cscAbbr;
    private final int icoResId;
    private final int cardResId;

    public static CardType parseCardType(String type) {
        for (CardType cardType : values()) {
            if (cardType.name.equalsIgnoreCase(type)) {
                return cardType;
            }
        }
        return UNKNOWN;
    }

    private CardType(String name, String cscAbbr, int icoResId, int cardResId) {
        this.name = name;
        this.cscAbbr = cscAbbr;
        this.icoResId = icoResId;
        this.cardResId = cardResId;
    }

    public String getName() {
        return name;
    }

    public String getCscAbbr() {
        return cscAbbr;
    }

    public int getIcoResId() {
        return icoResId;
    }

    public int getCardResId() {
        return cardResId;
    }
}
