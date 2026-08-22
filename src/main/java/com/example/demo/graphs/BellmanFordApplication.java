package com.example.demo.graphs;

import java.util.ArrayList;
import java.util.List;

import processing.core.PApplet;

/**
 * Interactive visual demonstration of the Bellman-Ford shortest-path algorithm.
 *
 * Bellman-Ford finds the shortest distance from one source node to every other
 * node. Unlike Dijkstra's algorithm, it can handle negative edge weights. It
 * repeatedly relaxes every edge: if the route to an edge's destination becomes
 * shorter by going through its source, the destination's distance is updated.
 *
 * A graph with V nodes needs at most V - 1 full passes because a shortest simple
 * path can use at most V - 1 edges. Bellman-Ford then makes one extra pass. If
 * any distance can still improve, the graph contains a reachable negative cycle
 * and no finite shortest path exists for the affected nodes.
 *
 * Pseudocode:
 *   distance[source] = 0
 *   repeat V - 1 times:
 *       for every edge (from, to, weight):
 *           distance[to] = min(distance[to], distance[from] + weight)
 *   scan every edge once more
 *   if an edge can still improve, report a negative cycle
 *
 * Controls:
 *   SPACE - relax the next edge
 *   A     - toggle automatic stepping
 *   R     - reset the graph and distances
 *   UP/DOWN - change automatic step speed
 */
public class BellmanFordApplication extends PApplet {

    private static final int NODE_COUNT = 6;
    private static final int TOP_BAR_HEIGHT = 74;
    private static final int SOURCE = 0;

    private final List<Node> nodes = new ArrayList<>();
    private final List<Edge> edges = new ArrayList<>();
    private float[] distances;
    private int[] predecessors;
    private int edgeIndex;
    private int pass;
    private boolean checkingCycle;
    private boolean negativeCycle;
    private boolean running;
    private boolean finished;
    private int stepDelay = 18;
    private int frameCounter;
    private Edge currentEdge;
    private String status = "Press SPACE to start relaxing edges";

    public static void main(String[] args) {
        PApplet.main(BellmanFordApplication.class.getName());
    }

    @Override
    public void settings() {
        size(900, 650);
    }

    @Override
    public void setup() {
        surface.setTitle("Bellman-Ford Algorithm Sandbox");
        textFont(createFont("SansSerif", 16));
        createGraph();
        resetAlgorithm();
    }

