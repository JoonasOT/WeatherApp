package fi.tuni.prog3.weatherapp.frontend.search;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public final class SearchResult extends BorderPane {
    public SearchResult(String city, String countryCode) {
        super();
        super.setPadding(new Insets(10, 10, 10, 10));

        HBox content = new HBox(10);
        Label cityL = new Label(city);
        Label countryL = new Label(countryCode);
        content.getChildren().addAll(cityL, countryL);
        super.setCenter(content);
    }
}
