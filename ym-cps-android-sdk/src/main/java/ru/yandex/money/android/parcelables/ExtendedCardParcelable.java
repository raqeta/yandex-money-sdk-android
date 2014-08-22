package ru.yandex.money.android.parcelables;

import android.os.Parcel;
import android.os.Parcelable;

import com.yandex.money.api.model.ExternalCard;

/**
 * @author vyasevich
 */
public class ExtendedCardParcelable implements Parcelable {

    private final ExternalCard extendedCard;

    public ExtendedCardParcelable(ExternalCard extendedCard) {
        this.extendedCard = extendedCard;
    }

    private ExtendedCardParcelable(Parcel parcel) {
        extendedCard = new ExternalCard(parcel.readString(), parcel.readString(), parcel.readString(),
                parcel.readString());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(extendedCard.getPanFragment());
        dest.writeString(extendedCard.getType());
        dest.writeString(extendedCard.getFundingSourceType());
        dest.writeString(extendedCard.getMoneySourceToken());
    }

    public ExternalCard getExtendedCard() {
        return extendedCard;
    }

    public static final Creator<ExtendedCardParcelable> CREATOR =
            new Creator<ExtendedCardParcelable>() {
                @Override
                public ExtendedCardParcelable createFromParcel(Parcel source) {
                    return new ExtendedCardParcelable(source);
                }

                @Override
                public ExtendedCardParcelable[] newArray(int size) {
                    return new ExtendedCardParcelable[size];
                }
            };
}
