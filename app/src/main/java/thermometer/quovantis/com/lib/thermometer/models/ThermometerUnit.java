package thermometer.quovantis.com.lib.thermometer.models;

public enum ThermometerUnit {
    CELSIUS((byte) 0),
    FAHRENHEIT((byte) 1);

    private final byte value;

    ThermometerUnit(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }

    /**
     * Method to get ThermometerUnit for a value
     *
     * @param value thermometer unit value
     * @return ThermometerUnit mapped with corresponding value else default FAHRENHEIT will
     * be returned
     */
    public static ThermometerUnit getEnumForValue(byte value) {
        return value == 0 ? CELSIUS : FAHRENHEIT;
    }
}
