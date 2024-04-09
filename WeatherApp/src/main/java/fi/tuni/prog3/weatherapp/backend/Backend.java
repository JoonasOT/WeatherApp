package fi.tuni.prog3.weatherapp.backend;

import fi.tuni.prog3.weatherapp.backend.api.API;
import fi.tuni.prog3.weatherapp.backend.api.iCallable;
import fi.tuni.prog3.weatherapp.backend.api.ip.IP_Getter;
import fi.tuni.prog3.weatherapp.backend.api.openweather.OpenWeather;
import fi.tuni.prog3.weatherapp.backend.database.Database;
import fi.tuni.prog3.weatherapp.backend.database.cities.Cities.City;
import fi.tuni.prog3.weatherapp.backend.database.cities.builder.CityBuilder;
import fi.tuni.prog3.weatherapp.backend.database.geoip2.GeoLocation;
import fi.tuni.prog3.weatherapp.backend.database.geoip2.MaxMindGeoIP2;

import java.lang.reflect.Method;
import java.util.List;

public final class Backend {
    private static String CITIES_DATABASE_LOC = "./Databases/Cities";
    private static String GEOIP_DATABASE_LOC = "./Databases/GeoLite2-City_20240402/GeoLite2-City.mmdb";
    private static Backend INSTANCE;
    private final API OpenWeather;
    private final Database<List<City>> cityDatabase;
    private Backend(){
        API ipGetter = new IP_Getter.factory().construct();
        String IP = null;

        for (Method method : IP_Getter.Callables.class.getDeclaredMethods()) {
            try {
                var ipResult = ipGetter.call((iCallable) method.invoke(null));
                if (ipResult.isEmpty()) continue;
                IP = ipResult.get().getData();
                break;
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
        City location;
        if (IP != null) {
            Database<GeoLocation> geoipDatabase = new MaxMindGeoIP2(GEOIP_DATABASE_LOC);
            var locationResult = geoipDatabase.get(IP);
            location = locationResult.map(geoLoc -> new City(geoLoc.city().getName(), geoLoc.country().getIsoCode()))
                                     .orElseGet(() -> new City("N/A", "N/A"));
        }
        else location = new City("N/A", "N/A");
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
