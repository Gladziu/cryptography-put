package pl.application;

import java.math.BigInteger;
import java.security.SecureRandom;

public class DHGenerator {

    public static void main(String[] args) {
        SecureRandom random = new SecureRandom();

        // 1. Uzgodnienie n (liczba pierwsza) i g (pierwiastek pierwotny modulo n)
        BigInteger n = new BigInteger("23"); // Przykładowa liczba pierwsza
        BigInteger g = new BigInteger("5");  // Przykładowy pierwiastek pierwotny

        System.out.println("Uzgodnione n: " + n + ", g: " + g);

        // 2. A wybiera klucz prywatny x i oblicza X = g^x mod n
        BigInteger x = new BigInteger(n.bitLength() - 1, random);
        BigInteger X = g.modPow(x, n);
        System.out.println("A: Klucz prywatny x: " + x);
        System.out.println("A: Klucz publiczny X: " + X);

        // 3. B wybiera klucz prywatny y i oblicza Y = g^y mod n
        BigInteger y = new BigInteger(n.bitLength() - 1, random);
        BigInteger Y = g.modPow(y, n);
        System.out.println("B: Klucz prywatny y: " + y);
        System.out.println("B: Klucz publiczny Y: " + Y);

        // 4. A i B wymieniają klucze publiczne (X i Y)

        // 5. A oblicza wspólny klucz k = Y^x mod n
        BigInteger kA = Y.modPow(x, n);
        System.out.println("A: Wspólny klucz k: " + kA);

        // 6. B oblicza wspólny klucz k = X^y mod n
        BigInteger kB = X.modPow(y, n);
        System.out.println("B: Wspólny klucz k: " + kB);

        // 7. Sprawdzenie, czy klucze są identyczne
        if (kA.equals(kB)) {
            System.out.println("Wymiana klucza zakończona sukcesem. Wspólny klucz: " + kA);
        } else {
            System.out.println("Błąd w wymianie klucza.");
        }
    }
}
