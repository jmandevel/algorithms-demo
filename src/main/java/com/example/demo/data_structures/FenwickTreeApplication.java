package com.example.demo.data_structures;

import processing.core.PApplet;

/**
 * Interactive visual demonstration of a Fenwick tree, also called a Binary
 * Indexed Tree.
 *
 * A Fenwick tree stores partial sums so that two important operations are
 * fast: adding a value at one position and finding the sum from the first
 * position through a chosen position. Each tree entry covers a range whose
 * length is determined by its lowest set bit. For example, entry 8 covers
 * eight values, while entry 10 covers two values.
 *
 * To update position i, the algorithm adds the change to tree[i], then jumps
 * to i + (i & -i), the next entry that also includes position i. To query a
 * prefix through position i, it adds tree[i] to the answer, then jumps back
 * to i - (i & -i). These jumps make both operations O(log n) instead of
 * scanning the whole array. The tree values shown here are the stored partial
 * sums, not copies of the original array values.
 *
 * Controls:
 *   Mouse - select an array position
 *   LEFT/RIGHT - move the selected position
 *   U     - begin a point update (+5)
 *   Q     - begin a prefix-sum query
 *   SPACE - advance one operation step
 *   A     - toggle automatic stepping
 *   R     - reset the array and tree
 *   UP/DOWN - change automatic step speed
 */
public class FenwickTreeApplication extends PApplet {

    private static final int SIZE = 12;
    private static final int CELL_SIZE = 58;
    private static final int ARRAY_LEFT = 94;
    private static final int ARRAY_TOP = 175;
    private static final int TREE_TOP = 355;
    private static final int TOP_BAR_HEIGHT = 74;

    private final int[] values = {4, 2, 7, 1, 5, 3, 6, 2, 8, 1, 4, 3};
    private int[] tree;
    private int selectedIndex = 6;
    private int operationIndex;
    private int operationResult;
    private int updateAmount = 5;
    private boolean running;
    private boolean finished;
    private String operation = "idle";
    private String status = "Select a position, then press U or Q";
    private int stepDelay = 18;
    private int frameCounter;

    public static void main(String[] args) {
        PApplet.main(FenwickTreeApplication.class.getName());
    }

    @Override
    public void settings() {
        size(900, 650);
    }

    @Override
    public void setup() {
        surface.setTitle("Fenwick Tree Sandbox");
        textFont(createFont("SansSerif", 16));
        resetTree();
    }

    @Override
    public void draw() {
        background(21, 25, 34);
        drawHeader();
        drawArray();
        drawTree();

        if (running && frameCounter++ % stepDelay == 0) {
            advance();
        }
    }

    @Override
    public void keyPressed() {
        if (key == ' ' && !running && !finished) {
            advance();
        } else if (key == 'a' || key == 'A') {
            if (!"idle".equals(operation) && !finished) {
                running = !running;
                status = running ? "Running automatically" : "Paused - press SPACE to continue";
            }
        } else if (key == 'u' || key == 'U') {
            beginUpdate();
        } else if (key == 'q' || key == 'Q') {
            beginQuery();
        } else if (key == 'r' || key == 'R') {
            resetTree();
        } else if (keyCode == LEFT && !running) {
            selectedIndex = max(1, selectedIndex - 1);
        } else if (keyCode == RIGHT && !running) {
            selectedIndex = min(SIZE, selectedIndex + 1);
        } else if (keyCode == UP) {
            stepDelay = max(3, stepDelay - 3);
        } else if (keyCode == DOWN) {
            stepDelay = min(80, stepDelay + 3);
        }
    }

    @Override
    public void mousePressed() {
        int clickedIndex = (mouseX - ARRAY_LEFT) / CELL_SIZE + 1;
        boolean inArray = mouseY >= ARRAY_TOP && mouseY <= ARRAY_TOP + CELL_SIZE;
        if (inArray && clickedIndex >= 1 && clickedIndex <= SIZE && !running) {
            selectedIndex = clickedIndex;
        }
    }

    private void resetTree() {
        tree = new int[SIZE + 1];
        operation = "idle";
        operationIndex = 0;
        operationResult = 0;
        running = false;
        finished = false;
        frameCounter = 0;
        for (int index = 1; index <= SIZE; index++) {
            addToTree(index, values[index - 1]);
        }
        status = "Select a position, then press U or Q";
    }

    private void beginUpdate() {
        if (running || !"idle".equals(operation)) {
            return;
        }
        operation = "update";
        operationIndex = selectedIndex;
        finished = false;
        status = "Updating position " + selectedIndex + " by +" + updateAmount;
    }

