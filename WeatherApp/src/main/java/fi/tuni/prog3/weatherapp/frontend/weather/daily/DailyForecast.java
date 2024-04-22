package fi.tuni.prog3.weatherapp.frontend.weather.daily;

import fi.tuni.prog3.weatherapp.backend.Backend;
import fi.tuni.prog3.weatherapp.backend.api.general.Response;
import fi.tuni.prog3.weatherapp.backend.api.openweather.DailyWeather;
import fi.tuni.prog3.weatherapp.backend.api.openweather.JSON_OBJs;
import fi.tuni.prog3.weatherapp.frontend.scenes.WeatherScene;
import fi.tuni.prog3.weatherapp.frontend.weather.current.CurrentWeatherView;
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
    private Boolean isCurrentlyDay = null;
    public DailyForecast() {
        super();
        super.setMaxWidth(CurrentWeatherView.VIEW_WIDTH);
        super.setVbarPolicy(ScrollBarPolicy.NEVER);

        Label title = new Label("Daily weather forecast:");
        title.setFont(new Font(15));
        title.setPadding(new Insets(10));
        title.setAlignment(Pos.CENTER_LEFT);

        wrapper.getChildren().addAll(title, days);
        wrapper.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, new CornerRadii(0), new Insets(0))));

        Backend backend = Backend.getInstance();

        Optional<Response> response = backend.callOpenWeatherWith(
                        new DailyWeather.Callables.DailyWeatherCityNameCallable(WeatherScene.getCity())
                                        .addUnitsArg(WeatherScene.getUNIT())
        );

        if (response.isEmpty()) {
            System.err.println("Daily forecasts response was empty!");
            super.setMaxHeight(0);
        } else if (!response.get().CallWasOK()) {
            System.err.println("Daily forecasts response had an error!");
            super.setMaxHeight(0);
        } else {
            DailyWeather.DailyWeatherObj json = DailyWeather.fromJson(response.get().getData());
            for (DailyWeather.WeatherComplete w : json.list()) {
                var pane = new DailyWeatherPane(w);
                if (isCurrentlyDay == null) {
                    isCurrentlyDay = pane.isDay();
                }
                days.getChildren().add(pane);
            }
            super.setMinHeight(256);
            super.setContent(wrapper);
        }
    }
    public boolean isCurrentlyDay() {
        return isCurrentlyDay;
    }
}
