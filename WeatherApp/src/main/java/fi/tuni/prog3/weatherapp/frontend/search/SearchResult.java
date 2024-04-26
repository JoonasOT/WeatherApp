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

/**
 * This class renders a SearchResult node.
 *
 * @author Joonas Tuominen
 */
public final class SearchResult extends BorderPane {
    private final String city;
    private final String countryCode;
    public enum RESULT_TYPE { NEW, FAV, HIST }
    private static final Background background = new Background(
            new BackgroundFill(
                    Color.LIGHTGRAY,
                    new CornerRadii(5),
                    new Insets(1)
            )
    );

    /**
     * The constructor for a SearchResult
     * @param city The city name of the search result
     * @param countryCode The country code of the search result
     */
    public SearchResult(String city, String countryCode) {
        super();

        this.city = city;
        this.countryCode = countryCode;

        super.setPadding(new Insets(5, 10, 5, 10));
        super.setBackground(background);
        HBox content = new HBox(10);
        Label cityL = new Label(this.city);
        Label countryL = new Label(this.countryCode);
        content.getChildren().addAll(cityL, countryL);
        super.setCenter(content);
    }

    /**
     * The constructor for a SearchResult
     * @param city The city name of the search result
     * @param countryCode The country code of the search result
     * @param icon The FontAwesome glyph we want to attach to this SearchResult
     * @param style The style we want to set for the icon
     */
    public SearchResult(String city, String countryCode, FontAwesome icon, String style) {
        super();

        this.city = city;
        this.countryCode = countryCode;

        super.setPadding(new Insets(5, 10, 5, 10));
        super.setBackground(background);

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

    /**
     * Returns the city name and country code
     * @return city + ", " + countryCode
     */
    @Override
    public String toString() {
        return city + ", " + countryCode;
    }

    /**
     * Returns the city name and country code if they are set
     * @return toString() if both are not null, otherwise only the variables that aren't null
     */
    public String toStringIgnoreNull() {
        return (city != null ? (countryCode != null ? toString() : city) : (countryCode != null ? countryCode : ""));
    }

    /**
     * Takes a list of cities and turns them into a list of SearchResults based on the type of result they are
     * @param cities The cities list we want transform
     * @param type The type of SearchResults we want to create
     * @return The created list of SearchResults
     */
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

    /**
     * A static function for generating a list of SearchResults based on backend query
     * @param word The word we want to generate results for
     * @return The list of SearchResults created
     */
    public static ArrayList<SearchResult> GenerateResults(String word) {
        Backend backend = Backend.getInstance();
        List<Cities.City> cities = backend.getSimilarCities(word);
        if (cities == null) return new ArrayList<>();

        return CitiesToSearchResults(cities, RESULT_TYPE.NEW);
    }

    /**
     * A static function for getting all the cities set as favourite in a List of SearchResults
     * @return The array list created of SearchResults
     */
    public static ArrayList<SearchResult> GetFavourites() {
        return CitiesToSearchResults(Backend.getInstance().getFavourites(), RESULT_TYPE.FAV);
    }

    /**
     * A static function for getting all the city searches done by the user in a List of SearchResults
     * @return The array list created of SearchResults
     */
    public static ArrayList<SearchResult> GetHistory() {
        return CitiesToSearchResults(Backend.getInstance().getHistory(), RESULT_TYPE.HIST);
    }

    /**
     * A static function for getting all the favourites and history of the user (NOT UNIQUE).
     * Favourites are placed first and then history is added after them.
     * @return The array list created of SearchResults
     */
    public static ArrayList<SearchResult> GetFavouritesAndHistory() {
        var tmp = GetFavourites();
        tmp.addAll(GetHistory());
        return tmp;
    }
}
