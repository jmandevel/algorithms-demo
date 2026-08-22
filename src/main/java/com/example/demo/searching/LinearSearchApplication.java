package com.example.demo.searching;

import java.util.Random;

import processing.core.PApplet;

/**
 * Interactive visual demonstration of linear search.
 *
 * Linear search checks each value in a collection from the beginning until it
 * finds the target or reaches the end. It does not require the data to be
 * sorted, which makes it useful for small or unsorted collections. Its worst
 * case takes O(n) comparisons because it may need to inspect every value.
 *
 * This sketch highlights one array position at a time. Green values have not
 * been checked yet, blue values have already been checked, red is the current
 * comparison, and gold is the matching target.
 *
 * Controls:
 *   Mouse - click an array value to make it the target
 *   LEFT/RIGHT - select a nearby array value as the target
 *   +/- - adjust the target by one, including values not in the array
 *   SPACE - advance one search step
 *   A     - toggle automatic stepping
 *   R     - generate a new unsorted array and reset
 *   UP/DOWN - change automatic step speed
 */
public class LinearSearchApplication extends PApplet {

    private static final int ARRAY_SIZE = 15;
    private static final int CELL_SIZE = 50;
    private static final int ARRAY_LEFT = 75;
    private static final int ARRAY_TOP = 275;
    private static final int TOP_BAR_HEIGHT = 74;

    private final Random random = new Random();
    private int[] values;
    private boolean[] checked;
    private int target;
    private int currentIndex;
    private int foundIndex = -1;
    private boolean running;
    private boolean finished;
    private int stepDelay = 18;
    private int frameCounter;
    private String status = "Press SPACE to start linear search";

    public static void main(String[] args) {
        PApplet.main(LinearSearchApplication.class.getName());
    }

    @Override
    public void settings() {
        size(900, 650);
    }

    @Override
    public void setup() {
        surface.setTitle("Linear Search Sandbox");
        textFont(createFont("SansSerif", 16));
        generateArray();
    }

    @Override
    public void draw() {
        background(21, 25, 34);
        drawHeader();
        drawSearchInfo();
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
        } else if ((key == '+' || key == '=') && !running) {
            target++;
            resetSearch();
        } else if (key == '-' && !running) {
            target--;
            resetSearch();
        } else if (keyCode == LEFT && !running) {
            target = max(0, target - 1);
            resetSearch();
        } else if (keyCode == RIGHT && !running) {
            target = min(99, target + 1);
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
        for (int index = 0; index < ARRAY_SIZE; index++) {
            values[index] = random.nextInt(90) + 10;
        }
        target = values[ARRAY_SIZE / 2];
        resetSearch();
        status = "New unsorted array - press SPACE to start";
    }

    private void resetSearch() {
        checked = new boolean[ARRAY_SIZE];
        currentIndex = 0;
        foundIndex = -1;
        running = false;
        finished = false;
        frameCounter = 0;
        status = "Target: " + target + " - press SPACE to compare the next value";
    }

    private void advance() {
        if (currentIndex >= ARRAY_SIZE) {
            running = false;
            finished = true;
            status = "Target " + target + " is not in the array";
            return;
        }

        checked[currentIndex] = true;
        if (values[currentIndex] == target) {
            foundIndex = currentIndex;
            running = false;
            finished = true;
            status = "Found target " + target + " at index " + currentIndex;
            return;
        }

        status = values[currentIndex] + " does not match - move to the next value";
        currentIndex++;
        if (currentIndex >= ARRAY_SIZE) {
            running = false;
            finished = true;
            status = "Target " + target + " is not in the array";
        }
    }

    private void drawHeader() {
        fill(30, 36, 48);
        noStroke();
        rect(0, 0, width, TOP_BAR_HEIGHT);
        fill(241, 245, 249);
        textSize(24);
        text("Linear Search", 24, 31);
        fill(153, 165, 181);
        textSize(14);
        text(status, 24, 56);
        text("SPACE step   A auto   R reset   +/- target", width - 285, 27);
        text("Checked: " + countChecked() + "/" + ARRAY_SIZE + "   Speed: " + stepDelay,
                width - 285, 51);
    }

    private void drawSearchInfo() {
        fill(153, 165, 181);
        textSize(17);
        text("Unsorted array", ARRAY_LEFT, 210);
        textSize(14);
        text("Target: " + target, ARRAY_LEFT, 240);
        if (!finished && currentIndex < ARRAY_SIZE) {
            text("Next comparison: index " + currentIndex, ARRAY_LEFT + 150, 240);
        }
    }

    private void drawArray() {
        for (int index = 0; index < ARRAY_SIZE; index++) {
            int x = ARRAY_LEFT + index * CELL_SIZE;
            if (index == foundIndex) {
                fill(246, 183, 76);
            } else if (index == currentIndex && !finished) {
                fill(239, 102, 102);
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
        text("Green: not checked    Blue: checked    Red: current comparison    Gold: found",
                ARRAY_LEFT, ARRAY_TOP + 95);
    }

    private int countChecked() {
        int count = 0;
        for (boolean valueChecked : checked) {
            if (valueChecked) {
                count++;
            }
        }
        return count;
    }
}
