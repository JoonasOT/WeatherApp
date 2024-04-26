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

import fi.tuni.prog3.weatherapp.backend.io.ReadWrite;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * A singleton in charge of setting up the backend and facilitates the abilities to make APIs calls and database queries.
 *
 * @author Joonas Tuominen
 */
public final class Backend {
    private static final Logger logger = LogManager.getLogger(Backend.class);
    public static final String USER_AGENT = "OpenWeatherProject JoonasOT";
    private static final String CITIES_DATABASE_LOC = "./Databases/Cities";
    private static final String GEOIP_DATABASE_LOC = "./Databases/GeoLite2-City_20240402/GeoLite2-City.mmdb";
    private static final String FAVOURITES_SAVE_LOCATION = "./Data/user/favourites.json";
    private static final String HISTORY_SAVE_LOCATION = "./Data/user/history.json";
    private static Backend INSTANCE;
    private final API OpenWeather;
    private final Database<List<City>> cityDatabase;
    private static List<City> favourites = new LinkedList<>();
    private static List<City> history = new LinkedList<>();

    /**
     * Construction of the backend
     */
    private Backend(){
        // Get the IPService for deducing the approximate location of the user
        IPService ipService = IPService.getInstance();

        AtomicReference<City> tmp = new AtomicReference<>(new City("N/A", "N/A"));
        AtomicBoolean ok = new AtomicBoolean(false);
        do {
            logger.info("Trying to solve for the user's public IP address");
            ipService.solveIP();

            ipService.getIP().ifPresentOrElse(ip -> {
                logger.info("IP obtained with " + ipService.getServiceProviderName());

                if (ip.isEmpty()) {
                    logger.error("We weren't able to contact IP service provider!");
                    return;
                }

                // There is actually some IP!
                // Now deduce the location
                Database<GeoLocation> geoipDatabase = new MaxMindGeoIP2(GEOIP_DATABASE_LOC);
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
        } while (!ok.get()); // TODO: Maybe display that we weren't able to connect on GUI?

        logger.info("Geolocation estimation complete!");

        City location = tmp.get();
        loadHistoryAndFavourites();

        logger.info("Constructing the OpenWeather API");
        OpenWeather = new OpenWeather.factory().construct();
        logger.info("OpenWeather API constructed!");

        logger.info("Constructing the cities database");
        cityDatabase = new CityBuilder().setLocation(location.countryCode())
                                        .setDatabaseLocation(CITIES_DATABASE_LOC)
                                        .build();
        logger.info("Cities database constructed!");
    }

    /**
     * Get the backend singleton
     * @return The backend singleton
     */
    public static Backend getInstance() {
        return INSTANCE == null ? (INSTANCE = new Backend()) : INSTANCE;
    }

    /**
     * Gets all the cities with the same name as query or gets top N cities with the most similar name
     * @param query The query for cities
     * @return The list of cities
     */
    public List<City> getSimilarCities(String query) {
        logger.info("Got similar cities for: " + query);
        var result = cityDatabase.get(query);
        return result.map(cities -> cities.stream().filter(city -> !city.name().equals("-")).toList())
                .orElse(null);

    }

    /**
     * Call the OpenWeather API with a callable
     * @param callable The call we want to make on OpenWeather
     * @return The response of the call
     */
    public Optional<Response> callOpenWeatherWith(iCallable callable) {
        logger.info("Calling OW with: " + callable.getClass().getName());
        return OpenWeather.call(callable);
    }

    /**
     * Same as callOpenWeatherWith() but without adding logger messages
     * @param callable The call we want to make
     * @return The response
     */
    private Optional<Response> callOpenWeatherWithNoLog(iCallable callable) {
        return OpenWeather.call(callable);
    }

    /**
     * Calls the OpenWeather API with a callable and then tries to convert the got response json string to the given
     * object type using the object's method annotated with the FromJson annotation e.g.:
     *  <pre>{@code
     *      iCallable callable = new CurrentWeatherCityNameCallable("Tampere");
     *      CurrentWeatherObj x = backend.callOpenWeatherWith(callable, CurrentWeatherObj);
     *  }</pre>
     * @param callable The callable we want to call OpenWeather with
     * @param fromJsonClass A class that we can construct from the json formed from the response.
     *                      Must have the FromJson annotation attached!
     * @return An optional with the resulting object of the fromJsonClass or an empty optional on error
     * @param <R> The record we want to form from this operation
     */
    @SuppressWarnings("unchecked")
    public <R> Optional<R> callOpenWeatherWith(iCallable callable, Class<R> fromJsonClass) {
        logger.info("Calling OW with: " + callable.getClass().getName() + " and converting to " + fromJsonClass.getName());
        var tmp = OpenWeather.call(callable);
        if (tmp.isPresent() && tmp.get().CallWasOK()) {
            for (var m : fromJsonClass.getDeclaredMethods()) {
                if (m.isAnnotationPresent(FromJson.class)) {
                    try {
                        return Optional.of((R)m.invoke(null, tmp.get().getData()));
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                        e.printStackTrace(System.err);
                    }
                }
            }
        }
        throw new RuntimeException("Class " + fromJsonClass.getName() + " doesn't have a method with " +
                                    FromJson.class.getName() + " annotation attached to it!");
    }

    /**
     * Calls either the OpenStreetMap API or OpenWeather API based on if the given map tile is a map.
     * @param tile The map tile type (if OpenWeather isMap has to be false and layer set, if OpenStreetMap
     *             isMap has to be true and userAgent has to be set).
     * @param lat The latitude around which we want to construct the map
     * @param lon The longitude around which we want to construct the map
     * @param Z The z index aka Zoom
     * @param N The number of map tiles we want in each direction
     * @return The list of byte arrays received from the calls.
     */
    public List<byte[]> getNxNtiles(WeatherMap.Callables.MapTile tile, double lat, double lon, final int Z, final int N) {
        logger.info("Getting tiles for: " + tile);
        List<byte[]> result = new LinkedList<>();
        final int X = WeatherMap.longitudeToX(lon, Z);
        final int Y = WeatherMap.latitudeToY(lat, Z);

        for (int y : IntStream.range(Y - N/2, Y + N/2 + 1).toArray()) {
            for (int x : IntStream.range(X - N/2, X + N/2 + 1).toArray()) {
                // TODO: Upgrade to 2.0 Maps
                Optional<Response> response = callOpenWeatherWithNoLog(
                        tile.isMap()?   new WeatherMap.Callables.OpenStreetMapTileCallable(tile.userAgent(), x, y, Z):
                                        new WeatherMap.Callables.WeatherMapTileCallable(tile.layer(), x, y, Z)
                );
                result.add(response.map(Response::getAllBytes).orElse(new byte[]{}));
            }
        }
        return result;
    }

    /**
     * Method for loading history and favourites from disk
     */
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

    /**
     * Adds a given city to favourites.
     * @param city The city to be added
     */
    public void addFavourite(City city) {
        logger.info("Added " + city + " to favourites");
        favourites.add(city);
    }

    /**
     * Tries to remove a given city from favourites
     * @param city The city to be removed
     */
    public void removeFromFavourites(City city) {
        try {
            favourites.remove(city);
            logger.info("Removed " + city + " from favourites");
        } catch (Exception ignored) {
            logger.error("Couldn't remove " + city + " from favourites");
        }
    }

    /**
     * Adds a given city to history
     * @param city The city to be added
     */
    public void addToHistory(City city) {
        logger.info("Added " + city + " to history");
        history.add(city);
    }

    /**
     * Gets all the cities marked as favourite
     * @return The list of favourited cities
     */
    public List<City> getFavourites() {
        logger.info("Got favourites");
        return favourites;
    }

    /**
     * Gets all the cities in the user's history
     * @return List of all the user's search history
     */
    public List<City> getHistory() {
        logger.info("Got history");
        return history;
    }

    /**
     * Well... exactly as it says. Aka clears history.
     */
    public void nukeHistory() {
        logger.info("Removed history!");
        history.clear();
    }

    /**
     * Shuts the backend down. Writes the userdata to disk.
     */
    public static void Shutdown() {
        logger.info("Shutting the backend down!");
        Gson gson = new Gson();
        ReadWrite.write(FAVOURITES_SAVE_LOCATION, gson.toJson(favourites));
        ReadWrite.write(HISTORY_SAVE_LOCATION, gson.toJson(history));
    }
}
