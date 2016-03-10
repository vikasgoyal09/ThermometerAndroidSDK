package thermometer.quovantis.com.lib.bluetooth;

import android.bluetooth.BluetoothDevice;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static thermometer.quovantis.com.lib.thermometer.ThermometerConstants.UUIDS.*;

@RunWith(RobolectricGradleTestRunner.class)
@Config(sdk = 18)
public class BluetoothManagerTest extends TestCase {

    private BluetoothManager mManager;
    private BLECharChangeListener mListener;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        ManagerConfig managerConfig = new ManagerConfig()
                .setDeviceUUID(MY_UUID)
                .setCharactristicUUID(CHARACTERISTIC_ID)
                .setCharConfigUUID(CLIENT_CONFIG_ID)
                .setWriteCharUUID(WRITE_CHARACTERISTIC_ID);
        mManager = BluetoothManager.getInstance(RuntimeEnvironment.application, managerConfig);
        mManager.initService();
        mManager.scan(true);
        mListener = mock(BLECharChangeListener.class);
        mManager.setCallbackListener(mListener);

    }

    @After
    public void tearDown() throws Exception {
        mManager.disconnect();
        mManager.destroyService();
        mManager = null;
    }

    public void testConnect() throws Exception {
        verify(mListener).onDeviceStateChanged(null);
        List<BluetoothDevice> devices = mManager.getDevices();
        if (null != devices && !devices.isEmpty()) {
            boolean connect = mManager.connect(devices.get(0));
            Assert.assertFalse("Connection with device not established", connect);
        }
    }

    public void testSendCommands() throws Exception {
        List<BluetoothDevice> devices = mManager.getDevices();
        if (null != devices && !devices.isEmpty()) {
            boolean connect = mManager.connect(devices.get(0));
            Assert.assertFalse("Connection with device not established", connect);
            byte[] cmd = new byte[]{(byte) 0xF5, 0x10, 0, 0, (byte) 0xFF};
            mManager.sendCommands(cmd);
            verify(mListener).onMessageReceived(cmd);
        }
    }
}