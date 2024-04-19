package fi.tuni.prog3.weatherapp.frontend.fonts;

import javafx.scene.text.Font;

public enum FontAwesome {
    STAR("\uf005", FontLocations.FONT_AWESOME_REGULAR.location),
    STAR_FILLED("\uf005", FontLocations.FONT_AWESOME_SOLID.location),
    SEARCH("\uf689", FontLocations.FONT_AWESOME_REGULAR.location),
    HISTORY("\uF1DA", FontLocations.FONT_AWESOME_SOLID.location);

    public final String unicode;
    public final String fontLocation;
    FontAwesome(String unicode, String fontLocation) {
        this.unicode = unicode;
        this.fontLocation = fontLocation;
    }
}
