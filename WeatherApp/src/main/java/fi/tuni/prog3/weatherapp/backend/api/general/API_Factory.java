package fi.tuni.prog3.weatherapp.backend.api.general;

import fi.tuni.prog3.weatherapp.backend.security.Key;

import java.net.MalformedURLException;

public interface API_Factory {
    public Key getKey();
    public API construct() throws MalformedURLException;
}
