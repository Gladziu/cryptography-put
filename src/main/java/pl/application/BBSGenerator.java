package pl.application;

import java.math.BigInteger;
import java.security.SecureRandom;

public class BBSGenerator {
    private final BigInteger n;
    private BigInteger state;

    public BBSGenerator(BigInteger p, BigInteger q) {
        this.n = p.multiply(q);
        this.state = generateInitialState();
    }

    private BigInteger generateInitialState() {
        SecureRandom random = new SecureRandom();
        BigInteger x;
        do {
            x = new BigInteger(n.bitLength(), random);
        } while (x.gcd(n).compareTo(BigInteger.ONE) != 0); // gcd(x, n) == 1, pierwiastek pierwotny
        return x.modPow(BigInteger.TWO, n); // x^2 mod n
    }

    public int nextBit() {
        state = state.modPow(BigInteger.TWO, n);
        return state.testBit(state.bitCount()) ? 1 : 0;
    }

    public String generateBitString(int length) {
        StringBuilder bitString = new StringBuilder();
        for (int i = 0; i < length; i++) {
            bitString.append(nextBit());
        }
        return bitString.toString();
    }

    public static void main(String[] args) {
        // p i q => mod 4 = 3
        BigInteger p = new BigInteger("10007");
        BigInteger q = new BigInteger("10039");

        int numBits = 20000;

        BBSGenerator bbs = new BBSGenerator(p, q);
        String randomBits = bbs.generateBitString(numBits);

        System.out.println("Dwie duże liczby pierwsze p i q, które są kongruentne z 3 modulo 4: " + p + ", " + q);
        System.out.println("Wygenerowany ciąg: " + randomBits);

        new BBSTests(randomBits).runAllTests();

    }
}
