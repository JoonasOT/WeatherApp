package fi.tuni.prog3.keygen;

import fi.tuni.prog3.weatherapp.backend.security.Key;

/**
 * This is a quick function for saving creating encrypted API keys.
 * Run this as main with the args: "key_json_file.json" "encrypted_key_target_file"
 *
 * @author Joonas Tuominen
 */
public class KeyGen {
    /**
     * Function for encrypting Api keys.
     * @param args First the location of the file ready to be encrypted and then the location for the encrypted key file.
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Please provide two filepaths!\n" +
                                "First must be a path to an existing json file containing an API key you want to store" +
                                " in the format:\n" +
                                "{\n" +
                                "    id:\"<your key id>\"\n" +
                                "    key:\"<your api key>\"\n" +
                                "}\n\n" +

                                "The second filepath is the file location you want to store this encrypted version of" +
                                " your key.");
            return;
        }
        Key.encryptKey(args[0], args[1]);
    }
}
