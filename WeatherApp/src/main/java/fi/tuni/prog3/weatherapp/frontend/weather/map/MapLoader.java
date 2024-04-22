package fi.tuni.prog3.weatherapp.frontend.weather.map;

import fi.tuni.prog3.weatherapp.frontend.scenes.WeatherScene;
import javafx.application.Platform;
import javafx.scene.layout.VBox;

import java.util.concurrent.atomic.AtomicReference;

public class MapLoader {
    private record SetContentTask(VBox box, AtomicReference<WeatherMapView> out) implements Runnable {
        @Override
        public void run() {
            Object sync = out.get().getSync();
            synchronized (sync) {
                try {
                    sync.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            if (WeatherScene.hasShutdown()) return;
            Platform.runLater(() -> box.getChildren().add(out.get()));
        }
    }
    public static WeatherMapView loadTo(VBox box) {
        AtomicReference<WeatherMapView> out = new AtomicReference<>();
        Platform.runLater(() -> {
            if (WeatherScene.hasShutdown()) return;
            out.set(new WeatherMapView());
            new Thread(new SetContentTask(box, out)).start();
        });
        return out.get();
    }
}
