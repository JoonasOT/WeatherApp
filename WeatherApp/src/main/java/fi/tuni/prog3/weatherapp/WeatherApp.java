package fi.tuni.prog3.weatherapp;

import fi.tuni.prog3.weatherapp.backend.Backend;
import fi.tuni.prog3.weatherapp.frontend.scenes.SearchScene;
import fi.tuni.prog3.weatherapp.frontend.search.Search;
import fi.tuni.prog3.weatherapp.frontend.search.SearchResult;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


/**
 * JavaFX Weather Application.
 */
public class WeatherApp extends Application {

    @Override
    public void start(Stage stage) {

        stage.setScene(SearchScene.create());




        stage.setTitle("WeatherApp");
        stage.show();
    }
}