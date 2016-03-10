package thermometer.quovantis.com.lib.thermometer;

import thermometer.quovantis.com.lib.thermometer.models.ThermometerMode;
import thermometer.quovantis.com.lib.thermometer.models.ThermometerReading;
import thermometer.quovantis.com.lib.thermometer.models.ThermometerUnit;

/**
 * Utility class for providing operation for Thermometer
 * it have different functionality in shape of methods
 *
 * @see #getSettings(ThermometerUnit, ThermometerMode)
 * @see #getReadings(byte[], ThermometerReading)
 * @see #getCelsiusTemperature(float)
 * @see #THERM_SETTING_START_CMD
 */
public class ThermUtils {

    private static final byte THERM_SETTING_START_CMD = (byte) 0xF5;
    private static final byte THERM_END_CMD = (byte) 0xFF;
    /**
     * Byte[] buffer command for reading temperature from thermometer device
     */
    public static final byte[] THERM_TEMP_READ_CMD = new byte[]{THERM_SETTING_START_CMD
            , 0x10, 0, 0, THERM_END_CMD};

    /**
     * Method to get thermometer setting byte buffer for the mode and unit
     * setting values
     *
     * @param unit ThermometerUnit enum which contain the thermometer different unit value
     * @param mode ThermometerMode enum which contain the thermometer different mode value
     * @return byte[] buffer with provided setting values
     */
    public static byte[] getSettings(ThermometerUnit unit, ThermometerMode mode) {
        byte checkByte = (byte) (mode.getValue() ^ unit.getValue());

        byte[] config = new byte[]{THERM_SETTING_START_CMD, 0x11, 0x02, mode.getValue()
                , unit.getValue(), checkByte, THERM_END_CMD};

        return config;
    }

    /**
     * Method to read ThermometerReading object from byte[] buffer received in thermometer
     * reading response
     *
     * @param readings           byte [] buffer of thermometer readings
     * @param thermometerReading ThermometerReading object it can be either null or old object
     * @return ThermometerReading created or updated ThermometerReading object for the received byte[] reading buffer
     */
    public static ThermometerReading getReadings(byte[] readings, ThermometerReading thermometerReading) {
        if (readings == null || readings.length < 12) {
            return thermometerReading;
        }
        if (null == thermometerReading) {
            thermometerReading = new ThermometerReading();
        }

        thermometerReading.setThermometerMode(ThermometerMode.getEnumForValue(readings[9]));
        thermometerReading.setThermometerUnit(ThermometerUnit.getEnumForValue(readings[10]));

        int i = thermometerReading.getThermometerMode() == ThermometerMode.SURFACE ? 3 : 5;

        float temp = (((readings[i] & 0xFF) + ((readings[i + 1] & 0xFF) * 256f)) / 10f);
        thermometerReading.setTemperature(thermometerReading.getThermometerUnit() == ThermometerUnit.FAHRENHEIT
                ? getFahrenheitTemperature(temp) : temp);

        return thermometerReading;
    }

    /**
     * Convert into celsius temperature from Fahrenheit
     *
     * @param temp temperature
     * @return converted temperature
     */
    public static float getCelsiusTemperature(float temp) {
        if (temp <= 0) {
            return 0;
        }
        float abs = Math.abs((temp - 32f) / 1.8f);
        return round(abs, 1);
    }

    /**
     * Convert into Fahrenheit temperature from celsius
     *
     * @param temp temperature
     * @return converted temperature
     */
    public static float getFahrenheitTemperature(float temp) {
        if (temp <= 0) {
            return 0;
        }
        float abs = Math.abs((temp * 1.8f) + 32f);
        return round(abs, 1);
    }

    public static float round(float value, int digit) {
        int roundFigure = (int) Math.pow(10, digit);
        int valueMul = Math.round(value * roundFigure);
        return (float) valueMul / (float) roundFigure;
    }
}
