package fi.tuni.prog3.weatherapp.backend.database;

import java.util.Optional;

public interface Database<T> {
    public Optional<T> get(String what);
}
