package pl.application;

import java.util.HashMap;
import java.util.Map;

public class BBSTests {

    private String bitString;

    public BBSTests(String bitString) {
        this.bitString = bitString;
    }

    private int countSingleBit() {
        int numOnes = 0;
        for (int i = 0; i < bitString.length(); i++) {
            if (bitString.charAt(i) == '1') {
                numOnes++;
            }
        }
        return numOnes;
    }

    private Map<Integer, Integer> countSeriesOfBits() {
        Map<Integer, Integer> series = new HashMap<>();
        int sameBitsLength = 0;
        for (int i = 1; i < bitString.length(); i++) {
            if (bitString.charAt(i) == bitString.charAt(i - 1)) {
                sameBitsLength++;
            } else {
                if (sameBitsLength > 0) {
                    if (sameBitsLength >= 6) {
                        sameBitsLength = 6;
                    }
                    series.put(sameBitsLength, series.getOrDefault(sameBitsLength, 0) + 1);
                }
                sameBitsLength = 0;
            }
        }
        return series;
    }

    private boolean areLongSeries() {
        int sameBitsLength = 1;
        for (int i = 1; i < bitString.length(); i++) {
            if (bitString.charAt(i) == bitString.charAt(i - 1)) {
                sameBitsLength++;
            } else {
                if (sameBitsLength >= 26) {
                    return true;
                }
                sameBitsLength = 1;
            }
        }
        return false;
    }

    private double runPokerTest() {
        Map<String, Integer> pokerTest = new HashMap<>();
        for (int i = 0; i < bitString.length(); i += 4) {
            String bits = bitString.substring(i, i + 4);
            pokerTest.put(bits, pokerTest.getOrDefault(bits, 0) + 1);
        }
        int sumSqrt = 0;
        for (int count : pokerTest.values()) {
            sumSqrt += count * count;
        }
        return (16.0 / 5000.0) * sumSqrt - 5000.0;
    }

    public void runAllTests() {
        System.out.println("");

        float singleBit = countSingleBit();
        String singleBitResult = singleBit > 9725 && singleBit < 10275 ? "zaliczony" : "niezaliczony";
        System.out.println("Test pojedynczych bitów: " + singleBitResult + " [" + singleBit + "]" + " Powinno byc: 9725 < n(1) < 10275");

        Map<Integer, Integer> series = countSeriesOfBits();
        System.out.println("Test serii: ");
        for (int i = 1; i <= 6; i++) {
            System.out.println("Dlugosc serii " + i + ": " + series.get(i));
        }

        String longSeriesResult = areLongSeries() ? "niezaliczony" : "zaliczony";
        System.out.println("Test długiej serii: " + longSeriesResult);

        double pokerTestResult = runPokerTest();
        String pokerTestResultMessage = pokerTestResult > 2.16 && pokerTestResult < 46.17 ? "zaliczony" : "niezaliczony";
        System.out.println("Test pokerowy: " + pokerTestResultMessage + " [x=" + pokerTestResult + "] Powinno być: 2.16 < x < 46.17");
    }

}
