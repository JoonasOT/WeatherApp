package fi.tuni.prog3.weatherapp.frontend.weather.map;

import fi.tuni.prog3.weatherapp.backend.Backend;
import fi.tuni.prog3.weatherapp.backend.api.openweather.WeatherMap.WeatherLayer;
import fi.tuni.prog3.weatherapp.backend.api.openweather.WeatherMap.Callables.*;
import fi.tuni.prog3.weatherapp.backend.io.ReadWrite;
import fi.tuni.prog3.weatherapp.frontend.scenes.WeatherScene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;

import java.io.ByteArrayInputStream;
import java.util.Optional;

public class WeatherMapView extends BorderPane {
    private final Image[] OpenStreetMapImages;
    private final Image[] OpenWeatherImages;
    public WeatherMapView() {
        super();
        Backend backend = Backend.getInstance();
        var coords = WeatherScene.getCoords();

        var respose = backend.callOpenWeatherWith(new OpenStreetMapCallable("GitHub-JoonasOT-OpenWeatherTesting", 15, coords.lat(), coords.lon()));
        OpenStreetMapImages = respose.map(response -> new Image[]{new Image(new ByteArrayInputStream(response.getAllBytes()))}).orElse(null);

        respose = backend.callOpenWeatherWith(new WeatherMapCallable(WeatherLayer.PRECIPITATION, 15, coords.lat(), coords.lon()));
        OpenWeatherImages = respose.map(response -> new Image[]{new Image(new ByteArrayInputStream(response.getAllBytes()))}).orElse(null);
        respose.ifPresent(response -> ReadWrite.write("weather.png", response.getAllBytes()));
        assert OpenStreetMapImages != null;
        super.setBackground(new Background(new BackgroundImage(OpenStreetMapImages[0], BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT)));
        super.setMinSize(255, 255);
        super.setCenter(new Label());
    }
}
