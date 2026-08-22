package com.example.demo.sorting;

import java.util.Random;

import processing.core.PApplet;

/** Shared window and controls for the sorting algorithm demonstrations. */
abstract class SortingVisualizer extends PApplet {

    protected static final int VALUE_COUNT = 42;
    private static final int BAR_WIDTH = 18;
    private static final int GRAPH_LEFT = 18;
    private static final int GRAPH_TOP = 160;
    private static final int GRAPH_HEIGHT = 380;

    protected final Random random = new Random();
    protected int[] values = new int[VALUE_COUNT];
    protected int highlightA = -1;
    protected int highlightB = -1;
    protected int sortedThrough = -1;
    protected boolean running;
    protected boolean finished;
    protected int stepDelay = 3;
    protected int frameCounter;
    protected String status = "Press SPACE to start";

    protected abstract String algorithmName();

    protected abstract void resetAlgorithm();

    protected abstract void algorithmStep();

    @Override
    public void settings() {
        size(900, 650);
    }

    @Override
    public void setup() {
        surface.setTitle(algorithmName() + " Sandbox");
        textFont(createFont("SansSerif", 16));
        resetVisualization();
    }

    @Override
    public void draw() {
        background(21, 25, 34);
        drawHeader();
        drawValues();
        if (running && frameCounter++ % stepDelay == 0) {
            algorithmStep();
        }
    }

    @Override
    public void keyPressed() {
        if (key == ' ' && !running && !finished) {
            algorithmStep();
        } else if (key == 'a' || key == 'A') {
            if (!finished) {
                running = !running;
                status = running ? "Running automatically" : "Paused - press SPACE to continue";
            }
        } else if (key == 'r' || key == 'R') {
            resetVisualization();
        } else if (keyCode == UP) {
            stepDelay = max(1, stepDelay - 1);
        } else if (keyCode == DOWN) {
            stepDelay = min(30, stepDelay + 1);
        }
    }

    protected final void resetVisualization() {
        for (int index = 0; index < values.length; index++) {
            values[index] = random.nextInt(361) + 20;
        }
        highlightA = -1;
        highlightB = -1;
        sortedThrough = -1;
        running = false;
        finished = false;
        frameCounter = 0;
        resetAlgorithm();
        status = "New values generated - press SPACE to start";
    }

    protected final void finish(String message) {
        running = false;
        finished = true;
        highlightA = -1;
        highlightB = -1;
        sortedThrough = values.length - 1;
        status = message;
    }

    protected final void drawHeader() {
        fill(30, 36, 48);
        noStroke();
        rect(0, 0, width, 74);
        fill(241, 245, 249);
        textSize(24);
        text(algorithmName(), 24, 31);
        fill(153, 165, 181);
        textSize(14);
        text(status, 24, 56);
        text("SPACE step   A auto   R reset   UP/DOWN speed", width - 310, 27);
        text("Speed: " + stepDelay + "   Values: " + VALUE_COUNT, width - 310, 51);
    }

    protected final void drawValues() {
        fill(153, 165, 181);
        textSize(16);
        text("Array values", GRAPH_LEFT, 124);
        for (int index = 0; index < values.length; index++) {
            int x = GRAPH_LEFT + index * (BAR_WIDTH + 3);
            float barHeight = map(values[index], 0, 400, 0, GRAPH_HEIGHT);
            if (index == highlightA || index == highlightB) {
                fill(246, 183, 76);
            } else if (index <= sortedThrough) {
                fill(76, 201, 160);
            } else {
                fill(76, 125, 161);
            }
            noStroke();
            rect(x, GRAPH_TOP + GRAPH_HEIGHT - barHeight, BAR_WIDTH, barHeight);
            fill(142, 151, 166);
            textSize(10);
            text(values[index], x - 1, GRAPH_TOP + GRAPH_HEIGHT + 17);
        }
        stroke(56, 66, 82);
        line(GRAPH_LEFT, GRAPH_TOP + GRAPH_HEIGHT, width - GRAPH_LEFT,
                GRAPH_TOP + GRAPH_HEIGHT);
        fill(142, 151, 166);
        textSize(13);
        text("Gold: current comparison    Green: sorted position    Blue: unsorted value",
                GRAPH_LEFT, GRAPH_TOP + GRAPH_HEIGHT + 48);
    }

    protected final void swap(int first, int second) {
        int temporary = values[first];
        values[first] = values[second];
        values[second] = temporary;
    }
}
