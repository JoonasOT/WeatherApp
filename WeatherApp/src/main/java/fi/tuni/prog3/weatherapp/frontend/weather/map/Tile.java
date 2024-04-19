package fi.tuni.prog3.weatherapp.frontend.weather.map;

import fi.tuni.prog3.weatherapp.backend.api.openweather.WeatherMap;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.util.List;
import java.util.Map;

public class Tile extends StackPane {
    private final Map<WeatherMap.WeatherLayer, Image> layers;
    private final ImageView layerView = new ImageView();
    public Tile(Image image, Map<WeatherMap.WeatherLayer, Image> layers) {
        super();
        super.setBackground(new Background(new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT)));
        this.layers = layers;

        super.getChildren().add(layerView);
        setAlignment(layerView, Pos.CENTER);

        super.setMinSize(255, 255);
        super.setMaxSize(255, 255);
        super.setPrefSize(255, 255);
    }
    public void viewLayer(WeatherMap.WeatherLayer layer) {
        layerView.setImage(layers.get(layer));
    }
}
