package fi.tuni.prog3.weatherapp.backend;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

import fi.tuni.prog3.weatherapp.backend.io.ReadWrite;
import fi.tuni.prog3.weatherapp.frontend.scenes.WeatherScene;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public final class Backend {
    private static Logger logger = LogManager.getLogger(Backend.class);
    private static String USER_AGENT = "OpenWeatherProject JoonasOT";
    private static String CITIES_DATABASE_LOC = "./Databases/Cities";
    private static String GEOIP_DATABASE_LOC = "./Databases/GeoLite2-City_20240402/GeoLite2-City.mmdb";
    private static String FAVOURITES_SAVE_LOCATION = "./Data/user/favourites.json";
    private static String HISTORY_SAVE_LOCATION = "./Data/user/history.json";
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
            logger.info("Trying to solve for the user's public IP address");
            ipService.solveIP();

            ipService.getIP().ifPresentOrElse(ip -> {
                logger.info("IP obtained with " + ipService.getServiceProviderName());

                Database<GeoLocation> geoipDatabase = new MaxMindGeoIP2(GEOIP_DATABASE_LOC);
                if (ip.isEmpty()) {
                    logger.error("We weren't able to contact IP service provider!");
                    return;
                }
                var locationResult = geoipDatabase.get(ip);
                if (locationResult.isPresent()) {
                    var cityName = locationResult.get().city().getName();
                    var countryCode = locationResult.get().country().getIsoCode();

                    logger.info("User's location set to: " + cityName + ", " + countryCode);
                    tmp.set(new City(cityName, countryCode));
                    ok.set(true);
                } else {
                    logger.error("Weren't able to fetch user's location from DB!");
                    tmp.set(new City("N/A", "N/A"));
                }
            },
            () -> {
                logger.error("Was unable to connect to the internet!");
                try { Thread.sleep(1500); } catch (Exception ignored) { /* ??? */}
            });
        } while (!ok.get()); // TODO: Maybe display this in the GUI?

        logger.info("Geolocation estimation complete!");

        City location = tmp.get();
        loadHistoryAndFavourites();

        logger.info("Constructing the OpenWeather API");
        OpenWeather = new OpenWeather.factory().construct();
        logger.info("OpenWeather API constructed!");

        logger.info("Constructing the cities database");
        cityDatabase = new CityBuilder().setLocation(location.countryCode()).setDatabaseLocation(CITIES_DATABASE_LOC).build();
        logger.info("Cities database constructed!");
    }
    public static Backend getInstance() {
        return INSTANCE == null ? (INSTANCE = new Backend()) : INSTANCE;
    }
    public List<City> getSimilarCities(String query) {
        logger.info("Got similar cities for: " + query);
        var result = cityDatabase.get(query);
        return result.map(cities -> cities.stream().filter(city -> !city.name().equals("-")).toList())
                .orElse(null);

    }
    public Optional<Response> callOpenWeatherWith(iCallable callable) {
        logger.info("Calling OW with: " + callable.getClass().getName());
        return OpenWeather.call(callable);
    }
    private Optional<Response> callOpenWeatherWithNoLog(iCallable callable) {
        return OpenWeather.call(callable);
    }
    public <R> Optional<R> callOpenWeatherWith(iCallable callable, Class fromJsonClass) {
        logger.info("Calling OW with: " + callable.getClass().getName() + " and " + fromJsonClass.getName());
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
    public List<byte[]> getNxNtiles(WeatherMap.Callables.MapTile tile, double lat, double lon, final int Z, final int N) {
        logger.info("Getting tiles for: " + tile);
        List<byte[]> result = new LinkedList<>();
        final int X = WeatherMap.longitudeToX(lon, Z);
        final int Y = WeatherMap.latitudeToY(lat, Z);

        for (int y : IntStream.range(Y - N/2, Y + N/2 + 1).toArray()) {
            for (int x : IntStream.range(X - N/2, X + N/2 + 1).toArray()) {
                Optional<Response> response = callOpenWeatherWithNoLog(
                        tile.isMap()?   new WeatherMap.Callables.OpenStreetMapTileCallable(tile.userAgent(), x, y, Z):
                                new WeatherMap.Callables.WeatherMapTileCallable(tile.layer(), x, y, Z)
                );
                result.add(response.map(Response::getAllBytes).orElse(new byte[]{}));
            }
        }
        return result;
    }

    public void loadHistoryAndFavourites() {
        logger.info("Loading history and favourites");
        Gson gson = new Gson();
        Type type = new TypeToken<List<City>>(){}.getType();

        var res = ReadWrite.read(FAVOURITES_SAVE_LOCATION);
        if (res.isPresent()) {
            favourites = gson.fromJson(res.get(), type);
        } else favourites = new ArrayList<>();

        res = ReadWrite.read(HISTORY_SAVE_LOCATION);
        if (res.isPresent()) {
            history = gson.fromJson(res.get(), type);
        } else history = new ArrayList<>();
    }

    public void addFavourite(City city) {
        logger.info("Added " + city + " to favourites");
        favourites.add(city);
    }
    public void removeFromFavourites(City city) {
        try {
            favourites.remove(city);
            logger.info("Removed " + city + " from favourites");
        } catch (Exception ignored) {
            logger.error("Couldn't remove " + city + " from favourites");
        }
    }
    public void addToHistory(City city) {
        logger.info("Added " + city + " to history");
        history.add(city);
    }
    public List<City> getFavourites() {
        logger.info("Got favourites");
        return favourites;
    }
    public List<City> getHistory() {
        logger.info("Got history");
        return history;
    }
    public void nukeHistory() {
        logger.info("Removed history!");
        history.clear();
    }
    public static void Shutdown() {
        logger.info("Shutting the backend down!");
        Gson gson = new Gson();
        ReadWrite.write(FAVOURITES_SAVE_LOCATION, gson.toJson(favourites));
        ReadWrite.write(HISTORY_SAVE_LOCATION, gson.toJson(history));
    }
}
