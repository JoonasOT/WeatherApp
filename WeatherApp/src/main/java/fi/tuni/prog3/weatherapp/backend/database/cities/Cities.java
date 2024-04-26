package fi.tuni.prog3.weatherapp.backend.database.cities;

import fi.tuni.prog3.weatherapp.backend.api.general.API;
import fi.tuni.prog3.weatherapp.backend.api.general.API_Factory;
import fi.tuni.prog3.weatherapp.backend.api.general.iCallable;
import fi.tuni.prog3.weatherapp.backend.io.ReadWrite;
import fi.tuni.prog3.weatherapp.backend.database.Database;
import fi.tuni.prog3.weatherapp.backend.security.Key;
import fi.tuni.prog3.weatherapp.backend.database.cities.builder.CityBuilder;
import fi.tuni.prog3.weatherapp.backend.database.cities.loaders.BaseLoader;
import fi.tuni.prog3.weatherapp.backend.database.cities.loaders.CitiesLoader;
import fi.tuni.prog3.weatherapp.backend.database.cities.loaders.FallbackLoader;
import fi.tuni.prog3.weatherapp.backend.database.cities.loaders.ParallelLoader;

import fi.tuni.prog3.weatherapp.backend.database.cities.Cities.City;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.IntStream;

import static fi.tuni.prog3.weatherapp.backend.database.cities.Params.FileStructure.*;

/**
 * A class for constructing a database for storing different cities found on OpenWeather and getting them or
 * getting the best suggestions from the possible set of possibilities.
 *
 * @author Joonas tuominen
 */
public class Cities implements Database<List<City>> {
    public static int MAX_CITIES_RETURNED = 5;
    public static double COUNTRY_BIAS_FACTOR = 2.0;
    private final String location;
    private final String DatabaseLocation;
    private final String cityListLocation;
    private final String cityListOptimisedLocation;
    private City[] cities;
    private final boolean wasAbleToLoad;

    /**
     * A record for storing a city's name, and it's country's code (ISO)
     * @param name The name of the city
     * @param countryCode The ISO country code of the country
     */
    public record City(String name, String countryCode) {}


    /**
     * The constructor for a Cities database.
     * @deprecated DO NOT USE! Use the CitiesBuilder to construct this database
     * @param builder Takes a CitiesBuilder that builds the database.
     */
    public Cities(CityBuilder builder) {
        location = builder.getLocation();
        DatabaseLocation = builder.getDatabaseLocation();

        String tmp;
        cityListLocation = ((tmp = builder.getCityListLocation()) != null) ?
                tmp : DatabaseLocation + CITY_FALLBACK;
        cityListOptimisedLocation = ((tmp = builder.getOptimizedCityListLocation()) != null) ?
                tmp : DatabaseLocation + CITIES_OPTIMISED;

        wasAbleToLoad = load();
    }

    /**
     * Gets the city or returns the best N candidates. Where N is Cities.MAX_CITIES_RETURNED.
     * @param query The query
     * @return Empty if the database wasn't able to load, otherwise a list of cities with the exact same name or
     * the best candidates from the query
     */
    @Override
    public Optional<List<City>> get(String query) {
        if (!wasAbleToLoad) return Optional.empty();

        { // Scope for not storing init for any longer than necessary
            List<City> init = Arrays.stream(cities).filter(city -> city.name.equalsIgnoreCase(query)).toList();
            if (!init.isEmpty()) return Optional.of(init);
        }
        // Intermediate for storing city and weight
        record CityWithWeight(City city, double weight){}

        // Calculate edit distances with country bias
        double[] result = addCountryBias(calculateEditDistance(query));

        // Construct the intermediates
        CityWithWeight[] intermediate = new CityWithWeight[result.length];
        for (int i : IntStream.range(0, result.length).toArray()) intermediate[i] = new CityWithWeight(cities[i], result[i]);

        // Sort the intermediates
        Arrays.sort(intermediate, Comparator.comparingDouble(CityWithWeight::weight));

        var max = Math.min(MAX_CITIES_RETURNED, cities.length);

        // Get the top N or the full list if that was smaller
        return Optional.of(Arrays.stream(intermediate).map(CityWithWeight::city).toList().subList(0, max));
    }

