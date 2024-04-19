package fi.tuni.prog3.weatherapp.frontend.weather.map;

import fi.tuni.prog3.weatherapp.backend.Backend;
import fi.tuni.prog3.weatherapp.backend.api.openweather.WeatherMap;
import fi.tuni.prog3.weatherapp.frontend.scenes.WeatherScene;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MapGenerator implements Runnable {
    final int N;
    final int Z;
    final GridPane grid;
    final LinkedList<Tile> tiles;
    MapGenerator(int n, int z, GridPane grid, LinkedList<Tile> tiles) {
        N = n + (n + 1) % 2;
        Z = z;
        this.grid = grid;
        this.tiles = tiles;
    }
    @Override
    public void run() {
        Backend backend = Backend.getInstance();
        var coords = WeatherScene.getCoords();
        List<byte[]> buffers = backend.getNxNtiles(new WeatherMap.Callables.MapTile(true, null, "GitHub-JoonasOT-OpenWeatherTesting"), coords.lat(), coords.lon(), Z, N);

        List<Image> OpenStreetMapImages = buffers.stream().map(bytes -> new Image(new ByteArrayInputStream(bytes))).toList();


        Map<WeatherMap.WeatherLayer, List<Image>> layers = new HashMap<>();
        for (WeatherMap.WeatherLayer layer : WeatherMap.WeatherLayer.values()) {
            var tmp = backend.getNxNtiles(new WeatherMap.Callables.MapTile(false, layer, null), coords.lat(), coords.lon(), Z, N);
            layers.put(layer, tmp.stream().map(bytes -> new Image(new ByteArrayInputStream(bytes))).collect(Collectors.toList()));
        }

        for (int y : IntStream.range(0, N).toArray()) {
            for (int x : IntStream.range(0, N).toArray()) {
                Map<WeatherMap.WeatherLayer, Image> tmp = new HashMap<>();
                for (WeatherMap.WeatherLayer layer : layers.keySet()) {
                    tmp.put(layer, layers.get(layer).get(y*N+x));
                }
                Tile tile = new Tile(OpenStreetMapImages.get(y*N+x), tmp);
                tiles.add(tile);
            }
        }
        Platform.runLater(() -> {
            int i = 0;
            for (Tile tile : tiles) {
                grid.add(tile, i % N, i / N);
                i++;
            }
        });
    }
}
