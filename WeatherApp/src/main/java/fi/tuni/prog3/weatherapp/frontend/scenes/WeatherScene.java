package fi.tuni.prog3.weatherapp.frontend.scenes;

import fi.tuni.prog3.weatherapp.backend.database.cities.Cities;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;

public class WeatherScene extends Scene {
    private static WeatherScene INSTANCE;
    private static Stage STAGE;
    private static ScrollPane root = new ScrollPane();
    public WeatherScene(Stage stage) {
        super(root, 720, 720);
        if (INSTANCE != null) {
            throw new RuntimeException("WeatherScene has already been constructed!");
        }
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

        return INSTANCE;
    }
    public static void SwitchToSearchScene() {
        STAGE.setScene(SearchScene.getInstance());
    }
}
