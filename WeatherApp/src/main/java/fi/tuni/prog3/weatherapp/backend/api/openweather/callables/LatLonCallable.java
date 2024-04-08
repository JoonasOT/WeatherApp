package fi.tuni.prog3.weatherapp.backend.api.openweather.callables;

import java.util.Map;

public class LatLonCallable extends BaseCallable {
    public LatLonCallable(String url, double lat, double lon) {
        super(url, Map.of("{lat}", Double.toString(lat), "{lon}", Double.toString(lon)));
    }
}
