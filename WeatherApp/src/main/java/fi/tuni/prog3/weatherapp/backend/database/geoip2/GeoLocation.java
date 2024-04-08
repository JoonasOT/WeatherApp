package fi.tuni.prog3.weatherapp.backend.database.geoip2;

import com.maxmind.geoip2.record.City;
import com.maxmind.geoip2.record.Country;

public class GeoLocation {
    private Country country;
    private City city;
    public GeoLocation(Country country, City city) {
        this.country = country;
        this.city = city;
    }

    public City getCity() {
        return city;
    }

    public Country getCountry() {
        return country;
    }

    @Override
    public String toString() {
        return "GeoLocation{" +
                "country=" + country +
                ", city=" + city +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GeoLocation that = (GeoLocation) o;

        if (!country.equals(that.country)) return false;
        return city.equals(that.city);
    }

    @Override
    public int hashCode() {
        int result = country.hashCode();
        result = 31 * result + city.hashCode();
        return result;
    }
}