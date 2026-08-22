package com.example.demo.sorting;

/**
 * Interactive selection sort demonstration.
 *
 * Selection sort divides the array into sorted and unsorted sections. For each
 * position, it scans the remaining values to find the smallest one, then swaps
 * it into place. It uses very little extra memory, but always takes O(n^2)
 * comparisons because it scans the remaining section each time.
 *
 * Pseudocode:
 *   for each position from left to right:
 *       find the smallest value in the unsorted section
 *       swap it into the current position
 *       mark the position as sorted
 *
 */
public class SelectionSortApplication extends SortingVisualizer {

    private int sortedIndex;
    private int scanIndex;
    private int smallestIndex;

    public static void main(String[] args) {
        processing.core.PApplet.main(SelectionSortApplication.class.getName());
    }

    @Override
    protected String algorithmName() {
        return "Selection Sort";
    }

    @Override
    protected void resetAlgorithm() {
        sortedIndex = 0;
        scanIndex = 1;
        smallestIndex = 0;
    }

    @Override
    protected void algorithmStep() {
        if (sortedIndex >= values.length - 1) {
            finish("Selection sort complete");
            return;
        }
        if (scanIndex >= values.length) {
            swap(sortedIndex, smallestIndex);
            sortedThrough = sortedIndex;
            highlightA = sortedIndex;
            highlightB = smallestIndex;
            status = "Placed the smallest remaining value at index " + sortedIndex;
            sortedIndex++;
            scanIndex = sortedIndex + 1;
            smallestIndex = sortedIndex;
            return;
        }
        highlightA = smallestIndex;
        highlightB = scanIndex;
        if (values[scanIndex] < values[smallestIndex]) {
            smallestIndex = scanIndex;
            status = "New smallest value found at index " + scanIndex;
        } else {
            status = "Compared index " + scanIndex + " with the current minimum";
        }
        scanIndex++;
    }
}
