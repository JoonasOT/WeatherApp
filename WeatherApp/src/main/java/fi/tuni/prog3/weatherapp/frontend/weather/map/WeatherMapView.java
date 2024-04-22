package fi.tuni.prog3.weatherapp.frontend.weather.map;

import fi.tuni.prog3.weatherapp.backend.api.openweather.WeatherMap.WeatherLayer;
import fi.tuni.prog3.weatherapp.frontend.weather.current.CurrentWeatherView;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;

import java.util.*;

public class WeatherMapView extends StackPane {
    private final ScrollPane scrollPane = new ScrollPane();
    private LinkedList<Tile> tiles = new LinkedList<>();
    private static final int MAP_SIZE = 10;
    private static final int MAP_Z_INDEX = 9;
    private final Thread generator;
    public WeatherMapView() {
        super();

        super.getChildren().add(scrollPane);

        ComboBox<String> layerSelection = new ComboBox<>();
        layerSelection.getItems().addAll(Arrays.stream(WeatherLayer.values()).map(WeatherLayer::name).toList());
        layerSelection.setOnAction(a -> display(WeatherLayer.fromString(layerSelection.getValue())));
        layerSelection.setValue("Weather layer");

        super.getChildren().add(layerSelection);
        setAlignment(layerSelection, Pos.TOP_LEFT);

        GridPane grid = new GridPane();
        generator = new Thread(new MapGenerator(MAP_SIZE, MAP_Z_INDEX, grid, tiles));
        generator.start();

        scrollPane.setContent(grid);
        scrollPane.setHvalue(0.5);
        scrollPane.setVvalue(0.5);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setMaxSize(CurrentWeatherView.VIEW_WIDTH, CurrentWeatherView.VIEW_WIDTH);
    }

    public void display(WeatherLayer layer) {
        for (Tile tile : tiles) tile.viewLayer(layer);
    }
}
