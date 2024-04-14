package fi.tuni.prog3.weatherapp;

import fi.tuni.prog3.weatherapp.frontend.scenes.SearchScene;
import fi.tuni.prog3.weatherapp.frontend.scenes.WeatherScene;
import javafx.application.Application;
import javafx.stage.Stage;


/**
 * JavaFX Weather Application.
 */
public class WeatherApp extends Application {

    @Override
    public void start(Stage stage) {
        new SearchScene(stage);
        new WeatherScene(stage);

        stage.setScene(SearchScene.getInstance());

        stage.setTitle("WeatherApp");
        stage.show();
    }
}