package fi.tuni.prog3.weatherapp.frontend.weather;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Class for converting millis since epoch and OpenWeather time to a date and time.
 * Provides easy comparison operations between objects of this class.
 *
 * @author Joonas Tuominen
 */
public class MillisToTime implements Comparable<MillisToTime> {
    private final long millis;
    private boolean lastComparisonResult = true;

    /**
     * Record for storing the time
     * @param hour The hour of day
     * @param minute The minutes
     * @param second The seconds
     */
    public record Time(int hour, int minute, int second) {
        /**
         * Returns the time represented as a string
         * @return "%02d:%02d:%02d", hour, minute, second
         */
        @Override
        public String toString() {
            return String.format("%02d:%02d:%02d", hour, minute, second);
        }
    }

    /**
     * A record for storing the date
     * @param year The year
     * @param month The month of the year
     * @param day The day of the month
     */
    public record Date(int year, int month, int day) {
        /**
         * Returns the date represented as a string
         * @return "%02d/%02d/%04d", day, month, year
         */
        @Override
        public String toString() {
            return String.format("%02d/%02d/%04d", day, month, year);
        }
    }

    /**
     * The time of this object
     */
    public final Time time;

    /**
     * The date of this object
     */
    public final Date date;

    /**
     * Constructor for MillisToTime based on millis since epoch
     * @param millis Millis since epoch
     */
    public MillisToTime(long millis) {
        this.millis = millis + ZonedDateTime.now().getOffset().getTotalSeconds() * 1000L;
        time = new Time(getHour(), getMinutes(), getSeconds());
        LocalDate localDate = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate();
        date = new Date(localDate.getYear(), localDate.getMonthValue(), localDate.getDayOfMonth());
    }

    /**
     * Create a MillisToTime using OpenWeather times. OpenWeather for some reason stores only the seconds
     * @param millis The time from OpenWeather
     * @return The MillisToTime object created
     */
    public static MillisToTime fromOpenWeatherTime(long millis) {
        return new MillisToTime(millis * 1000L);
    }

    /**
     * Gets the day of this MillisToTime object
     * @return The day of the month
     */
    public int getDay() { return date.day(); }

    /**
     * Gets the month of this MillisToTime object
     * @return The month of the year
     */
    public int getMonth() {
        return date.month();
    }

    /**
     * Gets the year of this MillisToTime object
     * @return The year
     */
    public int getYear() {
        return date.day();
    }

    /**
     * Gets the hour of day of this MillisToTime object
     * @return The hour of day
     */
    public int getHour() {
        return (int)(millis / (1000 * 60 * 60)) % 24;
    }

    /**
     * Gets the minutes of this MillisToTime object
     * @return The minutes
     */
    public int getMinutes() {
        return (int)(millis / (1000 * 60)) % 60;
    }

    /**
     * Gets the seconds of this MillisToTime object
     * @return The seconds
     */
    public int getSeconds() {
        return (int)(millis / 1000) % 60;
    }

    /**
     * Returns this object, but sets the lastComparisonResult &= millis >= o.millis
     * @param o The other MillisToTime object we want to compare to
     * @return The object we called this with
     */
    public MillisToTime isLargerThan(MillisToTime o) {
        lastComparisonResult &= millis >= o.millis;
        return this;
    }

    /**
     * Returns this object, but sets the lastComparisonResult &= millis <= o.millis
     * @param o The other MillisToTime object we want to compare to
     * @return The object we called this with
     */
    public MillisToTime isSmallerThan(MillisToTime o) {
        lastComparisonResult &= millis <= o.millis;
        return this;
    }

    /**
     * Returns the value of the last comparison and resets the comparison result.
     * @return The truth value of the comparison chain
     */
    public boolean eval() {
        var result = lastComparisonResult;
        lastComparisonResult = true;
        return result;
    }

    /**
     * Compares two MillisToTime objects
     * @param o the object to be compared.
     * @return The comparison between the millis of each object
     */
    @Override
    public int compareTo(MillisToTime o) {
        return Long.compare(millis, o.millis);
    }
}
