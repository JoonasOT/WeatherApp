package fi.tuni.prog3.weatherapp.frontend.fonts;

/**
 * This is an enum used to store all the FontAwesome font glyphs used by the application.
 * Each enum value has the location of the font and the unicode character for the glyph.
 *
 * @author Joonas Tuominen
 */
public enum FontAwesome {
    STAR("\uf005", FontLocations.FONT_AWESOME_REGULAR.location),
    STAR_FILLED("\uf005", FontLocations.FONT_AWESOME_SOLID.location),
    SEARCH("\uf689", FontLocations.FONT_AWESOME_REGULAR.location),
    HISTORY("\uF1DA", FontLocations.FONT_AWESOME_SOLID.location);

    public final String unicode;
    public final String fontLocation;

    /**
     * Constructor for storing the unicode char and the font location
     * @param unicode The unicode char aka the location of the glyph in the font.
     * @param fontLocation The location of the font file on disc.
     */
    FontAwesome(String unicode, String fontLocation) {
        this.unicode = unicode;
        this.fontLocation = fontLocation;
    }
}