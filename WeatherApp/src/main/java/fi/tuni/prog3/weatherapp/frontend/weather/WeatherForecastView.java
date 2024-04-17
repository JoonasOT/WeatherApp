package fi.tuni.prog3.weatherapp.frontend.weather;

import fi.tuni.prog3.weatherapp.backend.Backend;
import fi.tuni.prog3.weatherapp.backend.api.general.Response;
import fi.tuni.prog3.weatherapp.backend.api.openweather.WeatherForecast;
import fi.tuni.prog3.weatherapp.backend.api.openweather.JSON_OBJs.Coord;
import fi.tuni.prog3.weatherapp.frontend.scenes.WeatherScene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;

import java.util.Optional;

public class WeatherForecastView extends ScrollPane {
    private final HBox states = new HBox();
    public WeatherForecastView() {
        super();
        super.setMaxWidth(CurrentWeatherView.VIEW_WIDTH);
        super.setVbarPolicy(ScrollBarPolicy.NEVER);
        super.setContent(states);

        Backend backend = Backend.getInstance();

        Coord coord = WeatherScene.getCoords();
        Optional<Response> response = backend.callOpenWeatherWith(
                coord != null ?
                       new WeatherForecast.Callables.WeatherForecastLatLonCallable(coord.lat(), coord.lon()) :
                       new WeatherForecast.Callables.WeatherForecastCityNameCallable(WeatherScene.getCity())
        );

        if (response.isEmpty()) {
            super.setContent(new Label("Was not able to reach OpenWeather"));
        } else if (!response.get().CallWasOK()) {
            super.setContent(new Label("Call to OpenWeather went amiss!\nTry again with another city!"));
        } else {
            WeatherForecast.WeatherForecastObj json = WeatherForecast.fromJson(response.get().getData());
            for (WeatherForecast.WeatherState w : json.list()) {
                states.getChildren().add(new Label(w.toString()));
            }
        }
    }

}
