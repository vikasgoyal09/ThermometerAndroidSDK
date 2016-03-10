package thermometer.quovantis.com.lib.thermometer;

import com.quovantis.bluetoothlibs.DeviceItem;
import com.quovantis.common.event.EventManager;
import com.quovantis.common.event.EventTypes;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.List;

@RunWith(RobolectricGradleTestRunner.class)
@Config(sdk = 18)
public class ThermometerManagerTest extends TestCase {


    private ThermometerManager mThermometerManager;
    private EventManager.EventReceivedListener mListener;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        mThermometerManager = ThermometerManager.getInstance(RuntimeEnvironment.application);
        mListener = Mockito.mock(EventManager.EventReceivedListener.class);
        mThermometerManager.startScanForBTDevices();
        EventManager.getInstance().registerForEvent(EventTypes.EVENT_ACTION_DEVICE_STATE_CHANGED, mListener);
    }

    @After
    public void tearDown() throws Exception {
        mThermometerManager.close();
        mThermometerManager.stopScanForBTDevices();
        EventManager.getInstance().unregisterReceiver(EventTypes.EVENT_ACTION_DEVICE_STATE_CHANGED, mListener);
        mThermometerManager = null;
    }

    public void testReadTemperature() throws Exception {
        List<DeviceItem> deviceItems = mThermometerManager.getDeviceItems();
        if (!deviceItems.isEmpty()) {
            mThermometerManager.connectBT(deviceItems.get(0));
            mThermometerManager.readTemperature();
        }
    }

    public void testChangeUnit() throws Exception {
        List<DeviceItem> deviceItems = mThermometerManager.getDeviceItems();
        if (!deviceItems.isEmpty()) {
            mThermometerManager.connectBT(deviceItems.get(0));
            mThermometerManager.changeUnit();
        }
    }

    public void testChangeMode() throws Exception {
        List<DeviceItem> deviceItems = mThermometerManager.getDeviceItems();
        if (!deviceItems.isEmpty()) {
            mThermometerManager.connectBT(deviceItems.get(0));
            mThermometerManager.changeMode();
        }
    }
}