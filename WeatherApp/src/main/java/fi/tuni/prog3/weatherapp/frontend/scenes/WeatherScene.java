package fi.tuni.prog3.weatherapp.frontend.scenes;

import static fi.tuni.prog3.weatherapp.WeatherApp.*;

import fi.tuni.prog3.weatherapp.frontend.weather.CustomToolBar;
import fi.tuni.prog3.weatherapp.frontend.weather.current.CurrentWeatherView;
import fi.tuni.prog3.weatherapp.frontend.weather.daily.DailyForecast;
import fi.tuni.prog3.weatherapp.frontend.weather.forecast.WeatherForecastView;
import fi.tuni.prog3.weatherapp.frontend.weather.map.MapLoader;

import fi.tuni.prog3.weatherapp.backend.Backend;
import fi.tuni.prog3.weatherapp.backend.api.openweather.Geocoder.*;
import fi.tuni.prog3.weatherapp.backend.api.openweather.JSON_OBJs.Coord;
import fi.tuni.prog3.weatherapp.backend.api.openweather.OpenWeather;
import fi.tuni.prog3.weatherapp.backend.database.cities.Cities;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


/**
 * This is a singleton for creating the weather scene to the user.
 *
 * @author Joonas Tuominen
 */
public class WeatherScene extends Scene {
    private static Stage STAGE;
    private static WeatherScene INSTANCE;
    private static OpenWeather.UNIT UNIT;
    private static final ScrollPane content = new ScrollPane();
    private static final StackPane root = new StackPane(content);
    private static CustomToolBar toolBar;
    private static boolean isFavourite;
    private static Cities.City currentCity;
    private static Coord coords = null;
    private static boolean alive = false;
    private static final Object threadSync = new Object();

    /**
     * Constructs the WeatherScene prerequisites.
     * @param stage The stage, where we want this scene to render to.
     * @throws RuntimeException If called for a second time
     */
    public WeatherScene(Stage stage) {
        super(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        if (INSTANCE != null) {
            throw new RuntimeException("WeatherScene has already been constructed!");
        }
        root.setPadding(new Insets(0));

        toolBar = new CustomToolBar();
        root.getChildren().add(toolBar);
        StackPane.setAlignment(toolBar, Pos.TOP_CENTER);
        content.setMaxHeight(720 - CustomToolBar.HEIGHT);
        content.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        content.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        STAGE = stage;

        INSTANCE = this;
    }

    /**
     * Get the WeatherScene if it has been constructed by WeatherApp.java
     * @return The WeatherScene instance
     * @throws RuntimeException If the SearchScene has not been instantiated by WeatherApp.java
     */
    public static WeatherScene getInstance() {
        if (INSTANCE == null) throw new RuntimeException("WeatherScene has not been constructed!");
        toolBar = new CustomToolBar(); // Update the CustomToolBar
        return INSTANCE;
    }

    /**
     * Get the units we want display data with.
     * @return The units we are using
     */
    public static OpenWeather.UNIT getUNIT(){ return UNIT; };

    /**
     * Generate the WeatherScene views based on a city and units
     * @param city The city we want to weather for
     * @param unit The units we want to use
     * @return The WeatherScene instance
     */
    public WeatherScene generateFromCity(Cities.City city, OpenWeather.UNIT unit) {
        alive = true;
        toolBar = new CustomToolBar();
        UNIT = unit;
        VBox views = new VBox(10);
        currentCity = city;

        Backend backend = Backend.getInstance();
        backend.addToHistory(city);

        // Check if the user has set this city as favourite
        isFavourite = backend.getFavourites().stream().anyMatch(city1 -> city1.equals(city));

        // Get the coordinates of the city
        coords = backend.callOpenWeatherWith(new Callables.GeocoderCallable(currentCity, 1), GeocoderObj.class)
                        .map(geocoderObj -> {
                            try { return new Coord(geocoderObj.cities()[0].lon(), geocoderObj.cities()[0].lat()); }
                            catch (ArrayIndexOutOfBoundsException ignored) { return null; }
                        }).orElse(null);

        // Create the current weather view
        CurrentWeatherView currentWeatherView = new CurrentWeatherView();

        // If the creation of the current weather view was ok the city exist
        // TODO: Change this to use coords maybe
        if (!currentWeatherView.isOK()) {
            views.getChildren().add(currentWeatherView);
        } else {
            // City was ok -> we can render all the other views
            var Daily = new DailyForecast();
            currentWeatherView.setPartOfDay(Daily.isCurrentlyDay());

            views.getChildren().addAll(
                    currentWeatherView,
                    Daily,
                    new WeatherForecastView()
            );

            // Start loading the map also to views
            MapLoader.loadTo(views);
        }

        views.setPadding(new Insets(0, 90, 0, 90));
        content.setContent(views);
        return INSTANCE;
    }

    /**
     * Get coordinates of the current city search
     * @return The coordinates for the city
     */
    public static Coord getCoords() { return coords; }

    /**
     * The city with country code if available
     * @return The city queried
     */
    public static String getCity() {
        return currentCity.name() + (currentCity.countryCode() != null ? "," + currentCity.countryCode() : "");
    }

    /**
     * Switch back to the SearchScene
     */
    public static void SwitchToSearchScene() {
        STAGE.setScene(SearchScene.getInstance());
    }

    /**
     * Check if this city has been added to favourites
     * @return True if this city is in favourites. False if not
     */
    public static boolean isThisFavourite() {
        return isFavourite;
    }

    /**
     * Add this current city to favourites
     */
    public static void AddCityToFavourites() {
        Backend backend = Backend.getInstance();
        if (isFavourite) backend.removeFromFavourites(currentCity);
        else backend.addFavourite(currentCity);
        isFavourite = !isFavourite;
    }

    /**
     * Shutdown the WeatherScene and all threads
     */
    public static void Shutdown() {
        alive = false;
        synchronized (threadSync) {
            threadSync.notifyAll();
        }
    }

    /**
     * Get the Sync object used for syncing the map load process
     * @return The synchronization object
     */
    public static Object getSyncObj() {
        return threadSync;
    }

    /**
     * Has the user terminated the application?
     * @return False if still alive. True if not
     */
    public static boolean hasShutdown() { return !alive; }
}
