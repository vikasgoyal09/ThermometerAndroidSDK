package thermometer.quovantis.com.lib.thermometer;

import android.content.Context;
import com.quovantis.bluetoothlibs.BLECharChangeListener;
import com.quovantis.bluetoothlibs.BluetoothManager;
import com.quovantis.bluetoothlibs.DeviceItem;
import com.quovantis.bluetoothlibs.ManagerConfig;
import com.quovantis.common.event.EventManager;
import com.quovantis.common.event.EventTypes;
import thermometer.quovantis.com.lib.thermometer.models.ThermometerMode;
import thermometer.quovantis.com.lib.thermometer.models.ThermometerReading;
import thermometer.quovantis.com.lib.thermometer.models.ThermometerUnit;

import java.util.ArrayList;
import java.util.List;

import static thermometer.quovantis.com.lib.thermometer.ThermometerConstants.UUIDS.*;

/**
 * ThermometerManager manager will be responsible for managing
 * all action and callback from thermometer bluetooth device
 * <p/>
 * To get  singleton instance of class use{@link ThermometerManager#getInstance(Context)} method
 * <p/>
 * To read temperature from thermometer device use {@link ThermometerManager#readTemperature()}
 * To change mode of thermometer device use {@link ThermometerManager#changeMode()}
 * To change unit of thermometer device use {@link ThermometerManager#changeUnit()}
 * <p/>
 * To receive callback for the devices register event with event manager for {@link EventTypes#EVENT_ACTION_DEVICE_STATE_CHANGED}
 * which provided {@link DeviceItem} in parameter of {@link EventManager.EventReceivedListener#onEventReceived(Object)}
 * <p/>
 * To receive callback for the readings register event with event manager for {@link EventTypes#EVENT_ACTION_TEMPERATURE_READING_RECEIVED}
 * which provided {@link ThermometerReading} in parameter of {@link EventManager.EventReceivedListener#onEventReceived(Object)}
 *
 * @see ThermometerManager#getInstance(Context)
 * @see ThermometerManager#connectThermometer(DeviceItem)
 * @see ThermometerManager#startScanForBTDevices()
 * @see ThermometerManager#readTemperature()
 * @see ThermometerManager#changeMode()
 * @see ThermometerManager#changeUnit()
 */
public class ThermometerManager {

    private BluetoothManager mBluetoothManager;
    private static ThermometerManager sInstance;
    private ThermometerReading mThermometerReading;
    private List<DeviceItem> mDeviceItems = new ArrayList<DeviceItem>(5);
    private DeviceItem mConnectedDevice;

    /**
     * Create new instance
     *
     * @param context Context
     */
    private ThermometerManager(Context context) {
        ManagerConfig managerConfig = new ManagerConfig()
                .setDeviceUUID(MY_UUID)
                .setCharactristicUUID(CHARACTERISTIC_ID)
                .setCharConfigUUID(CLIENT_CONFIG_ID)
                .setWriteCharUUID(WRITE_CHARACTERISTIC_ID);
        mDeviceItems.clear();
        mBluetoothManager = BluetoothManager.getInstance(context.getApplicationContext()
                , managerConfig);
        mBluetoothManager.setCallbackListener(new ThermometerCallback());
        mBluetoothManager.initService();
    }

    /**
     * Create Singleton instance of ThermometerManager
     *
     * @param context Context
     * @return Reference of ThermometerManager
     */
    public static ThermometerManager getInstance(Context context) {
        if (null == sInstance) {
            sInstance = new ThermometerManager(context);
        }
        return sInstance;
    }

    /**
     * Get list of visible bluetooth devices
     *
     * @return List<DeviceItem> bluetooth devices
     */
    public List<DeviceItem> getDeviceItems() {
        return mDeviceItems;
    }

    /**
     * Method for Starting scanning of bluetooth devices
     */
    public void startScanForBTDevices() {
        mBluetoothManager.scan(true);
    }

    /**
     * Method for Stopping scanning of bluetooth devices
     */
    public void stopScanForBTDevices() {
        mBluetoothManager.scan(false);
    }

