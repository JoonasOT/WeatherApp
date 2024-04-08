package fi.tuni.prog3.weatherapp.backend.database.geoip2;

import com.maxmind.geoip2.record.City;
import com.maxmind.geoip2.record.Country;

public record GeoLocation(Country country, City city) {}
