package thermometer.quovantis.com.lib.thermometer;

import java.util.UUID;

public interface ThermometerConstants {
    interface UUIDS {
        UUID MY_UUID = UUID.fromString("0000fe18-0000-1000-8000-00805f9b34fb");
        UUID CHARACTERISTIC_ID = UUID.fromString("0000fe10-0000-1000-8000-00805f9b34fb");
        UUID WRITE_CHARACTERISTIC_ID = UUID.fromString("0000fe11-0000-1000-8000-00805f9b34fb");
        UUID CLIENT_CONFIG_ID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    }
}

