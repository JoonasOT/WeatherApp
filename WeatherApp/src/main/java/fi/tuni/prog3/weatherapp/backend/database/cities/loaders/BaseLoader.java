package fi.tuni.prog3.weatherapp.backend.database.cities.loaders;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import fi.tuni.prog3.weatherapp.backend.io.ReadWrite;
import fi.tuni.prog3.weatherapp.backend.database.cities.Cities.City;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public final class BaseLoader implements CitiesLoader {
    private static BaseLoader INSTANCE;
    private State state = State.IDLE;
    private static final Gson gson = new Gson();
    private static City[] cities;
    private BaseLoader(){}
    public static BaseLoader GetInstance() {
        if (INSTANCE == null) {
            INSTANCE = new BaseLoader();
        }
        return INSTANCE;
    }
    @Override
    public void waitForReady() {}

    @Override
    public State getState() {
        return state;
    }

    @Override
    public void load(String fileLocation) {
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

    @Override
    public City[] getCities() {
        state = State.IDLE;
        return cities;
    }

    @Override
    public void close() {}
}
