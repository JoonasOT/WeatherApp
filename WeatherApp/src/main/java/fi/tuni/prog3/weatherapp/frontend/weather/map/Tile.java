package fi.tuni.prog3.weatherapp.frontend.weather.map;

import fi.tuni.prog3.weatherapp.backend.api.openweather.WeatherMap;

import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.util.Map;

/**
 * A JavaFX node that stores the base map and weather layers for a given map tile and shows the base map with potentially
 * some weather overlay on top.
 *
 * @author Joonas Tuominen
 */
public class Tile extends StackPane {
    private final Map<WeatherMap.WeatherLayer, Image> layers;
    private final ImageView layerView = new ImageView();

    /**
     * Construct a Tile from a base map tile image and a map of weather layers to weather map tile images
     * @param image The base image
     * @param layers A map mapping all the layers to some weather map tile images
     */
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

    /**
     * Overlay a weather layer on top of the base map tile image
     * @param layer The layer we want to view
     */
    public void viewLayer(WeatherMap.WeatherLayer layer) {
        layerView.setImage(layers.get(layer));
    }
}
