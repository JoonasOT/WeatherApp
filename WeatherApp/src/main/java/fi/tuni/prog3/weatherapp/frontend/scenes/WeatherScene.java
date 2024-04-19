package fi.tuni.prog3.weatherapp.frontend.scenes;

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
import fi.tuni.prog3.weatherapp.frontend.weather.map.WeatherMapView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class WeatherScene extends Scene {
    private static OpenWeather.UNIT UNIT;
    private static WeatherScene INSTANCE;
    private static Stage STAGE;
    private static final ScrollPane content = new ScrollPane();
    private static final StackPane root = new StackPane();
    private static CustomToolBar toolBar;
    private static boolean isFavourite;
    private static Cities.City currentCity;
    private static Coord coords = null;
    private static WeatherMapView mapView;
    public WeatherScene(Stage stage) {
        super(root, 900, 720);
        if (INSTANCE != null) {
            throw new RuntimeException("WeatherScene has already been constructed!");
        }
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(content);
        borderPane.setPadding(new Insets(0, 90, 0, 90));
        root.getChildren().add(borderPane);

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
        VBox views = new VBox(0);
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
            mapView = new WeatherMapView();
            views.getChildren().addAll(
                    currentWeatherView,
                    new DailyForecast(),
                    new WeatherForecastView(),
                    mapView
            );
        }
        content.setContent(views);
        content.setVvalue(0.0);
        return INSTANCE;
    }
    public static Coord getCoords() { return coords; } // TODO: Have a toggle here maybe or smt!
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
        if (mapView != null) mapView.kill();
    }
}
