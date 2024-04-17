package fi.tuni.prog3.weatherapp.backend.api.general;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation interface used to notate which method we want to use when performing internet requests.
 * Defaults to GET.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RequestMethod {
    /**
     * A method/field that describes the method we want to use.
     * @return the method to use in performing an internet request.
     */
    public String method() default "GET";
}
