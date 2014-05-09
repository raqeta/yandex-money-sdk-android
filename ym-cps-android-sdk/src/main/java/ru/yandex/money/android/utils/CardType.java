package ru.yandex.money.android.utils;

import ru.yandex.money.android.R;

/**
 * @author vyasevich
 */
public enum CardType {

    VISA("VISA", "CVV2", R.drawable.ym_visa, R.drawable.ym_visa_card, 3),
    MASTER_CARD("MasterCard", "CVC2", R.drawable.ym_mc, R.drawable.ym_mc_card, 3),
    AMERICAN_EXPRESS("AmericanExpress", "CID", R.drawable.ym_ae, R.drawable.ym_ae_card, 4), // also cscAbbr = 4DBC
    JCB("JCB", "CAV2", R.drawable.ym_default_card, R.drawable.ym_default_card, 3),
    UNKNOWN("UNKNOWN", "CSC", R.drawable.ym_default_card, R.drawable.ym_default_card, 3);

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
