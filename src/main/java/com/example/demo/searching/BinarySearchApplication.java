package com.example.demo.searching;

import java.util.Random;

import processing.core.PApplet;

/**
 * Interactive visual demonstration of binary search.
 *
 * Binary search works on sorted data. It checks the middle value of the
 * current search range. If that value is too small, everything to its left
 * can be ignored. If it is too large, everything to its right can be ignored.
 * The range is cut roughly in half after every comparison, giving binary
 * search O(log n) time instead of checking every value one by one.
 *
 * This sketch keeps three indexes: low and high mark the current range, while
 * mid marks the value being checked. The target can be changed to show both a
 * successful search and a search for a value that is not in the array.
 *
 * Controls:
 *   Mouse - click an array value to make it the target
 *   LEFT/RIGHT - select a nearby array value as the target
 *   +/- - adjust the target by one, including values not in the array
 *   SPACE - advance one search step
 *   A     - toggle automatic stepping
 *   R     - generate a new sorted array and reset
 *   UP/DOWN - change automatic step speed
 */
public class BinarySearchApplication extends PApplet {

    private static final int ARRAY_SIZE = 15;
    private static final int CELL_SIZE = 50;
    private static final int ARRAY_LEFT = 75;
    private static final int ARRAY_TOP = 275;
    private static final int TOP_BAR_HEIGHT = 74;

    private final Random random = new Random();
    private int[] values;
    private boolean[] checked;
    private int target;
    private int low;
    private int high;
    private int mid = -1;
    private int foundIndex = -1;
    private boolean running;
    private boolean finished;
    private int stepDelay = 18;
    private int frameCounter;
    private String status = "Press SPACE to start binary search";

    public static void main(String[] args) {
        PApplet.main(BinarySearchApplication.class.getName());
    }

    @Override
    public void settings() {
        size(900, 650);
    }

    @Override
    public void setup() {
        surface.setTitle("Binary Search Sandbox");
        textFont(createFont("SansSerif", 16));
        generateArray();
    }

    @Override
    public void draw() {
        background(21, 25, 34);
        drawHeader();
        drawSearchRange();
        drawArray();

        if (running && frameCounter++ % stepDelay == 0) {
            advance();
        }
    }

    @Override
    public void keyPressed() {
        if (key == ' ' && !running && !finished) {
            advance();
        } else if (key == 'a' || key == 'A') {
            if (!finished) {
                running = !running;
                status = running ? "Running automatically" : "Paused - press SPACE to continue";
            }
        } else if (key == 'r' || key == 'R') {
            generateArray();
        } else if ((key == '+' || key == '=') && !running && finished) {
            target++;
            resetSearch();
        } else if (key == '-' && !running && finished) {
            target--;
            resetSearch();
        } else if (keyCode == LEFT && !running) {
            target = max(values[0], target - 1);
            resetSearch();
        } else if (keyCode == RIGHT && !running) {
            target = min(values[ARRAY_SIZE - 1], target + 1);
            resetSearch();
        } else if (keyCode == UP) {
            stepDelay = max(3, stepDelay - 3);
        } else if (keyCode == DOWN) {
            stepDelay = min(80, stepDelay + 3);
        }
    }

    @Override
    public void mousePressed() {
        int clickedIndex = (mouseX - ARRAY_LEFT) / CELL_SIZE;
        boolean inArray = mouseY >= ARRAY_TOP && mouseY <= ARRAY_TOP + CELL_SIZE;
        if (inArray && clickedIndex >= 0 && clickedIndex < ARRAY_SIZE && !running) {
            target = values[clickedIndex];
            resetSearch();
        }
    }

    private void generateArray() {
        values = new int[ARRAY_SIZE];
        values[0] = random.nextInt(8) + 4;
        for (int index = 1; index < ARRAY_SIZE; index++) {
            values[index] = values[index - 1] + random.nextInt(8) + 2;
        }
        target = values[ARRAY_SIZE / 2];
        resetSearch();
        status = "New sorted array - press SPACE to start";
    }

    private void resetSearch() {
        checked = new boolean[ARRAY_SIZE];
        low = 0;
        high = ARRAY_SIZE - 1;
        mid = -1;
        foundIndex = -1;
        running = false;
        finished = false;
        frameCounter = 0;
        status = "Target: " + target + " - press SPACE to compare the middle";
    }

    private void advance() {
        if (low > high) {
            running = false;
            finished = true;
            status = "Target " + target + " is not in the array";
            return;
        }

        mid = low + (high - low) / 2;
        checked[mid] = true;
        if (values[mid] == target) {
            foundIndex = mid;
            running = false;
            finished = true;
            status = "Found target " + target + " at index " + mid;
        } else if (values[mid] < target) {
            status = values[mid] + " is too small - discard the left half";
            low = mid + 1;
        } else {
            status = values[mid] + " is too large - discard the right half";
            high = mid - 1;
        }
    }

    private void drawHeader() {
        fill(30, 36, 48);
        noStroke();
        rect(0, 0, width, TOP_BAR_HEIGHT);
        fill(241, 245, 249);
        textSize(24);
        text("Binary Search", 24, 31);
        fill(153, 165, 181);
        textSize(14);
        text(status, 24, 56);
        text("SPACE step   A auto   R reset   +/- target", width - 285, 27);
        text("Range: " + low + ".." + high + "   Speed: " + stepDelay, width - 285, 51);
    }

    private void drawSearchRange() {
        fill(153, 165, 181);
        textSize(17);
        text("Sorted array", ARRAY_LEFT, 210);
        textSize(14);
        text("Target: " + target, ARRAY_LEFT, 240);
        if (mid >= 0 && !finished) {
            text("Checking middle index " + mid, ARRAY_LEFT + 150, 240);
        }
    }

    private void drawArray() {
        for (int index = 0; index < ARRAY_SIZE; index++) {
            int x = ARRAY_LEFT + index * CELL_SIZE;
            boolean outsideRange = index < low || index > high;
            if (index == foundIndex) {
                fill(246, 183, 76);
            } else if (index == mid) {
                fill(239, 102, 102);
            } else if (outsideRange) {
                fill(30, 36, 48);
            } else if (checked[index]) {
                fill(76, 125, 161);
            } else {
                fill(76, 201, 160);
            }
            stroke(76, 86, 105);
            strokeWeight(1);
            rect(x, ARRAY_TOP, CELL_SIZE - 3, CELL_SIZE);
            fill(241, 245, 249);
            textAlign(CENTER, CENTER);
            textSize(16);
            text(values[index], x + CELL_SIZE / 2f, ARRAY_TOP + CELL_SIZE / 2f);
            fill(153, 165, 181);
            textSize(12);
            text(index, x + CELL_SIZE / 2f, ARRAY_TOP + CELL_SIZE + 15);
            textAlign(LEFT, BASELINE);
        }
        fill(142, 151, 166);
        textSize(13);
        text("Green: possible values    Blue: already checked    Red: current middle    Gold: found",
                ARRAY_LEFT, ARRAY_TOP + 95);
    }
}
