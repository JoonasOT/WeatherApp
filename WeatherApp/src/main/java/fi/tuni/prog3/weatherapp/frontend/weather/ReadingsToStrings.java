package fi.tuni.prog3.weatherapp.frontend.weather;

import fi.tuni.prog3.weatherapp.backend.api.openweather.OpenWeather.UNIT;


public class ReadingsToStrings {
    public static String getTemperature(double temp, UNIT unit) {
        return String.format("%.1f%s", temp, (unit != UNIT.STANDARD ? "°" : " ") + switch (unit) {
            case METRIC -> "C";
            case IMPERIAL -> "F";
            case STANDARD -> "K";
        });
    }
    public static String getWindSpeed(double wind, UNIT unit) {
        return String.format("%.1f%s", wind, " " + switch (unit) {
            case METRIC, STANDARD -> "m/s";
            case IMPERIAL -> "mph";
        });
    }
    public static String getWindDirection(int deg, UNIT unit) {
        return Integer.toString(deg) + "°";
    }
}
