package fi.tuni.prog3.weatherapp.backend.database.cities.loaders;

import fi.tuni.prog3.weatherapp.backend.database.cities.Cities.City;

/**
 * An interface for loading cities from a file.
 *
 * @author Joonas Tuominen
 */
public interface CitiesLoader extends AutoCloseable {
    /**
     * Enum that contains the different possible states of the loader.
     */
    public enum State { IDLE, LOADING, READY };

    /**
     * Function that waits until load is ready.
     */
    public void waitForReady();

    /**
     * Get the state of the loader.
     * @return State of the CitiesLoader.
     */
    public State getState();

    /**
     * A method that loads the cities from a given file location
     * @param fileLocation The file location from which we want to load from.
     * @throws RuntimeException If needed.
     */
    public void load(String fileLocation) throws RuntimeException;

    /**
     * Get the cities we loaded.
     * @return The cities we have loaded.
     */
    public City[] getCities();
}
