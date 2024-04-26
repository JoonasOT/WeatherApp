package fi.tuni.prog3.weatherapp.backend.database.cities;

/**
 * A structure that holds the configuration of the Cities database.
 *
 * @author Joonas Tuominen
 */
public class Params {
    /**
     * All the default file paths used by the Cities database.
     */
    public static final class FileStructure {
        public static String DatabaseLocation = "./db/Cities";
        public static String CITIES_OPTIMISED = "/cities_optimised_load";
        public static String CITY_COUNT = "/CityCount";
        public static String CITY_FALLBACK = "/city.list.json.gz";
        public static String CITY_FALLBACK_URL = "https://bulk.openweathermap.org/sample/city.list.json.gz";
    }
}