    @Override
    public void draw() {
        background(21, 25, 34);
        drawHeader();
        drawGraph();
        drawDistances();
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
            nodes.add(new Node(width / 2f + cos(angle) * 220,
                    TOP_BAR_HEIGHT + 265 + sin(angle) * 190));
        }
        addEdge(0, 1, 6);
        addEdge(0, 2, 7);
        addEdge(1, 2, 8);
        addEdge(1, 3, 5);
        addEdge(1, 4, -4);
        addEdge(2, 3, -3);
        addEdge(2, 4, 9);
        addEdge(3, 1, -2);
        addEdge(3, 5, 4);
        addEdge(4, 0, 2);
        addEdge(4, 3, 7);
        addEdge(4, 5, 6);
    }

    private void addEdge(int from, int to, int weight) {
        edges.add(new Edge(from, to, weight));
    }

    private void resetAlgorithm() {
        distances = new float[NODE_COUNT];
        predecessors = new int[NODE_COUNT];
        for (int index = 0; index < NODE_COUNT; index++) {
            distances[index] = Float.POSITIVE_INFINITY;
            predecessors[index] = -1;
        }
        distances[SOURCE] = 0;
        edgeIndex = 0;
        pass = 1;
        checkingCycle = false;
        negativeCycle = false;
        running = false;
        finished = false;
        frameCounter = 0;
        currentEdge = null;
        status = "Source is node 1 - press SPACE to relax edges";
    }

    private void advance() {
        if (checkingCycle) {
            negativeCycle = hasFurtherImprovement();
            checkingCycle = false;
            finished = true;
            running = false;
            status = negativeCycle ? "Negative cycle detected" : "Complete - no reachable negative cycle";
            return;
        }
        if (pass > NODE_COUNT - 1) {
            checkingCycle = true;
            status = "V - 1 passes complete - press SPACE to check for a negative cycle";
            return;
        }

        currentEdge = edges.get(edgeIndex);
        boolean improved = relax(currentEdge);
        status = "Pass " + pass + ": " + describe(currentEdge)
                + (improved ? " - distance improved" : " - no improvement");
        edgeIndex++;
        if (edgeIndex == edges.size()) {
            edgeIndex = 0;
            pass++;
        }
    }

    private boolean relax(Edge edge) {
        if (distances[edge.from] == Float.POSITIVE_INFINITY) {
            return false;
        }
        float candidate = distances[edge.from] + edge.weight;
        if (candidate < distances[edge.to]) {
            distances[edge.to] = candidate;
            predecessors[edge.to] = edge.from;
            return true;
        }
        return false;
    }

    private boolean hasFurtherImprovement() {
        for (Edge edge : edges) {
            if (distances[edge.from] != Float.POSITIVE_INFINITY
                    && distances[edge.from] + edge.weight < distances[edge.to]) {
                return true;
            }
        }
        return false;
    }

    private String describe(Edge edge) {
        return "edge " + (edge.from + 1) + " -> " + (edge.to + 1)
                + " (weight " + edge.weight + ")";
    }

    private String formatDistance(float distance) {
        return distance == Float.POSITIVE_INFINITY ? "inf" : nf(distance, 0, 0);
    }

    private void drawHeader() {
        fill(30, 36, 48);
        noStroke();
        rect(0, 0, width, TOP_BAR_HEIGHT);
        fill(241, 245, 249);
        textSize(24);
        text("Bellman-Ford", 24, 31);
        fill(153, 165, 181);
        textSize(14);
        text(status, 24, 56);
        text("SPACE step   A auto   R reset   UP/DOWN speed", width - 310, 27);
        text("Pass: " + Math.min(pass, NODE_COUNT) + "/" + (NODE_COUNT - 1)
                + "   Speed: " + stepDelay, width - 310, 51);
    }

    private void drawGraph() {
        fill(153, 165, 181);
        textSize(16);
        text("Directed weighted graph", 24, 112);
        for (Edge edge : edges) {
            Node from = nodes.get(edge.from);
            Node to = nodes.get(edge.to);
            stroke(edge == currentEdge ? color(246, 183, 76) : color(76, 86, 105));
            strokeWeight(edge == currentEdge ? 4 : 2);
            line(from.x, from.y, to.x, to.y);
            drawArrow(from, to, edge);
            fill(edge.weight < 0 ? color(239, 102, 102) : color(153, 165, 181));
            textSize(13);
            text(edge.weight, (from.x + to.x) / 2 + 5, (from.y + to.y) / 2 - 5);
        }
        for (int index = 0; index < nodes.size(); index++) {
            Node node = nodes.get(index);
            fill(index == SOURCE ? color(246, 183, 76) : color(76, 125, 161));
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

    private void drawArrow(Node from, Node to, Edge edge) {
        float angle = atan2(to.y - from.y, to.x - from.x);
        float arrowX = to.x - cos(angle) * 28;
        float arrowY = to.y - sin(angle) * 28;
        fill(edge == currentEdge ? color(246, 183, 76) : color(76, 86, 105));
        noStroke();
        triangle(arrowX, arrowY,
                arrowX - cos(angle - QUARTER_PI) * 10,
                arrowY - sin(angle - QUARTER_PI) * 10,
                arrowX - cos(angle + QUARTER_PI) * 10,
                arrowY - sin(angle + QUARTER_PI) * 10);
    }

    private void drawDistances() {
        fill(153, 165, 181);
        textSize(17);
        text("Shortest distances from node 1", 630, 120);
        for (int index = 0; index < NODE_COUNT; index++) {
            fill(index == SOURCE ? color(246, 183, 76) : color(76, 201, 160));
            noStroke();
            rect(650, 150 + index * 54, 190, 38);
            fill(21, 25, 34);
            textSize(15);
            text("Node " + (index + 1) + ": " + formatDistance(distances[index]), 664, 175 + index * 54);
            if (predecessors[index] >= 0) {
                fill(153, 165, 181);
                textSize(11);
                text("previous: node " + (predecessors[index] + 1), 664, 188 + index * 54);
            }
        }
        fill(142, 151, 166);
        textSize(12);
        text("Gold edge: current relaxation    Red weights: negative edges", 630, 510);
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
