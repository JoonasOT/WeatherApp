package fi.tuni.prog3.weatherapp.backend.api.general;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;

/**
 * A class that gets the response of an API call and holds it.
 *
 * @author Joonas Tuominen
 */
public class Response {
    private int statusCode;
    private boolean ok = true;
    private final String data;
    private final byte[] bytes;

    /**
     * The constructor of a response takes a connection from which it reads the response and constructs rest of the class.
     * @param connection An open HttpURLConnection we want to read the output from.
     */
    public Response(HttpURLConnection connection) {
        try {
            statusCode = connection.getResponseCode();
        } catch (IOException e) {
            ok = false;
        }

        bytes = getData(connection);
        data = new String(bytes, StandardCharsets.UTF_8).replace("\n", "");
    }

    /**
     * The actual method that reads from the connection streams.
     * @param connection The connection we want to read from.
     * @return The bytes of the InputStream or ErrorStream of the connection based on if CallWasOK()?
     */
    private byte[] getData(HttpURLConnection connection) {
        try (var stream = CallWasOK() ? connection.getInputStream() : connection.getErrorStream()){
            return stream.readAllBytes();
        } catch (Exception ignored) {}
        return new byte[]{};
    }

    /**
     * Getter for all the bytes read from the open connection.
     * @return The bytes of the Input or Error stream of the connection.
     */
    public byte[] getAllBytes() {
        return bytes;
    }

    /**
     * Getter for checking if the connection was ok.
     * @return True if the connection was indeed ok.
     */
    public boolean ConnectionIsOk() {
        return ok;
    }

    /**
     * Check if the HTTPS code of the response was a success.
     * @return True if the call was a success.
     */
    public boolean CallWasOK() {
        return HTTPS_CODE.getCode(statusCode) == HTTPS_CODE.SUCCESS;
    }

    /**
     * Getter for the status code of the response.
     * @return The status code of the response.
     */
    public int getStatus() {
        return statusCode;
    }

    /**
     * The content of the response in a String form.
     * @return The String we got back from the connection.
     */
    public String getData() {
        return data;
    }
}
