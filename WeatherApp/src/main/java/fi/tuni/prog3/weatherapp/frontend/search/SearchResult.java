package fi.tuni.prog3.weatherapp.frontend.search;

import fi.tuni.prog3.weatherapp.backend.Backend;
import fi.tuni.prog3.weatherapp.backend.database.cities.Cities;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public final class SearchResult extends BorderPane {
    private final String city;
    private final String countryCode;
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
    @Override
    public String toString() {
        return city + ", " + countryCode;
    }
    public static ArrayList<SearchResult> CitiesToSearchResults(List<Cities.City> cities) {
        ArrayList<SearchResult> results = new ArrayList<>(cities.size());
        for (Cities.City city : cities) {
            results.add(new SearchResult(city.name(), city.countryCode()));
        }
        return results;
    }
    public static ArrayList<SearchResult> GenerateResults(String word) {
        Backend backend = Backend.getInstance();
        List<Cities.City> cities = backend.getSimilarCities(word);
        if (cities == null) return new ArrayList<>();

        return CitiesToSearchResults(cities);
    }
    public static ArrayList<SearchResult> GetFavourites() {
        return CitiesToSearchResults(Backend.getInstance().getFavourites());
    }
    public static ArrayList<SearchResult> GetHistory() {
        return CitiesToSearchResults(Backend.getInstance().getHistory());
    }
    public static ArrayList<SearchResult> GetFavouritesAndHistory() {
        var tmp = GetFavourites();
        tmp.addAll(GetHistory());
        return tmp;
    }
}
