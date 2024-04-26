package fi.tuni.prog3.weatherapp.backend.database.cities;

import java.util.HashMap;

/**
 * An intermediate record used for getting useful information from city.list.json.gz
 * @param id The city id
 * @param name The city name
 * @param state The city's state
 * @param country The city's country
 * @param coord The city's coordinates
 *
 * @author Joonas Tuominen
 */
public record CityJSON(int id, String name, String state, String country, HashMap<String, Double> coord) {}
