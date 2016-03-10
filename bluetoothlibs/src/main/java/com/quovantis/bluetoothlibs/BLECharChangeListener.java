package com.quovantis.bluetoothlibs;

/**
 * Callback listener listing changes in bluetooth devices state and
 * changes in characteristics of bluetooth devices
 * <p/>
 * On Change of Bluetooth devices state callback will received in {@link BLECharChangeListener#onDeviceStateChanged(DeviceItem)}
 * <p/>
 * And if their is change in Characteristics of bluetooth device like if their is a message received
 * or data output from bluetooth device then call back will received in {@link BLECharChangeListener#onMessageReceived(byte[])}
 */
public interface BLECharChangeListener {
    /**
     * Method will be called on received some message from bluetooth device or if their is
     * some changes in characteristics of bluetooth deice
     *
     * @param message message will received in byte array format
     */
    public void onMessageReceived(byte[] message);

    /**
     * Method will be called in case of changes in bluetooth devices state
     * like if any device is connected or disconnected.
     *
     * @param deviceItem  DeviceItem if device is connected then complete object and in case of device is disconnected
     *                    then it will give only device address in DeviceItem reference
     */
    public void onDeviceStateChanged(DeviceItem deviceItem);
}
