package fi.tuni.prog3.weatherapp.backend.api.general;

import java.util.HashMap;
import java.util.Map;

/**
 * An interface used to make API calls.
 *
 * @author Joonas Tuominen
 */
public interface iCallable {
    /**
     * A constant for a iCallable that does not contain any arguments.
     */
    public static Map<String, String> NO_ARGS = new HashMap<>();

    /**
     * A method for getting the url we want to call.
     * @return The url we want to connect to.
     */
    public String url();

    /**
     * A method that supplies a mapping from all the user arguments to their values.
     * @return A map from all the arguments in the url to their corresponding values.
     */
    public Map<String, String> args();
}
