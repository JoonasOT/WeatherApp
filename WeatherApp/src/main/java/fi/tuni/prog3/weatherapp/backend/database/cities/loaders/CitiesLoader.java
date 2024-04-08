package fi.tuni.prog3.weatherapp.backend.database.cities.loaders;

import fi.tuni.prog3.weatherapp.backend.database.cities.Cities.City;

public interface CitiesLoader extends AutoCloseable {
    public void waitForReady();
    public enum State { IDLE, LOADING, READY };
    public State getState();
    public void load(String fileLocation) throws RuntimeException;
    public City[] getCities();
}
