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

/**
 * A JavaFX node used for displaying weather forecast data on the GUI
 *
 * @author Joonas Tuominen
 */
public class ForecastPanel extends VBox {
    private static final int LABEL_WIDTH = 50;
    private static final BackgroundFill backgroundFill = new BackgroundFill(
            Color.LIGHTGRAY,
            new CornerRadii(5),
            new Insets(0)
    );

    /**
     * A record for passing data like:
     * @param time The of the forecasted weather
     * @param weather The weather stats at the set time
     * @param temps The temperatures at the set time
     * @param wind The winds at the set time
     * @param pod The part of day of the time at the location
     */
    public record RequiredFields(MillisToTime time, JSON_OBJs.Weather weather, WeatherForecast.Stats temps,
                                 WeatherForecast.Wind wind, String pod){}

    /**
     * Construct a ForecastPanel with the required fields
     * @param fields The information passed to the ForecastPanel
     */
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

        boolean isDay = fields.pod.equals("d");

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
