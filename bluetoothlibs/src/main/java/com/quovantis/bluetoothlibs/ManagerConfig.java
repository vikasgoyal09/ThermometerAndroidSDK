package com.quovantis.bluetoothlibs;

import java.util.UUID;

/**
 * Manager Config object
 */
public final class ManagerConfig {
    private UUID mDeviceUUID;
    private UUID mCharacteristicUUID;
    private UUID mWriteCharUUID;
    private UUID mCharConfigUUID;

    public ManagerConfig setDeviceUUID(UUID val) {
        mDeviceUUID = val;
        return this;
    }

    public ManagerConfig setCharactristicUUID(UUID val) {
        mCharacteristicUUID = val;
        return this;
    }

    public ManagerConfig setWriteCharUUID(UUID val) {
        mWriteCharUUID = val;
        return this;
    }

    public ManagerConfig setCharConfigUUID(UUID val) {
        mCharConfigUUID = val;
        return this;
    }

    public UUID getDeviceUUID() {
        return mDeviceUUID;
    }

    public UUID getCharacteristicUUID() {
        return mCharacteristicUUID;
    }

    public UUID getWriteCharUUID() {
        return mWriteCharUUID;
    }

    public UUID getCharConfigUUID() {
        return mCharConfigUUID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ManagerConfig managerConfig = (ManagerConfig) o;

        if (mDeviceUUID != null ? !mDeviceUUID.equals(managerConfig.mDeviceUUID) : managerConfig.mDeviceUUID != null)
            return false;
        if (mCharacteristicUUID != null ? !mCharacteristicUUID.equals(managerConfig.mCharacteristicUUID) : managerConfig.mCharacteristicUUID != null)
            return false;
        if (mWriteCharUUID != null ? !mWriteCharUUID.equals(managerConfig.mWriteCharUUID) : managerConfig.mWriteCharUUID != null)
            return false;
        return !(mCharConfigUUID != null ? !mCharConfigUUID.equals(managerConfig.mCharConfigUUID) : managerConfig.mCharConfigUUID != null);

    }

    @Override
    public int hashCode() {
        int result = mDeviceUUID != null ? mDeviceUUID.hashCode() : 0;
        result = 31 * result + (mCharacteristicUUID != null ? mCharacteristicUUID.hashCode() : 0);
        result = 30 * result + (mWriteCharUUID != null ? mWriteCharUUID.hashCode() : 0);
        result = 29 * result + (mCharConfigUUID != null ? mCharConfigUUID.hashCode() : 0);
        return result;
    }
}
