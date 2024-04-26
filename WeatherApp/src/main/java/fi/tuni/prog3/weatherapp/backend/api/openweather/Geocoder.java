package fi.tuni.prog3.weatherapp.backend.api.openweather;

import com.google.gson.Gson;
import fi.tuni.prog3.weatherapp.backend.api.general.RequestMethod;
import fi.tuni.prog3.weatherapp.backend.api.general.iCallable;
import fi.tuni.prog3.weatherapp.backend.database.cities.Cities;

import java.util.Map;


/**
 * A structure that holds functionality for getting the coordinates of a city.
 *
 * @author Joonas Tuominen
 */
public class Geocoder {
    /**
     * Record that hold the following:
     * @param name The name of the found location
     * @param local_names The local names found ("lang. abbreviation" -> "name")
     * @param lat The latitude of the location
     * @param lon The longitude of the location
     * @param country The country of the location
     * @param state The state of the location
     */
    public record CityObj(String name, Map<String, String> local_names, double lat, double lon, String country, String state){}

    /**
     * Record that stores an array of CityObjs.
     * @param cities The CityObj array
     */
    public record GeocoderObj(CityObj[] cities){
        /**
         * A static method for forming a GeocoderObj from a json String
         * @param json A string containing json data that forms a GeocoderObj.
         * @return The formed GeocoderObj.
         */
        @FromJson
        public static GeocoderObj fromJson(String json) {
            Gson gson = new Gson();
            return new GeocoderObj(gson.fromJson(json, CityObj[].class));
        }
    }

    /**
     * A structure holding all the URLs used by the geocoder callables.
     */
    public static class URLs {
        public static final String GEOCODER = "http://api.openweathermap.org/geo/1.0/direct?q={city name}&limit={limit}&appid={API key}";
    }

    /**
     * A structure containing all the Geocoder callables.
     */
    public static class Callables {
        /**
         * A class for getting the coordinates of a city or cities if city.countryCode == null and limit > 1
         */
        @RequestMethod(method = "GET")
        public record GeocoderCallable(Cities.City city, int limit) implements iCallable {
            /**
             * Sets the callable url to the Geocoder url
             * @return The Geocoder url
             */
            @Override public String url() { return URLs.GEOCODER; }

            /**
             * Sets the callable args to the ones specified by GeocoderCallable
             * @return The arguments set in GeocoderCallable constructor
             */
            @Override
            public Map<String, String> args() {
                String out = city.name();
                if (city.countryCode() != null) out += "," + city().countryCode();

                return Map.of("{city name}", out, "{limit}", Integer.toString(limit));
            }
        }
    }
}
