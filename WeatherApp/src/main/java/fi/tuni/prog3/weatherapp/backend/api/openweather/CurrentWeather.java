package fi.tuni.prog3.weatherapp.backend.api.openweather;

import com.google.gson.Gson;
import fi.tuni.prog3.weatherapp.backend.api.RequestMethod;
import fi.tuni.prog3.weatherapp.backend.api.openweather.JSON_OBJs.*;
import fi.tuni.prog3.weatherapp.backend.api.openweather.callables.CityNameCallable;
import fi.tuni.prog3.weatherapp.backend.api.openweather.callables.LatLonCallable;
import fi.tuni.prog3.weatherapp.backend.api.openweather.callables.ZipCodeCallable;

import java.util.List;

public class CurrentWeather {
    public record StatsCurrent(double temp, int pressure, int humidity, double temp_min, double temp_max){};
    public record Wind(double speed, int deg){};
    public record SysInfo(int type, int id, double message, String country, long sunrise, long sunset){};
    public record JSON_OBJ(Coord coord, List<Weather> weather, String base, StatsCurrent main, int visibility, Wind wind,
                           Clouds clouds, long dt, SysInfo sys, long id, String name, int cod){};
    public static JSON_OBJ fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, JSON_OBJ.class);
    }
    public static class URLs {
        public static final String WEATHER_LAT_LON = "https://api.openweathermap.org/data/2.5/weather?lat={lat}&lon={lon}&appid={API key}";
        public static final String WEATHER_CITY_NAME = "https://api.openweathermap.org/data/2.5/weather?q={city name}&appid={API key}";
        public static final String WEATHER_ZIP_CODE = "https://api.openweathermap.org/data/2.5/weather?zip={zip code}&appid={API key}";
    }
    public static class Callables {
        @RequestMethod(method = "GET")
        public static class CurrentWeatherLatLonCallable extends LatLonCallable {
            public CurrentWeatherLatLonCallable(double lat, double lon) {
                super(URLs.WEATHER_LAT_LON, lat, lon);
            }
        };
        @RequestMethod(method = "GET")
        public static class CurrentWeatherCityNameCallable extends CityNameCallable {
            public CurrentWeatherCityNameCallable(String cityName) {
                super(URLs.WEATHER_CITY_NAME, cityName);
            }
        };
        @RequestMethod(method = "GET")
        public static class CurrentWeatherZipCodeCallable extends ZipCodeCallable {
            public CurrentWeatherZipCodeCallable(int zipCode) {
                super(URLs.WEATHER_ZIP_CODE, zipCode);
            }
        };
    }
}
