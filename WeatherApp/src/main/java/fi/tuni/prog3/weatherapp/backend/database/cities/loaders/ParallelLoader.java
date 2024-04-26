package fi.tuni.prog3.weatherapp.backend.database.cities.loaders;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import fi.tuni.prog3.weatherapp.backend.io.ReadWrite;
import fi.tuni.prog3.weatherapp.backend.database.cities.Cities.City;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static fi.tuni.prog3.weatherapp.backend.database.cities.Params.FileStructure.CITY_COUNT;

/**
 * A singleton that tries to load the optimised version of the cities list in parallel.
 *
 * @author Joonas Tuominen
 */
public class ParallelLoader implements CitiesLoader {
    private static ParallelLoader INSTANCE;
    private State state = State.IDLE;
    private static final Gson gson = new Gson();
    private static City[] cities;
    private static LinkedList<Future<Void>> futures = new LinkedList<>();
    private final ExecutorService executor = Executors.newCachedThreadPool();

    /**
     * Private constructor for singleton.
     */
    private ParallelLoader(){}

    /**
     * Get the instance of ParallelLoader or construct it.
     * @return The instance of base loader.
     */
    public static ParallelLoader GetInstance() {
        return INSTANCE == null ? (INSTANCE = new ParallelLoader()) : INSTANCE;
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
     * Shutdown the used executor service.
     */
    @Override
    public void close() {
        executor.shutdown();
    }

    /**
     * A record that is used to run loading in parallel
     * @param i The number of the line aka the index of the city
     * @param line The line of JSON containing the description of a city.
     */
    private record LoadTask(int i, String line) implements Callable<Void> {
        /**
         * The function ran in parallel by the executor service that converts line to City and stores it in cities[].
         * @return null
         */
        @Override
        public Void call() {
            cities[i] = gson.fromJson(line, City.class);
            return null;
        }
    }

    /**
     * A method that tries to load the optimised city list from given location in parallel.
     * @param fileLocation The folder location of the database and the file location from which we want to load separated
     *                     by a colon (:)
     * @throws RuntimeException If we couldn't find the file or parallel processing ran into an error.
     */
    @Override
    public void load(String fileLocation) throws RuntimeException {
        String[] locations = fileLocation.split(":");
        assert locations.length == 2;
        var readResult = ReadWrite.read(locations[0] + CITY_COUNT);
        if (readResult.isEmpty()) throw new RuntimeException("Wasn't able to load the city count!");
        int cityCount = Integer.parseInt(readResult.get());
        if (cityCount <= 0) throw new RuntimeException("City count was not valid!");

        state = State.LOADING;
        cities = new City[cityCount];
        futures = new LinkedList<>();

        try (var bf = new BufferedReader(new FileReader(locations[1]))){
            String line;
            int lineNumber = 0;
            while ( (line = bf.readLine()) != null ) {
                futures.add(executor.submit(new LoadTask(lineNumber, line)));
                lineNumber++;
            }
            if (cityCount != lineNumber) {
                throw new RuntimeException(String.format("City count was: %d and final line number was %d!%n",
                        cityCount, lineNumber));
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Get the cities we loaded.
     * @return The cities we have loaded.
     */
    @Override
    public City[] getCities() {
        state = State.IDLE;
        return cities;
    }

    /**
     * Function that waits until load is ready.
     */
    @Override
    public void waitForReady() {
        for (Future<Void> f : futures) {
            if (!f.isDone()) {
                try {
                    f.get();
                } catch (Exception ignored) {}
            }
        }
        state = State.READY;
    }
}
