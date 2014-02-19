package ru.yandex.money.android.sample.test;

/**
 * Created by dvmelnikov on 17/02/14.
 */
public class Card {

    public final static Card non3ds = new Card("4268037111484391", "04", "2017", "874", "TEST", "a@aa.aa");

    public static String ID_CARD_NUMBER = "card-number";
    public static String ID_MONTH = "month";
    public static String ID_YEAR = "year";
    public static String ID_CVC = "cardCvc";
    public static String ID_FIO = "fio";
    public static String ID_CPS_EMAIL = "email";

    private String number;
    private String month;
    private String year;
    private String cvc;
    private String fio;
    private String email;

    public Card(String number, String month, String year, String cvc, String fio, String email) {
        this.number = number;
        this.month = month;
        this.year = year;
        this.cvc = cvc;
        this.fio = fio;
        this.email = email;
    }

    public String getNumber() {
        return number;
    }

    public String getMonth() {
        return month;
    }

    public String getYear() {
        return year;
    }

    public String getCvc() {
        return cvc;
    }

    public String getFio() {
        return fio;
    }

    public String getEmail() {
        return email;
    }
}
