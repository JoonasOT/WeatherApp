package fi.tuni.prog3.weatherapp.backend.api.openweather;

/**
 * A structure holding common records used in json structures.
 *
 * @author Joonas Tuominen
 */
public class JSON_OBJs {
    /**
     * Record for coordinates.
     * @param lon The longitude.
     * @param lat The latitude.
     */
    public record Coord(double lon, double lat){};

    /**
     * Record for weather state.
     * @param id The weather condition code.
     * @param main The weather condition group.
     * @param description The description of weather.
     * @param icon The icon on OpenWeather for this state.
     */
    public record Weather(int id, String main, String description, String icon){};

    /**
     * Record for the clouds
     * @param all The cloudiness %.
     */
    public record Clouds(int all){};
}
