package com.example.demo.sorting;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Interactive quick sort demonstration.
 *
 * Quick sort chooses a pivot and partitions the array so smaller values move
 * before it and larger values move after it. It then repeats on both sides of
 * the pivot. Its average running time is O(n log n), although a poor pivot can
 * make the worst case O(n^2).
 *
 * Pseudocode:
 *   choose a pivot from the current range
 *   move values smaller than the pivot to its left
 *   move values larger than the pivot to its right
 *   repeat the process for the left and right ranges
 *
 */
public class QuickSortApplication extends SortingVisualizer {

    private final Deque<Range> ranges = new ArrayDeque<>();

    public static void main(String[] args) {
        processing.core.PApplet.main(QuickSortApplication.class.getName());
    }

    @Override
    protected String algorithmName() {
        return "Quick Sort";
    }

    @Override
    protected void resetAlgorithm() {
        ranges.clear();
        ranges.push(new Range(0, values.length - 1));
    }

    @Override
    protected void algorithmStep() {
        if (ranges.isEmpty()) {
            finish("Quick sort complete");
            return;
        }
        Range range = ranges.pop();
        if (range.low >= range.high) {
            status = "Range " + range.low + ".." + range.high + " needs no partition";
            return;
        }

        int pivotIndex = partition(range.low, range.high);
        highlightA = pivotIndex;
        highlightB = range.low;
        if (pivotIndex - 1 > range.low) {
            ranges.push(new Range(range.low, pivotIndex - 1));
        }
        if (pivotIndex + 1 < range.high) {
            ranges.push(new Range(pivotIndex + 1, range.high));
        }
        status = "Placed pivot " + values[pivotIndex] + " at index " + pivotIndex;
    }

    private int partition(int low, int high) {
        int pivot = values[high];
        int storeIndex = low;
        for (int index = low; index < high; index++) {
            if (values[index] <= pivot) {
                swap(storeIndex, index);
                storeIndex++;
            }
        }
        swap(storeIndex, high);
        return storeIndex;
    }

    private static final class Range {
        private final int low;
        private final int high;

        private Range(int low, int high) {
            this.low = low;
            this.high = high;
        }
    }
}
