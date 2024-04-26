package fi.tuni.prog3.weatherapp.backend.api.general;

import fi.tuni.prog3.weatherapp.backend.security.Key;

/**
 * A simple and mostly deprecated interface for forming APIs.
 *
 * @author Joonas Tuominen
 */
public interface API_Factory {
    /**
     * An API "must" contain a key and this is a getter for said key.
     * @return the API key used in the API.
     */
    public Key getKey();

    /**
     * Method for constructing an API.
     * @return An API formed from this factory.
     */
    public API construct();
}
