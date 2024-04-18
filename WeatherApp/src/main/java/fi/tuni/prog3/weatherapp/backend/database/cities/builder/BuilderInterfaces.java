package fi.tuni.prog3.weatherapp.backend.database.cities.builder;

import fi.tuni.prog3.weatherapp.backend.database.cities.Cities;

/**
 * A structure holding interfaces used for the CityBuilder
 *
 * @author Joonas Tuominen
 */
public class BuilderInterfaces {

    /**
     * An interface we get after setting all the necessary fields.
     */
    public interface SetOptionalFields {
        /**
         * Sets the location of the optimised city list (file on disk).
         * @param what The location of the optimised city list.
         * @return Loops back to this interface
         */
        SetOptionalFields setOptimisedCityListLocationTo(String what);
        /**
         * Sets the location of the fallback city list (file on disk).
         * @param what The location of the fallback city list.
         * @return Loops back to this interface
         */
        SetOptionalFields setFallbackCityListLocationTo(String what);

        /**
         * Build a Cities database from this builder.
         * @return Cities database formed from this builder.
         */
        Cities build();
    }

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
}
