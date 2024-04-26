package fi.tuni.prog3.weatherapp.frontend.search;

import javafx.application.Platform;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A class used to fill a given ContextMenu with SearchResults based on a search or favourites and history.
 *
 * @author Joonas Tuominen
 */
public class SuggestionFiller implements Runnable {
    private final String target;
    private final ContextMenu menu;
    private final Node where;
    private final SuggestionTextField textField;
    private final AtomicBoolean terminate = new AtomicBoolean(false);

    /**
     * The constructor for a SuggestionFiller
     * @param popUp The popUp we want to populate
     * @param menuLocation The node we want the menu to be attached to
     * @param textField The text field we can set the selected SearchResult value to
     * @param target The target string we want to generate suggestion for
     */
    public SuggestionFiller(ContextMenu popUp, Node menuLocation, SuggestionTextField textField, String target) {
        super();
        menu = popUp;
        menu.setMaxHeight(10);
        menu.setPrefHeight(10);
        where = menuLocation;
        this.textField = textField;
        this.target = target;
    }

    /**
     * Set this task to terminate
     */
    public void terminate() {
        terminate.set(true);
    }

    /**
     * Get if this task is terminating
     * @return true if yes, false if not
     */
    public boolean isTerminating() {
        return terminate.get();
    }

    /**
     * Method that is used for actually populating the given ContextMenu
     */
    @Override
    public void run() {
        // If the target was = "", the user wants the favourites and history otherwise generate suggestions
        List<SearchResult> results = target.isEmpty() ? SearchResult.GetFavouritesAndHistory() :
                                                        SearchResult.GenerateResults(target);

        // If we didn't get any results hide this menu
        if (results.isEmpty()) {
            Platform.runLater(() -> {
                if (terminate.get()) return;
                menu.hide();
            });
            return;
        } else { // We got some results. Time to make them unique
            Set<String> set = new HashSet<>();
            var tmp = new LinkedList<SearchResult>();
            for (SearchResult sr : results) {
                // As the favourites appear first, we only display them as favourite and not in history to remove
                // clutter as this makes the suggestion list a lot more readable
                if (!set.contains(sr.toString()) || sr.toStringIgnoreNull().isEmpty()) {
                    tmp.add(sr);
                    set.add(sr.toString());
                }
            }
            results = tmp;
        }

        // The final results to be added to the ContextMenu
        final List<SearchResult> finalResults = results;
        Platform.runLater(() -> {
            if (terminate.get()) return;
            menu.getItems().clear();
            menu.getItems().addAll(generateMenuItems(finalResults));
            if (!menu.isShowing()){
                if (terminate.get()) return;
                menu.show(where, Side.BOTTOM, 0, 0);
            }
        });
    }

    /**
     * Function that turns SearchResults into CustomMenuItems that can be added to a ContextMenu.
     * Also binds the setOnAction() to set the given TextField's value to the search results value and
     * automatically initiates search.
     * @param results The SearchResults we want to transform
     * @return List of CustomMenuItems generated from the results list
     */
    private List<CustomMenuItem> generateMenuItems(List<SearchResult> results) {
        List<CustomMenuItem> menuItems = new LinkedList<>();
        for (SearchResult res : results) {
            CustomMenuItem menuItem = new CustomMenuItem(res, true);
            menuItem.setOnAction(actionEvent -> {
                textField.setText(res.toStringIgnoreNull());
                textField.search();
                menu.hide();
            });
            menuItems.add(menuItem);
        }
        return menuItems;
    }
}
