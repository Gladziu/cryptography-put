package pl.application;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

public class BlockCipherAnalysis {


/*
    ECB (Electronic Codebook): Każdy blok jest szyfrowany niezależnie, więc błąd w jednym bloku nie wpływa na inne.
    CBC (Cipher Block Chaining): Każdy blok zależy od poprzedniego, więc błąd w jednym bloku może wpłynąć na deszyfrowanie kolejnego.
    OFB (Output Feedback) i CTR (Counter): Są trybami strumieniowymi, gdzie błąd wpływa zazwyczaj tylko na jeden blok (jedną część strumienia), ale nie na kolejne.
    CFB (Cipher Feedback): W tym trybie błąd może wpływać na kilka bloków, ale nie zawsze na wszystkie.

    Interpretacja bledów:
    ECB: Błąd wpływa tylko na jeden blok, ale tryb ma niską odporność na ataki.
    CBC: Błąd propaguje się na dwa bloki, jest bezpieczniejszy niż ECB, ale podatny na dwublokową propagację błędu.
    CFB: Błąd wpływa na kilka kolejnych bloków, ale efekt zanika po pewnym czasie.
    OFB i CTR: Błąd wpływa tylko na jeden blok, zapewniają wysoką odporność na błędy i bezpieczeństwo szyfrowania.
*/

    private static final int[] FILE_SIZES = {1024 * 1024, 5 * 1024 * 1024, 10 * 1024 * 1024};
    private static final String ALGORITHM = "AES";
    private static final String[] MODES = {"ECB", "CBC", "OFB", "CFB", "CTR"};
    private static final String SLASH_SIGN = "/";


    public static void main(String[] args) throws Exception {
        SecretKey key = generateKey();
        System.out.println("Pomiar czasu szyfrowania i deszyfrowania dla ECB, CBC, OFB, CFB, CTR:");
        for (int size : FILE_SIZES) {
            byte[] data = generateRandomData(size);
            System.out.println("Rozmiar danych: " + size / 1024 / 1024 + " MB");
            for (String mode : MODES) {
                System.out.print("Tryb: " + mode + " | ");
                analyzeCipherMode(mode, key, data);
            }
        }

        analyzeErrorPropagation(key);

        implementCBCWithECB(key);
    }

    private static void analyzeCipherMode(String mode, SecretKey key, byte[] data) throws Exception {
        Cipher cipher = getCipher(mode);
        IvParameterSpec ivSpec = getIvSpec(mode, cipher);

        // Szyfrowanie
        initializeEncryption(cipher, key, ivSpec);
        long startEncrypt = System.currentTimeMillis();
        byte[] encryptedData = cipher.doFinal(data);
        long encryptTime = System.currentTimeMillis() - startEncrypt;

        // Deszyfrowanie
        initializeDecription(cipher, key, ivSpec);
        long startDecrypt = System.currentTimeMillis();
        cipher.doFinal(encryptedData);
        long decryptTime = System.currentTimeMillis() - startDecrypt;

        System.out.print("Czas szyfrowania: " + encryptTime + " ms  | ");
        System.out.print("Czas deszyfroawania: " + decryptTime + " ms  | ");
        System.out.println("Suma: " + (decryptTime + encryptTime) + " ms");
    }

    private static IvParameterSpec getIvSpec(String mode, Cipher cipher) {
        return mode.equals("ECB") ? null : new IvParameterSpec(generateIv(cipher.getBlockSize()));
    }

    private static void analyzeErrorPropagation(SecretKey key) throws Exception {
        byte[] data = generateRandomData(64);
        System.out.println("\nAnaliza propagacji błędów:");
        System.out.println("Dane przed zaszyfrowaniem: " + Arrays.toString(data));
        for (String mode : MODES) {

            Cipher cipher = getCipher(mode);
            IvParameterSpec ivSpec = getIvSpec(mode, cipher);

            initializeEncryption(cipher, key, ivSpec);
            byte[] encryptedData = cipher.doFinal(data);

            // Wprowadzenie błędu w szyfrogramie - negacja 10 bitu
            encryptedData[10] = (byte) ~encryptedData[10];

            try {
                initializeDecription(cipher, key, ivSpec);
                byte[] decryptedData = cipher.doFinal(encryptedData);
                System.out.println("Tryb: " + mode + ". Dane po odszyfrowaniu: " + Arrays.toString(decryptedData));
            } catch (Exception e) {
                System.out.println("Błąd deszyfrowania: " + e.getMessage());
            }
        }
    }

