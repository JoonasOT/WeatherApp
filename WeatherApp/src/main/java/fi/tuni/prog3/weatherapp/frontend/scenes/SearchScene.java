package fi.tuni.prog3.weatherapp.frontend.scenes;

import fi.tuni.prog3.weatherapp.WeatherApp;
import fi.tuni.prog3.weatherapp.backend.Backend;
import fi.tuni.prog3.weatherapp.backend.api.openweather.OpenWeather;
import fi.tuni.prog3.weatherapp.backend.database.cities.Cities;
import fi.tuni.prog3.weatherapp.frontend.search.SearchResult;
import fi.tuni.prog3.weatherapp.frontend.search.SuggestionTextField;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;

public class SearchScene extends Scene {
    private static SearchScene INSTANCE;
    private static Stage STAGE;
    private static OpenWeather.UNIT units = OpenWeather.UNIT.METRIC;
    private static final BorderPane root = new BorderPane();
    public SearchScene(Stage stage){
        super(root, WeatherApp.WINDOW_WIDTH, WeatherApp.WINDOW_HEIGHT);
        if (INSTANCE != null) {
            throw new RuntimeException("SearchScene has already been constructed!");
        }
        String initialLoadFromHistory;
        {
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

        ComboBox<String> unitSelection = new ComboBox<>();
        unitSelection.getItems().addAll(List.of("Metric", "Imperial"));
        unitSelection.setValue("Units");
        unitSelection.setOnAction(x -> units = OpenWeather.UNIT.fromString(unitSelection.getValue()));
        unitSelection.setFocusTraversable(false);

        stack.getChildren().addAll(query, unitSelection);
        root.setCenter(stack);

        INSTANCE = this;
    }
    public static SearchScene getInstance() {
        if (INSTANCE == null) {
            throw new RuntimeException("SearchScene has not been constructed!");
        }
        return INSTANCE;
    }
    public static void ChangeToWeatherScene(Cities.City city) {
        STAGE.setScene(WeatherScene.getInstance().generateFromCity(city, units));
    }
}
