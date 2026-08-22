package com.example.demo.sorting;

/**
 * Interactive bogo sort demonstration.
 *
 * Bogo sort checks whether the values are sorted. If they are not, it shuffles
 * the array and tries again. Its expected running time is O(n * n!), making it
 * a deliberately impractical example that is useful for showing why algorithm
 * choice matters. This demo limits the active section to eight values so it
 * can eventually finish while the rest of the array provides visual context.
 *
 * Pseudocode:
 *   while the active values are not sorted:
 *       shuffle the active values randomly
 *   report success
 *
 */
public class BogoSortApplication extends SortingVisualizer {

    private static final int ACTIVE_LENGTH = 8;
    private long attempts;

    public static void main(String[] args) {
        processing.core.PApplet.main(BogoSortApplication.class.getName());
    }

    @Override
    protected String algorithmName() {
        return "Bogo Sort";
    }

    @Override
    protected void resetAlgorithm() {
        attempts = 0;
        sortedThrough = ACTIVE_LENGTH - 1;
    }

    @Override
    protected void algorithmStep() {
        attempts++;
        if (isSorted()) {
            running = false;
            finished = true;
            highlightA = -1;
            highlightB = -1;
            status = "Bogo sort finished after " + attempts + " attempt(s)";
            return;
        }
        for (int index = ACTIVE_LENGTH - 1; index > 0; index--) {
            int swapIndex = random.nextInt(index + 1);
            swap(index, swapIndex);
        }
        highlightA = 0;
        highlightB = ACTIVE_LENGTH - 1;
        status = "Attempt " + attempts + ": shuffled the first " + ACTIVE_LENGTH + " values";
    }

    private boolean isSorted() {
        for (int index = 1; index < ACTIVE_LENGTH; index++) {
            if (values[index - 1] > values[index]) {
                return false;
            }
        }
        return true;
    }
}
