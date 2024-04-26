package fi.tuni.prog3.weatherapp.backend.database.cities.builder;

/**
 * An interface to force setting the user's location first.
 */
public interface SetLocation {
    /**
     * A method for setting the user's location.
     * @param what The location
     * @return SetDataBaseFileLocation-interface that forces to set the DB location.
     */
    SetDatabaseFileLocation setLocation(String what);
}
