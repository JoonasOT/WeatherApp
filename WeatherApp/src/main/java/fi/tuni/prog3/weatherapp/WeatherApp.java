package fi.tuni.prog3.weatherapp;

import fi.tuni.prog3.weatherapp.frontend.CustomToolBar;
import fi.tuni.prog3.weatherapp.frontend.scenes.SearchScene;
import fi.tuni.prog3.weatherapp.frontend.scenes.WeatherScene;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


/**
 * JavaFX Weather Application.
 */
public class WeatherApp extends Application {

    @Override
    public void start(Stage stage) {
        stage.initStyle(StageStyle.UTILITY);

        new SearchScene(stage);
        new WeatherScene(stage);

        stage.setScene(SearchScene.getInstance());

        stage.setTitle("WeatherApp");
        stage.show();
    }
    @Override
    public void stop() {
        System.out.println("Shutting down!");
    }
}