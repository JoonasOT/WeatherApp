package fi.tuni.prog3.weatherapp.backend.api.ip;

import fi.tuni.prog3.weatherapp.backend.api.API;
import fi.tuni.prog3.weatherapp.backend.api.iCallable;

import java.lang.reflect.Method;
import java.util.Optional;

public final class IPService {
    private static IPService INSTANCE;
    private static final API ipGetter = new IP_Getter.factory().construct();
    private static Optional<String> IP = Optional.empty();
    private IPService() {
        solveIP();
    }
    public static IPService getInstance() {
        return INSTANCE != null ? INSTANCE : (INSTANCE = new IPService());
    }
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
    public Optional<String> getIP() {
        return IP;
    }
}
