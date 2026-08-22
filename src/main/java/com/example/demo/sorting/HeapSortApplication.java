package com.example.demo.sorting;

/**
 * Interactive heap sort demonstration.
 *
 * Heap sort first arranges the array as a max heap, where the largest value is
 * at the root. It swaps that root with the last unsorted position, shrinks the
 * heap, and repairs the heap. It always takes O(n log n) time and sorts in
 * place, using only a small amount of extra memory.
 *
 * Pseudocode:
 *   build a max heap from the array
 *   while the heap has more than one value:
 *       swap the largest root with the last heap value
 *       shrink the heap
 *       restore the max-heap property
 *
 */
public class HeapSortApplication extends SortingVisualizer {

    private int buildIndex;
    private int heapSize;

    public static void main(String[] args) {
        processing.core.PApplet.main(HeapSortApplication.class.getName());
    }

    @Override
    protected String algorithmName() {
        return "Heap Sort";
    }

    @Override
    protected void resetAlgorithm() {
        buildIndex = values.length / 2 - 1;
        heapSize = values.length;
    }

    @Override
    protected void algorithmStep() {
        if (buildIndex >= 0) {
            heapify(buildIndex);
            highlightA = buildIndex;
            status = "Repairing heap below index " + buildIndex;
            buildIndex--;
            return;
        }
        if (heapSize <= 1) {
            finish("Heap sort complete");
            return;
        }
        swap(0, heapSize - 1);
        highlightA = 0;
        highlightB = heapSize - 1;
        sortedThrough = values.length - heapSize;
        status = "Moved largest heap value to index " + (heapSize - 1);
        heapSize--;
        heapify(0);
    }

    private void heapify(int root) {
        int largest = root;
        int left = root * 2 + 1;
        int right = root * 2 + 2;
        if (left < heapSize && values[left] > values[largest]) {
            largest = left;
        }
        if (right < heapSize && values[right] > values[largest]) {
            largest = right;
        }
        if (largest != root) {
            swap(root, largest);
            heapify(largest);
        }
    }
}
