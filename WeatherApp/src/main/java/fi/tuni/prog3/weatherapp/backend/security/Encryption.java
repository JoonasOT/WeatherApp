package fi.tuni.prog3.weatherapp.backend.security;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.security.spec.KeySpec;



/**
 * A quick encryption structure mostly taken from stackoverflow results
 * <a href="https://stackoverflow.com/questions/59592205/java-encrypt-files-with-password">Link to discussion</a>
 */
public class Encryption {
    private static final int PBKDF2_ITERATION_COUNT = 300_000;
    private static final int PBKDF2_SALT_LENGTH = 16;
    private static final int AES_KEY_LENGTH = 256;
    private static final int GCM_NONCE_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;

    /**
     * Function for encrypting a byte array with some password.
     * @param input The byte array to be encrypted
     * @param password The password we want to use for encryption
     * @return The encrypted byte array
     */
    public static byte[] encryptAES256(byte[] input, String password) {
        try {
            SecureRandom secureRandom = SecureRandom.getInstanceStrong();
            // Derive the key, given password and salt
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
            // A salt is a unique, randomly generated string
            // that is added to each password as part of the hashing process
            byte[] salt = new byte[PBKDF2_SALT_LENGTH];
            secureRandom.nextBytes(salt);
            KeySpec keySpec =
                    new PBEKeySpec(password.toCharArray(), salt, PBKDF2_ITERATION_COUNT, AES_KEY_LENGTH);
            byte[] secret = factory.generateSecret(keySpec).getEncoded();
            SecretKey key = new SecretKeySpec(secret, "AES");

            // AES-GCM encryption
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            // A nonce or an initialization vector is a random value chosen at encryption time
            // and meant to be used only once
            byte[] nonce = new byte[GCM_NONCE_LENGTH];
            secureRandom.nextBytes(nonce);
            // An authentication tag
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, nonce);
            cipher.init(Cipher.ENCRYPT_MODE, key, gcmParameterSpec);
            byte[] encrypted = cipher.doFinal(input);
            // Salt and nonce can be stored together with the encrypted data
            // Both salt and nonce have fixed length, so can be prefixed to the encrypted data
            ByteBuffer byteBuffer = ByteBuffer.allocate(salt.length + nonce.length + encrypted.length);
            byteBuffer.put(salt);
            byteBuffer.put(nonce);
            byteBuffer.put(encrypted);
            return byteBuffer.array();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Function for decrypting a byte array with some password.
     * @param encrypted The byte array to be decrypt
     * @param password The password we want to use for decrypting
     * @return The decrypting byte array
     */
    public static byte[] decryptAES256(byte[] encrypted, String password) {
        try {
            // Salt and nonce have to be extracted
            ByteBuffer byteBuffer = ByteBuffer.wrap(encrypted);
            byte[] salt = new byte[PBKDF2_SALT_LENGTH];
            byteBuffer.get(salt);
            byte[] nonce = new byte[GCM_NONCE_LENGTH];
            byteBuffer.get(nonce);
            byte[] cipherBytes = new byte[byteBuffer.remaining()];
            byteBuffer.get(cipherBytes);

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
            KeySpec keySpec =
                    new PBEKeySpec(password.toCharArray(), salt, PBKDF2_ITERATION_COUNT, AES_KEY_LENGTH);
            byte[] secret = factory.generateSecret(keySpec).getEncoded();
            SecretKey key = new SecretKeySpec(secret, "AES");

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            // If encrypted data is altered, during decryption authentication tag verification will fail
            // resulting in AEADBadTagException
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, nonce);
            cipher.init(Cipher.DECRYPT_MODE, key, gcmParameterSpec);
            return cipher.doFinal(cipherBytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
