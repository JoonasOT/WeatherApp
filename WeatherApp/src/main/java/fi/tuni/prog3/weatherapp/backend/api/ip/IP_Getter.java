package fi.tuni.prog3.weatherapp.backend.api.ip;

import fi.tuni.prog3.weatherapp.backend.api.general.API;
import fi.tuni.prog3.weatherapp.backend.api.general.API_Factory;
import fi.tuni.prog3.weatherapp.backend.api.general.RequestMethod;
import fi.tuni.prog3.weatherapp.backend.api.general.iCallable;
import fi.tuni.prog3.weatherapp.backend.security.Key;

import java.util.Map;

/**
 * A class that forms an API that gets the user's public IP.
 *
 * @author Joonas Tuominen
 */
public class IP_Getter {
    /**
     * A structure that holds all the URLs this class uses.
     */
    private static class URLs {
        private static final String IP_AWS = "https://checkip.amazonaws.com/";
        private static final String IP_HAZ = "https://ipv4.icanhazip.com/";
        private static final String IP_MY_EXTERN = "https://myexternalip.com/raw";
        private static final String IP_ECHO = "https://ipecho.net/plain";
    };

    /**
     * A structure that holds all the different callables that can be used to call this API.
     */
    public static class Callables {
        @RequestMethod(method = "GET")
        private record IP_BASE_CALLABLE(String url, Map<String, String> args) implements iCallable {}
        public static iCallable IP_AWS() { return new IP_BASE_CALLABLE(URLs.IP_AWS, iCallable.NO_ARGS); }
        public static iCallable IP_HAZIP() {
            return new IP_BASE_CALLABLE(URLs.IP_HAZ, iCallable.NO_ARGS);
        }
        public static iCallable IP_MY_EXTERNAL() {
            return new IP_BASE_CALLABLE(URLs.IP_MY_EXTERN, iCallable.NO_ARGS);
        }
        public static iCallable IP_ECHO() {
            return new IP_BASE_CALLABLE(URLs.IP_ECHO, iCallable.NO_ARGS);
        }
    }

    /**
     * A dumb way of forming an API of this type.
     * This API factory does not require a key.
     */
    public static class factory implements API_Factory {
        private final Key key = new Key();

        /**
         * Constructor for an IP requester API.
         */
        public factory() {}

        /**
         * Construct an API from this API factory.
         * @return An API that can be used to run IP_GETTER.Callables
         */
        @Override
        public API construct() {
            return new API(this);
        }

        /**
         * A getter for the key of this IP requester. This is always a default key.
         * @return new Key()
         */
        @Override
        public Key getKey() {
            return key;
        }
    }
}
