package fi.tuni.prog3.weatherapp.backend.io;

import java.io.*;
import java.util.Optional;
import java.util.zip.GZIPInputStream;

/**
 * A structure that holds static methods for writing and reading files.
 *
 * @author Joonas Tuominen
 */
public class ReadWrite {
    public static final boolean PRINT_STACK_TRACE = false;

    /**
     * Function for writing a string to a file
     * @param where The location where we want to write
     * @param what What we want to write into the file
     * @return false if write failed, true otherwise
     */
    public static boolean write(String where, String what) {
        try(var bw = new BufferedWriter(new FileWriter(where))) {
            bw.write(what);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            if (PRINT_STACK_TRACE) e.printStackTrace(System.err);
            return false;
        }
        return true;
    }

    /**
     * Function for writing bytes to a file
     * @param where The location where we want to write
     * @param what What we want to write into the file
     * @return false if write failed, true otherwise
     */
    public static boolean write(String where, byte[] what) {
        try(var fs = new FileOutputStream(where)) {
            fs.write(what);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            if (PRINT_STACK_TRACE) e.printStackTrace(System.err);
            return false;
        }
        return true;
    }

    /**
     * Function for reading a string from a file
     * @param where The file from where we want to read from
     * @return An optional with the data if successful, otherwise an empty optional
     */
    public static Optional<String> read(String where) {
        StringBuilder content = new StringBuilder();
        try (var bf = new BufferedReader(new FileReader(where))) {
            String line;
            while ( (line = bf.readLine()) != null ) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            if (PRINT_STACK_TRACE) e.printStackTrace(System.err);
            return Optional.empty();
        }
        return Optional.of(content.substring(0, content.length() - 1));
    }

    /**
     * Function for reading a string from a GZ compressed file
     * @param where The file from where we want to read from
     * @return An optional with the data if successful, otherwise an empty optional
     */
    public static Optional<String> readGZ(String where) {
        StringBuilder content = new StringBuilder();
        try (var bf = new BufferedReader(
                      new InputStreamReader(
                      new GZIPInputStream(
                      new FileInputStream(where))))) {
                    /* THE STACK OF STREAMS :DD */

            String line;
            while ( (line = bf.readLine()) != null ) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            if (PRINT_STACK_TRACE) e.printStackTrace(System.err);
            return Optional.empty();
        }
        return Optional.of(content.toString());
    }
}
