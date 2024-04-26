package fi.tuni.prog3.weatherapp.frontend.weather.forecast;

import fi.tuni.prog3.weatherapp.backend.Backend;

import fi.tuni.prog3.weatherapp.backend.api.general.Response;
import fi.tuni.prog3.weatherapp.backend.api.openweather.HourlyForecast;
import fi.tuni.prog3.weatherapp.backend.api.openweather.HourlyForecast.HourlyForecastObj;
import fi.tuni.prog3.weatherapp.backend.api.openweather.WeatherForecast;
import fi.tuni.prog3.weatherapp.backend.api.openweather.WeatherForecast.WeatherForecastObj;

import fi.tuni.prog3.weatherapp.frontend.weather.MillisToTime;
import fi.tuni.prog3.weatherapp.frontend.scenes.WeatherScene;
import fi.tuni.prog3.weatherapp.frontend.weather.current.CurrentWeatherView;

import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * This is a JavaFX node used to represent the weather forecast data to the user in a readable form on the GUI.
 *
 * @author Joonas Tuominen
 */
public class WeatherForecastView extends HBox {
    /**
     * Construct the GUI element
     */
    public WeatherForecastView() {
        super();
        ScrollPane scrollPane = new ScrollPane();
        super.getChildren().addAll(new ForecastIndex(), scrollPane);

        scrollPane.setMaxWidth(CurrentWeatherView.VIEW_WIDTH - ForecastIndex.WIDTH - 1);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        Backend backend = Backend.getInstance();

        boolean isHourly = true;

        Optional<Response> response = backend.callOpenWeatherWith(
                        new HourlyForecast.Callables.HourlyWeatherForecastCityNameCallable(WeatherScene.getCity())
                                          .addUnitsArg(WeatherScene.getUNIT())
        );
        if (response.isEmpty() || !response.get().CallWasOK()) {
            System.err.println("Was not able to call the hourly API! Defaulting to the free forecasts");
            response = backend.callOpenWeatherWith(
                            new WeatherForecast.Callables.WeatherForecastCityNameCallable(WeatherScene.getCity())
                                               .addUnitsArg(WeatherScene.getUNIT())
            );
            isHourly = false;
        }

        // TODO: Make this nice!
        if (response.isEmpty()) {
            System.err.println("Weather forecast received an empty response!");
            scrollPane.setMaxHeight(0);
        } else if (!response.get().CallWasOK()) {
            System.err.println("Weather forecast call was not OK!");
            scrollPane.setMaxHeight(0);
        } else {
            String jsonString = response.get().getData();
            List<ForecastPanel> panels = isHourly? constructHourlyPanels(HourlyForecastObj.fromJson(jsonString)) :
                                                   constructDefaultPanels(WeatherForecastObj.fromJson(jsonString));

            HBox states = new HBox(5);
            states.getChildren().addAll(panels);
            scrollPane.setContent(states);
            scrollPane.setMinHeight(195);
        }
    }

    /**
     * Construct the ForecastPanels for hourly forecast data
     * @param obj The hourly forecast data
     * @return ForecastPanels formed from the operation
     */
    private List<ForecastPanel> constructHourlyPanels(HourlyForecast.HourlyForecastObj obj) {
        return getForecastPanels(obj.list());
    }

    /**
     * Construct the ForecastPanels for free forecast data
     * @param obj The forecast data with 3-hour steps
     * @return ForecastPanels formed from the operation
     */
    private List<ForecastPanel> constructDefaultPanels(WeatherForecast.WeatherForecastObj obj) {
        return getForecastPanels(obj.list());
    }

    /**
     * Construct ForecastPanels from a list of weather states
     * @param list The list of weather states
     * @return The list of formed ForecastPanels
     */
    private List<ForecastPanel> getForecastPanels(List<WeatherForecast.WeatherState> list) {
        LinkedList<ForecastPanel> panels = new LinkedList<>();
        for (WeatherForecast.WeatherState state : list) {
            panels.add(new ForecastPanel(new ForecastPanel.RequiredFields(
                    MillisToTime.fromOpenWeatherTime(state.dt()),
                    state.weather().get(0),
                    state.main(),
                    state.wind(),
                    state.sys().pod()
            )));
        }
        return panels;
    }
}
