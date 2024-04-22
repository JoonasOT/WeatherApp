package fi.tuni.prog3.weatherapp.backend.api.openweather;

import com.google.gson.Gson;
import fi.tuni.prog3.weatherapp.backend.api.general.RequestMethod;
import fi.tuni.prog3.weatherapp.backend.api.general.iCallable;
import fi.tuni.prog3.weatherapp.backend.database.cities.Cities;

import java.util.Map;

public class Geocoder {
    public record CityObj(String name, Map<String, String> local_names, double lat, double lon, String country, String state){}
    public record GeocoderObj(CityObj[] cities){
        @FromJson
        public static GeocoderObj fromJson(String json) {
            Gson gson = new Gson();
            return new GeocoderObj(gson.fromJson(json, CityObj[].class));
        }
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
