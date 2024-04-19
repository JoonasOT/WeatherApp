package fi.tuni.prog3.weatherapp.backend.api.openweather;

import fi.tuni.prog3.weatherapp.backend.api.general.RequestMethod;
import fi.tuni.prog3.weatherapp.backend.api.general.SetRequestProperty;
import fi.tuni.prog3.weatherapp.backend.api.general.iCallable;

import java.util.Map;

public class WeatherMap {
    public enum WeatherLayer {
        CLOUDS, PRECIPITATION, PRESSURE, WIND, TEMP;
        @Override
        public String toString() {
            return name().toLowerCase() + "_new";
        }
        public static WeatherLayer fromString(String s) {
            for (WeatherLayer layer : values()) if (layer.name().equals(s.toUpperCase())) return layer;
            return null;
        }
    }
    public static class URLs {
        public static final String WEATHER_MAP = "https://tile.openweathermap.org/map/{layer}/{z}/{x}/{y}.png?appid={API key}";
        public static final String OSM_MAP = "https://tile.openstreetmap.org/{z}/{x}/{y}.png";
    }
    public static class Callables {
        public record MapTile(boolean isMap, WeatherLayer layer, String userAgent){}
        @RequestMethod(method = "GET")
        public record WeatherMapCallable(WeatherLayer layer, int z, double lat, double log) implements iCallable {
            @Override public String url() { return URLs.WEATHER_MAP; }
            @Override
            public Map<String, String> args() {
                return Map.of(
                        "{layer}", layer.toString(),
                        "{z}", Integer.toString(z),
                        "{x}", Integer.toString(longitudeToX(log, z)),
                        "{y}", Integer.toString(latitudeToY(lat, z)));
            }
        }
        @RequestMethod(method = "GET")
        public record WeatherMapTileCallable(WeatherLayer layer, int x, int y, int z) implements iCallable {
            @Override public String url() { return URLs.WEATHER_MAP; }
            @Override
            public Map<String, String> args() {
                return Map.of(
                        "{layer}", layer.toString(),
                        "{z}", Integer.toString(z),
                        "{x}", Integer.toString(x),
                        "{y}", Integer.toString(y));
            }
        }
        @RequestMethod(method = "GET")
        public record OpenStreetMapCallable(String userAgent, int z, double lat, double log)
                                                                                    implements iCallable {
            @SetRequestProperty(Property = "User-Agent")
            public String getUserAgent() {
                return userAgent;
            }

            @Override public String url() { return URLs.OSM_MAP; }
            @Override
            public Map<String, String> args() {
                return Map.of(
                        "{z}", Integer.toString(z),
                        "{x}", Integer.toString(longitudeToX(log, z)),
                        "{y}", Integer.toString(latitudeToY(lat, z)));
            }
        }
        @RequestMethod(method = "GET")
        public record OpenStreetMapTileCallable(String userAgent, int x, int y, int z)
                implements iCallable {
            @SetRequestProperty(Property = "User-Agent")
            public String getUserAgent() {
                return userAgent;
            }

            @Override public String url() { return URLs.OSM_MAP; }
            @Override
            public Map<String, String> args() {
                return Map.of(
                        "{z}", Integer.toString(z),
                        "{x}", Integer.toString(x),
                        "{y}", Integer.toString(y));
            }
        }
    }

    public static int longitudeToX(double log, int z) {
        return (int) (Math.pow(2, z) * ((log + 180.0) / 360));
    }
    public static int latitudeToY(double lat, int z) {
        return (int) (Math.pow(2, z) / 2 * (1 - Math.log(Math.tan(lat * Math.PI/180.0) + 1.0 / Math.cos(lat * Math.PI / 180.0)) / Math.PI));
    }
}
