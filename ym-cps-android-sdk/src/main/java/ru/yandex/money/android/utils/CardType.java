package ru.yandex.money.android.utils;

import ru.yandex.money.android.R;

/**
 * @author vyasevich
 */
public enum CardType {

    VISA("VISA", "CVV2", R.drawable.visa, R.drawable.visa_card, 3),
    MASTER_CARD("MasterCard", "CVC2", R.drawable.mc, R.drawable.mc_card, 3),
    AMERICAN_EXPRESS("AmericanExpress", "CID", R.drawable.ae, R.drawable.ae_card, 4), // also cscAbbr = 4DBC
    JCB("JCB", "CAV2", R.drawable.default_card, R.drawable.default_card, 3),
    UNKNOWN("UNKNOWN", "CSC", R.drawable.default_card, R.drawable.default_card, 3);

    private final String name;
    private final String cscAbbr;
    private final int icoResId;
    private final int cardResId;
    private final int digits;

    public static CardType parseCardType(String type) {
        for (CardType cardType : values()) {
            if (cardType.name.equalsIgnoreCase(type)) {
                return cardType;
            }
        }
        return UNKNOWN;
    }

    private CardType(String name, String cscAbbr, int icoResId, int cardResId, int digits) {
        this.name = name;
        this.cscAbbr = cscAbbr;
        this.icoResId = icoResId;
        this.cardResId = cardResId;
        this.digits = digits;
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

    public int getDigits() {
        return digits;
    }
}
