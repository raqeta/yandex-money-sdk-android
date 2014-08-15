package ru.yandex.money.android.parcelables;

import android.os.Parcel;
import android.os.Parcelable;

import com.yandex.money.api.model.MoneySourceExternal;

/**
 * @author vyasevich
 */
public class MoneySourceParcelable implements Parcelable {

    private final MoneySourceExternal moneySource;

    public MoneySourceParcelable(MoneySourceExternal moneySource) {
        this.moneySource = moneySource;
    }

    private MoneySourceParcelable(Parcel parcel) {
        moneySource = new MoneySourceExternal(parcel.readString(), parcel.readString(), parcel.readString(),
                parcel.readString());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(moneySource.getType());
        dest.writeString(moneySource.getPaymentCardType());
        dest.writeString(moneySource.getPanFragment());
        dest.writeString(moneySource.getMoneySourceToken());
    }

    public MoneySourceExternal getMoneySource() {
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
}
