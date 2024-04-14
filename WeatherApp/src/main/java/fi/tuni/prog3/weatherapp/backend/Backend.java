package fi.tuni.prog3.weatherapp.backend;

import fi.tuni.prog3.weatherapp.backend.api.API;
import fi.tuni.prog3.weatherapp.backend.api.Response;
import fi.tuni.prog3.weatherapp.backend.api.ip.IPService;
import fi.tuni.prog3.weatherapp.backend.api.openweather.CurrentWeather;
import fi.tuni.prog3.weatherapp.backend.api.openweather.OpenWeather;
import fi.tuni.prog3.weatherapp.backend.api.openweather.WeatherForecast;
import fi.tuni.prog3.weatherapp.backend.api.openweather.WeatherMap;
import fi.tuni.prog3.weatherapp.backend.database.Database;
import fi.tuni.prog3.weatherapp.backend.database.cities.Cities.City;
import fi.tuni.prog3.weatherapp.backend.database.cities.builder.CityBuilder;
import fi.tuni.prog3.weatherapp.backend.database.geoip2.GeoLocation;
import fi.tuni.prog3.weatherapp.backend.database.geoip2.MaxMindGeoIP2;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public final class Backend {
    private static String USER_AGENT = "OpenWeatherProject JoonasOT";
    private static String CITIES_DATABASE_LOC = "./Databases/Cities";
    private static String GEOIP_DATABASE_LOC = "./Databases/GeoLite2-City_20240402/GeoLite2-City.mmdb";
    private static Backend INSTANCE;
    private final API OpenWeather;
    private final Database<List<City>> cityDatabase;
    private Backend(){
        IPService ipService = IPService.getInstance();
        AtomicReference<City> tmp = new AtomicReference<>(new City("N/A", "N/A"));
        ipService.getIP().ifPresent(ip -> {
            Database<GeoLocation> geoipDatabase = new MaxMindGeoIP2(GEOIP_DATABASE_LOC);
            var locationResult = geoipDatabase.get(ip);
            tmp.set(locationResult.map(geoLoc -> new City(geoLoc.city().getName(), geoLoc.country().getIsoCode()))
                    .orElseGet(() -> new City("N/A", "N/A")));
        });

        City location = tmp.get();

        System.out.println(location);
        OpenWeather = new OpenWeather.factory().construct();
        cityDatabase = new CityBuilder().setLocation(location.countryCode()).setDatabaseLocation(CITIES_DATABASE_LOC).build();
    }
    public static Backend getInstance() {
        return INSTANCE == null ? (INSTANCE = new Backend()) : INSTANCE;
    }
    public List<City> getSimilarCities(String query) {
        var result = cityDatabase.get(query);
        return result.map(cities -> cities.stream().filter(city -> !city.name().equals("-")).toList())
                .orElse(null);

    }
    public Optional<Response> getCurrentWeather(double lat, double lon) {
        return OpenWeather.call(new CurrentWeather.Callables.CurrentWeatherLatLonCallable(lat, lon));
    }
    public Optional<Response> getCurrentWeather(String cityName) {
        return OpenWeather.call(new CurrentWeather.Callables.CurrentWeatherCityNameCallable(cityName));
    }
    public Optional<Response> getWeatherForecast(double lat, double lon) {
        return OpenWeather.call(new WeatherForecast.Callables.WeatherForecastLatLonCallable(lat, lon));
    }
    public Optional<Response> getWeatherForecast(String cityName) {
        return OpenWeather.call(new WeatherForecast.Callables.WeatherForecastCityNameCallable(cityName));
    }
    public Optional<Response> getWeatherMap(WeatherMap.WeatherLayer layer, int z, double lat, double lon) {
        return OpenWeather.call(new WeatherMap.Callables.WeatherMapCallable(layer, z, lat, lon));
    }
    public Optional<Response> getMap(int z, double lat, double lon) {
        return OpenWeather.call(new WeatherMap.Callables.OpenStreetMapCallable(USER_AGENT, z, lat, lon));
    }
}
