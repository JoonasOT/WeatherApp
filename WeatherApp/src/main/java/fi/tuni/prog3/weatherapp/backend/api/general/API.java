package fi.tuni.prog3.weatherapp.backend.api.general;

import fi.tuni.prog3.weatherapp.backend.security.Key;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * A base class for all API functionality. Can be constructed via an API factory.
 * Is able to take an abstract iCallable and use it to create a request.
 *
 * @author Joonas Tuominen
 */
public class API {
    private final Key key;

    /**
     * Constructor for an abstract API takes an API factory (deprecated but still works)
     * @param factory an API factory used to construct this API.
     */
    public API(API_Factory factory) {
        key = factory.getKey();
    }

    /**
     * Call API with an iCallable. The callable may have additional information attached to it via
     * RequstMethod and SetRequestProperty -interfaces.
     * @param callable An object implementing the iCallable interface used to for the request.
     * @return An optional wrapping a Response object holding the response of the request.
     */
    public Optional<Response> call(iCallable callable) {
        try {
            String url_ = addArgs(callable.url(), callable.args());

            URL url = URI.create(API.addKey(url_, key)).toURL();
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            // Check if we have a request method attached to this class
            con.setRequestMethod(callable.getClass().isAnnotationPresent(RequestMethod.class) ?
                    callable.getClass().getAnnotation(RequestMethod.class).method() : "GET"
            );

            // Check if we have an additional methods that describe additional request properties
            for (Method method : callable.getClass().getDeclaredMethods()) {
                if (method.isAnnotationPresent(SetRequestProperty.class)) {
                    try {
                        con.setRequestProperty(method.getAnnotation(SetRequestProperty.class).Property(),
                            method.invoke(callable).toString());
                    } catch (Exception e) {
                        System.err.println("Was unable to invoke " + method.getName());
                        System.err.println(e.getMessage());
                    }
                }
            }

            // Form the response finally and return it
            Response response = new Response(con);
            con.disconnect();
            return Optional.of(response);
        } catch (IOException ignored) {
            return Optional.empty();
        }
    }

    /**
     * Helper function that simply replaces the string "{API KEY}" with the API key's value
     * @param url The url we want to add this key to
     * @param key The key we want to add to the url
     * @return The resulting string
     */
    private static String addKey(String url, Key key) {
        return url.replace("{API key}", key.getKey());
    }

    /**
     * A recursive function for adding all the arguments contained in a map to an url string.
     * @param url An url that we want to substitute arguments into.
     * @param args A map that connects all the arguments to their values.
     * @return The resultant string.
     */
    private static String addArgs(String url, Map<String, String> args) {
        if (args.isEmpty()) return url;
        var arg = args.keySet().stream().findFirst().get();
        return addArgs(url.replace(arg, args.get(arg)), args.keySet().stream().filter(a -> !Objects.equals(a, arg))
                .collect(Collectors.toMap(a -> a, args::get)));
    }
}
