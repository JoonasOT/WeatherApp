package fi.tuni.prog3.weatherapp.backend.api.general;

/**
 * An enum that holds all the HTTPS request response codes and their ranges (SUCCESS, CLIENT ERROR, SERVER ERROR...).
 *
 * @author Joonas Tuominen
 */
public enum HTTPS_CODE {
    INFO (100, 199),
    SUCCESS (200, 299),
    REDIRECTION (300, 399),
    CLIENT_ERROR (400, 499),
    SERVER_ERROR (500, 599);

    public final int min;
    public final int max;

    /**
     * Constructor for each value that takes the minimum value of the range and the maximum.
     * @param min The minimum value of the HTTPS code's range
     * @param max The maximum value of the HTTPS code's range
     */
    HTTPS_CODE(int min, int max) {
        this.min = min;
        this.max = max;
    }

    /**
     * A static function for forming a given value to the HTTPS code it represents
     * @param val The value we want to convert into an HTTPS code.
     * @return The HTTPS code of the value given.
     * @throws RuntimeException If the value doesn't belong to any HTTPS code range.
     */
    public static HTTPS_CODE getCode(int val) throws RuntimeException {
        for( var code : HTTPS_CODE.values() ) {
            if( code.min <= val && val <= code.max ) {
                return code;
            }
        }
        throw new RuntimeException("HTTPS code '" + Integer.toString(val) + "' is invalid!");
    }
}
