package com.example.demo;

import processing.core.PApplet;

/** Shared step display for the additional algorithm demonstrations. */
public abstract class StepAlgorithmVisualizer extends PApplet {
    private String[] steps;
    private int stepIndex;
    private int activeValue;
    private int[] workValues;
    private boolean running;
    private boolean finished;
    private int frameCounter;
    private int stepDelay = 18;
    private String status;

    protected abstract String algorithmName();
    protected abstract String[] algorithmSteps();

    @Override
    public void settings() { size(900, 650); }

    @Override
    public void setup() {
        surface.setTitle(algorithmName() + " Sandbox");
        textFont(createFont("SansSerif", 16));
        resetDemo();
    }

    @Override
    public void draw() {
        background(21, 25, 34);
        fill(30, 36, 48); noStroke(); rect(0, 0, width, 74);
        fill(241, 245, 249); textSize(24); text(algorithmName(), 24, 31);
        fill(153, 165, 181); textSize(14); text(status, 24, 56);
        text("SPACE step   A auto   R reset   UP/DOWN speed", width - 310, 27);
        text("Step: " + Math.min(stepIndex, steps.length) + "/" + steps.length, width - 310, 51);
        drawStepPanel();
        if (running && frameCounter++ % stepDelay == 0) advance();
    }

    @Override
    public void keyPressed() {
        if (key == ' ' && !running && !finished) advance();
        else if (key == 'a' || key == 'A') { if (!finished) running = !running; }
        else if (key == 'r' || key == 'R') resetDemo();
        else if (keyCode == UP) stepDelay = max(2, stepDelay - 2);
        else if (keyCode == DOWN) stepDelay = min(60, stepDelay + 2);
    }

    private void resetDemo() {
        steps = algorithmSteps(); stepIndex = 0; running = false; finished = false; frameCounter = 0;
        workValues = new int[12];
        for (int index = 0; index < workValues.length; index++) workValues[index] = index * 7 + 12;
        activeValue = -1;
        status = "Press SPACE to begin";
    }

    private void advance() {
        if (stepIndex >= steps.length) { running = false; finished = true; status = "Algorithm complete"; return; }
        activeValue = stepIndex % workValues.length;
        workValues[activeValue] = (workValues[activeValue] + stepIndex * 11 + 9) % 90 + 10;
        status = steps[stepIndex++];
        if (stepIndex == steps.length) { running = false; finished = true; }
    }

    private void drawStepPanel() {
        fill(30, 36, 48); stroke(76, 86, 105); rect(80, 150, 740, 330);
        fill(76, 201, 160); noStroke();
        float progress = steps.length == 0 ? 1 : (float) stepIndex / steps.length;
        rect(120, 220, 660 * progress, 18);
        fill(241, 245, 249); textSize(20);
        text(stepIndex == 0 ? "Ready" : steps[Math.min(stepIndex - 1, steps.length - 1)], 120, 300);
        fill(153, 165, 181); textSize(15);
        text("Working values", 120, 350);
        for (int index = 0; index < workValues.length; index++) {
            int x = 120 + index * 55;
            float barHeight = map(workValues[index], 0, 100, 0, 75);
            fill(index == activeValue ? color(246, 183, 76) : color(76, 125, 161));
            rect(x, 450 - barHeight, 34, barHeight);
            fill(153, 165, 181);
            textSize(10);
            text(workValues[index], x, 470);
        }
        textSize(15);
        text("Press R to reset, then use SPACE or A to control the demonstration.", 120, 520);
    }
}
