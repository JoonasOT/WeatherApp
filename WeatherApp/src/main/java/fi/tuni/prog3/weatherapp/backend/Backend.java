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

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
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

        AtomicBoolean ok = new AtomicBoolean(false);
        do {
            System.out.println("Trying to solve for the user's public IP address");
            ipService.solveIP();

            ipService.getIP().ifPresentOrElse(ip -> {
                System.out.println("IP obtained with " + ipService.getServiceProviderName());

                Database<GeoLocation> geoipDatabase = new MaxMindGeoIP2(GEOIP_DATABASE_LOC);
                if (ip.isEmpty()) {
                    System.err.println("We weren't able to contact IP service provider!");
                    return;
                }
                var locationResult = geoipDatabase.get(ip);
                if (locationResult.isPresent()) {
                    var cityName = locationResult.get().city().getName();
                    var countryCode = locationResult.get().country().getIsoCode();
                    System.out.println("User's location set to: " + cityName + ", " + countryCode);
                    tmp.set(new City(cityName, countryCode));
                    ok.set(true);
                } else {
                    System.err.println("Weren't able to fetch user's location from DB!");
                    tmp.set(new City("N/A", "N/A"));
                }
            },
            () -> {
                try { Thread.sleep(1500); } catch (Exception ignored) { /* ??? */}
            });
        } while (!ok.get()); // TODO: Maybe display this in the GUI?

        System.out.println("Geolocation estimation complete!");

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
