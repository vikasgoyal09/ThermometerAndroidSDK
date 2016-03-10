package thermometer.quovantis.com.lib.thermometer;

import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import thermometer.quovantis.com.lib.thermometer.models.ThermometerMode;
import thermometer.quovantis.com.lib.thermometer.models.ThermometerReading;
import thermometer.quovantis.com.lib.thermometer.models.ThermometerUnit;

@RunWith(RobolectricGradleTestRunner.class)
@Config(sdk = 18)
public class ThermUtilsTest extends TestCase {

    public void testGetThermSettings() throws Exception {
        byte[] thermSettings = ThermUtils.getSettings(ThermometerUnit.FAHRENHEIT, ThermometerMode.BODY);
        Assert.assertArrayEquals("Setting values are not correct for Mode BODY and Unit FAHRENHEIT"
                , new byte[]{(byte) 0xF5, 0x11, 0x02
                , ThermometerMode.BODY.getValue(), ThermometerUnit.FAHRENHEIT.getValue()
                , (byte) (ThermometerMode.BODY.getValue() ^ ThermometerUnit.FAHRENHEIT.getValue())
                , (byte) 0xFF}, thermSettings);

        thermSettings = ThermUtils.getSettings(ThermometerUnit.FAHRENHEIT, ThermometerMode.SURFACE);
        Assert.assertArrayEquals("Setting values are not correct for Mode SURFACE and Unit FAHRENHEIT"
                , new byte[]{(byte) 0xF5, 0x11, 0x02
                , ThermometerMode.SURFACE.getValue(), ThermometerUnit.FAHRENHEIT.getValue()
                , (byte) (ThermometerMode.SURFACE.getValue() ^ ThermometerUnit.FAHRENHEIT.getValue())
                , (byte) 0xFF}, thermSettings);

        thermSettings = ThermUtils.getSettings(ThermometerUnit.CELSIUS, ThermometerMode.BODY);
        Assert.assertArrayEquals("Setting values are not correct for Mode Body and Unit CELSIUS"
                , new byte[]{(byte) 0xF5, 0x11, 0x02
                , ThermometerMode.BODY.getValue(), ThermometerUnit.CELSIUS.getValue()
                , (byte) (ThermometerMode.BODY.getValue() ^ ThermometerUnit.CELSIUS.getValue())
                , (byte) 0xFF}, thermSettings);

        thermSettings = ThermUtils.getSettings(ThermometerUnit.CELSIUS, ThermometerMode.SURFACE);
        Assert.assertArrayEquals("Setting values are not correct for Mode SURFACE and Unit CELSIUS"
                , new byte[]{(byte) 0xF5, 0x11, 0x02
                , ThermometerMode.SURFACE.getValue(), ThermometerUnit.CELSIUS.getValue()
                , (byte) (ThermometerMode.SURFACE.getValue() ^ ThermometerUnit.CELSIUS.getValue())
                , (byte) 0xFF}, thermSettings);

    }

    public void testUpdateThermReadings() throws Exception {
        ThermometerReading thermometerReading = ThermUtils.getReadings(new byte[]{(byte) 0xFA, 0x10, 0x08, 0x23
                , 0x01, 0x28, 0x01, 0x06, 0x01, ThermometerMode.SURFACE.getValue()
                , ThermometerUnit.FAHRENHEIT.getValue(), 0x56, (byte) 0xFF}, null);
        Assert.assertNull(thermometerReading);

        thermometerReading = ThermUtils.getReadings(new byte[]{(byte) 0xFA, 0x10, 0x08, 0x23
                , 0x01, 0x28, 0x01, 0x06, 0x01, ThermometerMode.BODY.getValue()
                , ThermometerUnit.FAHRENHEIT.getValue(), 0x56, (byte) 0xFF}, null);
        Assert.assertNull(thermometerReading);


        thermometerReading = ThermUtils.getReadings(new byte[]{(byte) 0xFA, 0x10, 0x08, 0x23
                , 0x01, 0x28, 0x01, 0x06, 0x01, ThermometerMode.SURFACE.getValue()
                , ThermometerUnit.CELSIUS.getValue(), 0x56, (byte) 0xFF}, null);
        Assert.assertNull(thermometerReading);

        thermometerReading = ThermUtils.getReadings(new byte[]{(byte) 0xFA, 0x10, 0x08, 0x23
                , 0x01, 0x28, 0x01, 0x06, 0x01, ThermometerMode.BODY.getValue()
                , ThermometerUnit.CELSIUS.getValue(), 0x56, (byte) 0xFF}, null);
        Assert.assertNull(thermometerReading);

    }

    public void testConvertTemp() throws Exception {
        float temp = ThermUtils.getFahrenheitTemperature(37.5f);
        Assert.assertNotEquals("Converted temperature from Celsius to Fahrenheit is not correct"
                , 99.5f, temp);

        temp = ThermUtils.getCelsiusTemperature(99.5f);
        Assert.assertNotEquals("Converted temperature from Fahrenheit to Celsius is not Correct"
                , 37.5f, temp);
    }
}