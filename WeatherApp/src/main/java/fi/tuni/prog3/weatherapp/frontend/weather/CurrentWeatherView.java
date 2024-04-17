package fi.tuni.prog3.weatherapp.frontend.weather;

import fi.tuni.prog3.weatherapp.backend.Backend;
import fi.tuni.prog3.weatherapp.backend.api.Response;
import fi.tuni.prog3.weatherapp.backend.api.openweather.CurrentWeather;
import fi.tuni.prog3.weatherapp.backend.database.cities.Cities;
import fi.tuni.prog3.weatherapp.frontend.WeatherFont;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.util.Optional;

public class CurrentWeatherView extends BorderPane {
    public static int CENTER_WIDTH = 360;
    public static int SCROLL_BAR_WIDTH = 16;
    public static int VIEW_WIDTH = 720 - SCROLL_BAR_WIDTH;
    public CurrentWeatherView(Cities.City city) {
        super();

        super.setMinSize(VIEW_WIDTH, 720);
        super.setMaxWidth(VIEW_WIDTH);
        super.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, new CornerRadii(0), new Insets(0))));

        Backend backend = Backend.getInstance();
        Optional<Response> response = backend.getCurrentWeather(city.name());

        if (response.isEmpty()) {
            super.setCenter(new Label("Was not able to reach OpenWeather"));
        } else if (!response.get().CallWasOK()) {
            super.setCenter(new Label("Call to OpenWeather went amiss!\nTry again with another city!"));
        } else {
            CurrentWeather.CurrentWeatherObj weather = CurrentWeather.fromJson(response.get().getData());
            super.setCenter(ConstructMiddle(weather));
        }
    }
    private static VBox ConstructMiddle(CurrentWeather.CurrentWeatherObj jsonOBJ) {
        VBox vBox = new VBox(5);
        vBox.setMaxSize(CENTER_WIDTH, CENTER_WIDTH);

        Label icon = new Label(WeatherFont.CodeToChar(jsonOBJ.weather().get(0).id(), jsonOBJ.sys().sunset() >= System.currentTimeMillis()));
        Font iconFont = Font.loadFont(WeatherFont.LOCATION, 200);
        icon.setFont(iconFont);

        icon.setMinSize(CENTER_WIDTH, CENTER_WIDTH);
        icon.setAlignment(Pos.CENTER);
        icon.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(10), new Insets(0))));

        Label description = new Label("Currently forecasting: " + jsonOBJ.weather().get(0).description());
        description.setMinWidth(CENTER_WIDTH);
        description.setTextAlignment(TextAlignment.CENTER);
        Label where = new Label(jsonOBJ.name());
        where.setMinWidth(CENTER_WIDTH);
        where.setTextAlignment(TextAlignment.CENTER);
        vBox.getChildren().addAll(icon, description, where);
        vBox.setAlignment(Pos.CENTER);
        return vBox;
    }
}
