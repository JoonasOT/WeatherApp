package fi.tuni.prog3.weatherapp.backend.api.openweather;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation interface that is used to mark all the methods that create some record from a Response.
 *
 * @author Joonas Tuominen
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface FromJson {}
