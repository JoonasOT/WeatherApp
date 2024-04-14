package fi.tuni.prog3.weatherapp.frontend.scenes;

import fi.tuni.prog3.weatherapp.frontend.search.Search;
import fi.tuni.prog3.weatherapp.frontend.search.SearchResult;
import fi.tuni.prog3.weatherapp.frontend.search.SuggestionTextField;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class SearchScene {
    public static Scene create() {
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 720, 720);

        SuggestionTextField query = new SuggestionTextField();
        query.setMaxWidth(200);
        root.setCenter(query);

        return scene;
    }
}
