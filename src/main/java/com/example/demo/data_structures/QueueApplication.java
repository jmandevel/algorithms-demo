package com.example.demo.data_structures;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import processing.core.PApplet;

/**
 * Interactive visual demonstration of a queue.
 *
 * A queue is a first-in, first-out data structure. New values enter at the
 * back, and values leave from the front. This is useful when work should be
 * handled in arrival order, such as print jobs, messages, or breadth-first
 * search. Enqueue and dequeue are both O(1) with a suitable queue structure.
 *
 * Pseudocode:
 *   enqueue(value): add value to the back of the queue
 *   dequeue(): remove and return the value at the front
 *   peek(): look at the front value without removing it
 *
 * Controls:
 *   E     - prepare a new value to enqueue
 *   D     - prepare a dequeue operation
 *   SPACE - perform the prepared operation
 *   A     - toggle automatic stepping
 *   R     - reset the queue
 *   UP/DOWN - change automatic step speed
 */
public class QueueApplication extends PApplet {

    private static final int CAPACITY = 9;
    private static final int QUEUE_LEFT = 80;
    private static final int QUEUE_TOP = 255;
    private static final int CELL_WIDTH = 80;

    private final Deque<Integer> queue = new ArrayDeque<>();
    private final List<Integer> removedValues = new ArrayList<>();
    private int nextValue = 6;
    private String operation = "idle";
    private int pendingValue;
    private boolean running;
    private boolean finished;
    private int stepDelay = 18;
    private int frameCounter;
    private String status = "Press E to enqueue or D to dequeue";

    public static void main(String[] args) {
        PApplet.main(QueueApplication.class.getName());
    }

    @Override
    public void settings() {
        size(900, 650);
    }

    @Override
    public void setup() {
        surface.setTitle("Queue Sandbox");
        textFont(createFont("SansSerif", 16));
        resetQueue();
    }

    @Override
    public void draw() {
        background(21, 25, 34);
        drawHeader();
        drawQueue();
        drawOperation();
        if (running && frameCounter++ % stepDelay == 0) {
            advance();
        }
    }

    @Override
    public void keyPressed() {
        if (key == ' ' && !running && !finished && !"idle".equals(operation)) {
            advance();
        } else if (key == 'a' || key == 'A') {
            if (!finished && !"idle".equals(operation)) {
                running = !running;
                status = running ? "Running automatically" : "Paused - press SPACE to continue";
            }
        } else if (key == 'e' || key == 'E') {
            beginEnqueue();
        } else if (key == 'd' || key == 'D') {
            beginDequeue();
        } else if (key == 'r' || key == 'R') {
            resetQueue();
        } else if (keyCode == UP) {
            stepDelay = max(3, stepDelay - 3);
        } else if (keyCode == DOWN) {
            stepDelay = min(80, stepDelay + 3);
        }
    }

    private void resetQueue() {
        queue.clear();
        removedValues.clear();
        queue.add(2);
        queue.add(4);
        queue.add(7);
        queue.add(9);
        nextValue = 10;
        operation = "idle";
        running = false;
        finished = false;
        frameCounter = 0;
        status = "Front is " + queue.peek() + " - press E to enqueue or D to dequeue";
    }

    private void beginEnqueue() {
        if (running) {
            return;
        }
        if (queue.size() >= CAPACITY) {
            status = "Queue is full - dequeue a value first";
            return;
        }
        operation = "enqueue";
        pendingValue = nextValue++;
        finished = false;
        status = "Value " + pendingValue + " will enter at the back - press SPACE";
    }

    private void beginDequeue() {
        if (running) {
            return;
        }
        if (queue.isEmpty()) {
            status = "Queue is empty - enqueue a value first";
            return;
        }
        operation = "dequeue";
        pendingValue = queue.peek();
        finished = false;
        status = "Value " + pendingValue + " is at the front - press SPACE to remove it";
    }

    private void advance() {
        if ("enqueue".equals(operation)) {
            queue.addLast(pendingValue);
            status = "Enqueued " + pendingValue + " at the back";
        } else if ("dequeue".equals(operation)) {
            int removed = queue.removeFirst();
            removedValues.add(removed);
            status = "Dequeued " + removed + " from the front";
        }
        operation = "idle";
        running = false;
        finished = true;
    }

    private void drawHeader() {
        fill(30, 36, 48);
        noStroke();
        rect(0, 0, width, 74);
        fill(241, 245, 249);
        textSize(24);
        text("Queue", 24, 31);
        fill(153, 165, 181);
        textSize(14);
        text(status, 24, 56);
        text("E enqueue   D dequeue   SPACE step   A auto   R reset", width - 350, 27);
        text("Size: " + queue.size() + "/" + CAPACITY + "   Speed: " + stepDelay, width - 350, 51);
    }

    private void drawQueue() {
        fill(153, 165, 181);
        textSize(18);
        text("Queue", QUEUE_LEFT, 190);
        stroke(76, 86, 105);
        line(QUEUE_LEFT - 12, QUEUE_TOP - 18, QUEUE_LEFT - 12, QUEUE_TOP + 70);
        line(QUEUE_LEFT + CAPACITY * CELL_WIDTH + 8, QUEUE_TOP - 18,
                QUEUE_LEFT + CAPACITY * CELL_WIDTH + 8, QUEUE_TOP + 70);
        int index = 0;
        for (int value : queue) {
            int x = QUEUE_LEFT + index * CELL_WIDTH;
            boolean front = index == 0;
            boolean back = index == queue.size() - 1;
            fill(front && "dequeue".equals(operation) ? color(246, 183, 76)
                    : back && "enqueue".equals(operation) ? color(246, 183, 76)
                    : color(76, 201, 160));
            stroke(76, 86, 105);
            rect(x, QUEUE_TOP, CELL_WIDTH - 4, 54);
            fill(21, 25, 34);
            textAlign(CENTER, CENTER);
            textSize(18);
            text(value, x + (CELL_WIDTH - 4) / 2f, QUEUE_TOP + 27);
            textAlign(LEFT, BASELINE);
            index++;
        }
        fill(153, 165, 181);
        textSize(13);
        text("FRONT", QUEUE_LEFT - 8, QUEUE_TOP - 28);
        text("BACK", QUEUE_LEFT + Math.max(0, queue.size() - 1) * CELL_WIDTH, QUEUE_TOP - 28);
    }

    private void drawOperation() {
        fill(30, 36, 48);
        stroke(76, 86, 105);
        rect(QUEUE_LEFT, 390, 720, 125);
        fill(153, 165, 181);
        textSize(17);
        text("Operation", QUEUE_LEFT + 20, 425);
        fill(246, 183, 76);
        textSize(20);
        text("idle".equals(operation) ? "No operation prepared" : operation.toUpperCase(), QUEUE_LEFT + 150, 425);
        fill(241, 245, 249);
        textSize(16);
        text("Removed values: " + removedValues, QUEUE_LEFT + 20, 470);
        fill(142, 151, 166);
        textSize(13);
        text("Green: values in the queue    Gold: value involved in the prepared operation", QUEUE_LEFT + 20, 495);
    }
}
