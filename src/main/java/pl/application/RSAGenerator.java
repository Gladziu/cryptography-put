package pl.application;

import java.math.BigInteger;

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
        BigInteger e = BigInteger.valueOf(2);
        while (e.compareTo(phi) < 0) {
            if (e.gcd(phi).equals(BigInteger.ONE)) {
                break;
            }
            e = e.add(BigInteger.ONE);
        }
        return e;
    }

    private BigInteger generateD() {
        return e.modInverse(phi);
    }

    public static void main(String[] args) {
        BigInteger p = BigInteger.valueOf(53);
        BigInteger q = BigInteger.valueOf(59);
        RSAGenerator rsa = new RSAGenerator(p, q);

        String message = "Da";

        BigInteger encryptedValue = encryptMessage(rsa, message);
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

    private static BigInteger encryptMessage(RSAGenerator rsa, String message) {
        return new BigInteger(1, message.getBytes())
                .modPow(rsa.e, rsa.n)
                ;
    }

    private static String decryptMessage(RSAGenerator rsa, BigInteger encryptedMessage) {
        return new String(encryptedMessage
                .modPow(rsa.d, rsa.n)
                .toByteArray());
    }
}
