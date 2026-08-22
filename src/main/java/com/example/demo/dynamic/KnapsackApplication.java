package com.example.demo.dynamic;

import java.util.ArrayList;
import java.util.List;

import processing.core.PApplet;

/**
 * Interactive visual demonstration of the 0/1 knapsack algorithm.
 *
 * Each item has a weight and a value. The goal is to choose items with the
 * greatest total value without exceeding the bag's capacity. The "0/1" rule
 * means every item can be taken once or left behind; it cannot be split or
 * taken repeatedly.
 *
 * Dynamic programming compares two choices for every item and capacity:
 *
 *   skip the item: dp[item - 1][capacity]
 *   take the item: value + dp[item - 1][capacity - weight]
 *
 * The larger choice becomes dp[item][capacity]. This takes O(items * capacity)
 * time and memory. After filling the table, the selected items are found by
 * walking backward and checking where the table value changed.
 *
 * Pseudocode:
 *   for each item:
 *       for each capacity:
 *           if item does not fit, copy the value above
 *           otherwise, keep the better of skip and take
 *   walk backward through the table to recover selected items
 *
 * Controls:
 *   SPACE - calculate the next item/capacity cell
 *   A     - toggle automatic stepping
 *   R     - reset the table
 *   UP/DOWN - change automatic step speed
 */
public class KnapsackApplication extends PApplet {

    private static final int[] WEIGHTS = {2, 5, 4, 7, 3, 6};
    private static final int[] VALUES = {6, 10, 7, 13, 5, 11};
    private static final int CAPACITY = 18;
    private static final int TABLE_LEFT = 110;
    private static final int TABLE_TOP = 290;
    private static final int CELL_WIDTH = 38;
    private static final int CELL_HEIGHT = 38;

    private int[][] table;
    private int itemIndex;
    private int capacityIndex;
    private int maximumValue;
    private int chosenWeight;
    private final List<Integer> selectedItems = new ArrayList<>();
    private boolean running;
    private boolean finished;
    private int stepDelay = 12;
    private int frameCounter;
    private String status = "Press SPACE to start filling the table";

    public static void main(String[] args) {
        PApplet.main(KnapsackApplication.class.getName());
    }

    @Override
    public void settings() {
        size(900, 650);
    }

    @Override
    public void setup() {
        surface.setTitle("0/1 Knapsack Sandbox");
        textFont(createFont("SansSerif", 16));
        resetTable();
    }

