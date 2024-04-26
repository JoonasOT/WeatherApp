package fi.tuni.prog3.weatherapp.backend.api.openweather.callables;


import java.util.Map;

/**
 * A class that forms a BaseCallable for targeting calls to a specific city. Also provides a method for specifying a
 * target country code as well.
 *
 * @author Joonas Tuominen
 */
public class CityNameCallable extends BaseCallable {
    /**
     * Forms a CityNameCallable with the given url and binds the cityName to this request.
     * @param url The url we want to form the connection to.
     * @param cityName The city name we want to use in the request.
     */
    public CityNameCallable(String url, String cityName) {
        super(url, Map.of("{city name}", cityName));
    }

    /**
     * A method for adding a country code to accompany the city name.
     * @param code The country code of the country.
     * @return This CityNameCallable after this procedure.
     */
    public CityNameCallable addCountryCode(String code) {
        args.put("{city name}", args.get("{city name}") + "," + code);
        return this;
    }
}
