package fi.tuni.prog3.weatherapp.frontend.weather;

import fi.tuni.prog3.weatherapp.backend.Backend;
import fi.tuni.prog3.weatherapp.backend.database.cities.Cities;
import fi.tuni.prog3.weatherapp.frontend.scenes.SearchScene;
import fi.tuni.prog3.weatherapp.frontend.scenes.WeatherScene;
import fi.tuni.prog3.weatherapp.frontend.search.SearchResult;
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
import javafx.scene.text.Font;

import java.util.List;

public class CustomToolBar extends ToolBar {
    public static int HEIGHT = 25;
    public CustomToolBar() {
        super();
        super.setPrefHeight(HEIGHT);
        super.setMaxHeight(HEIGHT);
        super.setMinHeight(HEIGHT);
        super.setMaxWidth(720);

        super.setPadding(new Insets(0));
        super.getItems().addAll(BackToSearch(), /*Favourites(), History(), */ AddToFavourites());
    }
    private static Button BackToSearch() {
        Button button = new Button("Search");

        button.setPadding(new Insets(0));
        button.setPrefHeight(HEIGHT);
        button.setMaxHeight(HEIGHT);
        button.setMinHeight(HEIGHT);
        button.setOnAction(x -> WeatherScene.SwitchToSearchScene());
        return button;
    }
    private static Button AddToFavourites() {
        Button button = new Button("\uF005");
        button.setPadding(new Insets(0));
        button.setPrefSize(HEIGHT, HEIGHT);
        button.setMaxSize(HEIGHT, HEIGHT);
        button.setMinSize(HEIGHT, HEIGHT);

        Font normal = Font.loadFont("file:res/font/FontAwesome6Free-Regular-400.otf", 15);
        Font favourite = Font.loadFont("file:res/font/FontAwesome6Free-Solid-900.otf", 15);

        String styleNormal = "-fx-text-fill: black;";
        String styleFavourite = "-fx-text-fill: yellow;";

        button.setFont(WeatherScene.isThisFavourite()? favourite : normal);
        button.setStyle(WeatherScene.isThisFavourite()? styleFavourite : styleNormal);

        button.setOnAction(x -> {
            WeatherScene.AddCityToFavourites();
            if (WeatherScene.isThisFavourite()) {
                button.setStyle(styleFavourite);
                button.setFont(favourite);
            }
            else {
                button.setStyle(styleNormal);
                button.setFont(normal);
            }
        });

        return button;
    }
    private static ComboBox<SearchResult> FromList(List<Cities.City> list, String name) {
        ComboBox<SearchResult> comboBox = new ComboBox<>();
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
