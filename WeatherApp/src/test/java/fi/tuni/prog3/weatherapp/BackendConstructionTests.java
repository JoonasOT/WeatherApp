package fi.tuni.prog3.weatherapp;

import fi.tuni.prog3.weatherapp.backend.Backend;
import org.junit.jupiter.api.Test;


/**
 * A basic tests to see that the backend at least somewhat constructs itself
 * @author Joonas Tuominen
 */
public class BackendConstructionTests {
    @Test
    public void BackendConstructorWithoutOpenWeatherKey() {
        Backend.OPENWEATHER_API_KEY_LOCATION = "";
        Backend backend = Backend.getInstance();
        // Get the printout for error in constructor
        Backend.Shutdown();
    }

    @Test
    public void BackendConstructorDummyOpenWeatherKey() {
        Backend.OPENWEATHER_API_KEY_LOCATION = "./secrets/ApiKeys/test/dummy_key";
        Backend backend = Backend.getInstance();
        // Cities should fail
        Backend.Shutdown();
    }
}
