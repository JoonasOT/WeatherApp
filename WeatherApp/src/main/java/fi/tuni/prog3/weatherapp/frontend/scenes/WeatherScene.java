package fi.tuni.prog3.weatherapp.frontend.scenes;

import fi.tuni.prog3.weatherapp.WeatherApp;
import fi.tuni.prog3.weatherapp.backend.Backend;
import fi.tuni.prog3.weatherapp.backend.api.openweather.Geocoder;
import fi.tuni.prog3.weatherapp.backend.api.openweather.Geocoder.*;
import fi.tuni.prog3.weatherapp.backend.api.openweather.JSON_OBJs.Coord;
import fi.tuni.prog3.weatherapp.backend.api.openweather.OpenWeather;
import fi.tuni.prog3.weatherapp.backend.database.cities.Cities;
import fi.tuni.prog3.weatherapp.frontend.weather.CustomToolBar;
import fi.tuni.prog3.weatherapp.frontend.weather.current.CurrentWeatherView;
import fi.tuni.prog3.weatherapp.frontend.weather.daily.DailyForecast;
import fi.tuni.prog3.weatherapp.frontend.weather.forecast.WeatherForecastView;
import fi.tuni.prog3.weatherapp.frontend.weather.map.MapLoader;
import fi.tuni.prog3.weatherapp.frontend.weather.map.WeatherMapView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.concurrent.atomic.AtomicReference;

public class WeatherScene extends Scene {
    private static OpenWeather.UNIT UNIT;
    private static WeatherScene INSTANCE;
    private static Stage STAGE;
    private static final ScrollPane content = new ScrollPane();
    private static final StackPane root = new StackPane(content);
    private static CustomToolBar toolBar;
    private static boolean isFavourite;
    private static Cities.City currentCity;
    private static Coord coords = null;
    private static boolean alive = true;
    public WeatherScene(Stage stage) {
        super(root, WeatherApp.WINDOW_WIDTH, WeatherApp.WINDOW_HEIGHT);
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
    public static WeatherScene getInstance() {
        if (INSTANCE == null) {
            throw new RuntimeException("WeatherScene has not been constructed!");
        }
        toolBar = new CustomToolBar();
        return INSTANCE;
    }
    public static OpenWeather.UNIT getUNIT(){ return UNIT; };
    public WeatherScene generateFromCity(Cities.City city, OpenWeather.UNIT unit) {
        toolBar = new CustomToolBar();
        UNIT = unit;
        VBox views = new VBox(10);
        currentCity = city;

        Backend backend = Backend.getInstance();
        backend.addToHistory(city);
        isFavourite = backend.getFavourites().stream().anyMatch(city1 -> city1.equals(city));

        coords = backend.callOpenWeatherWith(new Callables.GeocoderCallable(currentCity, 1), Geocoder.class)
                        .map(geocoderObj -> {
                            try {
                                return new Coord(((GeocoderObj)geocoderObj).cities()[0].lon(),
                                        ((GeocoderObj)geocoderObj).cities()[0].lat());
                            } catch (ArrayIndexOutOfBoundsException ignored) {
                                return null;
                            }
                        })
                        .orElse(null);
        CurrentWeatherView currentWeatherView = new CurrentWeatherView();

        if (!currentWeatherView.isOK()) {
            views.getChildren().add(currentWeatherView);
        } else {
            var Daily = new DailyForecast();
            currentWeatherView.setPartOfDay(Daily.isCurrentlyDay());

            views.getChildren().addAll(
                    currentWeatherView,
                    Daily,
                    new WeatherForecastView()
            );
            MapLoader.loadTo(views);
        }
        views.setPadding(new Insets(0, 90, 0, 90));
        content.setContent(views);
        return INSTANCE;
    }
    public static Coord getCoords() { return coords; }
    public static String getCity() {
        return currentCity.name() + (currentCity.countryCode() != null ? "," + currentCity.countryCode() : "");
    }
    public static void SwitchToSearchScene() {
        STAGE.setScene(SearchScene.getInstance());
    }
    public static boolean isThisFavourite() {
        return isFavourite;
    }
    public static void AddCityToFavourites() {
        Backend backend = Backend.getInstance();
        if (isFavourite) backend.removeFromFavourites(currentCity);
        else backend.addFavourite(currentCity);
        isFavourite = !isFavourite;
    }
    public static void Shutdown() {
        alive = false;
    }
    public static boolean hasShutdown() { return !alive; }
}
