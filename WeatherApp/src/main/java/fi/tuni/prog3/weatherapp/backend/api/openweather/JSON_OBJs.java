package fi.tuni.prog3.weatherapp.backend.api.openweather;

public class JSON_OBJs {
    public record Coord(double lon, double lat){};
    public record Weather(int id, String main, String description, String icon){};
    public record Clouds(int all){};
}
