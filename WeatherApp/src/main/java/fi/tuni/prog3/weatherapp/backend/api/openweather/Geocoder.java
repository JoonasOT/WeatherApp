package fi.tuni.prog3.weatherapp.backend.api.openweather;

import com.google.gson.Gson;
import fi.tuni.prog3.weatherapp.backend.api.RequestMethod;
import fi.tuni.prog3.weatherapp.backend.api.iCallable;
import fi.tuni.prog3.weatherapp.backend.database.cities.Cities;

import java.util.List;
import java.util.Map;

public class Geocoder {
    public record CityObj(String name, Map<String, String> local_names, double lat, double lon, String country, String state){}
    public record GeocoderObj(List<CityObj> cities){}
    public GeocoderObj fromJSON(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, GeocoderObj.class);
    }
    public static class URLs {
        public static final String GEOCODER = "http://api.openweathermap.org/geo/1.0/direct?q={city name}&limit={limit}&appid={API key}";
    }
    public static class Callables {
        @RequestMethod(method = "GET")
        public record GeocoderCallable(Cities.City city, int limit) implements iCallable {
            @Override public String url() { return URLs.GEOCODER; }
            @Override
            public Map<String, String> args() {
                String out = city.name();
                if (city.countryCode() != null) out += "," + city().countryCode();

                return Map.of("{city name}", out, "{limit}", Integer.toString(limit));
            }
        }
    }
}
