package com.quovantis.bluetoothlibs;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Class will provide functionality to handle bluetooth related operation from here.
 * It will use BLE for scanning, connecting and communication between bluetooth
 * device and android app.
 * <p/>
 * To Create instance of the class use {@link BluetoothManager#getInstance(Context context, ManagerConfig)} method as the class is singleton.
 * param will take context and {@link ManagerConfig}
 * <p/>
 * {@link ManagerConfig} will provide configuration for the BluetoothManager
 * <p/>
 * To use services of Bluetooth manager first you need to call {@link BluetoothManager#initService()} method
 * which will initialize useful resources for the Bluetooth service
 */
public class BluetoothManager {
    private boolean mIsBluetoothActive = false;
    private final Context mContext;
    private ManagerConfig mManagerConfig;
    private BluetoothService mBluetoothService;
    private static BluetoothManager sInstance;
    private HashMap<String, BluetoothDevice> mDevices = new HashMap<String, BluetoothDevice>();
    private BluetoothCallbackHandler mBluetoothCallbackHandler;
    private BLECharChangeListener mCallbackListener;
    private final BluetoothStateChangesListener mListener;

    /**
     * Create new instance it is private as only singleton instance is allowed
     * which can be get using calling getInstance method
     *
     * @param context Context
     */
    protected BluetoothManager(Context context, ManagerConfig managerConfig) {
        mManagerConfig = managerConfig;
        mContext = context;

        mListener = new BluetoothStateChangesListener();
        context.registerReceiver(mListener, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));

        if (checkBluetoothEnable(context)) {
            mIsBluetoothActive = true;
            mBluetoothService = new BluetoothService(context, mManagerConfig.getDeviceUUID(),
                    mManagerConfig.getCharacteristicUUID(), mManagerConfig.getCharConfigUUID());
        }
    }

    /**
     * Get Singleton reference of class so all the child can have same state
     *
     * @param context       Context
     * @param managerConfig To provide connection provide ManagerConfig if you want to get already available instance
     *                      then pass null or config with same values
     * @return BluetoothManager instance
     */
    synchronized public static BluetoothManager getInstance(Context context, ManagerConfig managerConfig) {
        if (null == sInstance || (managerConfig != null
                && !managerConfig.equals(sInstance.mManagerConfig))) {
            sInstance = new BluetoothManager(context, managerConfig);
        }
        return sInstance;
    }

    /**
     * To get all scanned devices use this method
     *
     * @return List of all scanned devices
     */
    public List<BluetoothDevice> getDevices() {
        List<BluetoothDevice> devices = new ArrayList<BluetoothDevice>(mDevices.values());
        return devices;
    }

    /**
     * To get bluetooth device for provided adress
     *
     * @return Bluetooth device
     */
    public BluetoothDevice getDevice(String address) {
        BluetoothDevice device = mDevices.get(address);
        return device;
    }

    /**
     * Initialize resources to use bluetooth service
     */
    public void initService() {
        if (isBLEServiceAvailable()) return;
        mBluetoothCallbackHandler = new BluetoothCallbackHandler();
        mBluetoothService.setCallbackListener(mBluetoothCallbackHandler);
    }

    /**
     * Method use to start or stop scanning of devices
     *
     * @param scan true if want to start scanning for bluetooth devices else false
     */
    public void scan(boolean scan) {
        if (!checkBluetoothEnable(mContext)) {
            return;
        }
        if (null == mBluetoothCallbackHandler) {
            throw new IllegalStateException("Can't scan for device before calling #initService method");
        }
        mBluetoothService.scan(scan);
    }

    /**
     * Method use to connect with a bluetooth device
     *
     * @param device      Bluetooth device address which need to establish connection
     * @param autoConnect if false try to connect the bluetooth device immediately else wait for bluetooth
     *                    device availability
     * @return boolean true if device is connecting else false
     */
    public boolean connect(String device, boolean autoConnect) {
        if (isBLEServiceAvailable()) return false;
        if (TextUtils.isEmpty(device)) {
            throw new IllegalArgumentException("Connect device address reference can't be null in");
        }
        return mBluetoothService.connect(device, false);
    }

    /**
     * Method use to send commands on bluetooth device.
     *
     * @param commands Commands can be only byte array
     */
    public boolean sendCommands(byte[] commands) {
        if (isBLEServiceAvailable()) return false;
        mBluetoothService.send(commands,
                mManagerConfig.getWriteCharUUID());
        return true;
    }

    /**
     * Method use to disconnect from current connected bluetooth device
     */
    public void disconnect() {
        if (!mIsBluetoothActive) return;
        mBluetoothService.disconnect();
    }

    /**
     * Method use to release all resources and stop bluetooth service
     */
    public void destroyService() {
        sInstance = null;
        if (mIsBluetoothActive) {
            mBluetoothService.close();
        }
        mIsBluetoothActive = false;
        mBluetoothService = null;
        mContext.unregisterReceiver(mListener);
    }

    /**
     * Class will handle callback on device discovering
     */
    private boolean isBLEServiceAvailable() {
        if (!mIsBluetoothActive) {
            Toast.makeText(mContext, "Bluetooth service not found may be bluetooth is not on"
                    , Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }
    /**
     * Check whether bluetooth is enable or not
     *
     * @param context Context
     * @return true if it is enable else return false
     */
    private boolean checkBluetoothEnable(Context context) {
        //get bluetooth manager from system service
        android.bluetooth.BluetoothManager btManager = (android.bluetooth.BluetoothManager) context
                .getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter mBtAdapter = btManager.getAdapter();
        if (mBtAdapter == null || !mBtAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            enableBtIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(enableBtIntent);
            return false;
        }
        return true;
    }

    private class BluetoothCallbackHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            switch (msg.what) {
                case BluetoothService.ERROR_WHAT:
                    int errorType = data.getInt(BluetoothService.ERROR, -1);
                    if (-1 != errorType) {
                        switch (errorType) {
                            case BluetoothConstants.DEVICE_SOURCE_DISCONNECTED:
                                String deviceAddress = data.getString(BluetoothService.DATA);
                                if (!TextUtils.isEmpty(deviceAddress)) {
                                    mDevices.remove(deviceAddress);
                                    if (null != mCallbackListener) {
                                        DeviceItem item = new DeviceItem();
                                        item.setDeviceAddress(deviceAddress);
                                        item.setIsDiscovered(false);
                                        mCallbackListener.onDeviceStateChanged(item);
                                    }
                                }
                                break;
                        }
                    }
                    break;
                case BluetoothService.DEVICE_WHAT:
                    BluetoothDevice device = data.getParcelable(BluetoothService.DATA);
                    //check whether the device is already is exist or not
                    if (null != device && mDevices.get(device.getAddress()) == null) {
                        mDevices.put(device.getAddress(), device);
                        DeviceItem item = new DeviceItem();
                        item.setDeviceName(device.getName());
                        item.setDeviceAddress(device.getAddress());
                        item.setDeviceRSSI(data.getInt(BluetoothService.RSSI));
                        item.setIsDiscovered(true);
                        if (null != mCallbackListener) {
                            mCallbackListener.onDeviceStateChanged(item);
                        }
                    }
                    break;
                case BluetoothService.DATA_WHAT:
                    byte[] message = data.getByteArray(BluetoothService.DATA);
                    if (null != message && null != mCallbackListener) {
                        mCallbackListener.onMessageReceived(message);
                    }
                    break;
            }
        }
    }

    /**
     * Broadcast receiver for listening change in state of bluetooth
     */
    private class BluetoothStateChangesListener extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1)
                        == BluetoothAdapter.STATE_OFF) {
                    destroyService();
                } else if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1)
                        == BluetoothAdapter.STATE_ON) {
                    mIsBluetoothActive = true;
                    mBluetoothService = new BluetoothService(context, mManagerConfig.getDeviceUUID(),
                            mManagerConfig.getCharacteristicUUID(), mManagerConfig.getCharConfigUUID());
                    initService();
                }
            }

        }
    }

    /**
     * To Listen event from bluetooth manger register for callback listener here
     *
     * @param callbackListener BLECharChangeListener
     */
    public void setCallbackListener(BLECharChangeListener callbackListener) {
        mCallbackListener = callbackListener;
    }
}
