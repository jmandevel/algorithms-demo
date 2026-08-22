package com.example.demo.dynamic;

import processing.core.PApplet;

/**
 * Interactive visual demonstration of the Fibonacci sequence.
 *
 * The Fibonacci sequence begins with 0 and 1. Every later value is the sum of
 * the two values before it:
 *
 *   F(n) = F(n - 1) + F(n - 2)
 *
 * This demo uses an iterative approach. It stores the values already found,
 * then uses the last two values to calculate the next one. Storing the whole
 * sequence takes O(n) memory. If only the latest value is needed, the algorithm
 * can use O(1) memory by keeping just the previous two values.
 *
 * Pseudocode:
 *   sequence[0] = 0
 *   sequence[1] = 1
 *   for index from 2 through the requested length:
 *       sequence[index] = sequence[index - 1] + sequence[index - 2]
 *
 * Controls:
 *   SPACE - calculate the next Fibonacci term
 *   A     - toggle automatic stepping
 *   R     - reset the sequence
 *   UP/DOWN - change automatic step speed
 */
public class FibonacciApplication extends PApplet {

    private static final int TERM_COUNT = 16;
    private static final int BAR_LEFT = 56;
    private static final int BAR_TOP = 180;
    private static final int BAR_WIDTH = 48;
    private static final int BAR_HEIGHT = 315;
    private static final int TOP_BAR_HEIGHT = 74;

    private long[] sequence;
    private int calculatedThrough;
    private boolean running;
    private boolean finished;
    private int stepDelay = 18;
    private int frameCounter;
    private String status = "Press SPACE to calculate the next term";

    public static void main(String[] args) {
        PApplet.main(FibonacciApplication.class.getName());
    }

    @Override
    public void settings() {
        size(900, 650);
    }

    @Override
    public void setup() {
        surface.setTitle("Fibonacci Sequence Sandbox");
        textFont(createFont("SansSerif", 16));
        resetSequence();
    }

    @Override
    public void draw() {
        background(21, 25, 34);
        drawHeader();
        drawFormula();
        drawSequence();
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
            resetSequence();
        } else if (keyCode == UP) {
            stepDelay = max(3, stepDelay - 3);
        } else if (keyCode == DOWN) {
            stepDelay = min(80, stepDelay + 3);
        }
    }

    private void resetSequence() {
        sequence = new long[TERM_COUNT];
        sequence[0] = 0;
        sequence[1] = 1;
        calculatedThrough = 1;
        running = false;
        finished = false;
        frameCounter = 0;
        status = "Starting values are 0 and 1 - press SPACE to continue";
    }

    private void advance() {
        if (calculatedThrough >= TERM_COUNT - 1) {
            running = false;
            finished = true;
            status = "Sequence complete through term " + calculatedThrough;
            return;
        }

        int nextIndex = calculatedThrough + 1;
        sequence[nextIndex] = sequence[nextIndex - 1] + sequence[nextIndex - 2];
        calculatedThrough = nextIndex;
        status = "F(" + nextIndex + ") = F(" + (nextIndex - 1) + ") + F(" + (nextIndex - 2)
                + " = " + sequence[nextIndex];
        if (calculatedThrough == TERM_COUNT - 1) {
            running = false;
            finished = true;
        }
    }

    private void drawHeader() {
        fill(30, 36, 48);
        noStroke();
        rect(0, 0, width, TOP_BAR_HEIGHT);
        fill(241, 245, 249);
        textSize(24);
        text("Fibonacci Sequence", 24, 31);
        fill(153, 165, 181);
        textSize(14);
        text(status, 24, 56);
        text("SPACE step   A auto   R reset   UP/DOWN speed", width - 310, 27);
        text("Terms: " + (calculatedThrough + 1) + "/" + TERM_COUNT + "   Speed: " + stepDelay,
                width - 310, 51);
    }

    private void drawFormula() {
        fill(153, 165, 181);
        textSize(18);
        text("Each term is the sum of the previous two terms", BAR_LEFT, 125);
        fill(246, 183, 76);
        textSize(16);
        text("F(n) = F(n - 1) + F(n - 2)", BAR_LEFT, 150);
    }

    private void drawSequence() {
        long largest = sequence[calculatedThrough];
        for (int index = 0; index <= calculatedThrough; index++) {
            int x = BAR_LEFT + index * BAR_WIDTH;
            float barHeight = largest == 0 ? 0 : map(sequence[index], 0, largest, 8, BAR_HEIGHT);
            fill(index == calculatedThrough ? color(246, 183, 76) : color(76, 201, 160));
            stroke(76, 86, 105);
            rect(x, BAR_TOP + BAR_HEIGHT - barHeight, BAR_WIDTH - 5, barHeight);
            fill(241, 245, 249);
            textAlign(CENTER, CENTER);
            textSize(index > 11 ? 10 : 13);
            text(String.valueOf(sequence[index]), x + (BAR_WIDTH - 5) / 2f,
                    BAR_TOP + BAR_HEIGHT - barHeight - 13);
            fill(153, 165, 181);
            textSize(12);
            text("F(" + index + ")", x + (BAR_WIDTH - 5) / 2f, BAR_TOP + BAR_HEIGHT + 18);
            textAlign(LEFT, BASELINE);
        }
        fill(142, 151, 166);
        textSize(13);
        text("Green: calculated terms    Gold: newest term", BAR_LEFT, BAR_TOP + BAR_HEIGHT + 55);
    }
}
