package fi.tuni.prog3.weatherapp.backend.api.openweather;

import com.google.gson.Gson;

import fi.tuni.prog3.weatherapp.backend.api.general.RequestMethod;
import fi.tuni.prog3.weatherapp.backend.api.openweather.callables.CityNameCallable;
import fi.tuni.prog3.weatherapp.backend.api.openweather.callables.LatLonCallable;
import fi.tuni.prog3.weatherapp.backend.api.openweather.callables.ZipCodeCallable;

import java.util.List;


/**
 * A structure that holds functionality for getting the hourly weather forecast from OpenWeather.
 *
 * @author Joonas Tuominen
 */
public class HourlyForecast {
    /**
     * Record that stores the following:
     * @param city The city stats at the location
     * @param cod The code of the call
     * @param message An internal parameter
     * @param cnt The count of weather states returned
     * @param list The list of weather states
     */
    public record HourlyForecastObj(WeatherForecast.CityStats city, String cod, double message, int cnt, List<WeatherForecast.WeatherState> list){
        /**
         * A static method for forming a HourlyForecastObj from a json String
         * @param json A string containing json data that forms a HourlyForecastObj.
         * @return The formed HourlyForecastObj.
         */
        @FromJson
        public static HourlyForecastObj fromJson(String json) {
            Gson gson = new Gson();
            return gson.fromJson(json, HourlyForecastObj.class);
        }
    }

    /**
     * A structure holding all the URLs used by the hourly weather forecast callables.
     */
    public static class URLs {
        public static final String WEATHER_LAT_LON = "https://api.openweathermap.org/data/2.5/forecast/hourly?lat={lat}&lon={lon}&appid={API key}";
        public static final String WEATHER_CITY_NAME = "https://api.openweathermap.org/data/2.5/forecast/hourly?q={city name}&appid={API key}";
        public static final String WEATHER_ZIP_CODE = "https://api.openweathermap.org/data/2.5/forecast/hourly?zip={zip code}&appid={API key}";
    }

    /**
     * A structure containing all the highest level Callables for forming hourly weather forecast callables.
     */
    public static class Callables {
        /**
         * A class for getting the daily weather at a given latitude and longitude.
         */
        @RequestMethod(method = "GET")
        public static class HourlyWeatherForecastLatLonCallable extends LatLonCallable {
            /**
             * Construct a HourlyWeatherForecastLatLonCallable that gets the hourly weather forecast at a given latitude and longitude.
             * @param lat The latitude we want to set.
             * @param lon The longitude we want to set.
             */
            public HourlyWeatherForecastLatLonCallable(double lat, double lon) {
                super(URLs.WEATHER_LAT_LON, lat, lon);
            }
        }

        /**
         * A class for getting the daily weather for a given city.
         */
        @RequestMethod(method = "GET")
        public static class HourlyWeatherForecastCityNameCallable extends CityNameCallable {
            /**
             * Construct a HourlyWeatherForecastCityNameCallable that gets the hourly weather forecast for a given city.
             * @param cityName The city we want to query the weather for.
             */
            public HourlyWeatherForecastCityNameCallable(String cityName) {
                super(URLs.WEATHER_CITY_NAME, cityName);
            }
        }

        /**
         * A class for getting the daily weather at a given zip code.
         */
        @RequestMethod(method = "GET")
        public static class HourlyWeatherForecastZipCodeCallable extends ZipCodeCallable {
            /**
             * Construct a HourlyWeatherForecastZipCodeCallable that gets the hourly weather forecast at a given zip code.
             * @param zipCode The zip code where we want to get the weather at.
             */
            public HourlyWeatherForecastZipCodeCallable(int zipCode) {
                super(URLs.WEATHER_ZIP_CODE, zipCode);
            }
        };
    }
}
