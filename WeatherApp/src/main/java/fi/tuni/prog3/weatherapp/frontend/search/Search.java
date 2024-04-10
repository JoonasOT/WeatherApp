package fi.tuni.prog3.weatherapp.frontend.search;

import fi.tuni.prog3.weatherapp.backend.Backend;
import fi.tuni.prog3.weatherapp.backend.database.cities.Cities;

import java.util.ArrayList;
import java.util.List;

public class Search {
    public static ArrayList<SearchResult> GenerateResults(String word) {
        Backend backend = Backend.getInstance();
        List<Cities.City> cities = backend.getSimilarCities(word);
        if (cities == null) return new ArrayList<>();

        ArrayList<SearchResult> results = new ArrayList<>(cities.size());
        for (Cities.City city : cities) {
            results.add(new SearchResult(city.name(), city.countryCode()));
        }
        return results;
    }
}
