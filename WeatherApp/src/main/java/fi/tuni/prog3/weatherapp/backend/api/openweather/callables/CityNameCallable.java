package fi.tuni.prog3.weatherapp.backend.api.openweather.callables;


import java.util.Map;

public class CityNameCallable extends BaseCallable {
    public CityNameCallable(String url, String cityName) {
        super(url, Map.of("{city name}", cityName));
    }
    public CityNameCallable addCountryCode(String code) {
        args.put("{city name}", args.get("{city name}") + "," + code);
        return this;
    }
}
