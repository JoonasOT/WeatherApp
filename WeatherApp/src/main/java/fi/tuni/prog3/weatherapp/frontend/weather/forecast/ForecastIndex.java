package fi.tuni.prog3.weatherapp.frontend.weather.forecast;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * A JavaFX node that creates an index for WeatherForecastView
 *
 * @author Joonas Tuominen
 */
public class ForecastIndex extends VBox {
    public static int WIDTH = 100;

    /**
     * Construct the index with predetermined fields
     */
    public ForecastIndex() {
        super();
        super.setPadding(new Insets(2, 0, 2, 0));
        String weatherIconName = "Weather:";
        String[] stats = new String[] {
                "Date:",
                "Time:",
                weatherIconName,
                "Temperature:",
                "Feels like:",
                "Wind speed:",
                "Wind gusts:",
                "Wind direction:"
        };
        for (String stat : stats) {
            Label label = new Label(stat);
            int vPad = stat.equals(weatherIconName) ? 15 : 0;
            label.setPadding(new Insets(vPad, 10, vPad, 10));
            label.setMinWidth(WIDTH);
            label.setAlignment(Pos.CENTER_RIGHT);
            super.getChildren().add(label);
        }
    }
}
