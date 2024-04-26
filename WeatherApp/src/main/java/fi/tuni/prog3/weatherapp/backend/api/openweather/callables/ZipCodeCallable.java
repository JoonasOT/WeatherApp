package fi.tuni.prog3.weatherapp.backend.api.openweather.callables;

import java.util.Map;

/**
 * A class that forms a BaseCallable for targeting calls to a Zip code. Also provides a method for specifying the
 * country want target.
 *
 * @author Joonas Tuominen
 */
public class ZipCodeCallable extends BaseCallable {
    /**
     * Forms a ZipCodeCallable with the given url and binds the given zipCode to this url call.
     * @param url The url we want to call.
     * @param zipCode The zip code we want to bind to this call.
     */
    public ZipCodeCallable(String url, int zipCode) {
        super(url, Map.of("{zip code}", Integer.toString(zipCode)));
    }
    /**
     * A method for adding a country code to accompany the zip code.
     * @param code The country code of the country.
     * @return This CityNameCallable after this procedure.
     */
    public ZipCodeCallable addCountryCode(String code) {
        args.put("{zip code}", args.get("{zip code}") + "," + code);
        return this;
    }
}
