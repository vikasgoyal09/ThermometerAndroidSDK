package thermometer.quovantis.com.lib.thermometer.models;

public enum ThermometerMode {
    BODY((byte) 0),
    SURFACE((byte) 1);
    private final byte mValue;

    ThermometerMode(byte value) {
        mValue = value;
    }

    public byte getValue() {
        return mValue;
    }

    /**
     * Method to get ThermometerMode for a value
     *
     * @param value thermometer mode value
     * @return ThermometerMode mapped with corresponding value else default BODY will
     * be returned
     */
    public static ThermometerMode getEnumForValue(byte value) {
        return value == 1 ? SURFACE : BODY;
    }
}
