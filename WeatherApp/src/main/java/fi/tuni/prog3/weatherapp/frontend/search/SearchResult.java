package fi.tuni.prog3.weatherapp.frontend.search;

import fi.tuni.prog3.weatherapp.backend.Backend;
import fi.tuni.prog3.weatherapp.backend.database.cities.Cities;
import fi.tuni.prog3.weatherapp.frontend.fonts.FontAwesome;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.List;

public final class SearchResult extends BorderPane {
    private final String city;
    private final String countryCode;
    public enum RESULT_TYPE { NEW, FAV, HIST }
    public SearchResult(String city, String countryCode) {
        super();

        this.city = city;
        this.countryCode = countryCode;

        super.setPadding(new Insets(5, 10, 5, 10));
        super.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, new CornerRadii(5), new Insets(1))));
        HBox content = new HBox(10);
        Label cityL = new Label(this.city);
        Label countryL = new Label(this.countryCode);
        content.getChildren().addAll(cityL, countryL);
        super.setCenter(content);
    }
    public SearchResult(String city, String countryCode, FontAwesome icon, String style) {
        super();

        this.city = city;
        this.countryCode = countryCode;

        super.setPadding(new Insets(5, 10, 5, 10));
        super.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, new CornerRadii(5), new Insets(1))));
        HBox content = new HBox(10);
        Label cityL = new Label(this.city);
        Label countryL = new Label(this.countryCode);
        content.getChildren().addAll(cityL, countryL);
        super.setCenter(content);

        Label iconLabel = new Label(icon.unicode);
        if (style != null) iconLabel.setStyle(style);
        iconLabel.setFont(Font.loadFont(icon.fontLocation, 15));

        super.setLeft(iconLabel);
    }
    @Override
    public String toString() {
        return city + ", " + countryCode;
    }
    public String toStringIgnoreNull() {
        return (city != null ? (countryCode != null ? toString() : city) : (countryCode != null ? countryCode : ""));
    }
    public static ArrayList<SearchResult> CitiesToSearchResults(List<Cities.City> cities, RESULT_TYPE type) {
        ArrayList<SearchResult> results = new ArrayList<>(cities.size());
        for (Cities.City city : cities) {
            String[] s = new String[]{ city.name(), city.countryCode() };
            results.add(switch (type) {
                case NEW -> new SearchResult(s[0], s[1]);
                case FAV -> new SearchResult(s[0], s[1], FontAwesome.STAR_FILLED, "-fx-text-fill: yellow;");
                case HIST -> new SearchResult(s[0], s[1], FontAwesome.HISTORY, null);
            });
        }
        return results;
    }
    public static ArrayList<SearchResult> GenerateResults(String word) {
        Backend backend = Backend.getInstance();
        List<Cities.City> cities = backend.getSimilarCities(word);
        if (cities == null) return new ArrayList<>();

        return CitiesToSearchResults(cities, RESULT_TYPE.NEW);
    }
    public static ArrayList<SearchResult> GetFavourites() {
        return CitiesToSearchResults(Backend.getInstance().getFavourites(), RESULT_TYPE.FAV);
    }
    public static ArrayList<SearchResult> GetHistory() {
        return CitiesToSearchResults(Backend.getInstance().getHistory(), RESULT_TYPE.HIST);
    }
    public static ArrayList<SearchResult> GetFavouritesAndHistory() {
        var tmp = GetFavourites();
        tmp.addAll(GetHistory());
        return tmp;
    }
}
