package com.quovantis.bluetoothlibs;

import android.os.Parcel;
import android.os.Parcelable;

public class DeviceItem implements Parcelable {

    private String mDeviceName;
    private String mDeviceAddress;
    private int mDeviceRSSI;
    private boolean mConnected;
    private boolean mIsDiscovered;

    public String getDeviceName() {
        return mDeviceName;
    }

    public void setDeviceName(String deviceName) {
        mDeviceName = deviceName;
    }

    public String getDeviceAddress() {
        return mDeviceAddress;
    }

    public void setDeviceAddress(String deviceAddress) {
        mDeviceAddress = deviceAddress;
    }

    public int getDeviceRSSI() {
        return mDeviceRSSI;
    }

    public void setDeviceRSSI(int deviceRSSI) {
        mDeviceRSSI = deviceRSSI;
    }

    public boolean isConnected() {
        return mConnected;
    }

    public void setConnected(boolean connected) {
        mConnected = connected;
    }

    public boolean isDiscovered() {
        return mIsDiscovered;
    }

    public void setIsDiscovered(boolean isDiscovered) {
        mIsDiscovered = isDiscovered;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mDeviceName);
        dest.writeString(mDeviceAddress);
        dest.writeInt(mDeviceRSSI);
        dest.writeInt(mConnected ? 1 : 0);
        dest.writeInt(mIsDiscovered ? 1 : 0);
    }

    public static final Creator<DeviceItem> CREATOR = new Creator<DeviceItem>() {
        @Override
        public DeviceItem createFromParcel(Parcel source) {
            DeviceItem item = new DeviceItem();
            item.mDeviceName = source.readString();
            item.mDeviceAddress = source.readString();
            item.mDeviceRSSI = source.readInt();
            item.mConnected = source.readInt() == 1;
            item.mIsDiscovered = source.readInt() == 1;
            return item;
        }

        @Override
        public DeviceItem[] newArray(int size) {
            return new DeviceItem[0];
        }
    };


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DeviceItem item = (DeviceItem) o;

        return !(mDeviceAddress != null ? !mDeviceAddress.equals(item.mDeviceAddress) : item.mDeviceAddress != null);

    }

    @Override
    public int hashCode() {
        return mDeviceAddress != null ? mDeviceAddress.hashCode() : 0;
    }
}
