package ru.yandex.money.android.parcelables;

import android.os.Parcel;
import android.os.Parcelable;

import com.yandex.money.model.Error;
import com.yandex.money.model.methods.ProcessExternalPayment;
import com.yandex.money.model.methods.misc.MoneySourceExternal;

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
        ProcessExternalPayment.Status status =
                (ProcessExternalPayment.Status) parcel.readSerializable();
        Error error = (Error) parcel.readSerializable();
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
        dest.writeSerializable(pep.getStatus());
        dest.writeSerializable(pep.getError());
        dest.writeString(pep.getAcsUri());
        Parcelables.writeStringMap(dest, pep.getAcsParams());
        writeMoneySource(dest, flags);
        Parcelables.writeNullableLong(dest, pep.getNextRetry());
        dest.writeString(pep.getInvoiceId());
    }

    private void writeMoneySource(Parcel dest, int flags) {
        MoneySourceExternal moneySource = pep.getMoneySource();
        MoneySourceParcelable parcelable = moneySource == null ? null :
                new MoneySourceParcelable(moneySource);
        Parcelables.writeNullableParcelable(dest, parcelable, flags);
    }

    private MoneySourceExternal readMoneySource(Parcel parcel) {
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
