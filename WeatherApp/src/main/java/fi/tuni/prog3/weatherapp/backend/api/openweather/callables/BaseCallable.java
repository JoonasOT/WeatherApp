package fi.tuni.prog3.weatherapp.backend.api.openweather.callables;

import fi.tuni.prog3.weatherapp.backend.api.openweather.OpenWeather;
import fi.tuni.prog3.weatherapp.backend.api.general.iCallable;

import java.util.HashMap;
import java.util.Map;

/**
 * A base class for most of the Callables used in OpenWeather calls. Provides methods for adding unit
 * and language arguments to the request.
 *
 * @author Joonas Tuominen
 */
public class BaseCallable implements iCallable {
    protected String url;
    protected HashMap<String, String> args;

    /**
     * Constructor for a base callable that takes an url and a map for arguments to their values.
     * @param url The url we want to call.
     * @param args The arguments contained in the url and the values we want to switch them to.
     */
    public BaseCallable(String url, Map<String, String> args) {
        this.url = url;
        this.args = new HashMap<>(args);
    }

    /**
     * A getter for the url.
     * @return The url we want to call.
     */
    @Override
    public String url() {
        return url;
    }

    /**
     * The arguments of the call.
     * @return The arguments and their values.
     */
    @Override
    public Map<String, String> args() {
        return args;
    }

    /**
     * A simple method used to specify the units we want the request to be.
     * @param unit The unit we want the response to be in.
     * @return This BaseCallable after this operation.
     */
    public BaseCallable addUnitsArg(OpenWeather.UNIT unit) {
        url += "&units={unit}";
        args.put("{unit}", unit.toString());
        return this;
    }
    /**
     * A simple method used to specify the language we want the request to be.
     * @param language The language we want the response to be in.
     * @return This BaseCallable after this operation.
     */
    public BaseCallable addLangArg(OpenWeather.LANG language) {
        url += "&lang={lang}";
        args.put("{lang}", language.toString());
        return this;
    }
}
