package fi.tuni.prog3.weatherapp.frontend.weather;

import fi.tuni.prog3.weatherapp.backend.Backend;
import fi.tuni.prog3.weatherapp.frontend.fonts.FontAwesome;
import fi.tuni.prog3.weatherapp.frontend.fonts.FontLocations;
import fi.tuni.prog3.weatherapp.frontend.scenes.WeatherScene;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.text.Font;

public class CustomToolBar extends ToolBar {
    public static int HEIGHT = 25;
    public CustomToolBar() {
        super();
        super.setPrefHeight(HEIGHT);
        super.setMaxHeight(HEIGHT);
        super.setMinHeight(HEIGHT);
        super.setMaxWidth(900);

        super.setPadding(new Insets(0));
        super.getItems().addAll(BackToSearch(), AddToFavourites(), RemoveHistory());
    }
    private static Button BackToSearch() {
        Button button = new Button(FontAwesome.SEARCH.unicode);
        button.setFont(Font.loadFont(FontLocations.FONT_AWESOME_SOLID.location, 15));

        button.setPadding(new Insets(0));
        button.setPrefSize(HEIGHT, HEIGHT);
        button.setMaxSize(HEIGHT, HEIGHT);
        button.setMinSize(HEIGHT, HEIGHT);
        button.setOnAction(x -> WeatherScene.SwitchToSearchScene());
        return button;
    }
    private static Button AddToFavourites() {
        Button button = new Button(FontAwesome.STAR.unicode);
        button.setPadding(new Insets(0));
        button.setPrefSize(HEIGHT, HEIGHT);
        button.setMaxSize(HEIGHT, HEIGHT);
        button.setMinSize(HEIGHT, HEIGHT);

        String styleNormal = "-fx-text-fill: black;";
        String styleFavourite = "-fx-text-fill: yellow;";

        button.setFont(Font.loadFont(WeatherScene.isThisFavourite()? FontAwesome.STAR_FILLED.fontLocation :
                                                                     FontAwesome.STAR.fontLocation, 15));

        button.setStyle(WeatherScene.isThisFavourite()? styleFavourite : styleNormal);

        button.setOnAction(x -> {
            WeatherScene.AddCityToFavourites();
            if (WeatherScene.isThisFavourite()) {
                button.setStyle(styleFavourite);
                button.setFont(Font.loadFont(FontAwesome.STAR_FILLED.fontLocation, 15));
            }
            else {
                button.setStyle(styleNormal);
                button.setFont(Font.loadFont(FontAwesome.STAR.fontLocation, 15));
            }
        });

        return button;
    }

    private static Button RemoveHistory() {
        Button button = new Button("Reset history");

        button.setPadding(new Insets(0));
        button.setPrefHeight(HEIGHT);
        button.setMaxHeight(HEIGHT);
        button.setMinHeight(HEIGHT);
        button.setOnAction(x -> Backend.getInstance().nukeHistory());
        return button;
    }
}
