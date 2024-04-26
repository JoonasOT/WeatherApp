package fi.tuni.prog3.weatherapp.frontend.fonts;

/**
 * This is an enum used to store all the font locations used by the application.
 *
 * @author Joonas Tuominen
 */
public enum FontLocations {
    /**
     * Weather font location
     */
    WEATHER("file:./res/font/weather.ttf"),
    /**
     * FontAwesome font (regular charset) location
     */
    FONT_AWESOME_REGULAR("file:res/font/FontAwesome6Free-Regular-400.otf"),
    /**
     * FontAwesome font (bold charset) location
     */
    FONT_AWESOME_SOLID("file:res/font/FontAwesome6Free-Solid-900.otf");

    public final String location;

    /**
     * Constructor for each location.
     * @param location The location on disc, from where the font can be located from.
     */
    FontLocations(String location){ this.location = location; }
}
