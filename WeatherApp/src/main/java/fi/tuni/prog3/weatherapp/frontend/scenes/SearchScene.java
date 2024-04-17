package fi.tuni.prog3.weatherapp.frontend.scenes;

import fi.tuni.prog3.weatherapp.backend.database.cities.Cities;
import fi.tuni.prog3.weatherapp.frontend.search.SuggestionTextField;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class SearchScene extends Scene {
    private static SearchScene INSTANCE;
    private static Stage STAGE;
    private static final BorderPane root = new BorderPane();
    public SearchScene(Stage stage){
        super(root, 720, 720);

        if (INSTANCE != null) {
            throw new RuntimeException("SearchScene has already been constructed!");
        }

        STAGE = stage;

        SuggestionTextField query = new SuggestionTextField();
        query.setFocusTraversable(false);
        query.setMaxWidth(200);
        root.setCenter(query);

        INSTANCE = this;
    }
    public static SearchScene getInstance() {
        if (INSTANCE == null) {
            throw new RuntimeException("SearchScene has not been constructed!");
        }
        return INSTANCE;
    }
    public static void ChangeToWeatherScene(Cities.City city) {
        STAGE.setScene(WeatherScene.getInstance().generateFromCity(city));
    }
}
