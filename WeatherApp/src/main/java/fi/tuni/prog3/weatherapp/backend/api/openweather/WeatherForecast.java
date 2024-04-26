package fi.tuni.prog3.weatherapp.backend.api.openweather;

import com.google.gson.Gson;
import fi.tuni.prog3.weatherapp.backend.api.general.RequestMethod;
import fi.tuni.prog3.weatherapp.backend.api.openweather.JSON_OBJs.*;
import fi.tuni.prog3.weatherapp.backend.api.openweather.callables.CityNameCallable;
import fi.tuni.prog3.weatherapp.backend.api.openweather.callables.LatLonCallable;
import fi.tuni.prog3.weatherapp.backend.api.openweather.callables.ZipCodeCallable;

import java.util.List;
import java.util.Map;


/**
 * A structure that holds functionality for getting the weather forecast from OpenWeather.
 *
 * @author Joonas Tuominen
 */
public class WeatherForecast {
    /**
     * Record storing the following:
     * @param temp The temperature at the location
     * @param feels_like The feels like temperature
     * @param temp_min The minimum temperature for the period
     * @param temp_max The maximum temperature for the period
     * @param pressure The pressure at the location (sea level)
     * @param sea_level The pressure at sea level at the location
     * @param grnd_level The pressure at the ground level at the location
     * @param humidity The humidity percentage
     * @param temp_kf Internal parameter
     */
    public record Stats(double temp, double feels_like, double temp_min, double temp_max, int pressure, int sea_level,
                         int grnd_level, int humidity, double temp_kf){}

    /**
     * Record that stores the following:
     * @param speed The wind speed
     * @param deg The wind direction
     * @param gust The wind gust speed
     */
    public record Wind(double speed, int deg, double gust){}

    /**
     * Record that stores the part of day
     * @param pod "d" id day and "n" if night
     */
    public record PartOfDay(String pod){}

    /**
     * Record that stores the following:
     * @param dt The time of measuring, unix, UTC
     * @param main The main temperatures
     * @param weather List of length 1 containing the weather description
     * @param clouds The cloud stats
     * @param wind The wind stats
     * @param visibility The visibility
     * @param pop The probability of rain
     * @param rain Rain volume for the last 3 hours (mm)
     * @param sys Part of day
     * @param dt_txt Time of data forecasted, ISO, UTC
     */
    public record WeatherState(long dt, Stats main, List<Weather> weather, Clouds clouds, Wind wind, int visibility,
                                double pop, Map<String, Double> rain, PartOfDay sys, String dt_txt){}

    /**
     * Record that stores the following:
     * @param id City id
     * @param name City name
     * @param coord The city's coordinates
     * @param country The country of the city
     * @param population The population of the city
     * @param timezone The time zone of the city
     * @param sunrise The sunrise time at the city
     * @param sunset The sunset time at the city
     */
    public record CityStats(long id, String name, Coord coord, String country, long population, int timezone,
                             long sunrise, long sunset){}

    /**
     * Record that stores the following:
     * @param cod The response code
     * @param message An internal parameter
     * @param cnt The number of entries in the list
     * @param list The list of weather states
     * @param city The city stats of the query
     */
    public record WeatherForecastObj(String cod, int message, int cnt, List<WeatherState> list, CityStats city){
        @FromJson
        public static WeatherForecastObj fromJson(String json) {
            Gson gson = new Gson();
            return gson.fromJson(json, WeatherForecastObj.class);
        }
    }

    /**
     * A structure holding all the URLs used by the weather forecast callables.
     */
    public static class URLs {
        public static final String WEATHER_LAT_LON = "https://api.openweathermap.org/data/2.5/forecast?lat={lat}&lon={lon}&appid={API key}";
        public static final String WEATHER_CITY_NAME = "https://api.openweathermap.org/data/2.5/forecast?q={city name}&appid={API key}";
        public static final String WEATHER_ZIP_CODE = "https://api.openweathermap.org/data/2.5/forecast?zip={zip code}&appid={API key}";
    }

    /**
     * A structure containing all the highest level Callables for forming weather forecast callables.
     */
    public static class Callables {
        /**
         * A class for getting the daily weather at a given latitude and longitude.
         */
        @RequestMethod(method = "GET")
        public static class WeatherForecastLatLonCallable extends LatLonCallable {
            /**
             * Construct a WeatherForecastLatLonCallable that gets the weather forecast at a given latitude and longitude.
             * @param lat The latitude we want to set.
             * @param lon The longitude we want to set.
             */
            public WeatherForecastLatLonCallable(double lat, double lon) {
                super(URLs.WEATHER_LAT_LON, lat, lon);
            }
        }

        /**
         * A class for getting the daily weather for a given city.
         */
        @RequestMethod(method = "GET")
        public static class WeatherForecastCityNameCallable extends CityNameCallable {
            /**
             * Construct a WeatherForecastCityNameCallable that gets the weather forecast for a given city.
             * @param cityName The city we want to query the weather for.
             */
            public WeatherForecastCityNameCallable(String cityName) {
                super(URLs.WEATHER_CITY_NAME, cityName);
            }
        }

        /**
         * A class for getting the daily weather at a given zip code.
         */
        @RequestMethod(method = "GET")
        public static class WeatherForecastZipCodeCallable extends ZipCodeCallable {
            /**
             * Construct a WeatherForecastZipCodeCallable that gets the weather forecast at a given zip code.
             * @param zipCode The zip code where we want to get the weather at.
             */
            public WeatherForecastZipCodeCallable(int zipCode) {
                super(URLs.WEATHER_ZIP_CODE, zipCode);
            }
        }
    }
}
