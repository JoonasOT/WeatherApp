package fi.tuni.prog3.weatherapp.backend;

import fi.tuni.prog3.weatherapp.backend.api.API;
import fi.tuni.prog3.weatherapp.backend.api.ip.IPService;
import fi.tuni.prog3.weatherapp.backend.api.openweather.OpenWeather;
import fi.tuni.prog3.weatherapp.backend.database.Database;
import fi.tuni.prog3.weatherapp.backend.database.cities.Cities.City;
import fi.tuni.prog3.weatherapp.backend.database.cities.builder.CityBuilder;
import fi.tuni.prog3.weatherapp.backend.database.geoip2.GeoLocation;
import fi.tuni.prog3.weatherapp.backend.database.geoip2.MaxMindGeoIP2;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public final class Backend {
    private static String CITIES_DATABASE_LOC = "./Databases/Cities";
    private static String GEOIP_DATABASE_LOC = "./Databases/GeoLite2-City_20240402/GeoLite2-City.mmdb";
    private static Backend INSTANCE;
    private final API OpenWeather;
    private final Database<List<City>> cityDatabase;
    private Backend(){
        IPService ipService = IPService.getInstance();
        AtomicReference<City> tmp = new AtomicReference<>(new City("N/A", "N/A"));
        ipService.getIP().ifPresent(ip -> {
            Database<GeoLocation> geoipDatabase = new MaxMindGeoIP2(GEOIP_DATABASE_LOC);
            var locationResult = geoipDatabase.get(ip);
            tmp.set(locationResult.map(geoLoc -> new City(geoLoc.city().getName(), geoLoc.country().getIsoCode()))
                    .orElseGet(() -> new City("N/A", "N/A")));
        });

        City location = tmp.get();

        System.out.println(location);
        OpenWeather = new OpenWeather.factory().construct();
        cityDatabase = new CityBuilder().setLocation(location.countryCode()).setDatabaseLocation(CITIES_DATABASE_LOC).build();

        var cityQuery = cityDatabase.get(location.name());
        cityQuery.ifPresent(System.out::println);

    }
    public static Backend getInstance() {
        return INSTANCE == null ? (INSTANCE = new Backend()) : INSTANCE;
    }
}
