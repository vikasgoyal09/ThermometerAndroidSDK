/**
 * @author SiYu Lo
 */
package com.quovantis.bluetoothlibs;

import android.bluetooth.*;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.Set;
import java.util.UUID;

/**
 * Bluetooth service to get bluetooth device around and provide
 * connection with those devices.
 * It use BLE for connection with bluetooth devices.
 */
@SuppressWarnings("methodUnused")
public class BluetoothService {
    private static final String TAG = "BluetoothService";

    /**
     * It is the UUID of requested service we are looking in bluetooth devices
     */
    private final UUID mUUID;
    /**
     * On the connected device if we want to use a specific character then
     * provide that characteristic id in this parameter
     */
    private final UUID mCharacteristicId;
    /**
     * In the Characteristic to get a specific descriptor provide descriptor id
     * in this field
     */
    private final UUID mClientConfigId;

    public static final int DATA_WHAT = 1;
    public static final int DEVICE_WHAT = 2;

    public static final int ERROR_WHAT = -1;
    public static final String DATA = "data";
    public static final String ERROR = "error";

    public static final String RSSI = "rssi";
    public static final int ADV_DATA_FLAG = 0x01;

    public static final int LIMITED_AND_GENERAL_DISC_MASK = 0x03;
    private BluetoothAdapter mBtAdapter = null;
    public BluetoothGatt mBluetoothGatt = null;

    private String mBluetoothDeviceAddress;

    private Handler mCallbackListener = null;
    private GattCallbacks mGattCallbacks = new GattCallbacks();
    private LeScanCallback mLeScanCallback = new LeScanCallback();
    private Context mContext;

    /**
     * Create new instance of BluetoothService
     *
     * @param context          Context
     * @param uuid             It is the UUID of requested service we are looking in bluetooth devices
     * @param characteristicId On the connected device if we want to use a specific character then provide that characteristic id in this parameter
     * @param clientConfigId   In the Characteristic to get a specific descriptor provide descriptor id in this field
     */
    BluetoothService(Context context, UUID uuid, UUID characteristicId, UUID clientConfigId) {
        mUUID = uuid;
        mContext = context;
        mCharacteristicId = characteristicId;
        mClientConfigId = clientConfigId;
        //get bluetooth manager from system service
        BluetoothManager btManager = (BluetoothManager) mContext
                .getSystemService(Context.BLUETOOTH_SERVICE);
        mBtAdapter = btManager.getAdapter();
    }

    /**
     * Setter method for handling message and device list related event handler
     *
     * @param handler Handler reference on which message will be pass
     */
    void setCallbackListener(Handler handler) {
        mCallbackListener = handler;
    }

