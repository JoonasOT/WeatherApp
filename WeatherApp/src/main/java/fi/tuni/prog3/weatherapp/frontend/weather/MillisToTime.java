package fi.tuni.prog3.weatherapp.frontend.weather;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class MillisToTime implements Comparable<MillisToTime> {
    private final long millis;
    private boolean lastComparisonResult = true;
    public record Time(int hour, int minute, int second) {
        @Override
        public String toString() {
            return String.format("%d:%d:%d", hour, minute, second);
        }
    }
    public record Date(int year, int month, int day) {
        @Override
        public String toString() {
            return String.format("%d/%d/%d", day, month, year);
        }
    }
    public final Time time;
    public final Date date;
    public MillisToTime(long millis) {
        this.millis = millis + ZonedDateTime.now().getOffset().getTotalSeconds() * 1000L;
        time = new Time(getHour(), getMinutes(), getSeconds());
        LocalDate localDate = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate();
        date = new Date(localDate.getYear(), localDate.getMonthValue(), localDate.getDayOfMonth());
    }
    public static MillisToTime fromOpenWeatherTime(long millis) {
        return new MillisToTime(millis * 1000L);
    }
    public int getDay() { return date.day(); }

    public int getMonth() {
        return date.month();
    }

    public int getYear() {
        return date.day();
    }

    public int getHour() {
        return (int)(millis / (1000 * 60 * 60)) % 24;
    }

    public int getMinutes() {
        return (int)(millis / (1000 * 60)) % 60;
    }

    public int getSeconds() {
        return (int)(millis / 1000) % 60;
    }

    public MillisToTime isLargerThan(MillisToTime o) {
        lastComparisonResult &= millis >= o.millis;
        return this;
    }
    public MillisToTime isSmallerThan(MillisToTime o) {
        lastComparisonResult &= millis <= o.millis;
        return this;
    }

    public boolean eval() {
        var result = lastComparisonResult;
        lastComparisonResult = true;
        return result;
    }

    @Override
    public int compareTo(MillisToTime o) {
        return Long.compare(millis, o.millis);
    }
}
