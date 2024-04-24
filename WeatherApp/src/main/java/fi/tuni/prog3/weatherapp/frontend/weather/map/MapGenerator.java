package fi.tuni.prog3.weatherapp.frontend.weather.map;

import fi.tuni.prog3.weatherapp.backend.Backend;

import fi.tuni.prog3.weatherapp.backend.api.openweather.WeatherMap;
import fi.tuni.prog3.weatherapp.frontend.scenes.WeatherScene;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

import java.io.ByteArrayInputStream;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MapGenerator implements Runnable {
    private final int N;
    private final int Z;
    private final GridPane grid;
    private final LinkedList<Tile> tiles;
    private final Object sync = WeatherScene.getSyncObj();
    private final String OpenStreetMapUserAgent = "TUNI-Programming3-WeatherApp-JoonasOT";
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

        // Load OpenStreetMap base map
        // TODO: Do in a separate thread
        List<byte[]> buffers = backend.getNxNtiles(new WeatherMap.Callables.MapTile(true, null,
                                                    OpenStreetMapUserAgent), coords.lat(), coords.lon(), Z, N);
        List<Image> OpenStreetMapImages = buffers.stream().map(bytes -> new Image(new ByteArrayInputStream(bytes)))
                                                          .toList();


        // Create all the weather map layers:
        Map<WeatherMap.WeatherLayer, List<Image>> layers = new HashMap<>();

        // Executor and intermediate for parallelization
        ExecutorService executor = Executors.newCachedThreadPool();
        List<Pair<WeatherMap.WeatherLayer, Future<List<Image>>>> images = new LinkedList<>();

        for (WeatherMap.WeatherLayer layer : WeatherMap.WeatherLayer.values()) {
            if (WeatherScene.hasShutdown()) { executor.shutdown(); return; }
            images.add(new Pair<>(layer, executor.submit(new MapLayerGenerator(layer, coords, Z, N))));
        }

        for (var image : images) {
            try {
                layers.put(image.getKey(), image.getValue().get());
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        executor.shutdown();

        // Put values from map OBJ to the JavaFX grid
        for (int y : IntStream.range(0, N).toArray()) {
            for (int x : IntStream.range(0, N).toArray()) {
                if (WeatherScene.hasShutdown()) return;

                tiles.add(new Tile(
                    OpenStreetMapImages.get(y*N+x),
                    layers.keySet().stream().collect(Collectors.toMap(k -> k, k -> layers.get(k).get(y*N + x)))
                ));
            }
        }
        Platform.runLater(() -> {
            int i = 0;
            for (Tile tile : tiles) {
                if (WeatherScene.hasShutdown()) return;
                grid.add(tile, i % N, i / N);
                i++;
            }
        });
        synchronized (sync) { sync.notify(); }
    }
}
