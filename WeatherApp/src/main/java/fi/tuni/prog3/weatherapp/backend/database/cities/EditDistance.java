package fi.tuni.prog3.weatherapp.backend.database.cities;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.IntStream;

/**
 * A structure that holds multiple functions for calculating edit distances with a singular thread and in parallel
 *
 * @author Joonas Tuominen
 */
public class EditDistance {
    /**
     * Compares to characters ignoring the case
     * @param a The first character to be compared
     * @param b The other character to be compared
     * @return 0 if a == b, negative is a < b and positive if a > b
     */
    public static int compareCharIgnoreCase(char a, char b) {
        return Character.compare(Character.toUpperCase(a), Character.toUpperCase(b));
    }

    /**
     * Function for calculating the edit distance between two different words
     * @param want The word we want
     * @param attempt Out attempt of spelling the wanted word
     * @return The number of operations needed to get from attempt to want
     */
    public static int calculateEditDistance(String want, String attempt) {
        if (Math.min(want.length(), attempt.length()) == 0) {
            return Math.max(want.length(), attempt.length());
        }

        int[][] matrix = new int[want.length() + 1][attempt.length() + 1];

        // IntStream hell :DDD
        // Using IntStreams cuz intellij underlines each variable that is reassigned and that was really annoying so...
        for (int i : IntStream.range(1, want.length() + 1).toArray()) matrix[i][0] = i;
        for (int i : IntStream.range(1, attempt.length() + 1).toArray()) matrix[0][i] = i;
        matrix[0][0] = 0;

        // At the end of the day a super simple algorithm. Can be found on the internet pretty quick on a few tries
        for (int x : IntStream.range(0, want.length()).toArray()) {
            for (int y : IntStream.range(0, attempt.length()).toArray()) {
                int min = IntStream.of(matrix[x][y], matrix[x + 1][y], matrix[x][y + 1]).min().getAsInt();
                matrix[x + 1][y + 1] = compareCharIgnoreCase(want.charAt(x), attempt.charAt(y)) == 0 ? min : min + 1;
            }
        }
        return matrix[want.length()][attempt.length()];
    }

    /**
     * Function for calculating the edit distance of an attempt to an array of words
     * @param words The correct spellings
     * @param attempt The attempted spelling
     * @return The distances between the attempt and each word
     */
    public static int[] calculateDistances(String[] words, String attempt) {
        return Arrays.stream(words).mapToInt(w -> calculateEditDistance(w, attempt)).toArray();
    }

    /**
     * A record used for running the calculateEditDistance function in parallel
     * @param want The word we want
     * @param attempt The attempt in this task
     */
    private record Task(String want, String attempt) implements Callable<Integer> {
        @Override
        public Integer call() {
            return calculateEditDistance(want, attempt);
        }
    }

    /**
     * A function that calculates the edit distances of an attempt to a bunch of words
     * @param words The valid words
     * @param attempt The attempt of spelling some word
     * @return The edit distances between all these words and the attempt
     */
    public static int[] calculateDistancesParallel(String[] words, String attempt) {
        List<Task> tasks = Arrays.stream(words).map(word -> new Task(word, attempt)).toList();
        List<Future<Integer>> futures;
        try {
            ExecutorService executor = Executors.newCachedThreadPool();
            futures = executor.invokeAll(tasks);
            var result =  futures.stream().mapToInt(f -> {
                try {
                    return f.get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }).toArray();
            executor.shutdown();
            return result;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
