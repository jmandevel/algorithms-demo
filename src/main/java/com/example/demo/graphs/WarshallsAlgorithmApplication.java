package com.example.demo.graphs;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import processing.core.PApplet;

/**
 * Interactive visual demonstration of Warshall's transitive closure algorithm.
 *
 * Warshall's algorithm answers a reachability question for every pair of
 * nodes: can we travel from one node to another, possibly through other nodes?
 * It starts with a matrix containing the graph's direct connections. Then it
 * considers each node as an allowed intermediate stop. For every pair (from,
 * to), the rule is:
 *
 *     reachable[from][to] = reachable[from][to]
 *             OR (reachable[from][middle] AND reachable[middle][to])
 *
 * If a route through the current middle node exists, the matching matrix cell
 * becomes true. After all middle nodes have been considered, the matrix shows
 * every direct and indirect route in the graph. Green cells are routes found
 * so far, while bright cells changed during the most recent step.
 *
 * Controls:
 *   SPACE - advance one intermediate node
 *   A     - toggle automatic stepping
 *   R     - generate a new graph and reset
 *   UP/DOWN - change automatic step speed
 */
public class WarshallsAlgorithmApplication extends PApplet {

    private static final int NODE_COUNT = 7;
    private static final int TOP_BAR_HEIGHT = 74;
    private static final int GRAPH_CENTER_X = 260;
    private static final int MATRIX_LEFT = 545;
    private static final int MATRIX_TOP = 180;
    private static final int CELL_SIZE = 42;

    private final Random random = new Random();
    private final List<Node> nodes = new ArrayList<>();
    private final List<DirectedEdge> edges = new ArrayList<>();
    private boolean[][] reachable;
    private boolean[][] changed;
    private boolean running;
    private int middleNode = -1;
    private int stepDelay = 45;
    private int frameCounter;
    private String status = "Press SPACE to start Warshall's algorithm";

    public static void main(String[] args) {
        PApplet.main(WarshallsAlgorithmApplication.class.getName());
    }

    @Override
    public void settings() {
        size(1000, 700);
    }

    @Override
    public void setup() {
        surface.setTitle("Warshall's Algorithm Sandbox");
        textFont(createFont("SansSerif", 16));
        generateGraph();
    }

    @Override
    public void draw() {
        background(21, 25, 34);
        drawHeader();
        drawGraph();
        drawMatrix();

        if (running && frameCounter++ % stepDelay == 0) {
            advance();
        }
    }

    @Override
    public void keyPressed() {
        if (key == ' ' && !running) {
            advance();
        } else if (key == 'a' || key == 'A') {
            running = !running;
            status = running ? "Running automatically" : "Paused - press SPACE to continue";
        } else if (key == 'r' || key == 'R') {
            generateGraph();
        } else if (keyCode == UP) {
            stepDelay = max(5, stepDelay - 5);
        } else if (keyCode == DOWN) {
            stepDelay = min(120, stepDelay + 5);
        }
    }

    private void generateGraph() {
        nodes.clear();
        edges.clear();
        middleNode = -1;
        running = false;
        frameCounter = 0;
        status = "New graph generated - press SPACE to start";

        reachable = new boolean[NODE_COUNT][NODE_COUNT];
        changed = new boolean[NODE_COUNT][NODE_COUNT];
        for (int index = 0; index < NODE_COUNT; index++) {
            reachable[index][index] = true;
        }

        for (int index = 0; index < NODE_COUNT; index++) {
            float angle = TWO_PI * index / NODE_COUNT - HALF_PI;
            nodes.add(new Node(GRAPH_CENTER_X + cos(angle) * 175,
                    TOP_BAR_HEIGHT + 280 + sin(angle) * 175));
            addEdge(index, (index + 1) % NODE_COUNT);
        }
        for (int count = edges.size(); count < 12; count++) {
            addEdge(random.nextInt(NODE_COUNT), random.nextInt(NODE_COUNT));
        }
    }

    private void addEdge(int from, int to) {
        if (from == to || hasEdge(from, to)) {
            return;
        }
        edges.add(new DirectedEdge(from, to));
        reachable[from][to] = true;
    }

    private boolean hasEdge(int from, int to) {
        for (DirectedEdge edge : edges) {
            if (edge.from == from && edge.to == to) {
                return true;
            }
        }
        return false;
    }

    private void advance() {
        if (middleNode == NODE_COUNT - 1) {
            running = false;
            status = "Complete - matrix shows every reachable pair";
            return;
        }

        middleNode++;
        changed = new boolean[NODE_COUNT][NODE_COUNT];
        for (int from = 0; from < NODE_COUNT; from++) {
            for (int to = 0; to < NODE_COUNT; to++) {
                boolean wasReachable = reachable[from][to];
                reachable[from][to] = wasReachable
                        || (reachable[from][middleNode] && reachable[middleNode][to]);
                changed[from][to] = !wasReachable && reachable[from][to];
            }
        }
        status = "Using node " + (middleNode + 1) + " as the intermediate node";
    }

