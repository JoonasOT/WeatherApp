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

public class Cities implements Database<List<City>> {
    public static int MAX_CITIES_RETURNED = 5;

    public record City(String name, String countryCode) {}
    private final String location;
    private final String DatabaseLocation;
    private final String cityListLocation;
    private final String cityListOptimisedLocation;
    private City[] cities;
    private final boolean wasAbleToLoad;

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
    @Override
    public Optional<List<City>> get(String query) {
        if (!wasAbleToLoad) return Optional.empty();

        {
            List<City> init = Arrays.stream(cities).filter(city -> city.name.equalsIgnoreCase(query)).toList();

            if (!init.isEmpty()) {
                return Optional.of(init);
            }
        }
        record CityWithWeight(City city, double weight){}

        double[] result = addCountryBias(calculateEditDistance(query));

        CityWithWeight[] intermediate = new CityWithWeight[result.length];
        for (int i : IntStream.range(0, result.length).toArray()) {
            intermediate[i] = new CityWithWeight(cities[i], result[i]);
        }
        Arrays.sort(intermediate, Comparator.comparingDouble(CityWithWeight::weight));

        var max = Math.min(MAX_CITIES_RETURNED, cities.length);

        return Optional.of(Arrays.stream(intermediate).map(CityWithWeight::city).toList().subList(0, max));
    }
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
    private boolean loadFromFallback() {
        String location = String.join(":",
                new String[]{cityListLocation, cityListOptimisedLocation, DatabaseLocation + CITY_COUNT });

        try (CitiesLoader loader = FallbackLoader.GetInstance()) {
            loader.load(location);
            cities = loader.getCities();
            return true;
        } catch (Exception e) {
            var getFromOW = getCityListFromOpenWeatherBulk();
            if (!getFromOW) return false;

            try (CitiesLoader loader = FallbackLoader.GetInstance()) {
                loader.load(location);
                cities = loader.getCities();
                return true;
            } catch (Exception ee) {
                return false;
            }
        }
    }
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
    private int[] calculateEditDistance(String word) {
        String[] words = new String[cities.length];
        Arrays.stream(cities).map(City::name).toList().toArray(words);
        try {
            return EditDistance.calculateDistancesParallel(words, word);
        } catch (RuntimeException e) {
            return EditDistance.calculateDistances(words, word);
        }
    }
    private double[] addCountryBias(int[] original) {
        return IntStream.range(0, original.length).mapToDouble(
                i -> (double)original[i] / (location.equalsIgnoreCase(cities[i].countryCode) ? 2.0 : 1.0)
                ).toArray();
    }
}