    /**
     * To connect a bluetooth device provide device item
     *
     * @param deviceItem DeviceItem
     */
    public boolean connectThermometer(DeviceItem deviceItem) {
        if (null == deviceItem) {
            throw new IllegalArgumentException("DeviceItem can't be null for making connection");
        }
        mConnectedDevice = deviceItem;

        if (mBluetoothManager.connect(deviceItem.getDeviceAddress(), true)) {
            mThermometerReading = new ThermometerReading(0.0f
                    , ThermometerUnit.FAHRENHEIT, ThermometerMode.BODY);
            return true;
        }
        return false;
    }

    /**
     * Method is a command for reading temperature from Thermometer device
     * On temperature read the callback will be received in
     */
    synchronized public void readTemperature() {
        if (mConnectedDevice == null) {
            return;
        }
        mBluetoothManager.sendCommands(ThermUtils.THERM_TEMP_READ_CMD);
    }

    /**
     * Method will fire command to thermometer device for change unit
     * Unit can be either Fahrenheit or celsius the manager class knows current unit and
     * it will change it on thermometer device
     *
     * @return true if unit is changed on thermometer device else false
     */
    public boolean changeUnit() {
        if (mConnectedDevice == null) {
            return false;
        }
        if (null != mThermometerReading) {
            ThermometerUnit unit = mThermometerReading.getThermometerUnit();

            int newUnit = (unit.getValue() + 1) % 2;
            mThermometerReading.setThermometerUnit(ThermometerUnit.getEnumForValue((byte) newUnit));
            mThermometerReading.setTemperature(mThermometerReading.getThermometerUnit() == ThermometerUnit.FAHRENHEIT
                    ? ThermUtils.getFahrenheitTemperature(mThermometerReading.getTemperature())
                    : ThermUtils.getCelsiusTemperature(mThermometerReading.getTemperature()));

            byte[] thermSettings = ThermUtils.getSettings(ThermometerUnit.getEnumForValue((byte) newUnit)
                    , mThermometerReading.getThermometerMode());
            return mBluetoothManager.sendCommands(thermSettings);
        }
        return false;
    }

    /**
     * Method will fire command to thermometer device for change mode
     * Unit can be Surface, Body or Room, the manager class knows current mode and
     * it will change it on thermometer device
     *
     * @return true if mode is changed on thermometer device else false
     */
    public boolean changeMode() {
        if (mConnectedDevice == null) {
            return false;
        }
        if (null != mThermometerReading) {
            ThermometerMode mode = mThermometerReading.getThermometerMode();
            int newMode = (mode.getValue() + 1) % 2;
            mThermometerReading.setThermometerMode(ThermometerMode.getEnumForValue((byte) newMode));

            byte[] thermSettings = ThermUtils.getSettings(mThermometerReading.getThermometerUnit()
                    , ThermometerMode.getEnumForValue((byte) newMode));
            return mBluetoothManager.sendCommands(thermSettings);
        }
        return false;
    }

    /**
     * Disconnect will close connection for thermometer bluetooth device
     */
    public void disconnectBT() {
        if (mConnectedDevice == null) {
            return;
        }
        mConnectedDevice = null;
        mBluetoothManager.disconnect();
    }

    /**
     * To close the thermometer manager call this method which will
     * close all the service and release all the resources
     */
    public void close() {
        mBluetoothManager.destroyService();
        mDeviceItems.clear();
        mThermometerReading = null;
        sInstance = null;
    }

    /**
     * To get Thermometer reading
     *
     * @return ThermometerReading
     */
    public ThermometerReading getThermometerReading() {
        return mThermometerReading;
    }

    /**
     * Callback implementation for listening callback from Bluetooth manager
     * Currently callback for device state change and on message received is listen
     */
    private class ThermometerCallback implements BLECharChangeListener {
        @Override
        public void onMessageReceived(byte[] message) {
            mThermometerReading = ThermUtils.getReadings(message, mThermometerReading);
            EventManager.getInstance().broadcastEvent(EventTypes
                    .EVENT_ACTION_TEMPERATURE_READING_RECEIVED
                    , mThermometerReading);
        }

        @Override
        public void onDeviceStateChanged(DeviceItem deviceItem) {
            if (deviceItem.isDiscovered()) {
                mDeviceItems.add(deviceItem);
            } else {
                mDeviceItems.remove(deviceItem);
            }
            EventManager.getInstance().broadcastEvent(EventTypes.EVENT_ACTION_DEVICE_STATE_CHANGED
                    , deviceItem);
        }
    }
}
