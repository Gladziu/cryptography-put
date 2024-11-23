package pl.application;

import java.math.BigInteger;
import java.security.SecureRandom;

public class BBSGeneratorInProgress {

    public static void main(String[] args) {
        BigInteger p = new BigInteger("9725");
        BigInteger q = new BigInteger("10275");
        BigInteger n = p.multiply(q);
        BigInteger state = generateState(n);
        int numBits = 20000;

        String randomBits = generateBitString(numBits, state, n);

        System.out.println("Wygenerowany ciąg: " + randomBits);

        new BBSTests(randomBits).runAllTests();
    }

    private static BigInteger generateState(BigInteger n) {
        SecureRandom random = new SecureRandom();

        BigInteger seed;
        do {
            seed = new BigInteger(n.bitLength(), random);
        } while (seed.gcd(n).compareTo(BigInteger.ONE) != 0);

        return seed.modPow(BigInteger.TWO, n);
    }

    private static String generateBitString(int length, BigInteger state, BigInteger n) {
        StringBuilder bitString = new StringBuilder();
        for (int i = 0; i < length; i++) {
            bitString.append(nextBit(state, n));
        }
        return bitString.toString();
    }

    private static int nextBit(BigInteger state, BigInteger n) {
        state = state.modPow(BigInteger.TWO, n);

        return state.testBit(0) ? 1 : 0;
    }
}
