package fi.tuni.prog3.weatherapp;

import fi.tuni.prog3.weatherapp.backend.Backend;
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

        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 720, 720);
        stage.setScene(scene);

        VBox searchArea = new VBox();
        root.setCenter(searchArea);
        root.setPadding(new Insets(280, 10, 10, 10));
        TextField search = new TextField("TEST");
        searchArea.setMaxWidth(150);
        searchArea.getChildren().add(search);

        var results = Search.GenerateResults("sinäjoki");
        for (SearchResult sr : results) {
            searchArea.getChildren().add(sr);
        }


        if (false) {
            //Creating a new BorderPane.
            root.setPadding(new Insets(10, 10, 10, 10));

            //Adding HBox to the center of the BorderPane.
            root.setCenter(getCenterVBox());

            //Adding button to the BorderPane and aligning it to the right.
            var quitButton = getQuitButton();
            BorderPane.setMargin(quitButton, new Insets(10, 10, 0, 10));
            root.setBottom(quitButton);
            BorderPane.setAlignment(quitButton, Pos.TOP_RIGHT);
        }

        stage.setTitle("WeatherApp");
        stage.show();
    }

    public static void main(String[] args) {
        Backend backend = Backend.getInstance();

        launch();
    }

    private VBox getCenterVBox() {
        //Creating an HBox.
        VBox centerHBox = new VBox(10);

        //Adding two VBox to the HBox.
        centerHBox.getChildren().addAll(getTopHBox(), getBottomHBox());

        return centerHBox;
    }

    private HBox getTopHBox() {
        //Creating a VBox for the left side.
        HBox leftHBox = new HBox();
        leftHBox.setPrefHeight(330);
        leftHBox.setStyle("-fx-background-color: #8fc6fd;");

        leftHBox.getChildren().add(new Label("Top Panel"));

        return leftHBox;
    }

    private HBox getBottomHBox() {
        //Creating a VBox for the right side.
        HBox rightHBox = new HBox();
        rightHBox.setPrefHeight(330);
        rightHBox.setStyle("-fx-background-color: #b1c2d4;");

        rightHBox.getChildren().add(new Label("Bottom Panel"));

        return rightHBox;
    }

    private Button getQuitButton() {
        //Creating a button.
        Button button = new Button("Quit");

        //Adding an event to the button to terminate the application.
        button.setOnAction((ActionEvent event) -> {
            Platform.exit();
        });

        return button;
    }
}