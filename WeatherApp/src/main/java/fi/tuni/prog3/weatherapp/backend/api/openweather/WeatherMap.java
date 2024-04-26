package fi.tuni.prog3.weatherapp.backend.api.openweather;

import fi.tuni.prog3.weatherapp.backend.api.general.RequestMethod;
import fi.tuni.prog3.weatherapp.backend.api.general.SetRequestProperty;
import fi.tuni.prog3.weatherapp.backend.api.general.iCallable;

import java.util.Map;


/**
 * A structure that holds functionality for getting weather map tiles from OpenWeather and map tiles from OpenStreetMaps.
 *
 * @author Joonas Tuominen
 */
public class WeatherMap {
    /**
     * An enum for all the possible weather layers available on Weather Maps 1.0 on OpenWeather
     */
    public enum WeatherLayer {
        CLOUDS, PRECIPITATION, PRESSURE, WIND, TEMP;

        /**
         * Converts the layer name to lowercase and adds "_new"
         * @return layer name in lowercase + "_new"
         */
        @Override
        public String toString() {
            return name().toLowerCase() + "_new";
        }

        /**
         * Parses weather layer from a string
         * @param s String containing a name of a weather layer
         * @return The weather layer or null on error
         */
        public static WeatherLayer fromString(String s) {
            for (WeatherLayer layer : values()) if (layer.name().equals(s.toUpperCase())) return layer;
            return null;
        }
    }

    /**
     * A structure holding all the URLs used by the map callables.
     */
    public static class URLs {
        public static final String WEATHER_MAP = "https://tile.openweathermap.org/map/{layer}/{z}/{x}/{y}.png?appid={API key}";
        public static final String OSM_MAP = "https://tile.openstreetmap.org/{z}/{x}/{y}.png";
    }

    /**
     * A structure containing all the callables for map callables.
     */
    public static class Callables {
        /**
         * A record for sharing data of a map tile.
         * @param isMap Is this tile an OpenStreetMaps tile?
         * @param layer What is the OpenWeather weather layer?
         * @param userAgent What is the OpenStreetMaps user agent?
         */
        public record MapTile(boolean isMap, WeatherLayer layer, String userAgent){}

        /**
         * A callable for getting weather layer images from OpenWeather
         * @param layer The weather layer we want to get
         * @param z The z index or zoom index
         * @param lat The latitude of the location we want to get the photo of
         * @param log The longitude of the location we want to get the photo of
         */
        @RequestMethod(method = "GET")
        public record WeatherMapCallable(WeatherLayer layer, int z, double lat, double log) implements iCallable {
            /**
             * Sets the callable url to OpenWeather map url
             * @return OpenWeather map url
             */
            @Override public String url() { return URLs.WEATHER_MAP; }

            /**
             * Sets the arguments to be replaced to the ones gotten from WeatherMapCallable constructor
             * @return The arguments map
             */
            @Override
            public Map<String, String> args() {
                return Map.of(
                        "{layer}", layer.toString(),
                        "{z}", Integer.toString(z),
                        "{x}", Integer.toString(longitudeToX(log, z)),
                        "{y}", Integer.toString(latitudeToY(lat, z)));
            }
        }

        /**
         * A callable for getting weather layer images from OpenWeather
         * @param layer The weather layer we want to get
         * @param x The tile's x index
         * @param y The tile's y index
         * @param z The z index or zoom index
         */
        @RequestMethod(method = "GET")
        public record WeatherMapTileCallable(WeatherLayer layer, int x, int y, int z) implements iCallable {
            /**
             * Sets the callable url to OpenWeather map url
             * @return OpenWeather map url
             */
            @Override public String url() { return URLs.WEATHER_MAP; }
            /**
             * Sets the arguments to be replaced to the ones gotten from WeatherMapCallable constructor
             * @return The arguments map
             */
            @Override
            public Map<String, String> args() {
                return Map.of(
                        "{layer}", layer.toString(),
                        "{z}", Integer.toString(z),
                        "{x}", Integer.toString(x),
                        "{y}", Integer.toString(y));
            }
        }

        /**
         * A callable for getting map images from OpenStreetMaps
         * @param userAgent The user agent for OpenStreetMaps
         * @param z The z index or zoom index
         * @param lat The latitude of the location we want to get the photo of
         * @param log The longitude of the location we want to get the photo of
         */
        @RequestMethod(method = "GET")
        public record OpenStreetMapCallable(String userAgent, int z, double lat, double log)
                                                                                    implements iCallable {
            /**
             * A method for adding the User-Agent property for the OpenStreetMaps callable
             * @return The user agents value
             */
            @SetRequestProperty(Property = "User-Agent")
            public String getUserAgent() {
                return userAgent;
            }
            /**
             * Sets the callable url to OpenWeatherMap url
             * @return OpenWeatherMap url
             */
            @Override public String url() { return URLs.OSM_MAP; }

            /**
             * Sets the arguments to be replaced to the ones gotten from OpenStreetMapCallable constructor
             * @return The arguments map
             */
            @Override
            public Map<String, String> args() {
                return Map.of(
                        "{z}", Integer.toString(z),
                        "{x}", Integer.toString(longitudeToX(log, z)),
                        "{y}", Integer.toString(latitudeToY(lat, z)));
            }
        }

        /**
         * A callable for getting map tile images from OpenStreetMaps
         * @param userAgent The user agent for OpenStreetMaps
         * @param x The tile's x index
         * @param y The tile's y index
         * @param z The z index or zoom index
         */
        @RequestMethod(method = "GET")
        public record OpenStreetMapTileCallable(String userAgent, int x, int y, int z)
                implements iCallable {

            /**
             * A method for adding the User-Agent property for the OpenStreetMaps callable
             * @return The user agents value
             */
            @SetRequestProperty(Property = "User-Agent")
            public String getUserAgent() {
                return userAgent;
            }

            /**
             * Sets the callable url to OpenWeatherMap url
             * @return OpenWeatherMap url
             */
            @Override public String url() { return URLs.OSM_MAP; }

            /**
             * Sets the arguments to be replaced to the ones gotten from OpenStreetMapCallable constructor
             * @return The arguments map
             */
            @Override
            public Map<String, String> args() {
                return Map.of(
                        "{z}", Integer.toString(z),
                        "{x}", Integer.toString(x),
                        "{y}", Integer.toString(y));
            }
        }
    }

    /**
     * A static function for converting longitudes with given z index to an x tile index.
     * @param log The longitude we want to convert
     * @param z The z index we are using
     * @return The x index of the tile over this location
     */
    public static int longitudeToX(double log, int z) {
        return (int) (Math.pow(2, z) * ((log + 180.0) / 360));
    }

    /**
     * A static function for converting latitudes with given z index to an y tile index.
     * @param lat The latitude we want to convert
     * @param z The z index we are using
     * @return The y index of the tile over this location
     */
    public static int latitudeToY(double lat, int z) {
        return (int) (Math.pow(2, z) / 2 * (1 - Math.log(Math.tan(lat * Math.PI/180.0) + 1.0 / Math.cos(lat * Math.PI / 180.0)) / Math.PI));
    }
}
