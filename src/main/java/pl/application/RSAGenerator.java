package pl.application;

import java.math.BigInteger;
import java.security.SecureRandom;

public class RSAGenerator {

    private final BigInteger n;
    private final BigInteger phi;
    private final BigInteger e;
    private final BigInteger d;

    public RSAGenerator(BigInteger p, BigInteger q) {
        this.n = p.multiply(q);
        this.phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
        this.e = generateE();
        this.d = generateD();
    }

    private BigInteger generateE() {
        SecureRandom random = new SecureRandom();
        BigInteger e;
        do {
            e = new BigInteger(phi.bitLength(), random);
        } while (e.gcd(phi).compareTo(BigInteger.ONE) != 0); // gcd(e, phi) == 1
        return e;
    }

    private BigInteger generateD() {
        return e.modInverse(phi);
    }

    public static void main(String[] args) {
        BigInteger p = BigInteger.valueOf(53);
        BigInteger q = BigInteger.valueOf(59);
        RSAGenerator rsa = new RSAGenerator(p, q);

        String message = "Czesc! To jest tajna informacja i nikt niechciany nie powinien jej zobaczyc!";

        String encryptedValue = encryptMessage(rsa, message);
        String decrypteMessage = decryptMessage(rsa, encryptedValue);

        System.out.println("Infomracja jawna, oryginalna: " + message);
        System.out.println("Zaszyfrowana informacja: " + encryptedValue);
        System.out.println("Odszyfrowana informacja: " + decrypteMessage);
        System.out.println("Parametry RSA: p = " + p + ", q = " + q + ", n = " + rsa.n + ", phi = " + rsa.phi + ", e = " + rsa.e + ", d = " + rsa.d);
        if (message.equals(decrypteMessage)) {
            System.out.println("Infomracja jawna i odszyfrowana sa takie same");
        } else {
            System.out.println("Infomracja jawna i odszyfrowana sa rozne");
        }
    }

    private static String encryptMessage(RSAGenerator rsa, String message) {
        StringBuilder encryptedMessage = new StringBuilder();
        for (int i = 0; i < message.length(); i++) {
            char c = message.charAt(i);
            BigInteger charValue = BigInteger.valueOf(c);
            BigInteger encryptedChar = charValue.modPow(rsa.e, rsa.n);
            encryptedMessage.append(encryptedChar).append(" ");
        }
        return encryptedMessage.toString();
    }

    private static String decryptMessage(RSAGenerator rsa, String encryptedMessage) {
        StringBuilder message = new StringBuilder();
        String[] encryptedChars = encryptedMessage.split(" ");
        for (String encryptedChar : encryptedChars) {
            if (encryptedChar.isEmpty()) {
                continue;
            }
            BigInteger encryptedValue = new BigInteger(encryptedChar);
            BigInteger decryptedChar = encryptedValue.modPow(rsa.d, rsa.n);
            message.append((char) decryptedChar.intValue());
        }
        return message.toString();
    }
}
