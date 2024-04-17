package fi.tuni.prog3.weatherapp.backend.api.openweather.callables;

import java.util.Map;

/**
 * A class that forms a BaseCallable for targeting calls to a specific latitude and longitude.
 *
 * @author Joonas Tuominen
 */
public class LatLonCallable extends BaseCallable {
    /**
     * Constructs a LatLonCallable with the given url and binds this to the given latitude and longitude.
     * @param url The url we want to call.
     * @param lat The latitude we want to target.
     * @param lon The longitude we want to target.
     */
    public LatLonCallable(String url, double lat, double lon) {
        super(url, Map.of("{lat}", Double.toString(lat), "{lon}", Double.toString(lon)));
    }
}