    /**
     * Tries to load the cities from the optimised file first in parallel and then in a single threaded fassion.
     * If that ran into an error, try to load from the fallbacks
     * @return If we were able to load the cities at all
     */
    private boolean load() {
        try (CitiesLoader loader = ParallelLoader.GetInstance()) {
            loader.load(String.join(":", new String[]{ DatabaseLocation, cityListOptimisedLocation}));
            loader.waitForReady();
            cities = loader.getCities();
            return true;
        } catch (Exception e) {
            try (CitiesLoader loader = BaseLoader.GetInstance()){
                System.err.println("Wasn't able to load in parallel!");
                loader.load(cityListOptimisedLocation);
                ReadWrite.write(DatabaseLocation + CITY_COUNT, String.valueOf(cities.length));
                return true;
            } catch (Exception ee) {
                System.err.println("Wasn't able to load optimised file!");
                System.err.println(e.getMessage());
                return loadFromFallback();
            }
        }
    }

    /**
     * Tries to load the cities from the original GZ file. If successful writes the necessary files for load() to be
     * successful on the next load. If the first attempt of loading from the GZ file fails, it tries to download the
     * original version from OpenWeatherBulk and tries again.
     * @return False if was not able to load the cities at all, true if was successful.
     */
    private boolean loadFromFallback() {
        String location = String.join(":",
                new String[]{cityListLocation, cityListOptimisedLocation, DatabaseLocation + CITY_COUNT });

        // Attempt #1
        try (CitiesLoader loader = FallbackLoader.GetInstance()) {
            loader.load(location);
            cities = loader.getCities();
            return true;
        } catch (Exception e) {
            var getFromOW = getCityListFromOpenWeatherBulk();
            if (!getFromOW) return false; // wasn't able to download

            // Attempt #2
            try (CitiesLoader loader = FallbackLoader.GetInstance()) {
                loader.load(location);
                cities = loader.getCities();
                return true;
            } catch (Exception ee) {
                return false; // rip
            }
        }
    }

    /**
     * Method for downloading the original bulk cities file from OpenWeather
     * @return true if successful, false if not
     */
    private boolean getCityListFromOpenWeatherBulk() {
        try {
            API api = new API(new API_Factory() {
                @Override public Key getKey() { return new Key(); }
                @Override public API construct() { return new API(this); }
            });

            var result = api.call(new iCallable() {
                                @Override public String url() { return CITY_FALLBACK_URL; }
                                @Override public Map<String, String> args() { return NO_ARGS; }
            });

            if (result.isEmpty()) return false;

            var fs = new FileOutputStream(cityListLocation);
            fs.write(result.get().getAllBytes());
            fs.close();
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    /**
     * Method for calculating the edit distances of city names to an array of distances.
     * Tries to first execute in parallel, if that fails does it with a single thread.
     * Parallel version about 1/3 of the execution time of the single thread version.
     *
     * @param word The attempted spelling of some city
     * @return The list of edit distances to the cities array
     */
    private int[] calculateEditDistance(String word) {
        String[] words = new String[cities.length];
        Arrays.stream(cities).map(City::name).toList().toArray(words);
        try {
            return EditDistance.calculateDistancesParallel(words, word);
        } catch (RuntimeException e) {
            return EditDistance.calculateDistances(words, word);
        }
    }

    /**
     * Method for adding a country bias to each calculated edit distance. Divides the edit distance by
     * COUNTRY_BIAS_FACTOR for each city in the same country as the user of the software.
     * @param original The edit distance array
     * @return The resultant array after the operation
     */
    private double[] addCountryBias(int[] original) {
        return IntStream.range(0, original.length).mapToDouble(
                i -> (double)original[i] / (location.equalsIgnoreCase(cities[i].countryCode) ? COUNTRY_BIAS_FACTOR : 1.0)
                ).toArray();
    }
}
