package fi.tuni.prog3.weatherapp.frontend.search;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public final class SearchResult extends BorderPane {
    public SearchResult(String city, String countryCode) {
        super();
        super.setPadding(new Insets(5, 10, 5, 10));
        super.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, new CornerRadii(5), new Insets(1))));
        HBox content = new HBox(10);
        Label cityL = new Label(city);
        Label countryL = new Label(countryCode);
        content.getChildren().addAll(cityL, countryL);
        super.setCenter(content);
    }
}
