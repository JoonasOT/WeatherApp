package fi.tuni.prog3.weatherapp.frontend.weather.map;

import fi.tuni.prog3.weatherapp.backend.Backend;
import fi.tuni.prog3.weatherapp.backend.api.openweather.JSON_OBJs.Coord;
import fi.tuni.prog3.weatherapp.backend.api.openweather.WeatherMap;
import fi.tuni.prog3.weatherapp.frontend.scenes.WeatherScene;
import javafx.scene.image.Image;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class MapLayerGenerator implements Callable<List<Image>> {
    private final WeatherMap.WeatherLayer layer;
    private final int Z;
    private final int N;
    private final Coord coords;

    public MapLayerGenerator(WeatherMap.WeatherLayer layer, Coord coords, int Z, int N) {
        this.layer = layer;
        this.coords = coords;
        this.Z = Z;
        this.N = N;
    }

    @Override
    public List<Image> call() {
        if (WeatherScene.hasShutdown()) return new ArrayList<>();
        List<byte[]> bytes = Backend.getInstance().getNxNtiles(
                new WeatherMap.Callables.MapTile(false, layer, null), coords.lat(), coords.lon(), Z, N);
        if (WeatherScene.hasShutdown()) return new ArrayList<>();
        return bytes.stream().map(b -> new Image(new ByteArrayInputStream(b))).collect(Collectors.toList());
    }
}
