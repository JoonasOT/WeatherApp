package fi.tuni.prog3.weatherapp.backend;

import fi.tuni.prog3.weatherapp.backend.api.general.API;
import fi.tuni.prog3.weatherapp.backend.api.general.Response;
import fi.tuni.prog3.weatherapp.backend.api.general.iCallable;
import fi.tuni.prog3.weatherapp.backend.api.ip.IPService;
import fi.tuni.prog3.weatherapp.backend.api.openweather.*;
import fi.tuni.prog3.weatherapp.backend.database.Database;
import fi.tuni.prog3.weatherapp.backend.database.cities.Cities.City;
import fi.tuni.prog3.weatherapp.backend.database.cities.builder.CityBuilder;
import fi.tuni.prog3.weatherapp.backend.database.geoip2.GeoLocation;
import fi.tuni.prog3.weatherapp.backend.database.geoip2.MaxMindGeoIP2;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
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
    private static List<City> favourites = new LinkedList<>();
    private static List<City> history = new LinkedList<>();
    private Backend(){
        IPService ipService = IPService.getInstance();
        AtomicReference<City> tmp = new AtomicReference<>(new City("N/A", "N/A"));
        ipService.getIP().ifPresent(ip -> {
            Database<GeoLocation> geoipDatabase = new MaxMindGeoIP2(GEOIP_DATABASE_LOC);
            if (ip.equals("127.0.0.1")) {
                System.err.println("User doesn't seem to be connected to the internet!");
                tmp.set(new City("N/A", "N/A"));
            }
            var locationResult = geoipDatabase.get(ip);
            tmp.set(locationResult.map(geoLoc -> new City(geoLoc.city().getName(), geoLoc.country().getIsoCode()))
                    .orElseGet(() -> new City("N/A", "N/A")));
        });

        City location = tmp.get();

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
    public Optional<Response> callOpenWeatherWith(iCallable callable) {
        return OpenWeather.call(callable);
    }
    public <R> Optional<R> callOpenWeatherWith(iCallable callable, Class fromJsonClass) {
        var tmp = OpenWeather.call(callable);
        if (tmp.isPresent() && tmp.get().CallWasOK()) {
            for (var m : fromJsonClass.getDeclaredMethods()) {
                if (m.getName().equals("fromJson")) {
                    try {
                        return Optional.of((R)m.invoke(null, tmp.get().getData()));
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                        e.printStackTrace(System.err);
                    }
                }
            }
        }
        return Optional.empty();
    }
    public void addFavourite(City city) { favourites.add(city); }
    public void removeFromFavourites(City city) {
        try {
            favourites.remove(city);
        } catch (Exception ignored) {}
    }
    public void addToHistory(City city) { history.add(city); }
    public List<City> getFavourites() {
        return favourites;
    }
    public List<City> getHistory() {
        return history;
    }
}
