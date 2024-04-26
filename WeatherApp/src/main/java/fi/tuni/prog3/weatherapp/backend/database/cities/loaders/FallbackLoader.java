package fi.tuni.prog3.weatherapp.backend.database.cities.loaders;

import com.google.gson.*;
import fi.tuni.prog3.weatherapp.backend.database.cities.CityJSON;
import fi.tuni.prog3.weatherapp.backend.io.ReadWrite;
import fi.tuni.prog3.weatherapp.backend.database.cities.Cities.City;
import fi.tuni.prog3.weatherapp.backend.database.cities.Cities;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * A singleton that tries to load the unoptimised version of the cities list.
 *
 * @author Joonas Tuominen
 */
public class FallbackLoader implements CitiesLoader {
    private static FallbackLoader INSTANCE;
    private static City[] cities;
    private static State state = State.IDLE;
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private static Future<Boolean> cityCountFuture;
    private static Future<Boolean> cityOptimisedFuture;

    /**
     * Private constructor for singleton.
     */
    private FallbackLoader() {}

    /**
     * Get the instance of FallbackLoader or construct it.
     * @return The instance of base loader.
     */
    public static FallbackLoader GetInstance() {
        return INSTANCE == null ? (INSTANCE = new FallbackLoader()) : INSTANCE;
    }

    /**
     * Function that waits until load is ready.
     */
    @Override
    public void waitForReady() {
        try {
            cityCountFuture.get();
        } catch (Exception e){
            System.err.println("Fallback loader waitForReady error msg: " + e.getMessage());
        }
        try {
            cityOptimisedFuture.get();
        } catch (Exception e){
            System.err.println("Fallback loader waitForReady error msg: " + e.getMessage());
        };
    }

    /**
     * Get the state of the loader.
     * @return State of the CitiesLoader.
     */
    @Override
    public State getState() {
        return state;
    }

    /**
     * A method that tries to load the un-optimised city list and then save the optimised version onto disk.
     * @param fileLocation The fallback city list location, the optimised city list location and the location of the
     *                     file storing the count of cities all separated by a colon (:)
     * @throws RuntimeException If we couldn't find the file or file was corrupted.
     */
    @Override
    public void load(String fileLocation) throws RuntimeException {
        String[] locations = fileLocation.split(":");
        if (locations.length != 3) throw new RuntimeException("Invalid file locations given to Fallback loader!");

        String fallbackLocation = locations[0];
        String optimisedCitiesLocation = locations[1];
        String cityCountLocation = locations[2];

        state = State.LOADING;
        String content;
        {
            var tmp = ReadWrite.readGZ(fallbackLocation);
            if (tmp.isPresent()) content = tmp.get();
            else {
                System.err.println("Unable to get fallback city list");
                throw new RuntimeException("File not found!");
            }
        }

        Gson gson = new Gson();
        CityJSON[] cities = gson.fromJson(content, CityJSON[].class);

        if(cities == null) {
            System.err.println("Cities is null! -> Gson conversion failed!");
            throw new RuntimeException("Fallback city list corrupted!");
        }

        // Turn the cities array into a set of City objects that are unique.
        // For some reason the city.list.json.gz contains multiples of the same city in some cases.
        Set<Cities.City> citySet = Arrays.stream(cities).map(c -> new City(c.name(), c.country())).collect(Collectors.toSet());
        FallbackLoader.cities = new City[citySet.size()];
        int i = 0;
        for (Cities.City c : citySet) {
            FallbackLoader.cities[i] = c;
            i++;
        }
        final int finalRow = i;
        state = State.READY;

        // As we have finally managed to convert the files to a optimised form we then also save these to a file
        // for faster load times on repeated runs.
        // TODO: Could be better with creating an extended thread class
        cityCountFuture = executor.submit(() -> ReadWrite.write(cityCountLocation, Integer.toString(finalRow)));
        cityOptimisedFuture = executor.submit(() -> {
            StringBuilder outContent = new StringBuilder();
            for (City c : FallbackLoader.cities) {
                outContent.append(gson.toJson(c)).append("\n");
            }
            return ReadWrite.write(optimisedCitiesLocation, outContent.substring(0, outContent.length() - 1));
        });
    }

    /**
     * Get the cities we loaded.
     * @return The cities we have loaded.
     */
    @Override
    public City[] getCities() {
        return cities;
    }

    /**
     * Close and shutdown used resources.
     */
    @Override
    public void close() {
        waitForReady();
        executor.shutdown();
    }
}
