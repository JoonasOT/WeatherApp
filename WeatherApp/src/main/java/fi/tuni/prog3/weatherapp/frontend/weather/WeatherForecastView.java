package fi.tuni.prog3.weatherapp.frontend.weather;

import fi.tuni.prog3.weatherapp.backend.Backend;
import fi.tuni.prog3.weatherapp.backend.api.Response;
import fi.tuni.prog3.weatherapp.backend.api.openweather.CurrentWeather;
import fi.tuni.prog3.weatherapp.backend.api.openweather.WeatherForecast;
import fi.tuni.prog3.weatherapp.backend.database.cities.Cities;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;

import java.util.Optional;

public class WeatherForecastView extends ScrollPane {
    private final HBox states = new HBox();
    public WeatherForecastView(Cities.City city) {
        super();
        super.setMaxWidth(CurrentWeatherView.VIEW_WIDTH);
        super.setVbarPolicy(ScrollBarPolicy.NEVER);
        super.setContent(states);

        Backend backend = Backend.getInstance();
        Optional<Response> response = backend.getWeatherForecast(city.name());

        if (response.isEmpty()) {
            super.setContent(new Label("Was not able to reach OpenWeather"));
        } else if (!response.get().CallWasOK()) {
            super.setContent(new Label("Call to OpenWeather went amiss!\nTry again with another city!"));
        } else {
            WeatherForecast.JSON_OBJ json = WeatherForecast.fromJson(response.get().getData());
            System.out.println(json);
            for (WeatherForecast.WeatherState w : json.list()) {
                states.getChildren().add(new Label(w.toString()));
            }
        }
    }

}
