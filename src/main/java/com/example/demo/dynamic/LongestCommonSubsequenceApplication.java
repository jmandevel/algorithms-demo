package com.example.demo.dynamic;

import processing.core.PApplet;

/**
 * Interactive visual demonstration of the longest common subsequence algorithm.
 *
 * The longest common subsequence, or LCS, is the longest sequence of characters
 * that appears in two strings in the same order. The characters do not need to
 * be next to each other. For example, the LCS of "ALGORITHM" and "LOGARITHM"
 * is "LORITHM".
 *
 * Dynamic programming stores the best answer for every pair of prefixes. If
 * the current characters match, the table extends the diagonal answer. If
 * they differ, it keeps the larger answer from the top or left cell:
 *
 *   match:    table[row][column] = table[row - 1][column - 1] + 1
 *   differ:   table[row][column] = max(table[row - 1][column],
 *                                    table[row][column - 1])
 *
 * Filling the table takes O(m * n) time and memory. Once it is complete,
 * following the table backward reveals which characters form the LCS.
 *
 * Pseudocode:
 *   for each character in the first string:
 *       for each character in the second string:
 *           if the characters match, take the diagonal value plus one
 *           otherwise, take the larger value from above or the left
 *   start at the bottom-right cell and trace backward to build the result
 *
 * Controls:
 *   SPACE - advance one table or backtracking step
 *   A     - toggle automatic stepping
 *   R     - reset the table
 *   UP/DOWN - change automatic step speed
 */
public class LongestCommonSubsequenceApplication extends PApplet {

    private static final String FIRST = "ALGORITHM";
    private static final String SECOND = "LOGARITHM";
    private static final int CELL_SIZE = 48;
    private static final int TABLE_LEFT = 180;
    private static final int TABLE_TOP = 220;

    private int[][] table;
    private int row;
    private int column;
    private int traceRow;
    private int traceColumn;
    private String result = "";
    private boolean tracing;
    private boolean running;
    private boolean finished;
    private int stepDelay = 18;
    private int frameCounter;
    private String status = "Press SPACE to fill the comparison table";

    public static void main(String[] args) {
        PApplet.main(LongestCommonSubsequenceApplication.class.getName());
    }

    @Override
    public void settings() {
        size(900, 650);
    }

    @Override
    public void setup() {
        surface.setTitle("Longest Common Subsequence Sandbox");
        textFont(createFont("SansSerif", 16));
        resetTable();
    }

