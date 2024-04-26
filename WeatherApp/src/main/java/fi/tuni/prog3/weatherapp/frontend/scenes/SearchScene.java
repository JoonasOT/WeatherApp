package fi.tuni.prog3.weatherapp.frontend.scenes;

import static fi.tuni.prog3.weatherapp.WeatherApp.*;

import fi.tuni.prog3.weatherapp.frontend.search.SearchResult;
import fi.tuni.prog3.weatherapp.frontend.search.SuggestionTextField;

import fi.tuni.prog3.weatherapp.backend.Backend;
import fi.tuni.prog3.weatherapp.backend.api.openweather.OpenWeather;
import fi.tuni.prog3.weatherapp.backend.database.cities.Cities;

import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;

/**
 * This is a singleton for creating the search scene to the user.
 *
 * @author Joonas Tuominen
 */
public class SearchScene extends Scene {
    private static SearchScene INSTANCE;
    private static Stage STAGE;
    private static OpenWeather.UNIT units = OpenWeather.UNIT.METRIC;
    private static final BorderPane root = new BorderPane();

    /**
     * The constructor for the SearchScene. CALL ONCE!
     * @param stage The stage where we want this SearchScene to render to
     * @throws RuntimeException If called for a second time
     */
    public SearchScene(Stage stage) {
        super(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        if (INSTANCE != null) {
            throw new RuntimeException("SearchScene has already been constructed!");
        }

        String initialLoadFromHistory;
        { // Get the last city searched from the backend
            var tmp = Backend.getInstance().getHistory();
            try {
                initialLoadFromHistory = new SearchResult(tmp.get(tmp.size()-1).name(), tmp.get(tmp.size()-1).countryCode())
                                                        .toStringIgnoreNull();
            } catch (IndexOutOfBoundsException ignored) { initialLoadFromHistory = ""; }
        }

        STAGE = stage;

        VBox stack = new VBox(10);
        stack.setMaxWidth(200);
        stack.setMaxHeight(40);

        SuggestionTextField query = new SuggestionTextField();
        query.setText(initialLoadFromHistory);
        query.setFocusTraversable(false);

        // TODO: The default units could be selected from the IP Service result
        ComboBox<String> unitSelection = new ComboBox<>();
        unitSelection.getItems().addAll(List.of("Metric", "Imperial"));
        unitSelection.setValue("Units");
        unitSelection.setOnAction(x -> units = OpenWeather.UNIT.fromString(unitSelection.getValue()));
        unitSelection.setFocusTraversable(false);

        stack.getChildren().addAll(query, unitSelection);
        root.setCenter(stack);

        INSTANCE = this;
    }

    /**
     * Get the SearchScene if it has been constructed by WeatherApp.java
     * @return The SearchScene instance
     * @throws RuntimeException If the SearchScene has not been instantiated by WeatherApp.java
     */
    public static SearchScene getInstance() {
        if (INSTANCE == null) throw new RuntimeException("SearchScene has not been constructed!");
        return INSTANCE;
    }

    /**
     * Change the scene to the WeatherScene with a given city
     * @param city The city we want to form the WeatherScene with
     */
    public static void ChangeToWeatherScene(Cities.City city) {
        STAGE.setScene(WeatherScene.getInstance().generateFromCity(city, units));
    }
}
