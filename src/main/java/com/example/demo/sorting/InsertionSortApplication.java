package com.example.demo.sorting;

/**
 * Interactive insertion sort demonstration.
 *
 * Insertion sort grows a sorted section from left to right. It takes the next
 * value and shifts larger sorted values one position right until the value can
 * be inserted. It is O(n^2) in general, but it is simple and efficient for
 * small or nearly sorted arrays.
 *
 * Pseudocode:
 *   mark the first value as sorted
 *   for each remaining value:
 *       hold the value as the key
 *       shift larger sorted values one position right
 *       insert the key into the empty position
 *
 */
public class InsertionSortApplication extends SortingVisualizer {

    private int currentIndex;
    private int insertPosition;
    private int key;
    private boolean holdingKey;

    public static void main(String[] args) {
        processing.core.PApplet.main(InsertionSortApplication.class.getName());
    }

    @Override
    protected String algorithmName() {
        return "Insertion Sort";
    }

    @Override
    protected void resetAlgorithm() {
        currentIndex = 1;
        insertPosition = 1;
        key = 0;
        holdingKey = false;
        sortedThrough = 0;
    }

    @Override
    protected void algorithmStep() {
        if (currentIndex >= values.length) {
            finish("Insertion sort complete");
            return;
        }
        if (!holdingKey) {
            key = values[currentIndex];
            insertPosition = currentIndex;
            holdingKey = true;
            highlightA = currentIndex;
            status = "Holding " + key + " and searching its sorted position";
            return;
        }
        if (insertPosition > 0 && values[insertPosition - 1] > key) {
            values[insertPosition] = values[insertPosition - 1];
            insertPosition--;
            highlightA = insertPosition;
            highlightB = insertPosition + 1;
            status = "Shifted a larger value right";
        } else {
            values[insertPosition] = key;
            sortedThrough = currentIndex;
            currentIndex++;
            holdingKey = false;
            highlightB = -1;
            status = "Inserted " + key + " into the sorted section";
        }
    }
}
