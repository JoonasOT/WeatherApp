package fi.tuni.prog3.weatherapp.frontend.weather.current;

import fi.tuni.prog3.weatherapp.backend.Backend;
import fi.tuni.prog3.weatherapp.backend.api.general.Response;
import fi.tuni.prog3.weatherapp.backend.api.openweather.CurrentWeather;
import fi.tuni.prog3.weatherapp.backend.api.openweather.JSON_OBJs.Coord;
import fi.tuni.prog3.weatherapp.frontend.MillisToTime;
import fi.tuni.prog3.weatherapp.frontend.weather.WeatherFont;
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

public class CurrentWeatherView extends BorderPane {
    public static int CENTER_WIDTH = 360;
    public static int SCROLL_BAR_WIDTH = 16;
    public static int VIEW_WIDTH = 720 - SCROLL_BAR_WIDTH;
    public CurrentWeatherView() {
        super();

        super.setMinSize(VIEW_WIDTH, 720);
        super.setMaxWidth(VIEW_WIDTH);
        super.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, new CornerRadii(0), new Insets(0))));

        Backend backend = Backend.getInstance();

        Coord coord = WeatherScene.getCoords();

        Optional<Response> response = backend.callOpenWeatherWith(
                (coord != null ?
                            new CurrentWeather.Callables.CurrentWeatherLatLonCallable(coord.lat(), coord.lon()) :
                            new CurrentWeather.Callables.CurrentWeatherCityNameCallable(WeatherScene.getCity()))
                        .addUnitsArg(WeatherScene.getUNIT())
        );

        if (response.isEmpty()) {
            super.setCenter(new Label("Was not able to reach OpenWeather"));
        } else if (!response.get().CallWasOK()) {
            super.setCenter(new Label("Call to OpenWeather went amiss!\nTry again with another city!"));
        } else {
            CurrentWeather.CurrentWeatherObj weather = CurrentWeather.fromJson(response.get().getData());
            super.setCenter(ConstructMiddle(weather));
        }
    }
    private static VBox ConstructMiddle(CurrentWeather.CurrentWeatherObj jsonOBJ) {
        VBox vBox = new VBox(5);
        vBox.setMaxSize(CENTER_WIDTH, CENTER_WIDTH);

        MillisToTime sunrise = new MillisToTime(jsonOBJ.sys().sunrise());
        MillisToTime sunset = new MillisToTime(jsonOBJ.sys().sunset());
        MillisToTime now = new MillisToTime(System.currentTimeMillis());

        boolean isDay = now.isLargerThan(sunrise).isSmallerThan(sunset).eval();
        Label icon = new Label(WeatherFont.CodeToChar(jsonOBJ.weather().get(0).id(), isDay));
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
}
