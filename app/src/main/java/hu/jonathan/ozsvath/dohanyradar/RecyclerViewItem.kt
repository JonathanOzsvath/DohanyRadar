package hu.jonathan.ozsvath.dohanyradar

import android.os.Parcel
import android.os.Parcelable

data class RecyclerViewItem(
    val id: Int,
    val name: String,
    val address: String,
    val longitude: Double,
    var latitude: Double,
    val is_open: Boolean
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readByte() != 0.toByte()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(address)
        parcel.writeDouble(longitude)
        parcel.writeDouble(latitude)
        parcel.writeByte(if (is_open) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RecyclerViewItem> {
        override fun createFromParcel(parcel: Parcel): RecyclerViewItem {
            return RecyclerViewItem(parcel)
        }

        override fun newArray(size: Int): Array<RecyclerViewItem?> {
            return arrayOfNulls(size)
        }
    }
}