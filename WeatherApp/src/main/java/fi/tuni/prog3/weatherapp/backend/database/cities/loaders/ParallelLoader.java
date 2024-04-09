package fi.tuni.prog3.weatherapp.backend.database.cities.loaders;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

public class ParallelLoader implements CitiesLoader {
    private static ParallelLoader INSTANCE;
    private State state = State.IDLE;
    private static final Gson gson = new Gson();
    private static City[] cities;
    private static LinkedList<Future<Void>> futures = new LinkedList<>();
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private ParallelLoader(){}
    public static ParallelLoader GetInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ParallelLoader();
        }
        return INSTANCE;
    }
    @Override
    public State getState() {
        return state;
    }

    @Override
    public void close() {
        executor.shutdown();
    }

    private record LoadTask(int i, String line) implements Callable<Void> {
        @Override
        public Void call() {
            Map<String, String> res = gson.fromJson(line, new TypeToken<Map<String, String>>(){}.getType());
            cities[i] = new City(res.get("name"), res.get("countryCode"));
            return null;
        }
    }
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

    @Override
    public City[] getCities() {
        state = State.IDLE;
        return cities;
    }
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
