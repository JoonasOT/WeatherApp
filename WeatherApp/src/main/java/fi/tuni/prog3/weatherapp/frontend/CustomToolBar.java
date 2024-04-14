package fi.tuni.prog3.weatherapp.frontend;

import fi.tuni.prog3.weatherapp.backend.Backend;
import fi.tuni.prog3.weatherapp.backend.database.cities.Cities;
import fi.tuni.prog3.weatherapp.frontend.scenes.SearchScene;
import fi.tuni.prog3.weatherapp.frontend.scenes.WeatherScene;
import fi.tuni.prog3.weatherapp.frontend.search.SearchResult;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

import java.util.List;

public class CustomToolBar extends ToolBar {
    private static CustomToolBar INSTANCE;
    public static int HEIGHT = 25;
    private CustomToolBar() {
        super();
        super.setPrefHeight(HEIGHT);
        super.setMaxHeight(HEIGHT);
        super.setMinHeight(HEIGHT);
        super.setMaxWidth(720);

        super.setPadding(new Insets(0));
        super.getItems().addAll(BackToSearch(), Favourites(), History());
    }
    public static CustomToolBar getInstance() {
        return INSTANCE == null ? (INSTANCE = new CustomToolBar()) : INSTANCE;
    }
    private static Button BackToSearch() {
        Button button = new Button("Search");

        button.setPadding(new Insets(0));
        button.setPrefHeight(HEIGHT);
        button.setMaxHeight(HEIGHT);
        button.setMinHeight(HEIGHT);
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                WeatherScene.SwitchToSearchScene();
            }
        });
        return button;
    }
    private static ComboBox<SearchResult> FromList(List<Cities.City> list, String name) {
        ComboBox<SearchResult> comboBox = new ComboBox();
        comboBox.getItems().addAll(SearchResult.CitiesToSearchResults(list));

        comboBox.setPrefHeight(HEIGHT);
        comboBox.setMaxHeight(HEIGHT);
        comboBox.setMinHeight(HEIGHT);

        comboBox.setPadding(new Insets(0));



        comboBox.setValue(new SearchResult(name, ""));
        comboBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                String[] tmp = comboBox.getValue().toString().split(", ");
                Cities.City city = tmp.length == 2 ? new Cities.City(tmp[0], tmp[1]) :
                        new Cities.City(comboBox.getValue().toString(), null);
                SearchScene.ChangeToWeatherScene(city);
            }
        });
        return comboBox;
    }
    private static ComboBox Favourites() {
        return FromList(Backend.getInstance().getFavourites(), "Favourites");
    }
    private static ComboBox History() {
        return FromList(Backend.getInstance().getHistory(), "History");
    }
}
