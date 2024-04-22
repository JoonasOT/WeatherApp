package fi.tuni.prog3.weatherapp.frontend.weather.map;

import fi.tuni.prog3.weatherapp.frontend.scenes.WeatherScene;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class MapLoader {
    private static Label noMapYetLabel = new Label("Map is loading...");
    private static Object sync = WeatherScene.getSyncObj();

    private record SetContentTask(VBox box, WeatherMapView out) implements Runnable {
        @Override
        public void run() {
            synchronized (sync) {
                System.out.println("Freed!");
                try {
                    sync.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            if (WeatherScene.hasShutdown()) return;
            Platform.runLater(() -> {
                box.getChildren().remove(noMapYetLabel);
                box.getChildren().add(out);
            });
        }
    }
    public static void loadTo(VBox box) {
        box.getChildren().add(noMapYetLabel);
        if (WeatherScene.hasShutdown()) return;
        new Thread(new SetContentTask(box, new WeatherMapView())).start();
    }
}
