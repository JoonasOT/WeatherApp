package fi.tuni.prog3.weatherapp.backend.api.openweather;

import com.google.gson.Gson;
import fi.tuni.prog3.weatherapp.backend.api.general.RequestMethod;
import fi.tuni.prog3.weatherapp.backend.api.openweather.JSON_OBJs.*;
import fi.tuni.prog3.weatherapp.backend.api.openweather.callables.CityNameCallable;
import fi.tuni.prog3.weatherapp.backend.api.openweather.callables.LatLonCallable;
import fi.tuni.prog3.weatherapp.backend.api.openweather.callables.ZipCodeCallable;

import java.util.List;

/**
 * A structure that holds functionality for getting the current weather from OpenWeather.
 *
 * @author Joonas Tuominen
 */
public class CurrentWeather {
    /**
     * Record that stores the following:
     * @param temp The current temperature
     * @param pressure The current pressure
     * @param humidity The current humidity
     * @param temp_min The day's minimum temperature
     * @param temp_max The day's maximum temperature
     */
    public record StatsCurrent(double temp, int pressure, int humidity, double temp_min, double temp_max){}

    /**
     * Record the stores the following:
     * @param speed The current wind speed
     * @param deg The current wind direction
     * @param gust The current wind gust speeds
     */
    public record Wind(double speed, int deg, double gust){}

    /**
     * Record that stores the following:
     * @param type Some "internal parameter"
     * @param id Some "internal parameter"
     * @param message Some "internal parameter"
     * @param country The country code
     * @param sunrise The sunrise time unix, UTC
     * @param sunset The sunset time unix, UTC
     */
    public record SysInfo(int type, int id, double message, String country, long sunrise, long sunset){}

    /**
     * Record that stores the following:
     * @param coord The coordinates
     * @param weather List of len 1 containing the weather
     * @param base Some "internal parameter"
     * @param main The current temperature status
     * @param visibility The current visibility
     * @param wind The current wind status
     * @param clouds The current cloud status
     * @param dt The time of calculation unix, UTC
     * @param sys The system statistics
     * @param id The city id
     * @param name The city name
     * @param cod The status https code
     */
    public record CurrentWeatherObj(Coord coord, List<Weather> weather, String base, StatsCurrent main, int visibility, Wind wind,
                                    Clouds clouds, long dt, SysInfo sys, long id, String name, int cod){}

    /**
     * A static method for forming a CurrentWeatherObj from a json String
     * @param json A string containing json data that forms a CurrentWeatherObj.
     * @return The formed CurrentWeatherObj.
     */
    public static CurrentWeatherObj fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, CurrentWeatherObj.class);
    }

    /**
     * A structure holding all the URLs used by the current weather callables.
     */
    public static class URLs {
        public static final String WEATHER_LAT_LON = "https://api.openweathermap.org/data/2.5/weather?lat={lat}&lon={lon}&appid={API key}";
        public static final String WEATHER_CITY_NAME = "https://api.openweathermap.org/data/2.5/weather?q={city name}&appid={API key}";
        public static final String WEATHER_ZIP_CODE = "https://api.openweathermap.org/data/2.5/weather?zip={zip code}&appid={API key}";
    }

    /**
     * A structure containing all the highest level Callables for forming current weather requests.
     */
    public static class Callables {
        /**
         * A class for getting the current weather at a given latitude and longitude.
         */
        @RequestMethod
        public static class CurrentWeatherLatLonCallable extends LatLonCallable {
            /**
             * Construct a CurrentWeatherLatLonCallable that gets the current weather at a given latitude and longitude.
             * @param lat The latitude we want to set.
             * @param lon The longitude we want to set.
             */
            public CurrentWeatherLatLonCallable(double lat, double lon) {
                super(URLs.WEATHER_LAT_LON, lat, lon);
            }
        }
        /**
         * A class for getting the current weather for a given city.
         */
        @RequestMethod
        public static class CurrentWeatherCityNameCallable extends CityNameCallable {
            /**
             * Construct a CurrentWeatherCityNameCallable that gets the current weather for a given city.
             * @param cityName The city we want to query the weather for.
             */
            public CurrentWeatherCityNameCallable(String cityName) {
                super(URLs.WEATHER_CITY_NAME, cityName);
            }
        }

        /**
         * A class for getting the current weather at a given zip code.
         */
        @RequestMethod
        public static class CurrentWeatherZipCodeCallable extends ZipCodeCallable {
            /**
             * Construct a CurrentWeatherZipCodeCallable that gets the current weather at a given zip code.
             * @param zipCode The zip code where we want to get the weather at.
             */
            public CurrentWeatherZipCodeCallable(int zipCode) {
                super(URLs.WEATHER_ZIP_CODE, zipCode);
            }
        };
    }
}
