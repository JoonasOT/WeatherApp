package fi.tuni.prog3.weatherapp.backend.api.openweather;

public class JSON_OBJs {
    public record Coord(String lon, String lat){};
    public record Weather(int id, String main, String description, String icon){};
    public record Clouds(int all){};
}
