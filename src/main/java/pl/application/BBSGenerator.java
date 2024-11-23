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
        BigInteger seed;
        do {
            seed = new BigInteger(n.bitLength(), random);
        } while (seed.gcd(n).compareTo(BigInteger.ONE) != 0); // gcd(seed, n) == 1
        return seed.modPow(BigInteger.TWO, n); // seed^2 mod n
    }

    public int nextBit() {
        state = state.modPow(BigInteger.TWO, n);
        return state.testBit(0) ? 1 : 0;
    }

    public String generateBitString(int length) {
        StringBuilder bitString = new StringBuilder();
        for (int i = 0; i < length; i++) {
            bitString.append(nextBit());
        }
        return bitString.toString();
    }

    public static void main(String[] args) {
        BigInteger p = new BigInteger("9725");
        BigInteger q = new BigInteger("10275");

        int numBits = 20000;

        BBSGenerator bbs = new BBSGenerator(p, q);
        String randomBits = bbs.generateBitString(numBits);

        System.out.println("Wygenerowany ciąg: " + randomBits);

        new BBSTests(randomBits).runAllTests();

    }
}
