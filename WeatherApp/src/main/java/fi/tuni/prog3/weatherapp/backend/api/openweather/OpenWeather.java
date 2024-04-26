package fi.tuni.prog3.weatherapp.backend.api.openweather;

import fi.tuni.prog3.weatherapp.WeatherApp;
import fi.tuni.prog3.weatherapp.backend.Backend;
import fi.tuni.prog3.weatherapp.backend.api.general.API;
import fi.tuni.prog3.weatherapp.backend.api.general.API_Factory;
import fi.tuni.prog3.weatherapp.backend.security.Key;

import java.io.IOException;

/**
 * A Structure that holds the most centralized OpenWeather functionality:
 * units, languages and factory for constructing an API class object
 *
 * @author Joonas Tuominen
 */
public class OpenWeather {
    /**
     * All the possible units allowed in an OpenWeather API call
     */
    public enum UNIT {
        STANDARD, METRIC, IMPERIAL;

        /**
         * A static function that parses the unit from a string (case-insensitive). If unable to find
         * a unit with the same name, returns METRIC by default.
         * @param s The string we want to parse the unit from
         * @return The unit parsed or if unsuccessful defaults to METRIC.
         */
        public static UNIT fromString(String s) {
            for (var v : values()) if (v.name().equals(s.toUpperCase())) return v;
            return METRIC;
        }

        /**
         * Returns the name of the unit in lowercase.
         * @return the name of unit in lowercase.
         */
        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }

    /**
     * All the possible languages allowed in an OpenWeather API call
     */
    public enum LANG {
        AF, AL, AR, AZ, BG, CA, CZ, DA, DE, EL, EN, EU, FA, FI, FR, GL, HE, HI, HR, HU, ID, IT, JA, KR, LA, LT, MK, NO,
        NL, PL, PT, PT_BR, RO, RU, SV, SE, SK, SL, SP, ES, SR, TH, TR, UA, UK, VI, ZH_CN, ZH_TW, ZU;
        /**
         * Returns the name of the language abbreviation in lowercase.
         * @return the name of language abbreviation in lowercase.
         */
        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }

    /**
     * API factory used to construct an OpenWeather API class.
     */
    public static class factory implements API_Factory {
        private Key key = null;

        /**
         * Constructor for a OpenWeather factory
         */
        public factory() {
            // Get the key
            try {
                key = new Key(Backend.OPENWEATHER_API_KEY_LOCATION);
            }
            catch (IOException e) {
                System.err.println("Key threw an error!");
                e.printStackTrace(System.err);
            }
        }

        /**
         * Constructs an OpenWeather API class
         * @return API for OpenWeather
         */
        @Override
        public API construct() {
            return new API(this);
        }

        /**
         * Function for getting the API key
         * @return The api key
         */
        @Override
        public Key getKey() {
            return key;
        }
    }
}
