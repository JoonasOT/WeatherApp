package fi.tuni.prog3.weatherapp.frontend.search;

import fi.tuni.prog3.weatherapp.backend.Backend;
import fi.tuni.prog3.weatherapp.backend.database.cities.Cities;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class Search {
    public static ArrayList<SearchResult> GenerateResults(String word) {
        Backend backend = Backend.getInstance();
        List<Cities.City> cities = backend.getSimilarCities(word);
        if (cities == null) return new ArrayList<>();

        ArrayList<SearchResult> results = new ArrayList<>(cities.size());
        for (Cities.City city : cities) {
            System.out.println(city);
            results.add(new SearchResult(city.name(), city.countryCode()));
        }
        return results;
    }
    public static VBox toVBox(ArrayList<SearchResult> searchResults) {
        VBox results = new VBox();
        results.getChildren().addAll(searchResults);
        return results;
    }
    public static TextField GenerateSearchField(String initialGuess) {
        TextField field = new TextField(initialGuess);

        return field;
    }
}
