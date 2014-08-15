package ru.yandex.money.android.parcelables;

import android.os.Parcel;
import android.os.Parcelable;

import com.yandex.money.api.methods.RequestExternalPayment;
import com.yandex.money.api.model.Error;

import java.math.BigDecimal;

import ru.yandex.money.android.utils.Parcelables;

/**
 * @author vyasevich
 */
public class RequestExternalPaymentParcelable implements Parcelable {

    private final RequestExternalPayment rep;

    public RequestExternalPaymentParcelable(RequestExternalPayment rep) {
        this.rep = rep;
    }

    private RequestExternalPaymentParcelable(Parcel parcel) {
        RequestExternalPayment.Status status = (RequestExternalPayment.Status) parcel.readSerializable();
        Error error = (Error) parcel.readSerializable();
        String requestId = parcel.readString();
        BigDecimal bigDecimal = Parcelables.readBigDecimal(parcel);
        rep = new RequestExternalPayment(status, error, requestId,
                bigDecimal == null ? null : bigDecimal, parcel.readString());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(rep.getStatus());
        dest.writeSerializable(rep.getError());
        dest.writeString(rep.getRequestId());
        Parcelables.writeBigDecimal(dest, rep.getContractAmount());
        dest.writeString(rep.getTitle());
    }

    public RequestExternalPayment getRequestExternalPayment() {
        return rep;
    }

    public static final Creator<RequestExternalPaymentParcelable> CREATOR =
            new Creator<RequestExternalPaymentParcelable>() {
                @Override
                public RequestExternalPaymentParcelable createFromParcel(Parcel source) {
                    return new RequestExternalPaymentParcelable(source);
                }

                @Override
                public RequestExternalPaymentParcelable[] newArray(int size) {
                    return new RequestExternalPaymentParcelable[size];
                }
            };
}
