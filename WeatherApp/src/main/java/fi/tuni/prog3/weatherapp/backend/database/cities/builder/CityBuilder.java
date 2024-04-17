package fi.tuni.prog3.weatherapp.backend.database.cities.builder;

import fi.tuni.prog3.weatherapp.backend.database.cities.builder.BuilderInterfaces.*;
import fi.tuni.prog3.weatherapp.backend.database.cities.Cities;

/**
 * A builder for a Cities database.
 *
 * @author Joonas Tuominen
 */
public class CityBuilder implements SetOptionalFields, SetDatabaseFileLocation, SetLocation {
    private String location;
    private String databaseLocation;
    private String cityListLocation;
    private String optimizedCityListLocation;

    /**
     * Sets the location of the optimised city list (file on disk).
     * @param what The location of the optimised city list.
     * @return Loops back to this interface
     */
    @Override
    public SetOptionalFields setOptimisedCityListLocationTo(String what) {
        optimizedCityListLocation = what;
        return this;
    }

    /**
     * Sets the location of the fallback city list (file on disk).
     * @param what The location of the fallback city list.
     * @return Loops back to this interface
     */
    @Override
    public SetOptionalFields setFallbackCityListLocationTo(String what) {
        cityListLocation = what;
        return this;
    }

    /**
     * A method for setting the databases location (folder on disk).
     * @param what The location of the database.
     * @return SetOptionalFields-interface.
     */
    @Override
    public SetOptionalFields setDatabaseLocation(String what) {
        databaseLocation = what;
        return this;
    }

    /**
     * A method for setting the user's location.
     * @param what The location
     * @return SetDataBaseFileLocation-interface that forces to set the DB location.
     */
    @Override
    public SetDatabaseFileLocation setLocation(String what) {
        location = what;
        return this;
    }

    /**
     * Build a Cities database from this builder.
     * @return Cities database formed from this builder.
     */
    @Override
    public Cities build() {
        return new Cities(this);
    }

    /**
     * Get the location of the user.
     * @return The user's location.
     */
    public String getLocation() {
        return location;
    }

    /**
     * Get the database folder's location.
     * @return The database folder's location.
     */
    public String getDatabaseLocation() {
        return databaseLocation;
    }

    /**
     * Get the non-optimised city list's location.
     * @return The location of the city list file.
     */
    public String getCityListLocation() {
        return cityListLocation;
    }

    /**
     * Get the optimised city list's location.
     * @return The location of the city list file.
     */
    public String getOptimizedCityListLocation() {
        return optimizedCityListLocation;
    }

}
