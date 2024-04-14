package fi.tuni.prog3.weatherapp.backend.api.openweather.callables;

import fi.tuni.prog3.weatherapp.backend.api.openweather.OpenWeather;
import fi.tuni.prog3.weatherapp.backend.api.iCallable;

import java.util.HashMap;
import java.util.Map;

public class BaseCallable implements iCallable {
    protected String url;
    protected HashMap<String, String> args;
    public BaseCallable(String url, Map<String, String> args) {
        this.url = url;
        this.args = new HashMap<>(args);
    }
    @Override
    public String url() {
        return url;
    }

    @Override
    public Map<String, String> args() {
        return args;
    }
    public BaseCallable addUnitsArg(OpenWeather.UNIT unit) {
        url += "&units={unit}";
        args.put("{unit}", unit.toString());
        return this;
    }
    public BaseCallable addLangArg(OpenWeather.LANG language) {
        url += "&lang={lang}";
        args.put("{lang}", language.toString());
        return this;
    }
}