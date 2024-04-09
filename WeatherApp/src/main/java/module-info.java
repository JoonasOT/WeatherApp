module fi.tuni.progthree.weatherapp {
    exports fi.tuni.prog3.weatherapp;
    requires javafx.controls;
    requires com.google.gson;
    opens fi.tuni.prog3.weatherapp.backend.security to com.google.gson;
    opens fi.tuni.prog3.weatherapp.backend.database.cities to com.google.gson;
    opens fi.tuni.prog3.weatherapp.backend.api to com.google.gson;
    requires com.maxmind.geoip2;
}
