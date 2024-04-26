package fi.tuni.prog3.weatherapp.backend.database;

import java.util.Optional;

/**
 * An interface for creating Databases that return something of the type T.
 * @param <T> The type of data the database returns.
 *
 * @author Joonas Tuominen
 */
public interface Database<T> {
    /**
     * A method for querying the database.
     * @param what The query
     * @return The result of the database query. Maybe be empty if an error arose.
     */
    public Optional<T> get(String what);
}
