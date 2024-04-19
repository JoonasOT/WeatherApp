package fi.tuni.prog3.weatherapp.frontend.fonts;

public enum FontLocations {
    WEATHER("file:./res/font/weather.ttf"),
    FONT_AWESOME_REGULAR("file:res/font/FontAwesome6Free-Regular-400.otf"),
    FONT_AWESOME_SOLID("file:res/font/FontAwesome6Free-Solid-900.otf");

    public final String location;
    FontLocations(String location){ this.location = location; }
}
