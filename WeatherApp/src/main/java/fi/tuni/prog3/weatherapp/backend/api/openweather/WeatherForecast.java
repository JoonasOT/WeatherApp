package fi.tuni.prog3.weatherapp.backend.api.openweather;

import com.google.gson.Gson;
import fi.tuni.prog3.weatherapp.backend.api.general.RequestMethod;
import fi.tuni.prog3.weatherapp.backend.api.openweather.JSON_OBJs.*;
import fi.tuni.prog3.weatherapp.backend.api.openweather.callables.CityNameCallable;
import fi.tuni.prog3.weatherapp.backend.api.openweather.callables.LatLonCallable;
import fi.tuni.prog3.weatherapp.backend.api.openweather.callables.ZipCodeCallable;

import java.util.List;
import java.util.Map;

public class WeatherForecast {
    public record Stats(double temp, double feels_like, double temp_min, double temp_max, int pressure, int sea_level,
                         int grnd_level, int humidity, double temp_kf){};
    public record Wind(double speed, int deg, double gust){};
    public record PartOfDay(String pod){};
    public record WeatherState(long dt, Stats main, List<Weather> weather, Clouds clouds, Wind wind, int visibility,
                                double pop, Map<String, Double> rain, PartOfDay sys, String dt_txt){};
    public record CityStats(long id, String name, Coord coord, String country, long population, int timezone,
                             long sunrise, long sunset){};
    public record WeatherForecastObj(String cod, int message, int cnt, List<WeatherState> list, CityStats city){};
    public static WeatherForecastObj fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, WeatherForecastObj.class);
    }
    public static class URLs {
        public static final String WEATHER_LAT_LON = "https://api.openweathermap.org/data/2.5/forecast?lat={lat}&lon={lon}&appid={API key}";
        public static final String WEATHER_CITY_NAME = "https://api.openweathermap.org/data/2.5/forecast?q={city name}&appid={API key}";
        public static final String WEATHER_ZIP_CODE = "https://api.openweathermap.org/data/2.5/forecast?zip={zip code}&appid={API key}";
    }
    public static class Callables {
        @RequestMethod(method = "GET")
        public static class WeatherForecastLatLonCallable extends LatLonCallable {
            public WeatherForecastLatLonCallable(double lat, double lon) {
                super(URLs.WEATHER_LAT_LON, lat, lon);
            }
        };
        @RequestMethod(method = "GET")
        public static class WeatherForecastCityNameCallable extends CityNameCallable {
            public WeatherForecastCityNameCallable(String cityName) {
                super(URLs.WEATHER_CITY_NAME, cityName);
            }
        };
        @RequestMethod(method = "GET")
        public static class WeatherForecastZipCodeCallable extends ZipCodeCallable {
            public WeatherForecastZipCodeCallable(int zipCode) {
                super(URLs.WEATHER_ZIP_CODE, zipCode);
            }
        };
    }
}
