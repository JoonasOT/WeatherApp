package fi.tuni.prog3.weatherapp.frontend.weather;

import fi.tuni.prog3.weatherapp.backend.Backend;
import fi.tuni.prog3.weatherapp.backend.api.general.Response;
import fi.tuni.prog3.weatherapp.backend.api.openweather.DailyWeather;
import fi.tuni.prog3.weatherapp.backend.api.openweather.JSON_OBJs;
import fi.tuni.prog3.weatherapp.backend.api.openweather.OpenWeather;
import fi.tuni.prog3.weatherapp.backend.api.openweather.WeatherForecast;
import fi.tuni.prog3.weatherapp.frontend.scenes.WeatherScene;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.Optional;

public class DailyForecast extends ScrollPane {
    private VBox wrapper = new VBox(0);
    private HBox days = new HBox(0);
    public DailyForecast() {
        super();
        super.setMaxWidth(CurrentWeatherView.VIEW_WIDTH);
        super.setMinHeight(256);
        super.setVbarPolicy(ScrollBarPolicy.NEVER);
        super.setContent(wrapper);

        Label title = new Label("Daily weather forecast:");
        title.setFont(new Font(15));
        title.setPadding(new Insets(10));
        title.setAlignment(Pos.CENTER_LEFT);

        wrapper.getChildren().addAll(title, days);
        wrapper.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, new CornerRadii(0), new Insets(0))));

        Backend backend = Backend.getInstance();

        JSON_OBJs.Coord coord = WeatherScene.getCoords();
        Optional<Response> response = backend.callOpenWeatherWith(
                (coord != null ?
                        new DailyWeather.Callables.DailyWeatherLatLonCallable(coord.lat(), coord.lon()) :
                        new DailyWeather.Callables.DailyWeatherCityNameCallable(WeatherScene.getCity()))
                        .addUnitsArg(WeatherScene.getUNIT())
        );

        if (response.isEmpty()) {
            super.setContent(new Label("Was not able to reach OpenWeather"));
        } else if (!response.get().CallWasOK()) {
            super.setContent(new Label("Call to OpenWeather went amiss!\nTry again with another city!"));
        } else {
            DailyWeather.DailyWeatherObj json = DailyWeather.fromJson(response.get().getData());
            for (DailyWeather.WeatherComplete w : json.list()) {
                days.getChildren().add(new DailyWeatherPane(w));
            }
        }
    }
}
