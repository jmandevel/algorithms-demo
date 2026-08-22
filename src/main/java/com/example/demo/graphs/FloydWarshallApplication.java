package com.example.demo.graphs;

import java.util.ArrayList;
import java.util.List;

import processing.core.PApplet;

/**
 * Interactive visual demonstration of the Floyd-Warshall algorithm.
 *
 * Floyd-Warshall finds the shortest distance between every pair of nodes. It
 * stores those distances in a matrix. For each possible intermediate node k,
 * it asks whether traveling from i to j through k is shorter than the route
 * currently known:
 *
 *   distance[i][j] = min(distance[i][j],
 *                         distance[i][k] + distance[k][j])
 *
 * The algorithm works with positive or negative edge weights, as long as the
 * graph has no negative cycle. It takes O(V^3) time and O(V^2) memory, making
 * it useful when distances between many pairs are needed.
 *
 * Pseudocode:
 *   set distance[i][i] to zero
 *   copy every direct edge into the distance matrix
 *   for each possible middle node k:
 *       for each source node i:
 *           for each destination node j:
 *               keep the shorter direct or through-k route
 *
 * Controls:
 *   SPACE - calculate the next matrix cell
 *   A     - toggle automatic stepping
 *   R     - reset the matrix
 *   UP/DOWN - change automatic step speed
 */
public class FloydWarshallApplication extends PApplet {

    private static final int NODE_COUNT = 6;
    private static final int INF = 999;
    private static final int MATRIX_LEFT = 590;
    private static final int MATRIX_TOP = 190;
    private static final int CELL_SIZE = 45;

    private final List<Node> nodes = new ArrayList<>();
    private final List<Edge> edges = new ArrayList<>();
    private int[][] distance;
    private int intermediate;
    private int source;
    private int destination;
    private boolean running;
    private boolean finished;
    private int stepDelay = 18;
    private int frameCounter;
    private String status = "Press SPACE to begin Floyd-Warshall";

    public static void main(String[] args) {
        PApplet.main(FloydWarshallApplication.class.getName());
    }

    @Override
    public void settings() {
        size(1000, 700);
    }

    @Override
    public void setup() {
        surface.setTitle("Floyd-Warshall Algorithm Sandbox");
        textFont(createFont("SansSerif", 16));
        createGraph();
        resetAlgorithm();
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
        if (key == ' ' && !running && !finished) {
            advance();
        } else if (key == 'a' || key == 'A') {
            if (!finished) {
                running = !running;
                status = running ? "Running automatically" : "Paused - press SPACE to continue";
            }
        } else if (key == 'r' || key == 'R') {
            resetAlgorithm();
        } else if (keyCode == UP) {
            stepDelay = max(3, stepDelay - 3);
        } else if (keyCode == DOWN) {
            stepDelay = min(80, stepDelay + 3);
        }
    }

    private void createGraph() {
        for (int index = 0; index < NODE_COUNT; index++) {
            float angle = TWO_PI * index / NODE_COUNT - HALF_PI;
            nodes.add(new Node(285 + cos(angle) * 190, 350 + sin(angle) * 190));
        }
        addEdge(0, 1, 5);
        addEdge(0, 2, 2);
        addEdge(1, 3, 1);
        addEdge(2, 1, 1);
        addEdge(2, 3, 6);
        addEdge(2, 4, 4);
        addEdge(3, 4, 2);
        addEdge(3, 5, 7);
        addEdge(4, 5, 1);
        addEdge(5, 0, 8);
    }

    private void addEdge(int from, int to, int weight) {
        edges.add(new Edge(from, to, weight));
    }

    private void resetAlgorithm() {
        distance = new int[NODE_COUNT][NODE_COUNT];
        for (int row = 0; row < NODE_COUNT; row++) {
            for (int column = 0; column < NODE_COUNT; column++) {
                distance[row][column] = row == column ? 0 : INF;
            }
        }
        for (Edge edge : edges) {
            distance[edge.from][edge.to] = Math.min(distance[edge.from][edge.to], edge.weight);
        }
        intermediate = 0;
        source = 0;
        destination = 0;
        running = false;
        finished = false;
        frameCounter = 0;
        status = "Matrix initialized - press SPACE to use node 1 as the middle";
    }

