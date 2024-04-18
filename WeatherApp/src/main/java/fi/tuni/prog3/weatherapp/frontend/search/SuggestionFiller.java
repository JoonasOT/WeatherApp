package fi.tuni.prog3.weatherapp.frontend.search;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class SuggestionFiller implements Runnable {
    private String target;
    private final ContextMenu menu;
    private final Node where;
    private final SuggestionTextField textField;
    private AtomicBoolean terminate = new AtomicBoolean(false);
    public SuggestionFiller(ContextMenu popUp, Node menuLocation, SuggestionTextField textField, String target) {
        super();
        menu = popUp;
        where = menuLocation;
        this.textField = textField;
        this.target = target;
    }
    public void terminate() {
        terminate.set(true);
    }
    public boolean isTerminating() {
        return terminate.get();
    }
    @Override
    public void run() {
        List<SearchResult> results = target.isEmpty() ? SearchResult.GetFavouritesAndHistory() :
                SearchResult.GenerateResults(target);

        if (results.isEmpty()) {
            Platform.runLater(() -> {
                if (terminate.get()) return;
                menu.hide();
            });
            return;
        }

        Platform.runLater(() -> {
            if (terminate.get()) return;
            menu.getItems().clear();
            menu.getItems().addAll(generateMenuItems(results));
            if (!menu.isShowing()){
                if (terminate.get()) return;
                menu.show(where, Side.BOTTOM, 0, 0);
            }
        });
    }

    private List<CustomMenuItem> generateMenuItems(List<SearchResult> results) {
        List<CustomMenuItem> menuItems = new LinkedList<>();
        for (SearchResult res : results) {
            CustomMenuItem menuItem = new CustomMenuItem(res, true);
            menuItem.setOnAction(actionEvent -> {
                textField.setText(res.toString());
                textField.search();
                menu.hide();
            });
            menuItems.add(menuItem);
        }
        return menuItems;
    }
}