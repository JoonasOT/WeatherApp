package fi.tuni.prog3.weatherapp.frontend.weather.current;

import fi.tuni.prog3.weatherapp.backend.Backend;

import fi.tuni.prog3.weatherapp.backend.api.general.Response;
import fi.tuni.prog3.weatherapp.backend.api.openweather.CurrentWeather;
import fi.tuni.prog3.weatherapp.backend.api.openweather.CurrentWeather.CurrentWeatherObj;

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

import java.util.Optional;

/**
 * A JavaFX node that creates shows the user Statistics of the current weather at a defined city
 *
 * @author Joonas Tuominen
 */
public class CurrentWeatherView extends BorderPane {
    public static int CENTER_WIDTH = 360;
    public static int SCROLL_BAR_WIDTH = 0;
    public static int VIEW_WIDTH = 720 - SCROLL_BAR_WIDTH;
    private boolean isOK = false;
    private Label icon;

    /**
     * Constructor for a CurrentWeatherView pane
     */
    public CurrentWeatherView() {
        super();

        super.setMinSize(VIEW_WIDTH, 720);
        super.setMaxWidth(VIEW_WIDTH);
        super.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, new CornerRadii(0), new Insets(0))));

        Backend backend = Backend.getInstance();

        Optional<Response> response = backend.callOpenWeatherWith(
                            new CurrentWeather.Callables.CurrentWeatherCityNameCallable(WeatherScene.getCity())
                                              .addUnitsArg(WeatherScene.getUNIT())
        );

        // TODO: Replace this common structure with something nicer
        if (response.isEmpty()) {
            super.setCenter(new Label("Was not able to reach OpenWeather"));
        } else if (!response.get().CallWasOK()) {
            super.setCenter(new Label("Call to OpenWeather went amiss!\nTry again with another city!"));
        } else {
            CurrentWeatherObj weather = CurrentWeatherObj.fromJson(response.get().getData());
            super.setCenter(ConstructMiddle(weather));
            isOK = true;
        }
    }

    /**
     * A private method for constructing the middle area that is responsible for actually displaying the data
     * @param jsonOBJ The CurrentWeatherObj we want to get the data from
     * @return The formed middle area
     */
    private VBox ConstructMiddle(CurrentWeather.CurrentWeatherObj jsonOBJ) {
        VBox vBox = new VBox(5);
        vBox.setMaxSize(CENTER_WIDTH, CENTER_WIDTH);

        icon = new Label(WeatherFont.CodeToChar(jsonOBJ.weather().get(0).id(), true));
        Font iconFont = Font.loadFont(WeatherFont.LOCATION, 200);
        icon.setFont(iconFont);

        icon.setMinSize(CENTER_WIDTH, CENTER_WIDTH);
        icon.setAlignment(Pos.CENTER);
        icon.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(10), new Insets(0))));

        Label description = new Label("Currently forecasting: " + jsonOBJ.weather().get(0).description() +
                " and " + ReadingsToStrings.getTemperature(jsonOBJ.main().temp(), WeatherScene.getUNIT()) + " (" +
                ReadingsToStrings.getTemperature(jsonOBJ.main().feels_like(), WeatherScene.getUNIT()) + ")");
        description.setMinWidth(CENTER_WIDTH);
        description.setTextAlignment(TextAlignment.CENTER);

        Label where = new Label("Measured at: " + jsonOBJ.name() +
                                    " @ " + MillisToTime.fromOpenWeatherTime(jsonOBJ.dt()).time.toString());
        where.setMinWidth(CENTER_WIDTH);
        where.setTextAlignment(TextAlignment.CENTER);

        vBox.getChildren().addAll(icon, description, where);
        vBox.setAlignment(Pos.CENTER);
        return vBox;
    }

    /**
     * Sets the time of day of the icon (the sunrise and sunset in the CurrentWeatherObj seem to be bugged).
     * @param isDay True if we want to set the day glyph. False if we want the night glyph
     */
    public void setPartOfDay(boolean isDay) {
        icon.setText(Character.toString(icon.getText().charAt(0) + (isDay ? 0 : 1)));
    }

    /**
     * Get if this object was able to form the CurrenWeatherObj
     * @return False if the query given resulted in an error. True if successfully created.
     */
    public boolean isOK() {
        return isOK;
    }
}
