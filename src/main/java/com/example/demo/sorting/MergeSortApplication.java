package com.example.demo.sorting;

/**
 * Interactive merge sort demonstration.
 *
 * Merge sort divides the array into smaller pieces, sorts those pieces, then
 * merges neighboring sorted pieces. The merge step repeatedly chooses the
 * smaller front value from the two pieces. Its running time is O(n log n), and
 * it keeps the same predictable performance even when the data is reversed.
 *
 * Pseudocode:
 *   split the array into blocks of size 1
 *   while the blocks do not cover the whole array:
 *       merge each neighboring pair of sorted blocks
 *       double the block size
 *
 */
public class MergeSortApplication extends SortingVisualizer {

    private int[] temporary;
    private int blockSize;
    private int leftStart;

    public static void main(String[] args) {
        processing.core.PApplet.main(MergeSortApplication.class.getName());
    }

    @Override
    protected String algorithmName() {
        return "Merge Sort";
    }

    @Override
    protected void resetAlgorithm() {
        temporary = new int[values.length];
        blockSize = 1;
        leftStart = 0;
    }

    @Override
    protected void algorithmStep() {
        if (blockSize >= values.length) {
            finish("Merge sort complete");
            return;
        }
        if (leftStart >= values.length) {
            blockSize *= 2;
            leftStart = 0;
            status = "Moving to sorted blocks of size " + blockSize;
            return;
        }

        int middle = Math.min(leftStart + blockSize, values.length);
        int rightEnd = Math.min(leftStart + blockSize * 2, values.length);
        int first = leftStart;
        int second = middle;
        int output = leftStart;
        while (first < middle && second < rightEnd) {
            temporary[output++] = values[first] <= values[second]
                    ? values[first++] : values[second++];
        }
        while (first < middle) {
            temporary[output++] = values[first++];
        }
        while (second < rightEnd) {
            temporary[output++] = values[second++];
        }
        for (int index = leftStart; index < rightEnd; index++) {
            values[index] = temporary[index];
        }
        highlightA = leftStart;
        highlightB = rightEnd - 1;
        status = "Merged values " + leftStart + ".." + (rightEnd - 1)
                + " from blocks of size " + blockSize;
        leftStart += blockSize * 2;
    }
}