    /**
     * Class is callback implementation for interface
     * {@link android.bluetooth.BluetoothAdapter.LeScanCallback}
     * which provide callback on scanning of new device in #onLeScan method
     * The callback will receive on background thread
     */
    private class LeScanCallback implements BluetoothAdapter.LeScanCallback {
        /**
         * Callback method on scan new device
         * {@inheritDoc}
         */
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            broadCastDevice(device, rssi, scanRecord);
        }
    }

    private void broadCastDevice(BluetoothDevice device, int rssi, byte[] scanRecord) {
        if (!checkIfBroadcastMode(scanRecord)) {
            Bundle bundle = new Bundle();
            Message msg = Message.obtain(mCallbackListener, DEVICE_WHAT);
            bundle.putParcelable(DATA, device);
            bundle.putInt(RSSI, rssi);
            msg.setData(bundle);
            msg.sendToTarget();
        } else {
            Log.i(TAG, "device =" + device + " is in Broadcast mode, hence not displaying");
        }
    }

    /**
     * GATT client callbacks
     * The callback will be received on background thread.
     */
    private class GattCallbacks extends BluetoothGattCallback {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status,
                int newState) {
            // TODO Auto-generated method stub
            Log.d(TAG, "onConnectionStateChange (" + gatt.getDevice().getAddress() + ")");
            if (newState == BluetoothProfile.STATE_CONNECTED && mBluetoothGatt != null) {
                mBluetoothGatt.discoverServices();
            }
            if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Bundle bundle = new Bundle();
                Message msg = Message.obtain(mCallbackListener, ERROR_WHAT);
                bundle.putString(DATA, gatt.getDevice().getAddress());
                bundle.putInt(ERROR, BluetoothConstants.DEVICE_SOURCE_DISCONNECTED);
                msg.setData(bundle);
                msg.sendToTarget();
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            // TODO Auto-generated method stub

            BluetoothGattCharacteristic characteristic = getBluetoothGattCharacteristic(
            );

            if (enableNotification(characteristic, true)) {
                Log.v(TAG, "enableNotification is ok...");
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                BluetoothGattCharacteristic characteristic) {
            byte[] buffer = characteristic.getValue();
            Bundle bundle = new Bundle();
            Message msg = Message.obtain(mCallbackListener, DATA_WHAT);
            bundle.putByteArray(DATA, buffer);
            msg.setData(bundle);
            msg.sendToTarget();
        }
    }

    /*
     * Broadcast mode checker API
     */
    boolean checkIfBroadcastMode(byte[] scanRecord) {
        int offset = 0;
        while (offset < (scanRecord.length - 2)) {
            int len = scanRecord[offset++];
            if (len == 0) {
                break; // Length == 0 , we ignore rest of the packet
            }

            int type = scanRecord[offset++];
            switch (type) {
                case ADV_DATA_FLAG:

                    if (len >= 2) {
                        // The usual scenario(2) and More that 2 octets scenario.
                        // Since this data will be in Little endian format, we
                        // are interested in first 2 bits of first byte
                        byte flag = scanRecord[offset];
                    /*
                     * 00000011(0x03) - LE Limited Discoverable Mode and LE
                     * General Discoverable Mode
                     */
                        return (flag & LIMITED_AND_GENERAL_DISC_MASK) <= 0;
                    } else if (len == 1) {
                        continue;// ignore that packet and continue with the rest
                    }
                default:
                    offset += (len - 1);
                    break;
            }
        }
        return false;
    }

    /**
     * To get characteristic offer by bluetooth device to provided UUID
     *
     * @return BluetoothGattCharacteristic
     */
    BluetoothGattCharacteristic getBluetoothGattCharacteristic() {
        BluetoothGattService service = mBluetoothGatt.getService(mUUID);
        if (service == null) {
            Log.e(TAG, "service not found!");
            return null;
        }
        BluetoothGattCharacteristic character = service
                .getCharacteristic(mCharacteristicId);
        if (character == null) {
            Log.e(TAG, "Characteristic not found!");
            return null;
        }
        return character;
    }

    /**
     * To listen changes from bluetooth device register for notification listener and
     * type of notification listen by the service
     *
     * @param characteristic Characteristic of bluetooth device with Current UUID
     * @param enable         true if want to enable the notification else false
     * @return is notification is enable or not
     */
    boolean enableNotification(BluetoothGattCharacteristic characteristic, boolean enable) {
        if (mBluetoothGatt == null)
            return false;
        if (!mBluetoothGatt.setCharacteristicNotification(characteristic, enable))
            return false;

        BluetoothGattDescriptor clientConfig = characteristic
                .getDescriptor(mClientConfigId);
        if (clientConfig == null)
            return false;

        if (enable) {
            Log.i(TAG, "enable notification");
            clientConfig.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        } else {
            Log.i(TAG, "disable notification");
            clientConfig.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
        }
        return mBluetoothGatt.writeDescriptor(clientConfig);
    }

    /**
     * To start or stop scanning for devices call this method it will
     * use #startLeScan and #stopLeScan method for that which takes #mLeScanCallback
     * in parameter for callback on bluetooth device found nearby
     *
     * @param start boolean value true if want to start scanning else false for stop scanning.
     */
    void scan(boolean start) {
        if (mBtAdapter == null)
            return;
        if (start) {
            Set<BluetoothDevice> devices = mBtAdapter.getBondedDevices();
            //If their is any already bounded devices with bluetooth adapter then pass it from here
            if (!devices.isEmpty()) {
                for (BluetoothDevice device : devices) {
                    broadCastDevice(device, 0, new byte[]{});
                }
            }
            if (null != mUUID) {
                mBtAdapter.startLeScan(new UUID[]{mUUID}, mLeScanCallback);
            } else {
                mBtAdapter.startLeScan(mLeScanCallback);
            }
        } else {
            mBtAdapter.stopLeScan(mLeScanCallback);

        }
    }

    /**
     * To connect a bluetooth device pass its address in parameters
     * it will first check is the connection with this device is already establish
     * then use already exists BluetoothGatt for connection else get new BluetoothGhat
     * from device.
     *
     * @param address     Bluetooth device address
     * @param autoConnect if false try to connect the bluetooth device immediately else wait for bluetooth
     *                    device availability
     * @return if device is connected then return true else false
     */
    boolean connect(final String address, boolean autoConnect) {
        if (mBtAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        // Previously connected device.  Try to reconnect.
        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress)
                && mBluetoothGatt != null) {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            return mBluetoothGatt.connect();
        }

        final BluetoothDevice device = mBtAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        mBluetoothGatt = device.connectGatt(mContext, autoConnect, mGattCallbacks);
        mBluetoothDeviceAddress = address;
        return true;
    }

    /**
     * Send data on connected device use this method which accepts
     * byte array in parameter for sending data on connecting device
     *
     * @param buffer        data byte array
     * @param writeCharUUID for write in bluetooth device character UUID
     */
    void send(byte[] buffer, UUID writeCharUUID) {
        if (mBluetoothGatt != null) {
            BluetoothGattService writeService = mBluetoothGatt.getService(mUUID);
            if (writeService == null) {
                return;
            }

            BluetoothGattCharacteristic writeCharacteristic = writeService
                    .getCharacteristic(writeCharUUID);
            if (writeCharacteristic == null) {
                return;
            }
            writeCharacteristic.setValue(buffer);
            mBluetoothGatt.writeCharacteristic(writeCharacteristic);
        }
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    void disconnect() {
        if (mBtAdapter == null || mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.disconnect();
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }
}
