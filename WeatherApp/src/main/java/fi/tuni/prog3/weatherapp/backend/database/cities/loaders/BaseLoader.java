package fi.tuni.prog3.weatherapp.backend.database.cities.loaders;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import fi.tuni.prog3.weatherapp.backend.io.ReadWrite;
import fi.tuni.prog3.weatherapp.backend.database.cities.Cities.City;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * A singleton that tries to load the optimised version of the cities list.
 *
 * @author Joonas Tuominen
 */
public final class BaseLoader implements CitiesLoader {
    private static BaseLoader INSTANCE;
    private State state = State.IDLE;
    private static final Gson gson = new Gson();
    private static City[] cities;

    /**
     * Private constructor for singleton.
     */
    private BaseLoader(){}

    /**
     * Get the instance of BaseLoader or construct it.
     * @return The instance of base loader.
     */
    public static BaseLoader GetInstance() {
        return INSTANCE == null ? (INSTANCE = new BaseLoader()) : INSTANCE;
    }

    /**
     * Function that waits until load is ready.
     */
    @Override
    public void waitForReady() {}

    /**
     * Get the state of the loader.
     * @return State of the CitiesLoader.
     */
    @Override
    public State getState() {
        return state;
    }

    /**
     * A method that tries to load the optimised city list from given location.
     * @param fileLocation The file location from which we want to load from.
     * @throws RuntimeException If we couldn't find the file.
     */
    @Override
    public void load(String fileLocation) throws RuntimeException {
        var readResult = ReadWrite.read(fileLocation);
        if (readResult.isEmpty()) throw new RuntimeException("Couldn't read cities from " + fileLocation + "!");
        String content = readResult.get();

        List<String> lines = content.lines().toList();
        state = State.LOADING;

        cities = new City[lines.size()];

        for (int i : IntStream.range(0, cities.length).toArray()) {
            Map<String, String> res = gson.fromJson(lines.get(i), new TypeToken<Map<String, String>>(){}.getType());
            cities[i] = new City(res.get("name"), res.get("countryCode"));
        }
        state = State.READY;
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
     * Close and shutdown used resources.
     */
    @Override
    public void close() {}
}
