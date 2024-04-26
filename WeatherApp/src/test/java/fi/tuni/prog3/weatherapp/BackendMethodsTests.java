package fi.tuni.prog3.weatherapp;

import fi.tuni.prog3.weatherapp.backend.Backend;
import fi.tuni.prog3.weatherapp.backend.database.cities.Cities;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * A bunch of different tests to check favourites and history function.
 *
 * Nothing else implemented as I don't have time :(
 *
 * @author Joonas Tuominen
 */
public class BackendMethodsTests {
    Backend backend;
    List<Cities.City> history;
    List<Cities.City> favourites;
    @BeforeEach
    public void SetupBackend() {
        Backend.OPENWEATHER_API_KEY_LOCATION = "";
        backend = Backend.getInstance();
        // Get the printout for error in constructor
        history = new ArrayList<>(backend.getHistory());
        favourites = new ArrayList<>(backend.getFavourites());
    }

    @AfterEach
    public void ShutdownBackend() {
        Backend.Shutdown();
    }

    @Test
    public void TestGetHistory() {
        assertNotEquals(backend.getHistory(), null);
    }

    @Test
    public void TestGetFavourites() {
        assertNotEquals(backend.getFavourites(), null);
    }

    @Test
    public void TestHistoryNuke() {
        backend.nukeHistory();
        boolean empty = backend.getHistory().isEmpty();
        AddToHistory(history);
        assertTrue(empty);
    }

    @Test
    public void TestFavouriteNuke() {
        ClearFavourites();
        backend.nukeHistory();
        boolean empty = backend.getHistory().isEmpty();
        AddToFavourites(favourites);
        assertTrue(empty);
    }

    @Test
    public void TestAddToFavourites() {
        var tmp = new Cities.City("x", null);
        var pre = new HashSet<>(favourites);
        backend.addFavourite(tmp);

        boolean good = false;
        int c = 0;
        for (var x : backend.getFavourites()) {
            if (pre.contains(x)) continue;
            good = tmp.equals(x);
            System.err.println("X is: " + x);
            c++;
        }
        backend.removeFromFavourites(tmp);

        assertTrue(good);
        assertEquals(c, 1);
    }

    @Test
    public void TestHistoryAdditionAndReset() {
        var old = backend.getHistory();
        backend.nukeHistory();

        List<Cities.City> history = List.of(
                new Cities.City("1", null),
                new Cities.City(null, "cc"),
                new Cities.City("2", "cc"),
                new Cities.City(null, null)
        );
        AddToHistory(history);

        var res = backend.getHistory();

        boolean good = true;
        for (var i : IntStream.range(0, res.size()).toArray()) {
            if (!history.get(i).equals(res.get(i))) {
                good = false;
                System.err.println(res.get(i).toString() + " doesn't match " + history.get(i).toString());
            }
        }

        backend.nukeHistory();
        AddToHistory(old);

        assertTrue(good);
    }

    @Test
    public void TestHistoryPersistence() {
        var tmp = new Cities.City("x", null);

        var pre = new HashSet<>(history);
        backend.addToHistory(tmp);
        Backend.Shutdown();
        boolean good = false;
        int c = 0;
        for (var x : backend.getHistory()) {
            if (pre.contains(x)) continue;
            good = tmp.equals(x);
            System.err.println("X is: " + x);
            c++;
        }
        backend.getHistory().remove(tmp);

        assertTrue(good);
        assertEquals(c, 1);
    }

    @Test
    public void TestFavouritePersistence() {
        var tmp = new Cities.City("x", null);

        var pre = new HashSet<>(favourites);
        backend.addFavourite(tmp);
        Backend.Shutdown();
        boolean good = false;
        int c = 0;
        for (var x : backend.getFavourites()) {
            if (pre.contains(x)) continue;
            good = tmp.equals(x);
            System.err.println("X is: " + x);
            c++;
        }
        backend.removeFromFavourites(tmp);

        assertTrue(good);
        assertEquals(c, 1);
    }

    private void AddToHistory(List<Cities.City> cities) {
        synchronized (backend) {
            for (var c : cities) backend.addToHistory(c);
        }
    }
    private void ClearFavourites() {
        backend.getFavourites().clear();
    }
    private void AddToFavourites(final List<Cities.City> cities) {
        synchronized (backend) {
            for (var i : IntStream.range(0, cities.size()).toArray()) {
                backend.addFavourite(favourites.get(i));
            }
        }
    }
}
