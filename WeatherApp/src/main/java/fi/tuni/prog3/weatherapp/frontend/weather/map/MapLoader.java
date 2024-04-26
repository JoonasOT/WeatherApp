package fi.tuni.prog3.weatherapp.frontend.weather.map;

import fi.tuni.prog3.weatherapp.frontend.scenes.WeatherScene;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * A class used to load a map onto the GUI asynchronously
 *
 * @author Joonas Tuominen
 */
public class MapLoader {
    private static final Label noMapYetLabel = new Label("Map is loading...");
    private static final Object sync = WeatherScene.getSyncObj();

    /**
     * A record for running the map load after it has been completed
     * @param box The VBox we want to add the map to
     * @param out The WeatherMapView we want to add to the box
     */
    private record SetContentTask(VBox box, WeatherMapView out) implements Runnable {
        /**
         * Function that waits the thread until the sync object has been notified that everything is ready or the
         * map formation has been terminated
         */
        @Override
        public void run() {
            synchronized (sync) {
                try {
                    sync.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            if (WeatherScene.hasShutdown()) return; // Map formation has been terminated so exit here
            Platform.runLater(() -> {
                box.getChildren().remove(noMapYetLabel);
                box.getChildren().add(out);
            });
        }
    }

    /**
     * Function for loading a Map to a given VBox. Displays a label while the map is loading.
     * @param box The VBox we want to attach this map to
     */
    public static void loadTo(VBox box) {
        box.getChildren().add(noMapYetLabel);
        if (WeatherScene.hasShutdown()) return;
        new Thread(new SetContentTask(box, new WeatherMapView())).start();
    }
}