    @Override
    public void draw() {
        background(21, 25, 34);
        drawHeader();
        drawItems();
        drawTable();
        drawSolution();
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
            resetTable();
        } else if (keyCode == UP) {
            stepDelay = max(3, stepDelay - 3);
        } else if (keyCode == DOWN) {
            stepDelay = min(60, stepDelay + 3);
        }
    }

    private void resetTable() {
        table = new int[WEIGHTS.length + 1][CAPACITY + 1];
        itemIndex = 1;
        capacityIndex = 0;
        maximumValue = 0;
        chosenWeight = 0;
        selectedItems.clear();
        running = false;
        finished = false;
        frameCounter = 0;
        status = "Press SPACE to start filling the table";
    }

    private void advance() {
        if (itemIndex > WEIGHTS.length) {
            recoverSolution();
            running = false;
            finished = true;
            status = "Complete - best value: " + maximumValue + " using weight " + chosenWeight;
            return;
        }

        int skip = table[itemIndex - 1][capacityIndex];
        int take = WEIGHTS[itemIndex - 1] <= capacityIndex
                ? VALUES[itemIndex - 1] + table[itemIndex - 1][capacityIndex - WEIGHTS[itemIndex - 1]]
                : -1;
        table[itemIndex][capacityIndex] = Math.max(skip, take);
        maximumValue = table[itemIndex][capacityIndex];
        status = "Item " + itemIndex + ", capacity " + capacityIndex
                + ": skip = " + skip + ", take = " + (take < 0 ? "does not fit" : take);

        capacityIndex++;
        if (capacityIndex > CAPACITY) {
            capacityIndex = 0;
            itemIndex++;
        }
    }

    private void recoverSolution() {
        selectedItems.clear();
        int remainingCapacity = CAPACITY;
        for (int item = WEIGHTS.length; item > 0; item--) {
            if (table[item][remainingCapacity] != table[item - 1][remainingCapacity]) {
                selectedItems.add(item);
                remainingCapacity -= WEIGHTS[item - 1];
            }
        }
        chosenWeight = CAPACITY - remainingCapacity;
    }

    private void drawHeader() {
        fill(30, 36, 48);
        noStroke();
        rect(0, 0, width, 74);
        fill(241, 245, 249);
        textSize(24);
        text("0/1 Knapsack", 24, 31);
        fill(153, 165, 181);
        textSize(14);
        text(status, 24, 56);
        text("SPACE step   A auto   R reset   UP/DOWN speed", width - 310, 27);
        text("Item: " + Math.min(itemIndex, WEIGHTS.length) + "/" + WEIGHTS.length
                + "   Capacity: " + capacityIndex + "/" + CAPACITY, width - 310, 51);
    }

    private void drawItems() {
        fill(153, 165, 181);
        textSize(16);
        text("Items", 70, 112);
        for (int index = 0; index < WEIGHTS.length; index++) {
            int x = 70 + index * 130;
            boolean selected = selectedItems.contains(index + 1);
            fill(selected ? color(246, 183, 76) : color(76, 125, 161));
            stroke(76, 86, 105);
            rect(x, 130, 112, 74);
            fill(241, 245, 249);
            textSize(15);
            text("Item " + (index + 1), x + 12, 153);
            textSize(13);
            text("weight: " + WEIGHTS[index], x + 12, 174);
            text("value: " + VALUES[index], x + 12, 191);
        }
    }

    private void drawTable() {
        fill(153, 165, 181);
        textSize(16);
        text("DP table: best value for each item prefix and capacity", TABLE_LEFT, 265);
        for (int capacity = 0; capacity <= CAPACITY; capacity++) {
            fill(153, 165, 181);
            textSize(11);
            textAlign(CENTER, CENTER);
            text(capacity, TABLE_LEFT + capacity * CELL_WIDTH + CELL_WIDTH / 2f, TABLE_TOP - 12);
        }
        for (int item = 0; item <= WEIGHTS.length; item++) {
            for (int capacity = 0; capacity <= CAPACITY; capacity++) {
                boolean active = item == itemIndex && capacity == capacityIndex && !finished;
                boolean known = item < itemIndex || item == 0;
                fill(active ? color(246, 183, 76) : known ? color(76, 201, 160) : color(45, 53, 68));
                stroke(56, 66, 82);
                rect(TABLE_LEFT + capacity * CELL_WIDTH, TABLE_TOP + item * CELL_HEIGHT,
                        CELL_WIDTH - 2, CELL_HEIGHT - 2);
                fill(241, 245, 249);
                textSize(11);
                text(known || (item == itemIndex && capacity < capacityIndex)
                    ? String.valueOf(table[item][capacity]) : "-",
                        TABLE_LEFT + capacity * CELL_WIDTH + CELL_WIDTH / 2f,
                        TABLE_TOP + item * CELL_HEIGHT + CELL_HEIGHT / 2f);
            }
            fill(153, 165, 181);
            textSize(11);
            textAlign(RIGHT, CENTER);
            text(item, TABLE_LEFT - 8, TABLE_TOP + item * CELL_HEIGHT + CELL_HEIGHT / 2f);
        }
        textAlign(LEFT, BASELINE);
    }

    private void drawSolution() {
        fill(142, 151, 166);
        textSize(13);
        text("Rows = number of items considered    Columns = capacity    Gold = current cell",
                TABLE_LEFT, 575);
        if (finished) {
            fill(246, 183, 76);
            textSize(16);
            text("Selected items: " + selectedItems + "   Total value: " + maximumValue
                    + "   Total weight: " + chosenWeight, TABLE_LEFT, 610);
        }
    }
}