    private void drawHeader() {
        fill(30, 36, 48);
        noStroke();
        rect(0, 0, width, TOP_BAR_HEIGHT);
        fill(241, 245, 249);
        textSize(24);
        text("Warshall's Algorithm", 24, 31);
        fill(153, 165, 181);
        textSize(14);
        text(status, 24, 56);
        text("SPACE step   A auto   R reset   UP/DOWN speed", width - 310, 27);
        String progress = middleNode < 0 ? "Ready" : "Middle: " + (middleNode + 1) + "/" + NODE_COUNT;
        text(progress + "   Speed: " + stepDelay, width - 310, 51);
    }

    private void drawGraph() {
        fill(153, 165, 181);
        textSize(16);
        text("Directed graph", 24, 112);
        for (DirectedEdge edge : edges) {
            Node from = nodes.get(edge.from);
            Node to = nodes.get(edge.to);
            stroke(76, 86, 105);
            strokeWeight(2);
            line(from.x, from.y, to.x, to.y);
            drawArrowHead(from, to);
        }

        if (middleNode >= 0) {
            Node middle = nodes.get(middleNode);
            noFill();
            stroke(246, 183, 76);
            strokeWeight(3);
            ellipse(middle.x, middle.y, 58, 58);
        }

        for (int index = 0; index < nodes.size(); index++) {
            Node node = nodes.get(index);
            fill(index == middleNode ? color(246, 183, 76) : color(76, 201, 160));
            stroke(245, 245, 245);
            strokeWeight(2);
            ellipse(node.x, node.y, 42, 42);
            fill(21, 25, 34);
            textAlign(CENTER, CENTER);
            textSize(16);
            text(index + 1, node.x, node.y - 1);
            textAlign(LEFT, BASELINE);
        }
    }

    private void drawArrowHead(Node from, Node to) {
        float angle = atan2(to.y - from.y, to.x - from.x);
        float arrowX = to.x - cos(angle) * 24;
        float arrowY = to.y - sin(angle) * 24;
        fill(76, 86, 105);
        noStroke();
        triangle(arrowX, arrowY,
                arrowX - cos(angle - QUARTER_PI) * 10,
                arrowY - sin(angle - QUARTER_PI) * 10,
                arrowX - cos(angle + QUARTER_PI) * 10,
                arrowY - sin(angle + QUARTER_PI) * 10);
    }

    private void drawMatrix() {
        fill(153, 165, 181);
        textSize(16);
        text("Reachability matrix", MATRIX_LEFT, 112);
        textSize(12);
        text("Rows: from    Columns: to", MATRIX_LEFT, 135);

        for (int index = 0; index < NODE_COUNT; index++) {
            fill(153, 165, 181);
            textAlign(CENTER, CENTER);
            text(index + 1, MATRIX_LEFT + 56 + index * CELL_SIZE, MATRIX_TOP - 20);
            textAlign(RIGHT, CENTER);
            text(index + 1, MATRIX_LEFT + 35, MATRIX_TOP + 20 + index * CELL_SIZE);
            textAlign(LEFT, BASELINE);
        }

        for (int from = 0; from < NODE_COUNT; from++) {
            for (int to = 0; to < NODE_COUNT; to++) {
                if (changed[from][to]) {
                    fill(246, 183, 76);
                } else if (reachable[from][to]) {
                    fill(76, 201, 160);
                } else {
                    fill(45, 53, 68);
                }
                noStroke();
                rect(MATRIX_LEFT + 42 + to * CELL_SIZE,
                        MATRIX_TOP + from * CELL_SIZE, CELL_SIZE - 3, CELL_SIZE - 3);
                fill(reachable[from][to] ? color(21, 25, 34) : color(142, 151, 166));
                textAlign(CENTER, CENTER);
                text(reachable[from][to] ? "1" : "0",
                        MATRIX_LEFT + 42 + to * CELL_SIZE + (CELL_SIZE - 3) / 2f,
                        MATRIX_TOP + from * CELL_SIZE + (CELL_SIZE - 3) / 2f);
            }
        }
        textAlign(LEFT, BASELINE);
        fill(142, 151, 166);
        textSize(12);
        text("Green = known route    Gold = found this step", MATRIX_LEFT, MATRIX_TOP + NODE_COUNT * CELL_SIZE + 28);
    }

    private static final class Node {
        private final float x;
        private final float y;

        private Node(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }

    private static final class DirectedEdge {
        private final int from;
        private final int to;

        private DirectedEdge(int from, int to) {
            this.from = from;
            this.to = to;
        }
    }
}
