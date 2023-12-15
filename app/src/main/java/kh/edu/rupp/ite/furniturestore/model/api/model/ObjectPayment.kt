package kh.edu.rupp.ite.furniturestore.model.api.model

import android.os.Parcel
import android.os.Parcelable

data class ObjectPayment(val product_id: Int, val shopping_card_id: Int, val qty: Int, val price: Double) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readDouble(),

        )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(product_id)
        parcel.writeInt(shopping_card_id)
        parcel.writeInt(qty)
        parcel.writeDouble(price)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ObjectPayment> {
        override fun createFromParcel(parcel: Parcel): ObjectPayment {
            return ObjectPayment(parcel)
        }

        override fun newArray(size: Int): Array<ObjectPayment?> {
            return arrayOfNulls(size)
        }
    }
}