    @Override
    public void draw() {
        background(21, 25, 34);
        drawHeader();
        drawStrings();
        drawTable();
        drawResult();
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
            stepDelay = min(80, stepDelay + 3);
        }
    }

    private void resetTable() {
        table = new int[FIRST.length() + 1][SECOND.length() + 1];
        row = 1;
        column = 1;
        traceRow = FIRST.length();
        traceColumn = SECOND.length();
        result = "";
        tracing = false;
        running = false;
        finished = false;
        frameCounter = 0;
        status = "Press SPACE to fill the comparison table";
    }

    private void advance() {
        if (!tracing && row <= FIRST.length()) {
            calculateCell();
            return;
        }
        if (!tracing) {
            tracing = true;
            traceRow = FIRST.length();
            traceColumn = SECOND.length();
            status = "Table complete - press SPACE to trace the LCS backward";
            return;
        }
        traceResult();
    }

    private void calculateCell() {
        char firstCharacter = FIRST.charAt(row - 1);
        char secondCharacter = SECOND.charAt(column - 1);
        if (firstCharacter == secondCharacter) {
            table[row][column] = table[row - 1][column - 1] + 1;
            status = "'" + firstCharacter + "' matches - take the diagonal and add one";
        } else {
            table[row][column] = Math.max(table[row - 1][column], table[row][column - 1]);
            status = "Characters differ - keep the larger top or left value";
        }
        column++;
        if (column > SECOND.length()) {
            column = 1;
            row++;
        }
    }

    private void traceResult() {
        if (traceRow == 0 || traceColumn == 0) {
            finished = true;
            running = false;
            status = "LCS complete: " + result;
            return;
        }
        char firstCharacter = FIRST.charAt(traceRow - 1);
        char secondCharacter = SECOND.charAt(traceColumn - 1);
        if (firstCharacter == secondCharacter) {
            result = firstCharacter + result;
            traceRow--;
            traceColumn--;
            status = "Matched '" + firstCharacter + "' - move diagonally and keep it";
        } else if (table[traceRow - 1][traceColumn] >= table[traceRow][traceColumn - 1]) {
            traceRow--;
            status = "Move up - the top cell has the best subsequence so far";
        } else {
            traceColumn--;
            status = "Move left - the left cell has the best subsequence so far";
        }
    }

    private void drawHeader() {
        fill(30, 36, 48);
        noStroke();
        rect(0, 0, width, 74);
        fill(241, 245, 249);
        textSize(24);
        text("Longest Common Subsequence", 24, 31);
        fill(153, 165, 181);
        textSize(14);
        text(status, 24, 56);
        text("SPACE step   A auto   R reset   UP/DOWN speed", width - 310, 27);
        text("Cell: " + Math.min(row, FIRST.length()) + "/" + FIRST.length()
                + "   Speed: " + stepDelay, width - 310, 51);
    }

    private void drawStrings() {
        fill(153, 165, 181);
        textSize(17);
        text("First string", 50, 130);
        text("Second string", 50, 170);
        fill(246, 183, 76);
        textSize(22);
        text(FIRST, 190, 130);
        text(SECOND, 190, 170);
    }

    private void drawTable() {
        fill(153, 165, 181);
        textSize(16);
        text("Prefix comparison table", TABLE_LEFT, 195);
        for (int currentColumn = 0; currentColumn <= SECOND.length(); currentColumn++) {
            fill(153, 165, 181);
            textAlign(CENTER, CENTER);
            text(currentColumn == 0 ? "-" : String.valueOf(SECOND.charAt(currentColumn - 1)),
                    TABLE_LEFT + (currentColumn + 1) * CELL_SIZE + CELL_SIZE / 2f, TABLE_TOP - 18);
        }
        for (int currentRow = 0; currentRow <= FIRST.length(); currentRow++) {
            fill(153, 165, 181);
            textAlign(CENTER, CENTER);
            text(currentRow == 0 ? "-" : String.valueOf(FIRST.charAt(currentRow - 1)),
                    TABLE_LEFT + CELL_SIZE / 2f, TABLE_TOP + (currentRow + 1) * CELL_SIZE + CELL_SIZE / 2f);
            for (int currentColumn = 0; currentColumn <= SECOND.length(); currentColumn++) {
                boolean active = !tracing && currentRow == row && currentColumn == column;
                boolean traceActive = tracing && currentRow == traceRow && currentColumn == traceColumn;
                fill(active || traceActive ? color(246, 183, 76) : color(76, 125, 161));
                stroke(56, 66, 82);
                rect(TABLE_LEFT + (currentColumn + 1) * CELL_SIZE,
                        TABLE_TOP + (currentRow + 1) * CELL_SIZE, CELL_SIZE - 2, CELL_SIZE - 2);
                fill(241, 245, 249);
                text(table[currentRow][currentColumn],
                        TABLE_LEFT + (currentColumn + 1) * CELL_SIZE + CELL_SIZE / 2f,
                        TABLE_TOP + (currentRow + 1) * CELL_SIZE + CELL_SIZE / 2f);
            }
        }
        textAlign(LEFT, BASELINE);
    }

    private void drawResult() {
        fill(153, 165, 181);
        textSize(18);
        text("Longest common subsequence", 50, 590);
        fill(246, 183, 76);
        textSize(22);
        text(finished ? result : "...", 350, 590);
    }
}
