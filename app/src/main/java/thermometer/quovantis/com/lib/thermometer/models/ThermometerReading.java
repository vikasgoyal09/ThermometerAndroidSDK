package thermometer.quovantis.com.lib.thermometer.models;

/**
 * Model represent the thermometer reading
 * It use enum value for Thermometer unit in {@link ThermometerUnit} and
 * {@link ThermometerMode} for Thermometer mode
 *
 * @see ThermometerMode#getEnumForValue(byte) and
 * @see ThermometerUnit#getEnumForValue(byte)
 * the above method are use to get enum for their corresponding value
 */
public class ThermometerReading {
    /**
     * Temperature of thermometer
     */
    private float mTemperature;

    /**
     * Mapped Thermometer Unit it can be either Celsius or Fahrenheit
     */
    private ThermometerUnit mThermometerUnit;

    /**
     * Mapped Thermometer mode it can be either Body,Surface or Room
     */
    private ThermometerMode mThermometerMode;

    /**
     * Create new instance
     */
    public ThermometerReading() {
    }

    /**
     * Create new instance with parameter values
     *
     * @param temperature     Temperature
     * @param thermometerUnit Thermometer Unit
     * @param thermometerMode Thermometer mode
     */
    public ThermometerReading(float temperature, ThermometerUnit thermometerUnit
            , ThermometerMode thermometerMode) {
        mTemperature = temperature;
        mThermometerUnit = thermometerUnit;
        mThermometerMode = thermometerMode;
    }

    public float getTemperature() {
        return mTemperature;
    }

    public void setTemperature(float temperature) {
        mTemperature = temperature;
    }

    public ThermometerUnit getThermometerUnit() {
        return mThermometerUnit;
    }

    public void setThermometerUnit(ThermometerUnit thermometerUnit) {
        mThermometerUnit = thermometerUnit;
    }

    public ThermometerMode getThermometerMode() {
        return mThermometerMode;
    }

    public void setThermometerMode(ThermometerMode thermometerMode) {
        mThermometerMode = thermometerMode;
    }
}
