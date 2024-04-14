package fi.tuni.prog3.weatherapp.frontend.weather;

import fi.tuni.prog3.weatherapp.backend.Backend;
import fi.tuni.prog3.weatherapp.backend.api.Response;
import fi.tuni.prog3.weatherapp.backend.api.openweather.CurrentWeather;
import fi.tuni.prog3.weatherapp.backend.database.cities.Cities;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

import java.util.Optional;

public class CurrentWeatherView extends BorderPane {
    public CurrentWeatherView(Cities.City city) {
        super();

        super.setMinSize(720, 720);
        super.setMaxWidth(720);
        super.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, new CornerRadii(0), new Insets(0))));

        Backend backend = Backend.getInstance();
        var response = backend.getCurrentWeather(city.name());

        if (response.isEmpty()) {
            super.setCenter(new Label("Was not able to reach OpenWeather"));
        } else if (!response.get().CallWasOK()) {
            super.setCenter(new Label("Call to OpenWeather went amiss!\nTry again with another city!"));
        } else {
            CurrentWeather.JSON_OBJ weather = CurrentWeather.fromJson(response.get().getData());
            System.out.println(weather);
            super.setCenter(new Label(weather.toString()));
        }
    }
}
