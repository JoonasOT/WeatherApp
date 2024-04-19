package fi.tuni.prog3.weatherapp;

import fi.tuni.prog3.weatherapp.backend.Backend;
import fi.tuni.prog3.weatherapp.backend.security.Key;
import fi.tuni.prog3.weatherapp.frontend.scenes.SearchScene;
import fi.tuni.prog3.weatherapp.frontend.scenes.WeatherScene;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


/**
 * JavaFX Weather Application.
 */
public class WeatherApp extends Application {
    public static int WINDOW_WIDTH = 900;
    public static int WINDOW_HEIGHT = 720;
    public static void main(String[] args) {
        Key.encryptKey("secrets/ApiKeys/OpenWeatherNew.json", "secrets/ApiKeys/OpenWeatherNew");
        launch( args );
    }
    @Override
    public void start(Stage stage) {
        Backend.getInstance();

        stage.initStyle(StageStyle.UTILITY);

        new SearchScene(stage);
        new WeatherScene(stage);

        stage.setScene(SearchScene.getInstance());

        stage.setTitle("WeatherApp");
        stage.show();
    }
    @Override
    public void stop() {
        WeatherScene.Shutdown();
        System.out.println("Shutting down!");
    }
}