package fi.tuni.prog3.weatherapp.backend.security;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import fi.tuni.prog3.weatherapp.backend.io.iReadAndWriteToFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Map;
import java.util.stream.IntStream;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * An elementary class for holding onto API keys
 *
 * @author Joonas Tuominen
 */
public class Key {
    // TODO: TPM for password generation
    private static final String passwd = "lsmdaondoapnvgäka1+r tfpa "; // I want to nuke this with TPM but doesn't work yet
    public static String SECRET_LOCATION = "secrets/";
    private String id = "";
    private String key = "";

    /**
     * A quick and dumb way of obfuscating the password further
     * @param gen The string we want to obfuscate
     * @return The obfuscated string
     */
    private static String generatePassword(String gen) {
        byte[] ba = gen.getBytes();
        assert !gen.isEmpty();
        byte x = (byte) (gen.chars().reduce((l, r) -> l ^ r).getAsInt() / (int)(Math.pow(2, Byte.SIZE) - 1));
        for (int i : IntStream.range(0, ba.length).toArray()) ba[i] ^= x;
        return new String(ba);
    }

    /**
     * The API Key constructor
     * @param file The file from where the key should be loaded from
     * @throws IOException On an invalid file location
     * @throws SecurityException If the AES decrypting fails
     */
    public Key(String file) throws IOException, SecurityException {
        String decryptedString;
        try {
            var in = new FileInputStream(file);
            decryptedString = new String(Encryption.decryptAES256(in.readAllBytes(), generatePassword(passwd)), UTF_8);
            in.close();
        } catch (RuntimeException e) {
            throw new SecurityException(e);
        }

        Gson gson = new Gson();
        Key fromJson = gson.fromJson(decryptedString, Key.class);
        id = fromJson.id;
        key = fromJson.key;
    }

    /**
     * Constructor for an empty key. Use this if your api doesn't require an API key.
     */
    public Key() {
        id = "Unknown";
        key = "N/A";
    }

    /**
     * A getter for the value of the key
     * @return The API key
     */
    public String getKey() { return key; }

    /**
     * A getter for the ID of the key
     * @return The key's ID
     */
    public String getId() { return id; }

    /**
     * A static function for reading in a decrypted key file and writing out an encrypted version of said file
     * @param keyIn The key to be encrypted (location)
     * @param keyOut The resultant key's location
     * @return True on success, false if not
     */
    public static boolean encryptKey(String keyIn, String keyOut) {
        StringBuilder content = new StringBuilder();
        String tmp;
        try (var in = new BufferedReader(new FileReader(keyIn));
            var out = new FileOutputStream(keyOut)) {
            while ( (tmp = in.readLine()) != null ) {
                content.append(tmp);
            }

            byte[] encrypted_content = Encryption.encryptAES256(content.toString()
                                                .getBytes(StandardCharsets.UTF_8),
                                                generatePassword(passwd)
            );
            out.write(encrypted_content);
        } catch(IOException e) { return false; }
        return true;
    }
}
