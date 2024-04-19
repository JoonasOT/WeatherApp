package fi.tuni.prog3.weatherapp.frontend.weather.forecast;

import fi.tuni.prog3.weatherapp.backend.Backend;
import fi.tuni.prog3.weatherapp.backend.api.general.Response;
import fi.tuni.prog3.weatherapp.backend.api.openweather.HourlyForecast;
import fi.tuni.prog3.weatherapp.backend.api.openweather.WeatherForecast;
import fi.tuni.prog3.weatherapp.backend.api.openweather.JSON_OBJs.Coord;
import fi.tuni.prog3.weatherapp.frontend.scenes.WeatherScene;
import fi.tuni.prog3.weatherapp.frontend.weather.current.CurrentWeatherView;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class WeatherForecastView extends ScrollPane {
    private final HBox states = new HBox();
    public WeatherForecastView() {
        super();
        super.setMaxWidth(CurrentWeatherView.VIEW_WIDTH);
        super.setVbarPolicy(ScrollBarPolicy.NEVER);

        Backend backend = Backend.getInstance();

        Coord coord = WeatherScene.getCoords();
        boolean isHourly = true;

        Optional<Response> response = backend.callOpenWeatherWith(
                (coord != null ?
                        new HourlyForecast.Callables.HourlyWeatherForecastLatLonCallable(coord.lat(), coord.lon()) :
                        new HourlyForecast.Callables.HourlyWeatherForecastCityNameCallable(WeatherScene.getCity()))
                        .addUnitsArg(WeatherScene.getUNIT())
        );
        if (response.isEmpty() || !response.get().CallWasOK()) {
            System.err.println("Was not able to call the hourly API! Defaulting to the free forecasts");
            response = backend.callOpenWeatherWith(
                    (coord != null ?
                            new WeatherForecast.Callables.WeatherForecastLatLonCallable(coord.lat(), coord.lon()) :
                            new WeatherForecast.Callables.WeatherForecastCityNameCallable(WeatherScene.getCity()))
                            .addUnitsArg(WeatherScene.getUNIT())
            );
            isHourly = false;
        }


        if (response.isEmpty()) {
            System.err.println("Weather forecast received an empty response!");
            super.setMaxHeight(0);
        } else if (!response.get().CallWasOK()) {
            System.err.println("Weather forecast call was not OK!");
            super.setMaxHeight(0);
        } else {
            String jsonString = response.get().getData();
            List<ForecastPanel> panels = isHourly? constructHourlyPanels(HourlyForecast.fromJson(jsonString)) :
                                                   constructDefaultPanels(WeatherForecast.fromJson(jsonString));

            states.getChildren().addAll(panels);
            super.setContent(states);
        }
    }

    private List<ForecastPanel> constructHourlyPanels(HourlyForecast.HourlyForecastObj obj) {
        return getForecastPanels(obj.list(), obj.city());
    }
    private List<ForecastPanel> constructDefaultPanels(WeatherForecast.WeatherForecastObj obj) {
        return getForecastPanels(obj.list(), obj.city());
    }

    private List<ForecastPanel> getForecastPanels(List<WeatherForecast.WeatherState> list, WeatherForecast.CityStats city) {
        LinkedList<ForecastPanel> panels = new LinkedList<>();
        for (WeatherForecast.WeatherState state : list) {
            panels.add(new ForecastPanel(new ForecastPanel.RequiredFields(
                    state.weather().get(0),
                    state.main(),
                    state.wind(),
                    city.sunrise(),
                    city.sunrise()
            )));
        }
        return panels;
    }
}
