package fi.tuni.prog3.weatherapp.frontend.search;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.TextField;

import java.util.LinkedList;
import java.util.List;

public class SuggestionTextField extends TextField {
    private final ContextMenu popUp = new ContextMenu();
    public SuggestionTextField() {
        super();
        focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
                popUp.hide();
            }
        });
        textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                List<SearchResult> results = getText().isEmpty() ? SearchResult.GetFavouritesAndHistory() :
                                                                   SearchResult.GenerateResults(getText());

                if (results.isEmpty()) {
                    popUp.hide();
                    return;
                }

                popUp.getItems().clear();
                popUp.getItems().addAll(generateMenuItems(results));
                if (!popUp.isShowing()) popUp.show(SuggestionTextField.this, Side.BOTTOM, 0, 0);
            }
        });
    }
    private List<CustomMenuItem> generateMenuItems(List<SearchResult> results) {
        List<CustomMenuItem> menuItems = new LinkedList<>();
        for (SearchResult res : results) {
            CustomMenuItem menuItem = new CustomMenuItem(res, true);
            menuItem.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    setText(res.toString());
                    popUp.hide();
                }
            });
            menuItems.add(menuItem);
        }
        return menuItems;
    }
}
