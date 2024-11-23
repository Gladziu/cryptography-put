package pl.application;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.security.*;
import java.util.Arrays;

public class CBCWithECBAndPKCS5 {
    public static void main(String[] args) throws Exception {
        // Przykładowy tekst do zaszyfrowania
        byte[] plaintext = "This is a longer text to test CBC encryption with AES!".getBytes();

        byte[] keyBytes = generateKey(); // Generowanie klucza
        byte[] iv = generateIV(); // Losowy wektor inicjalizujący

        System.out.println("Plaintext: " + new String(plaintext));

        // Szyfrowanie w trybie CBC przy użyciu ECB
        byte[] ciphertext = encryptCBC(plaintext, keyBytes, iv);
        System.out.println("Ciphertext: " + Arrays.toString(ciphertext));

        // Deszyfrowanie w trybie CBC przy użyciu ECB
        byte[] decrypted = decryptCBC(ciphertext, keyBytes, iv);
        System.out.println("Decrypted: " + new String(decrypted));
    }

    // Szyfrowanie CBC przy użyciu ECB z PKCS5Padding
    public static byte[] encryptCBC(byte[] plaintext, byte[] key, byte[] iv) throws Exception {
        Cipher ecbCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        SecretKey secretKey = new SecretKeySpec(key, "AES");
        ecbCipher.init(Cipher.ENCRYPT_MODE, secretKey);

        // Długość plaintextu jest obsługiwana automatycznie przez PKCS5Padding
        int blockSize = ecbCipher.getBlockSize();
        byte[] ciphertext = new byte[(int) Math.ceil(plaintext.length / (double) blockSize) * blockSize];
        byte[] previousBlock = iv;

        for (int i = 0; i < plaintext.length; i += blockSize) {
            // Przycinanie bloku (w przypadku ostatniego bloku automatyczne wypełnienie przez PKCS5)
            byte[] block = Arrays.copyOfRange(plaintext, i, Math.min(i + blockSize, plaintext.length));

            // XOR z poprzednim szyfrogramem lub IV
            block = xor(block, previousBlock);

            // Szyfrowanie bloku w trybie ECB
            byte[] encryptedBlock = ecbCipher.doFinal(block);

            // Zapisanie szyfrogramu
            System.arraycopy(encryptedBlock, 0, ciphertext, i, blockSize);

            // Ustawienie poprzedniego bloku
            previousBlock = encryptedBlock;
        }

        return ciphertext;
    }

    // Deszyfrowanie CBC przy użyciu ECB z PKCS5Padding
    public static byte[] decryptCBC(byte[] ciphertext, byte[] key, byte[] iv) throws Exception {
        Cipher ecbCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        SecretKey secretKey = new SecretKeySpec(key, "AES");
        ecbCipher.init(Cipher.DECRYPT_MODE, secretKey);

        int blockSize = ecbCipher.getBlockSize();
        byte[] plaintext = new byte[ciphertext.length];
        byte[] previousBlock = iv;

        for (int i = 0; i < ciphertext.length; i += blockSize) {
            byte[] block = Arrays.copyOfRange(ciphertext, i, i + blockSize);

            // Odszyfrowanie bloku w trybie ECB
            byte[] decryptedBlock = ecbCipher.doFinal(block);

            // XOR z poprzednim szyfrogramem lub IV
            decryptedBlock = xor(decryptedBlock, previousBlock);

            // Zapisanie odszyfrowanego bloku
            System.arraycopy(decryptedBlock, 0, plaintext, i, decryptedBlock.length);

            // Ustawienie poprzedniego bloku
            previousBlock = block;
        }

        // Padding PKCS5 jest automatycznie usuwany przez Cipher
        return plaintext;
    }

    // Funkcja generująca losowy klucz AES (128-bitowy)
    public static byte[] generateKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128); // Klucz 128-bitowy
        return keyGen.generateKey().getEncoded();
    }

    // Funkcja generująca losowy IV (dla AES to 16 bajtów)
    public static byte[] generateIV() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return iv;
    }

    // XOR dwóch bloków danych
    public static byte[] xor(byte[] a, byte[] b) {
        byte[] result = new byte[a.length];
        for (int i = 0; i < a.length; i++) {
            result[i] = (byte) (a[i] ^ b[i]);
        }
        return result;
    }
}
