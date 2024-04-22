package fi.tuni.prog3.weatherapp.backend.api.openweather;

import com.google.gson.Gson;
import fi.tuni.prog3.weatherapp.backend.api.general.RequestMethod;
import fi.tuni.prog3.weatherapp.backend.api.openweather.callables.CityNameCallable;
import fi.tuni.prog3.weatherapp.backend.api.openweather.callables.LatLonCallable;
import fi.tuni.prog3.weatherapp.backend.api.openweather.callables.ZipCodeCallable;

import java.util.List;

public class HourlyForecast {
    public record HourlyForecastObj(WeatherForecast.CityStats city, String cod, double message, int cnt, List<WeatherForecast.WeatherState> list){
        @FromJson
        public static HourlyForecastObj fromJson(String json) {
            Gson gson = new Gson();
            return gson.fromJson(json, HourlyForecastObj.class);
        }
    }

    public static class URLs {
        public static final String WEATHER_LAT_LON = "https://api.openweathermap.org/data/2.5/forecast/hourly?lat={lat}&lon={lon}&appid={API key}";
        public static final String WEATHER_CITY_NAME = "https://api.openweathermap.org/data/2.5/forecast/hourly?q={city name}&appid={API key}";
        public static final String WEATHER_ZIP_CODE = "https://api.openweathermap.org/data/2.5/forecast/hourly?zip={zip code}&appid={API key}";
    }
    public static class Callables {
        @RequestMethod(method = "GET")
        public static class HourlyWeatherForecastLatLonCallable extends LatLonCallable {
            public HourlyWeatherForecastLatLonCallable(double lat, double lon) {
                super(URLs.WEATHER_LAT_LON, lat, lon);
            }
        };
        @RequestMethod(method = "GET")
        public static class HourlyWeatherForecastCityNameCallable extends CityNameCallable {
            public HourlyWeatherForecastCityNameCallable(String cityName) {
                super(URLs.WEATHER_CITY_NAME, cityName);
            }
        };
        @RequestMethod(method = "GET")
        public static class HourlyWeatherForecastZipCodeCallable extends ZipCodeCallable {
            public HourlyWeatherForecastZipCodeCallable(int zipCode) {
                super(URLs.WEATHER_ZIP_CODE, zipCode);
            }
        };
    }
}
