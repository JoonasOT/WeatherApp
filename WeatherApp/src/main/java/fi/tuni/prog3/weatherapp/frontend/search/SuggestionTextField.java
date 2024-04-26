package fi.tuni.prog3.weatherapp.frontend.search;

import fi.tuni.prog3.weatherapp.backend.database.cities.Cities;
import fi.tuni.prog3.weatherapp.frontend.scenes.SearchScene;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

/**
 * Class for creating a JavaFX node for a suggestive TextField. Suggest potential cities similar to the typed text
 * or if is set to empty, it shows the user their favourite cities and search history (history that is not shown in
 * favourites).
 *
 * @author Joonas Tuominen
 */
public class SuggestionTextField extends TextField {
    private final ContextMenu popUp = new ContextMenu();
    private SuggestionFiller task = new SuggestionFiller(popUp, SuggestionTextField.this, SuggestionTextField.this, "");

    /**
     * Constructor for a suggestive TextField
     */
    public SuggestionTextField() {
        super();
        task.terminate();
        focusedProperty().addListener((observableValue, aBoolean, t1) -> popUp.hide());
        textProperty().addListener((observableValue, s, t1) -> {
            if (!task.isTerminating()) task.terminate();

            // Get new task, so we can terminate if value changed quickly
            task = new SuggestionFiller(popUp, SuggestionTextField.this, SuggestionTextField.this, getText());

            // Run task in another thread so, it doesn't pause the whole GUI while calculating
            new Thread(task).start();
        });

        super.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                search();
            }
        });
    }

    /**
     * Search for a city and instantiate the change from SearchScene to the WeatherScene based on the typed input
     */
    public void search() {
        String[] fields = getText().split(", ");
        Cities.City city = fields.length == 2 ? new Cities.City(fields[0], fields[1]) :
                new Cities.City(getText(), null);
        SearchScene.ChangeToWeatherScene(city);
        task.terminate();
    }
}
