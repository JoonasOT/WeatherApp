package fi.tuni.prog3.weatherapp.frontend.weather.forecast;

import fi.tuni.prog3.weatherapp.backend.api.openweather.JSON_OBJs;
import fi.tuni.prog3.weatherapp.backend.api.openweather.WeatherForecast;
import fi.tuni.prog3.weatherapp.frontend.weather.MillisToTime;
import fi.tuni.prog3.weatherapp.frontend.scenes.WeatherScene;
import fi.tuni.prog3.weatherapp.frontend.weather.ReadingsToStrings;
import fi.tuni.prog3.weatherapp.frontend.fonts.WeatherFont;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class ForecastPanel extends VBox {
    private static final int LABEL_WIDTH = 50;
    private static final BackgroundFill backgroundFill =
            new BackgroundFill(Color.LIGHTGRAY, new CornerRadii(5), new Insets(0));
    public record RequiredFields(MillisToTime time, JSON_OBJs.Weather weather, WeatherForecast.Stats temps, WeatherForecast.Wind wind,
                                 MillisToTime sunrise, MillisToTime sunset){}
    public ForecastPanel(RequiredFields fields) {
        super();
        super.setPadding(new Insets(2, 10, 5, 10));
        super.setBackground(new Background(backgroundFill));

        Label date = new Label(String.format("%02d/%02d",fields.time.date.day(), fields.time.date.month()));
        Label time = new Label(String.format("%02d:%02d", fields.time.time.hour(), fields.time.time.minute()));
        date.setAlignment(Pos.TOP_CENTER);
        date.setMinWidth(LABEL_WIDTH);
        time.setAlignment(Pos.CENTER);
        time.setMinWidth(LABEL_WIDTH);

        boolean isDay = new MillisToTime(System.currentTimeMillis())
                                .isLargerThan(fields.sunrise)
                                .isSmallerThan(fields.sunset)
                                .eval();

        Label icon = new Label(WeatherFont.CodeToChar(fields.weather().id(), isDay));
        Font iconFont = Font.loadFont(WeatherFont.LOCATION, 50);
        icon.setFont(iconFont);
        icon.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(10), new Insets(0))));

        super.getChildren().addAll(date, time, icon);

        String[] stats = new String[]{
                ReadingsToStrings.getTemperature(fields.temps.temp(), WeatherScene.getUNIT()),
                ReadingsToStrings.getTemperature(fields.temps.feels_like(), WeatherScene.getUNIT()),
                ReadingsToStrings.getWindSpeed(fields.wind.speed(), WeatherScene.getUNIT()),
                ReadingsToStrings.getWindSpeed(fields.wind.gust(), WeatherScene.getUNIT()),
                ReadingsToStrings.getWindDirection(fields.wind.deg(), WeatherScene.getUNIT())
        };
        for (String stat : stats) {
            Label label = new Label(stat);
            label.setMinWidth(LABEL_WIDTH);
            label.setAlignment(Pos.CENTER_LEFT);
            super.getChildren().add(label);
        }
    }
}
