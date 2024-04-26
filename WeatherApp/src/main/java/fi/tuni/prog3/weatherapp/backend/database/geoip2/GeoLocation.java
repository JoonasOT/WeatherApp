package fi.tuni.prog3.weatherapp.backend.database.geoip2;

import com.maxmind.geoip2.record.City;
import com.maxmind.geoip2.record.Country;

/**
 * The result of an MaxMindGeoIP2 database query.
 * @param country The country of the result
 * @param city The city of the result
 */
public record GeoLocation(Country country, City city) {}
