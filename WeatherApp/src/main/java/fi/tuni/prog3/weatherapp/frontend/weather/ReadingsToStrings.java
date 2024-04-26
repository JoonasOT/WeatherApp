package fi.tuni.prog3.weatherapp.frontend.weather;

import fi.tuni.prog3.weatherapp.backend.api.openweather.OpenWeather.UNIT;

/**
 * A structure that holds some methods for converting weather readings to strings with given units
 *
 * @author Joonas Tuominen
 */
public class ReadingsToStrings {
    /**
     * Convert a given temperature to a string with the given units
     * @param temp The temperature we want to form a string of
     * @param unit The units we want to use
     * @return The resulting string
     */
    public static String getTemperature(double temp, UNIT unit) {
        return String.format("%.1f%s", temp, (unit != UNIT.STANDARD ? "°" : " ") + switch (unit) {
            case METRIC -> "C";
            case IMPERIAL -> "F";
            case STANDARD -> "K";
        });
    }

    /**
     * Convert a given wind speed to a string with the given units
     * @param wind The wind speed we want to form a string of
     * @param unit The units we want to use
     * @return The resulting string
     */
    public static String getWindSpeed(double wind, UNIT unit) {
        return String.format("%.1f%s", wind, " " + switch (unit) {
            case METRIC, STANDARD -> "m/s";
            case IMPERIAL -> "mph";
        });
    }

    /**
     * Convert a given wind direction to a string with the given units
     * @param deg The wind direction we want to form a string of
     * @param unit The units we want to use
     * @return The resulting string
     */
    public static String getWindDirection(int deg, UNIT unit) {
        return Integer.toString(deg) + (unit != UNIT.STANDARD ? "°" : "");
    }
}
