package ru.yandex.money.android.parcelables;

import android.os.Parcel;
import android.os.Parcelable;

import com.yandex.money.model.common.MoneySource;

import ru.yandex.money.android.utils.Parcelables;

/**
 * @author vyasevich
 */
public class MoneySourceParcelable implements Parcelable {

    private final MoneySource moneySource;

    public MoneySourceParcelable(MoneySource moneySource) {
        this.moneySource = moneySource;
    }

    private MoneySourceParcelable(Parcel parcel) {
        WalletSourceParcelable walletSourceParcelable =
                parcel.readParcelable(WalletSourceParcelable.class.getClassLoader());
        assert walletSourceParcelable != null : "walletSourceParcelable is null";
        CardSourceParcelable cardSourceParcelable =
                parcel.readParcelable(CardSourceParcelable.class.getClassLoader());
        assert cardSourceParcelable != null : "cardSourceParcelable is null";
        moneySource = new MoneySource(walletSourceParcelable.getWalletSource(),
                cardSourceParcelable.getCardSource());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(new WalletSourceParcelable(moneySource.getWallet()), flags);
        dest.writeParcelable(new CardSourceParcelable(moneySource.getCard()), flags);
    }

    public MoneySource getMoneySource() {
        return moneySource;
    }

    public static final Creator<MoneySourceParcelable> CREATOR =
            new Creator<MoneySourceParcelable>() {
                @Override
                public MoneySourceParcelable createFromParcel(Parcel source) {
                    return new MoneySourceParcelable(source);
                }

                @Override
                public MoneySourceParcelable[] newArray(int size) {
                    return new MoneySourceParcelable[size];
                }
            };

    private static class WalletSourceParcelable implements Parcelable {

        private final MoneySource.WalletSource walletSource;

        public WalletSourceParcelable(MoneySource.WalletSource walletSource) {
            this.walletSource = walletSource;
        }

        public WalletSourceParcelable(Parcel parcel) {
            walletSource = new MoneySource.WalletSource(Parcelables.readBoolean(parcel));
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            Parcelables.writeBoolean(dest, walletSource.isAllowed());
        }

        public MoneySource.WalletSource getWalletSource() {
            return walletSource;
        }

        public static final Creator<WalletSourceParcelable> CREATOR =
                new Creator<WalletSourceParcelable>() {
                    @Override
                    public WalletSourceParcelable createFromParcel(Parcel source) {
                        return new WalletSourceParcelable(source);
                    }

                    @Override
                    public WalletSourceParcelable[] newArray(int size) {
                        return new WalletSourceParcelable[size];
                    }
                };
    }

    private static class CardSourceParcelable implements Parcelable {

        private final MoneySource.CardSource cardSource;

        public CardSourceParcelable(MoneySource.CardSource cardSource) {
            this.cardSource = cardSource;
        }

        private CardSourceParcelable(Parcel parcel) {
            WalletSourceParcelable walletSourceParcelable =
                    parcel.readParcelable(WalletSourceParcelable.class.getClassLoader());
            assert walletSourceParcelable != null : "walletSourceParcelable is null";
            cardSource = new MoneySource.CardSource(
                    walletSourceParcelable.getWalletSource().isAllowed(),
                    Parcelables.readBoolean(parcel), parcel.readString(), parcel.readString());
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeParcelable(new WalletSourceParcelable(cardSource), flags);
            Parcelables.writeBoolean(dest, cardSource.isCscRequired());
            dest.writeString(cardSource.getPanFragment());
            dest.writeString(cardSource.getType());
        }

        public MoneySource.CardSource getCardSource() {
            return cardSource;
        }

        public static final Creator<CardSourceParcelable> CREATOR =
                new Creator<CardSourceParcelable>() {
                    @Override
                    public CardSourceParcelable createFromParcel(Parcel source) {
                        return new CardSourceParcelable(source);
                    }

                    @Override
                    public CardSourceParcelable[] newArray(int size) {
                        return new CardSourceParcelable[size];
                    }
                };
    }
}
