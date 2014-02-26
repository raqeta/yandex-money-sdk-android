package ru.yandex.money.android.parcelables;

import android.os.Parcel;
import android.os.Parcelable;

import com.yandex.money.model.cps.ProcessExternalPayment;
import com.yandex.money.model.cps.misc.MoneySource;

import java.util.Map;

import ru.yandex.money.android.utils.Parcelables;

/**
 * @author vyasevich
 */
public class ProcessExternalPaymentParcelable implements Parcelable {

    private final ProcessExternalPayment pep;

    public ProcessExternalPaymentParcelable(ProcessExternalPayment pep) {
        this.pep = pep;
    }

    private ProcessExternalPaymentParcelable(Parcel parcel) {
        String status = parcel.readString();
        String error = parcel.readString();
        String acsUri = parcel.readString();
        Map<String, String> acsParams = Parcelables.readStringMap(parcel);
        this.pep = new ProcessExternalPayment(status, error, acsUri, acsParams,
                readMoneySource(parcel), Parcelables.readNullableLong(parcel),
                parcel.readString());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(pep.getStatus());
        dest.writeString(pep.getError());
        dest.writeString(pep.getAcsUri());
        Parcelables.writeStringMap(dest, pep.getAcsParams());
        writeMoneySource(dest, flags);
        Parcelables.writeNullableLong(dest, pep.getNextRetry());
        dest.writeString(pep.getInvoiceId());
    }

    private void writeMoneySource(Parcel dest, int flags) {
        MoneySource moneySource = pep.getMoneySource();
        MoneySourceParcelable parcelable = moneySource == null ? null :
                new MoneySourceParcelable(moneySource);
        Parcelables.writeNullableParcelable(dest, parcelable, flags);
    }

    private MoneySource readMoneySource(Parcel parcel) {
        MoneySourceParcelable parcelable =
                (MoneySourceParcelable) Parcelables.readNullableParcelable(
                        parcel, MoneySourceParcelable.class.getClassLoader());
        return parcelable == null ? null : parcelable.getMoneySource();
    }

    public ProcessExternalPayment getProcessExternalPayment() {
        return pep;
    }

    public static final Creator<ProcessExternalPaymentParcelable> CREATOR =
            new Creator<ProcessExternalPaymentParcelable>() {
                @Override
                public ProcessExternalPaymentParcelable createFromParcel(Parcel source) {
                    return new ProcessExternalPaymentParcelable(source);
                }

                @Override
                public ProcessExternalPaymentParcelable[] newArray(int size) {
                    return new ProcessExternalPaymentParcelable[size];
                }
            };
}
