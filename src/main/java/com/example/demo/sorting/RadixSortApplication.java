package com.example.demo.sorting;

/**
 * Interactive radix sort demonstration.
 *
 * Radix sort orders non-negative integers one digit position at a time. It
 * starts with the ones digit, then the tens digit, and so on. Each pass must
 * be stable, meaning values with the same digit keep their previous order.
 * For a fixed number of digits, radix sort can approach O(n) time.
 *
 * Pseudocode:
 *   place values into buckets by their current digit
 *   collect the buckets in digit order
 *   move to the next digit place
 *   repeat until there are no more digits
 *
 */
public class RadixSortApplication extends SortingVisualizer {

    private int[] output;
    private int[] counts;
    private int exponent;
    private int maximum;

    public static void main(String[] args) {
        processing.core.PApplet.main(RadixSortApplication.class.getName());
    }

    @Override
    protected String algorithmName() {
        return "Radix Sort";
    }

    @Override
    protected void resetAlgorithm() {
        output = new int[values.length];
        counts = new int[10];
        exponent = 1;
        maximum = 0;
        for (int value : values) {
            maximum = Math.max(maximum, value);
        }
    }

    @Override
    protected void algorithmStep() {
        if (maximum / exponent == 0) {
            finish("Radix sort complete");
            return;
        }
        java.util.Arrays.fill(counts, 0);
        for (int value : values) {
            counts[(value / exponent) % 10]++;
        }
        for (int index = 1; index < counts.length; index++) {
            counts[index] += counts[index - 1];
        }
        for (int index = values.length - 1; index >= 0; index--) {
            int digit = (values[index] / exponent) % 10;
            output[--counts[digit]] = values[index];
        }
        System.arraycopy(output, 0, values, 0, values.length);
        status = "Stable pass completed for digit place " + exponent;
        highlightA = exponent;
        exponent *= 10;
    }
}
