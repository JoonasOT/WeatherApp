package fi.tuni.prog3.weatherapp.backend.database.cities.builder;

import fi.tuni.prog3.weatherapp.backend.database.cities.Cities;

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
