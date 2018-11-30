package cc.bodyplus.health.ble.bean

import android.bluetooth.BluetoothDevice
import android.os.Parcel
import android.os.Parcelable

/**
 * Created by shihoo.wang on 2018/4/20.
 * Email shihu.wang@bodyplus.cc
 */

data class MyBleDevice(private var _id:Int=0) :  Parcelable {
    var macAddress: String? = null
    var deviceSn: String? = null
    var rssi: Int = 0
    var deviceName: String? = null
    var bluetoothDevice: BluetoothDevice? = null
    var deviceHw: String? = null
    var hasHwInfo: Boolean = false
    var isDfuStatus: Boolean = false

    constructor(parcel: Parcel) : this() {
        macAddress = parcel.readString()
        deviceSn = parcel.readString()
        rssi = parcel.readInt()
        deviceName = parcel.readString()
        bluetoothDevice = parcel.readParcelable(BluetoothDevice::class.java.classLoader)
        deviceHw = parcel.readString()
        hasHwInfo = parcel.readByte() != 0.toByte()
        isDfuStatus = parcel.readByte() != 0.toByte()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(macAddress)
        parcel.writeString(deviceSn)
        parcel.writeInt(rssi)
        parcel.writeString(deviceName)
        parcel.writeParcelable(bluetoothDevice, flags)
        parcel.writeString(deviceHw)
        parcel.writeByte(if (hasHwInfo) 1 else 0)
        parcel.writeByte(if (isDfuStatus) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MyBleDevice> {
        override fun createFromParcel(parcel: Parcel): MyBleDevice {
            return MyBleDevice(parcel)
        }

        override fun newArray(size: Int): Array<MyBleDevice?> {
            return arrayOfNulls(size)
        }
    }
}