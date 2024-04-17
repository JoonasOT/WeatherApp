package fi.tuni.prog3.weatherapp.frontend.scenes;

import fi.tuni.prog3.weatherapp.backend.database.cities.Cities;
import fi.tuni.prog3.weatherapp.frontend.CustomToolBar;
import fi.tuni.prog3.weatherapp.frontend.weather.CurrentWeatherView;
import fi.tuni.prog3.weatherapp.frontend.weather.WeatherForecastView;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class WeatherScene extends Scene {
    private static WeatherScene INSTANCE;
    private static Stage STAGE;
    private static final ScrollPane content = new ScrollPane();
    private static final BorderPane root = new BorderPane(content);
    private static Cities.City currentCity;
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
        return INSTANCE;
    }
    public WeatherScene generateFromCity(Cities.City city) {
        VBox views = new VBox(0);
        currentCity = city;
        views.getChildren().addAll(
                new CurrentWeatherView(city),
                new WeatherForecastView(city)
        );
        content.setContent(views);

        return INSTANCE;
    }
    public static void SwitchToSearchScene() {
        STAGE.setScene(SearchScene.getInstance());
    }
    public static void AddCityToFavourites() {
        throw new RuntimeException("TODO");
    }
}
