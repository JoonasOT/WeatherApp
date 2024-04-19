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
    public static String OPENWEATHER_API_KEY_LOCATION;
    public static void main(String[] args) {
        if (args.length == 1) OPENWEATHER_API_KEY_LOCATION = args[0];
        else OPENWEATHER_API_KEY_LOCATION = "./secrets/ApiKeys/OpenWeather";
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
        Backend.Shutdown();
        System.out.println("Shutting down!");
    }
}