    private void advance() {
        if (intermediate >= NODE_COUNT) {
            running = false;
            finished = true;
            status = "Complete - all-pairs shortest distances found";
            return;
        }

        int direct = distance[source][destination];
        int throughMiddle = distance[source][intermediate] == INF
                || distance[intermediate][destination] == INF
                ? INF : distance[source][intermediate] + distance[intermediate][destination];
        if (throughMiddle < direct) {
            distance[source][destination] = throughMiddle;
            status = "Node " + (intermediate + 1) + " improves " + (source + 1)
                    + " -> " + (destination + 1) + " from " + display(direct)
                    + " to " + throughMiddle;
        } else {
            status = "Node " + (intermediate + 1) + " does not improve "
                    + (source + 1) + " -> " + (destination + 1);
        }

        destination++;
        if (destination == NODE_COUNT) {
            destination = 0;
            source++;
        }
        if (source == NODE_COUNT) {
            source = 0;
            intermediate++;
            if (intermediate < NODE_COUNT) {
                status = "Now using node " + (intermediate + 1) + " as the middle node";
            }
        }
    }

    private String display(int value) {
        return value == INF ? "inf" : String.valueOf(value);
    }

    private void drawHeader() {
        fill(30, 36, 48);
        noStroke();
        rect(0, 0, width, 74);
        fill(241, 245, 249);
        textSize(24);
        text("Floyd-Warshall", 24, 31);
        fill(153, 165, 181);
        textSize(14);
        text(status, 24, 56);
        text("SPACE step   A auto   R reset   UP/DOWN speed", width - 310, 27);
        text("Middle: " + Math.min(intermediate + 1, NODE_COUNT) + "/" + NODE_COUNT
                + "   Speed: " + stepDelay, width - 310, 51);
    }

    private void drawGraph() {
        fill(153, 165, 181);
        textSize(16);
        text("Directed weighted graph", 24, 112);
        for (Edge edge : edges) {
            Node from = nodes.get(edge.from);
            Node to = nodes.get(edge.to);
            boolean active = edge.from == source || edge.to == destination
                    || edge.from == intermediate || edge.to == intermediate;
            stroke(active ? color(246, 183, 76) : color(76, 86, 105));
            strokeWeight(active ? 3 : 2);
            line(from.x, from.y, to.x, to.y);
            fill(edge.weight < 0 ? color(239, 102, 102) : color(153, 165, 181));
            textSize(13);
            text(edge.weight, (from.x + to.x) / 2, (from.y + to.y) / 2);
        }
        for (int index = 0; index < nodes.size(); index++) {
            Node node = nodes.get(index);
            fill(index == intermediate ? color(246, 183, 76) : color(76, 125, 161));
            stroke(245, 245, 245);
            strokeWeight(2);
            ellipse(node.x, node.y, 48, 48);
            fill(21, 25, 34);
            textAlign(CENTER, CENTER);
            textSize(16);
            text(index + 1, node.x, node.y);
            textAlign(LEFT, BASELINE);
        }
    }

    private void drawMatrix() {
        fill(153, 165, 181);
        textSize(17);
        text("Shortest-distance matrix", MATRIX_LEFT, 125);
        textSize(12);
        text("Rows: from    Columns: to", MATRIX_LEFT, 148);
        for (int index = 0; index < NODE_COUNT; index++) {
            fill(153, 165, 181);
            textAlign(CENTER, CENTER);
            text(index + 1, MATRIX_LEFT + 62 + index * CELL_SIZE, MATRIX_TOP - 18);
            textAlign(RIGHT, CENTER);
            text(index + 1, MATRIX_LEFT + 40, MATRIX_TOP + 22 + index * CELL_SIZE);
            textAlign(LEFT, BASELINE);
        }
        for (int row = 0; row < NODE_COUNT; row++) {
            for (int column = 0; column < NODE_COUNT; column++) {
                boolean active = row == source && column == destination && !finished;
                fill(active ? color(246, 183, 76) : distance[row][column] == INF
                        ? color(45, 53, 68) : color(76, 201, 160));
                noStroke();
                rect(MATRIX_LEFT + 45 + column * CELL_SIZE,
                        MATRIX_TOP + row * CELL_SIZE, CELL_SIZE - 3, CELL_SIZE - 3);
                fill(distance[row][column] == INF ? color(142, 151, 166) : color(21, 25, 34));
                textAlign(CENTER, CENTER);
                text(display(distance[row][column]),
                        MATRIX_LEFT + 45 + column * CELL_SIZE + CELL_SIZE / 2f,
                        MATRIX_TOP + row * CELL_SIZE + CELL_SIZE / 2f);
            }
        }
        textAlign(LEFT, BASELINE);
        fill(142, 151, 166);
        textSize(12);
        text("Green = known distance    Gold = current from-to pair    inf = no route known yet",
                MATRIX_LEFT, MATRIX_TOP + NODE_COUNT * CELL_SIZE + 28);
    }

    private static final class Node {
        private final float x;
        private final float y;

        private Node(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }

    private static final class Edge {
        private final int from;
        private final int to;
        private final int weight;

        private Edge(int from, int to, int weight) {
            this.from = from;
            this.to = to;
            this.weight = weight;
        }
    }
}
