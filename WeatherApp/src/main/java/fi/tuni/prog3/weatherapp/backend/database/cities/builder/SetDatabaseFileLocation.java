package fi.tuni.prog3.weatherapp.backend.database.cities.builder;

/**
 * An interface to force setting database location before building.
 */
public interface SetDatabaseFileLocation {
    /**
     * A method for setting the databases location (folder on disk).
     * @param what The location of the database.
     * @return SetOptionalFields-interface.
     */
    SetOptionalFields setDatabaseLocation(String what);
}
