package fi.tuni.prog3.weatherapp.frontend.scenes;

import fi.tuni.prog3.weatherapp.frontend.search.Search;
import fi.tuni.prog3.weatherapp.frontend.search.SearchResult;
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
        VBox searchAndResults = new VBox(5);
        // searchAndResults.setBackground(new Background(new BackgroundFill(Color.GRAY, new CornerRadii(5), new Insets(0))));
        TextField search = Search.GenerateSearchField("sinäjoki");
        BorderPane results = new BorderPane();
        searchAndResults.getChildren().addAll(search, results);

        root.setCenter(searchAndResults);
        searchAndResults.setMaxWidth(150);
        root.setPadding(new Insets(360, 10, 10, 10));
        results.setTop(Search.toVBox(Search.GenerateResults(search.getText())));
        search.setOnKeyReleased(keyEvent -> {
            results.getChildren().clear();
            results.setTop(Search.toVBox(Search.GenerateResults(search.getText())));
        });

        return scene;
    }
}
