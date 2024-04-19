package fi.tuni.prog3.weatherapp.frontend.weather.forecast;

import fi.tuni.prog3.weatherapp.backend.api.openweather.JSON_OBJs;
import fi.tuni.prog3.weatherapp.backend.api.openweather.WeatherForecast;
import fi.tuni.prog3.weatherapp.frontend.MillisToTime;
import fi.tuni.prog3.weatherapp.frontend.scenes.WeatherScene;
import fi.tuni.prog3.weatherapp.frontend.weather.ReadingsToStrings;
import fi.tuni.prog3.weatherapp.frontend.weather.WeatherFont;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.Map;

public class ForecastPanel extends VBox {
    private static final BackgroundFill backgroundFill =
            new BackgroundFill(Color.LIGHTGRAY, new CornerRadii(5), new Insets(0));
    public record RequiredFields(JSON_OBJs.Weather weather, WeatherForecast.Stats temps, WeatherForecast.Wind wind,
                                 long sunrise, long sunset){}
    public ForecastPanel(RequiredFields fields) {
        super();
        super.setBackground(new Background(backgroundFill));

        MillisToTime sunrise = MillisToTime.fromOpenWeatherTime(fields.sunrise());
        MillisToTime sunset = MillisToTime.fromOpenWeatherTime(fields.sunset());
        MillisToTime now = new MillisToTime(System.currentTimeMillis());

        boolean isDay = now.isLargerThan(sunrise).isSmallerThan(sunset).eval();

        Label icon = new Label(WeatherFont.CodeToChar(fields.weather().id(), isDay));
        Font iconFont = Font.loadFont(WeatherFont.LOCATION, 100);
        icon.setFont(iconFont);

        super.getChildren().add(icon);
        Map<String, String> stats = Map.of(
                "Temperature", ReadingsToStrings.getTemperature(fields.temps.temp(), WeatherScene.getUNIT()),
                "Feels like", ReadingsToStrings.getTemperature(fields.temps.feels_like(), WeatherScene.getUNIT()),
                "Wind speed", ReadingsToStrings.getWindSpeed(fields.wind.speed(), WeatherScene.getUNIT()),
                "Wind gust", ReadingsToStrings.getWindSpeed(fields.wind.gust(), WeatherScene.getUNIT()),
                "Wind direction", ReadingsToStrings.getWindDirection(fields.wind.deg(), WeatherScene.getUNIT())
        );
        for (String stat : stats.keySet()) {
            Label label = new Label(stats.get(stat));
            super.getChildren().add(label);
        }
    }
}