    private static Cipher getCipher(String mode) throws NoSuchAlgorithmException, NoSuchPaddingException {
        String padding = mode.equals("CTR") ? "NoPadding" : "PKCS5Padding";
        Cipher cipher = Cipher.getInstance(ALGORITHM + SLASH_SIGN + mode + SLASH_SIGN + padding);
        return cipher;
    }

    private static Cipher initializeEncryption(Cipher cipher, SecretKey key, IvParameterSpec ivSpec) throws InvalidAlgorithmParameterException, InvalidKeyException {
        if (ivSpec != null) {
            cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
        } else {
            cipher.init(Cipher.ENCRYPT_MODE, key);
        }
        return cipher;
    }

    private static Cipher initializeDecription(Cipher cipher, SecretKey key, IvParameterSpec ivSpec) throws InvalidAlgorithmParameterException, InvalidKeyException {
        if (ivSpec != null) {
            cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
        } else {
            cipher.init(Cipher.DECRYPT_MODE, key);
        }
        return cipher;
    }

    private static SecretKey generateKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
        keyGen.init(128);
        return keyGen.generateKey();
    }

    private static byte[] generateIv(int size) {
        byte[] iv = new byte[size];
        new SecureRandom().nextBytes(iv);
        return iv;
    }

    private static byte[] generateRandomData(int size) {
        byte[] data = new byte[size];
        new SecureRandom().nextBytes(data);
        return data;
    }

    private static void implementCBCWithECB(SecretKey key) throws Exception {
        byte[] data = generateRandomData(64);
        byte[] iv = generateIv(16); // 16 bajtów (128 bitów)
        Cipher ecbCipher = Cipher.getInstance(ALGORITHM + "/ECB/NoPadding");

        byte[] customCBCEncryptResult = customCBCEncrypt(ecbCipher, data, key, iv);
        byte[] bytes = customCBCDecrypt(ecbCipher, customCBCEncryptResult, key, iv);
        System.out.println("\nImplementacja CBC za pomocą ECB");
        System.out.println("Dane przed zaszyfrowaniem: " + Arrays.toString(data));
        System.out.println("Dane po zaszyfrowaniu: " + Arrays.toString(customCBCEncryptResult));
        System.out.println("Dane po odszyfrowaniu: " + Arrays.toString(bytes));
    }

    // Implementacja trybu CBC za pomocą ECB
    public static byte[] customCBCEncrypt(Cipher ecbCipher, byte[] data, SecretKey key, byte[] iv) throws Exception {
        ecbCipher.init(Cipher.ENCRYPT_MODE, key);

        byte[] block = new byte[16];
        byte[] encryptedData = new byte[64];

        for (int i = 0; i < data.length; i += 16) {
            System.arraycopy(data, i, block, 0, 16);
            xorBlock(block, iv);
            byte[] encryptedBlock = ecbCipher.doFinal(block);
            System.arraycopy(encryptedBlock, 0, encryptedData, i, 16);
            iv = encryptedBlock;
        }
        return encryptedData;
    }

    public static byte[] customCBCDecrypt(Cipher ecbCipher, byte[] encryptedData, SecretKey key, byte[] iv) throws Exception {
        ecbCipher.init(Cipher.DECRYPT_MODE, key);

        byte[] block = new byte[16];
        byte[] decryptedData = new byte[encryptedData.length];

        for (int i = 0; i < encryptedData.length; i += 16) {
            System.arraycopy(encryptedData, i, block, 0, 16);
            byte[] decryptedBlock = ecbCipher.doFinal(block);
            xorBlock(decryptedBlock, iv);
            System.arraycopy(decryptedBlock, 0, decryptedData, i, 16);
            iv = block;
        }
        return decryptedData;
    }

    private static void xorBlock(byte[] block, byte[] iv) {
        for (int i = 0; i < block.length; i++) {
            block[i] ^= iv[i];
        }
    }
}
