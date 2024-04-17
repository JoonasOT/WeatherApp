package fi.tuni.prog3.weatherapp.backend.api.ip;

import fi.tuni.prog3.weatherapp.backend.api.general.API;
import fi.tuni.prog3.weatherapp.backend.api.general.iCallable;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * A singleton service that provides the user's public IP.
 *
 * @author Joonas Tuominen
 */
public final class IPService {
    private static IPService INSTANCE;
    private static final API ipGetter = new IP_Getter.factory().construct();
    private static Optional<String> IP = Optional.empty();

    /**
     * Hidden constructor that solves the user's public IP.
     */
    private IPService() {
        solveIP();
    }

    /**
     * Get the service and construct it if necessary.
     * @return The singleton IPService.
     */
    public static IPService getInstance() {
        return INSTANCE != null ? INSTANCE : (INSTANCE = new IPService());
    }

    /**
     * A method for getting the current public IP address of the user. To get the result of this method use the
     * getIP() function.
     */
    public void solveIP() {
        for (Method method : IP_Getter.Callables.class.getDeclaredMethods()) {
            try {
                var ipResult = ipGetter.call((iCallable) method.invoke(null));
                if (ipResult.isEmpty()) continue;
                IP = Optional.of(ipResult.get().getData());
                return;
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }

    /**
     * Getter for the user's public IP.
     * @return An optional containing the user's public IP.
     */
    public Optional<String> getIP() {
        return IP;
    }
}
