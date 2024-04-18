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
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class WeatherScene extends Scene {
    private static OpenWeather.UNIT UNIT;
    private static WeatherScene INSTANCE;
    private static Stage STAGE;
    private static final ScrollPane content = new ScrollPane();
    private static final BorderPane root = new BorderPane(content);
    private static boolean isFavourite;
    private static Cities.City currentCity;
    private static Coord coords = null;
    public WeatherScene(Stage stage) {
        super(root, 720, 720);
        if (INSTANCE != null) {
            throw new RuntimeException("WeatherScene has already been constructed!");
        }
        root.setTop(new CustomToolBar());
        content.setMaxHeight(720 - CustomToolBar.HEIGHT);
        content.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        STAGE = stage;

        INSTANCE = this;
    }
    public static WeatherScene getInstance() {
        if (INSTANCE == null) {
            throw new RuntimeException("WeatherScene has not been constructed!");
        }
        root.setTop(new CustomToolBar());
        return INSTANCE;
    }
    public static OpenWeather.UNIT getUNIT(){ return UNIT; };
    public WeatherScene generateFromCity(Cities.City city, OpenWeather.UNIT unit) {
        UNIT = unit;
        VBox views = new VBox(0);
        currentCity = city;

        Backend backend = Backend.getInstance();
        isFavourite = backend.getFavourites().stream().anyMatch(city1 -> city1.equals(city));

        coords = backend.callOpenWeatherWith(new Callables.GeocoderCallable(currentCity, 1),Geocoder.class)
                        .map(geocoderObj -> {
                            try {
                                return new Coord(((GeocoderObj)geocoderObj).cities()[0].lon(),
                                        ((GeocoderObj)geocoderObj).cities()[0].lat());
                            } catch (ArrayIndexOutOfBoundsException ignored) {
                                return null;
                            }
                        })
                        .orElse(null);

        views.getChildren().addAll(
                new CurrentWeatherView(),
                new DailyForecast(),
                new WeatherForecastView()
        );
        content.setContent(views);

        return INSTANCE;
    }
    public static Coord getCoords() { return null; } // TODO: Have a toggle here maybe or smt!
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
}