    private void beginQuery() {
        if (running || !"idle".equals(operation)) {
            return;
        }
        operation = "query";
        operationIndex = selectedIndex;
        operationResult = 0;
        finished = false;
        status = "Querying prefix through position " + selectedIndex;
    }

    private void advance() {
        if ("update".equals(operation)) {
            tree[operationIndex] += updateAmount;
            int changedIndex = operationIndex;
            operationIndex += lowestBit(operationIndex);
            if (operationIndex > SIZE) {
                values[selectedIndex - 1] += updateAmount;
                operation = "idle";
                finished = true;
                status = "Update complete - changed tree entry " + changedIndex;
            } else {
                status = "Added " + updateAmount + " to tree[" + changedIndex
                        + "]; jump to tree[" + operationIndex + "]";
            }
        } else if ("query".equals(operation)) {
            int readIndex = operationIndex;
            operationResult += tree[readIndex];
            operationIndex -= lowestBit(operationIndex);
            if (operationIndex == 0) {
                operation = "idle";
                finished = true;
                status = "Query complete - prefix sum = " + operationResult;
            } else {
                status = "Added tree[" + readIndex + "] to result; jump to tree["
                        + operationIndex + "]";
            }
        }
    }

    private void addToTree(int index, int amount) {
        while (index <= SIZE) {
            tree[index] += amount;
            index += lowestBit(index);
        }
    }

    private int lowestBit(int index) {
        return index & -index;
    }

    private void drawHeader() {
        fill(30, 36, 48);
        noStroke();
        rect(0, 0, width, TOP_BAR_HEIGHT);
        fill(241, 245, 249);
        textSize(24);
        text("Fenwick Tree", 24, 31);
        fill(153, 165, 181);
        textSize(14);
        text(status, 24, 56);
        text("U update   Q query   SPACE step   A auto   R reset", width - 350, 27);
        text("Position: " + selectedIndex + "   Speed: " + stepDelay, width - 350, 51);
    }

    private void drawArray() {
        fill(153, 165, 181);
        textSize(17);
        text("Original array", ARRAY_LEFT, 135);
        for (int index = 1; index <= SIZE; index++) {
            int x = ARRAY_LEFT + (index - 1) * CELL_SIZE;
            fill(index == selectedIndex ? color(246, 183, 76) : color(45, 53, 68));
            stroke(76, 86, 105);
            rect(x, ARRAY_TOP, CELL_SIZE - 3, CELL_SIZE);
            fill(241, 245, 249);
            textAlign(CENTER, CENTER);
            textSize(17);
            text(values[index - 1], x + CELL_SIZE / 2f, ARRAY_TOP + CELL_SIZE / 2f);
            fill(153, 165, 181);
            textSize(12);
            text("" + index, x + CELL_SIZE / 2f, ARRAY_TOP + CELL_SIZE + 14);
            textAlign(LEFT, BASELINE);
        }
    }

    private void drawTree() {
        fill(153, 165, 181);
        textSize(17);
        text("Fenwick tree entries and the ranges they store", ARRAY_LEFT, 315);
        for (int index = 1; index <= SIZE; index++) {
            int x = ARRAY_LEFT + (index - 1) * CELL_SIZE;
            boolean active = ("update".equals(operation) || "query".equals(operation))
                    && index == operationIndex;
            fill(active ? color(246, 183, 76) : color(76, 125, 161));
            stroke(76, 86, 105);
            rect(x, TREE_TOP, CELL_SIZE - 3, CELL_SIZE);
            fill(241, 245, 249);
            textAlign(CENTER, CENTER);
            textSize(16);
            text(tree[index], x + CELL_SIZE / 2f, TREE_TOP + CELL_SIZE / 2f);
            fill(153, 165, 181);
            textSize(12);
            text("" + index, x + CELL_SIZE / 2f, TREE_TOP + CELL_SIZE + 14);
            textSize(10);
            int rangeStart = index - lowestBit(index) + 1;
            text(rangeStart + ".." + index, x + CELL_SIZE / 2f, TREE_TOP + CELL_SIZE + 29);
            textAlign(LEFT, BASELINE);
        }
        fill(142, 151, 166);
        textSize(13);
        text("Gold: current jump    Blue: stored partial sum    Labels below: covered array range",
                ARRAY_LEFT, TREE_TOP + CELL_SIZE + 65);
    }
}
