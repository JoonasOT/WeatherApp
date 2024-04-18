package fi.tuni.prog3.weatherapp.backend.api.general;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation interface used to add additional RequestProperties to an iCallable.
 * When attached to a method the API.call() method can use the declared method to get the value of the request property.
 * Thus the method this is attached to MUST return the value we want the set request property to be.
 *
 * @author Joonas Tuominen
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SetRequestProperty {
    /**
     * A method (aka field for @interfaces) that names the Property we want to add
     * @return The name of the request property
     */
    public String Property();
}
