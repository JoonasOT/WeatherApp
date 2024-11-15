package fi.tuni.prog3.weatherapp.frontend.weather.daily;

import fi.tuni.prog3.weatherapp.backend.api.openweather.DailyWeather;

import fi.tuni.prog3.weatherapp.frontend.weather.MillisToTime;
import fi.tuni.prog3.weatherapp.frontend.fonts.WeatherFont;
import fi.tuni.prog3.weatherapp.frontend.scenes.WeatherScene;
import fi.tuni.prog3.weatherapp.frontend.weather.ReadingsToStrings;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

/**
 * This is a JavaFX node used to represent the daily weather to the user in a readable form on the GUI.
 *
 * @author Joonas Tuominen
 */
public class DailyWeatherPane extends BorderPane {
    private boolean isDay;
    private final Label icon;
    private final int weather_id;

    /**
     * Construct a DailyWeatherPane based on given complete weather description
     * @param weatherComplete The weather description we want to form the pane from
     */
    public DailyWeatherPane(DailyWeather.WeatherComplete weatherComplete) {
        super();
        super.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(5), new Insets(2))));
        super.setPadding(new Insets(10));
        super.setMaxSize(50, 50);

        VBox center = new VBox(5);

        String dateString = MillisToTime.fromOpenWeatherTime(weatherComplete.dt()).date.toString();

        Label date = new Label(dateString);
        date.setAlignment(Pos.TOP_CENTER);
        date.setMinWidth(145);
        date.setTextAlignment(TextAlignment.CENTER);

        MillisToTime sunrise = MillisToTime.fromOpenWeatherTime(weatherComplete.sunrise());
        MillisToTime sunset = MillisToTime.fromOpenWeatherTime(weatherComplete.sunset());
        MillisToTime now = new MillisToTime(System.currentTimeMillis());

        isDay = now.isLargerThan(sunrise).isSmallerThan(sunset).eval();
        weather_id = weatherComplete.weather().get(0).id();
        icon = new Label(WeatherFont.CodeToChar(weather_id, isDay));
        Font iconFont = Font.loadFont(WeatherFont.LOCATION, 150);
        icon.setFont(iconFont);


        String min = ReadingsToStrings.getTemperature(weatherComplete.temp().min(), WeatherScene.getUNIT());
        String max = ReadingsToStrings.getTemperature(weatherComplete.temp().max(), WeatherScene.getUNIT());

        Label temps = new Label(min + " ... " + max);
        temps.setAlignment(Pos.BOTTOM_CENTER);
        temps.setMinWidth(145);
        temps.setTextAlignment(TextAlignment.CENTER);

        center.getChildren().addAll(date, icon, temps);
        super.setCenter(center);
    }

    /**
     * Set the time of day of the icon to a given value
     * @param isDay Is it day time?
     */
    public void setDay(boolean isDay) {
        if (this.isDay == isDay) return;
        icon.setText(WeatherFont.CodeToChar(weather_id, isDay));
        this.isDay = isDay;
    }

    /**
     * Get if it is day time right now
     * @return True if it currently is daytime for the location
     */
    public boolean isDay() {
        return isDay;
    }
}
