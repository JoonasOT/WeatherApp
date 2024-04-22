package fi.tuni.prog3.weatherapp.backend.api.openweather;

import com.google.gson.Gson;
import fi.tuni.prog3.weatherapp.backend.api.general.RequestMethod;
import fi.tuni.prog3.weatherapp.backend.api.openweather.callables.CityNameCallable;
import fi.tuni.prog3.weatherapp.backend.api.openweather.callables.LatLonCallable;
import fi.tuni.prog3.weatherapp.backend.api.openweather.callables.ZipCodeCallable;
import fi.tuni.prog3.weatherapp.backend.api.openweather.JSON_OBJs.*;

import java.util.List;

public class DailyWeather {
    public record CityStats(long id, String name, Coord coord, String country, long population, int timezone){}
    public record Temperature(double day, double min, double max, double night, double eve, double morn){}
    public record FeelsLike(double day, double night, double eve, double morn){}
    public record WeatherComplete(long dt, long sunrise, long sunset, Temperature temp, FeelsLike feels_like,
                                  int pressure, int humidity, List<Weather> weather, double speed, int deg, double gust,
                                  int clouds, double pop, double rain, double snow) {}
    public record DailyWeatherObj(CityStats city, String cod, double message, int cnt, List<WeatherComplete> list){
        @FromJson
        public static DailyWeatherObj fromJson(String json) {
            Gson gson = new Gson();
            return gson.fromJson(json, DailyWeatherObj.class);
        }
    }

    public static class URLs {
        public static final String WEATHER_LAT_LON = "https://api.openweathermap.org/data/2.5/forecast/daily?lat={lat}&lon={lon}&appid={API key}";
        public static final String WEATHER_CITY_NAME = "https://api.openweathermap.org/data/2.5/forecast/daily?q={city name}&appid={API key}";
        public static final String WEATHER_ZIP_CODE = "https://api.openweathermap.org/data/2.5/forecast/daily?zip={zip code}&appid={API key}";
    }
    public static class Callables {
        @RequestMethod(method = "GET")
        public static class DailyWeatherLatLonCallable extends LatLonCallable {
            public DailyWeatherLatLonCallable(double lat, double lon) { super(URLs.WEATHER_LAT_LON, lat, lon); }
        };
        @RequestMethod(method = "GET")
        public static class DailyWeatherCityNameCallable extends CityNameCallable {
            public DailyWeatherCityNameCallable(String cityName) { super(URLs.WEATHER_CITY_NAME, cityName); }
        };
        @RequestMethod(method = "GET")
        public static class DailyWeatherZipCodeCallable extends ZipCodeCallable {
            public DailyWeatherZipCodeCallable(int zipCode) {
                super(URLs.WEATHER_ZIP_CODE, zipCode);
            }
        };
    }
}
