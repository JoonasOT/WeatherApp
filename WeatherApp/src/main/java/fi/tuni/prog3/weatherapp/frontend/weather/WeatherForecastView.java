package fi.tuni.prog3.weatherapp.frontend.weather;

import fi.tuni.prog3.weatherapp.backend.database.cities.Cities;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class WeatherForecastView extends VBox {
    public WeatherForecastView(Cities.City city) {
        super();
        super.getChildren().addAll(new Label(city.toString()));
    }
}
