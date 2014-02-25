package ru.yandex.money.android.utils;

/**
 * @author vyasevich
 */
public enum CardType {

    VISA("VISA", "CVV2"),
    MASTER_CARD("MasterCard", "CVC2"),
    AMERICAN_EXPRESS("AmericanExpress", "CID"), // also cscAbbr = 4DBC
    JCB("JCB", "CAV2"),
    UNKNOWN("UNKNOWN", "CSC");

    private final String name;
    private final String cscAbbr;

    public static CardType parseCardType(String type) {
        for (CardType cardType : values()) {
            if (cardType.name.equalsIgnoreCase(type)) {
                return cardType;
            }
        }
        return UNKNOWN;
    }

    private CardType(String name, String cscAbbr) {
        this.name = name;
        this.cscAbbr = cscAbbr;
    }

    public String getName() {
        return name;
    }

    public String getCscAbbr() {
        return cscAbbr;
    }
}
