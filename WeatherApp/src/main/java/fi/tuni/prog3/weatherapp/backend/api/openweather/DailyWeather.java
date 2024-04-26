package fi.tuni.prog3.weatherapp.backend.api.openweather;

import com.google.gson.Gson;

import fi.tuni.prog3.weatherapp.backend.api.general.RequestMethod;
import fi.tuni.prog3.weatherapp.backend.api.openweather.callables.CityNameCallable;
import fi.tuni.prog3.weatherapp.backend.api.openweather.callables.LatLonCallable;
import fi.tuni.prog3.weatherapp.backend.api.openweather.callables.ZipCodeCallable;
import fi.tuni.prog3.weatherapp.backend.api.openweather.JSON_OBJs.*;

import java.util.List;

/**
 * A structure that holds functionality for getting the daily weather forecast from OpenWeather.
 *
 * @author Joonas Tuominen
 */
public class DailyWeather {
    /**
     * Record that stores the following:
     * @param id The city id
     * @param name THe city name
     * @param coord The coordinates of the city
     * @param country The country of the city
     * @param population The population of the city
     * @param timezone The timezone of the city
     */
    public record CityStats(long id, String name, Coord coord, String country, long population, int timezone){}

    /**
     * Record that stores the following:
     * @param day The average temperature during at day
     * @param min The day's minimum temperature
     * @param max The day's maximum temperature
     * @param night The average temperature during at night
     * @param eve The average temperature in the evening
     * @param morn The average temperature in the morning
     */
    public record Temperature(double day, double min, double max, double night, double eve, double morn){}

    /**
     * Record that stores the following:
     * @param day The feels like temperature during the day
     * @param night The feels like temperature during the night
     * @param eve The feels like temperature during the evening
     * @param morn The feels like temperature during the morning
     */
    public record FeelsLike(double day, double night, double eve, double morn){}

    /**
     * Record that stores the following:
     * @param dt Time of data forecasted
     * @param sunrise Time of sunrise at the location
     * @param sunset Time of sunset at the location
     * @param temp The temperatures at the location
     * @param feels_like The feels like temperatures at the location
     * @param pressure The pressure at the location
     * @param humidity The humidity at the location
     * @param weather The weather conditions for the day
     * @param speed The wind speed at the location
     * @param deg The wind direction at the location
     * @param gust The wind gust speed at the location
     * @param clouds The cloud coverage at the location
     * @param pop The probability of rain at the location
     * @param rain The amount of rain (mm) at the location
     * @param snow The amount of snow (mm) at the location
     */
    public record WeatherComplete(long dt, long sunrise, long sunset, Temperature temp, FeelsLike feels_like,
                                  int pressure, int humidity, List<Weather> weather, double speed, int deg, double gust,
                                  int clouds, double pop, double rain, double snow) {}

    /**
     * Record that stores the following:
     * @param city The city stats of the query
     * @param cod An internal parameter
     * @param message An internal parameter
     * @param cnt The number of days returned
     * @param list A list of weather descriptions
     */
    public record DailyWeatherObj(CityStats city, String cod, double message, int cnt, List<WeatherComplete> list){
        /**
         * A static method for forming a DailyWeatherObj from a json String
         * @param json A string containing json data that forms a DailyWeatherObj.
         * @return The formed DailyWeatherObj.
         */
        @FromJson
        public static DailyWeatherObj fromJson(String json) {
            Gson gson = new Gson();
            return gson.fromJson(json, DailyWeatherObj.class);
        }
    }

    /**
     * A structure holding all the URLs used by the daily weather callables.
     */
    public static class URLs {
        public static final String WEATHER_LAT_LON = "https://api.openweathermap.org/data/2.5/forecast/daily?lat={lat}&lon={lon}&appid={API key}";
        public static final String WEATHER_CITY_NAME = "https://api.openweathermap.org/data/2.5/forecast/daily?q={city name}&appid={API key}";
        public static final String WEATHER_ZIP_CODE = "https://api.openweathermap.org/data/2.5/forecast/daily?zip={zip code}&appid={API key}";
    }

    /**
     * A structure containing all the highest level Callables for forming daily weather callables.
     */
    public static class Callables {
        /**
         * A class for getting the daily weather at a given latitude and longitude.
         */
        @RequestMethod(method = "GET")
        public static class DailyWeatherLatLonCallable extends LatLonCallable {
            /**
             * Construct a DailyWeatherLatLonCallable that gets the daily weather at a given latitude and longitude.
             * @param lat The latitude we want to set.
             * @param lon The longitude we want to set.
             */
            public DailyWeatherLatLonCallable(double lat, double lon) { super(URLs.WEATHER_LAT_LON, lat, lon); }
        }

        /**
         * A class for getting the daily weather for a given city.
         */
        @RequestMethod(method = "GET")
        public static class DailyWeatherCityNameCallable extends CityNameCallable {
            /**
             * Construct a DailyWeatherCityNameCallable that gets the daily weather for a given city.
             * @param cityName The city we want to query the weather for.
             */
            public DailyWeatherCityNameCallable(String cityName) { super(URLs.WEATHER_CITY_NAME, cityName); }
        }

        /**
         * A class for getting the daily weather at a given zip code.
         */
        @RequestMethod(method = "GET")
        public static class DailyWeatherZipCodeCallable extends ZipCodeCallable {
            /**
             * Construct a DailyWeatherZipCodeCallable that gets the daily weather at a given zip code.
             * @param zipCode The zip code where we want to get the weather at.
             */
            public DailyWeatherZipCodeCallable(int zipCode) {
                super(URLs.WEATHER_ZIP_CODE, zipCode);
            }
        };
    }
}
