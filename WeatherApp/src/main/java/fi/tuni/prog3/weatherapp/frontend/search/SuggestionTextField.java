package fi.tuni.prog3.weatherapp.frontend.search;

import fi.tuni.prog3.weatherapp.backend.database.cities.Cities;
import fi.tuni.prog3.weatherapp.frontend.scenes.SearchScene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

public class SuggestionTextField extends TextField {
    private final ContextMenu popUp = new ContextMenu();
    private SuggestionFiller task = new SuggestionFiller(popUp, SuggestionTextField.this, SuggestionTextField.this, "");
    public SuggestionTextField() {
        super();
        task.terminate();
        focusedProperty().addListener((observableValue, aBoolean, t1) -> popUp.hide());
        textProperty().addListener((observableValue, s, t1) -> {
            if (!task.isTerminating()) task.terminate();
            task = new SuggestionFiller(popUp, SuggestionTextField.this, SuggestionTextField.this, getText());
            new Thread(task).start();
        });

        super.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                search();
            }
        });
    }
    public void search() {
        String[] fields = getText().split(", ");
        Cities.City city = fields.length == 2 ? new Cities.City(fields[0], fields[1]) :
                new Cities.City(getText(), null);
        SearchScene.ChangeToWeatherScene(city);
        task.terminate();
    }
}
