package com.example.demo.sorting;

/**
 * Interactive bubble sort demonstration.
 *
 * Bubble sort repeatedly compares neighboring values and swaps them when they
 * are out of order. After each full pass, the largest remaining value bubbles
 * to the right end. It is easy to understand but usually takes O(n^2) time.
 *
 * Pseudocode:
 *   repeat until no unsorted values remain:
 *       compare each neighboring pair in the unsorted section
 *       swap a pair when the left value is larger
 *       mark the largest value at the right as sorted
 *
 */
public class BubbleSortApplication extends SortingVisualizer {

    private int passEnd;
    private int comparisonIndex;

    public static void main(String[] args) {
        processing.core.PApplet.main(BubbleSortApplication.class.getName());
    }

    @Override
    protected String algorithmName() {
        return "Bubble Sort";
    }

    @Override
    protected void resetAlgorithm() {
        passEnd = values.length - 1;
        comparisonIndex = 0;
    }

    @Override
    protected void algorithmStep() {
        if (passEnd <= 0) {
            finish("Bubble sort complete");
            return;
        }
        if (comparisonIndex >= passEnd) {
            sortedThrough = values.length - passEnd;
            passEnd--;
            comparisonIndex = 0;
            status = "Pass complete - largest remaining value moved right";
            return;
        }
        highlightA = comparisonIndex;
        highlightB = comparisonIndex + 1;
        if (values[comparisonIndex] > values[comparisonIndex + 1]) {
            swap(comparisonIndex, comparisonIndex + 1);
            status = "Swapped neighboring values";
        } else {
            status = "Neighbors already in order";
        }
        comparisonIndex++;
    }
}
