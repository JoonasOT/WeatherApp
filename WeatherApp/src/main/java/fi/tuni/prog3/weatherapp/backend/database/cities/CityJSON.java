package fi.tuni.prog3.weatherapp.backend.database.cities;

import com.google.gson.annotations.Expose;

import java.util.HashMap;
import java.util.Objects;

public class CityJSON {
    public final int id;
    public final String name;
    public final String state;
    public final String country;
    public final HashMap<String, Double> coord;
    public CityJSON(int id, String name, String state, String country, HashMap<String, Double> coord) {
        this.id = id;
        this.name = name;
        this.state = state;
        this.country = country;
        this.coord = coord;
    }

    public String state() {
        return state;
    }

    public String country() {
        return country;
    }

    public HashMap<String, Double> coord() {
        return coord;
    }

    public String name() {
        return name;
    }

    public int id() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CityJSON cityJSON = (CityJSON) o;

        if (id != cityJSON.id) return false;
        if (!Objects.equals(name, cityJSON.name)) return false;
        if (!Objects.equals(state, cityJSON.state)) return false;
        if (!Objects.equals(country, cityJSON.country)) return false;
        return Objects.equals(coord, cityJSON.coord);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (state != null ? state.hashCode() : 0);
        result = 31 * result + (country != null ? country.hashCode() : 0);
        result = 31 * result + (coord != null ? coord.hashCode() : 0);
        return result;
    }
}
