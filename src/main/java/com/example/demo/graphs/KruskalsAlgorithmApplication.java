package com.example.demo.graphs;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import processing.core.PApplet;

/**
 * Interactive visual demonstration of Kruskal's minimum spanning tree algorithm.
 *
 * Kruskal's algorithm connects every node in a weighted graph while keeping the
 * total edge weight as small as possible. It sorts all edges from lightest to
 * heaviest, then considers them in that order. An edge is added when it joins
 * two different groups of nodes. An edge is skipped when its endpoints are
 * already connected, because adding it would create a loop.
 *
 * The process ends after the tree has one fewer edge than the number of nodes.
 * In this sketch, green edges were accepted, red edges were skipped because
 * they would create a loop, and gray edges have not been considered yet.
 *
 * Controls:
 *   SPACE - advance one step
 *   A     - toggle automatic stepping
 *   R     - generate a new graph and reset
 *   UP/DOWN - change automatic step speed
 */
public class KruskalsAlgorithmApplication extends PApplet {

    private static final int NODE_COUNT = 10;
    private static final int EDGE_COUNT = 18;
    private static final int TOP_BAR_HEIGHT = 74;

    private final Random random = new Random();
    private final List<Node> nodes = new ArrayList<>();
    private final List<Edge> edges = new ArrayList<>();
    private final List<Edge> treeEdges = new ArrayList<>();
    private UnionFind components;
    private boolean running;
    private int edgeCursor;
    private int stepDelay = 45;
    private int frameCounter;
    private String status = "Press SPACE to start Kruskal's algorithm";

    public static void main(String[] args) {
        PApplet.main(KruskalsAlgorithmApplication.class.getName());
    }

    @Override
    public void settings() {
        size(900, 650);
    }

    @Override
    public void setup() {
        surface.setTitle("Kruskal's Algorithm Sandbox");
        textFont(createFont("SansSerif", 16));
        generateGraph();
    }

    @Override
    public void draw() {
        background(21, 25, 34);
        drawHeader();
        drawEdges();
        drawNodes();

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
        treeEdges.clear();
        components = new UnionFind(NODE_COUNT);
        edgeCursor = 0;
        running = false;
        frameCounter = 0;
        status = "New graph generated - press SPACE to start";

        for (int index = 0; index < NODE_COUNT; index++) {
            float angle = TWO_PI * index / NODE_COUNT - HALF_PI;
            float radius = 215 + random(-35, 35);
            nodes.add(new Node(width / 2f + cos(angle) * radius,
                    TOP_BAR_HEIGHT + 275 + sin(angle) * radius));
        }

        // The ring guarantees that every generated graph is connected.
        for (int index = 0; index < NODE_COUNT; index++) {
            addEdge(index, (index + 1) % NODE_COUNT);
        }
        while (edges.size() < EDGE_COUNT) {
            addEdge(random.nextInt(NODE_COUNT), random.nextInt(NODE_COUNT));
        }
        edges.sort(Comparator.comparingInt(edge -> edge.weight));
    }

    private void addEdge(int first, int second) {
        if (first == second || hasEdge(first, second)) {
            return;
        }
        edges.add(new Edge(first, second, random.nextInt(91) + 10));
    }

    private boolean hasEdge(int first, int second) {
        for (Edge edge : edges) {
            if ((edge.first == first && edge.second == second)
                    || (edge.first == second && edge.second == first)) {
                return true;
            }
        }
        return false;
    }

    private void advance() {
        if (treeEdges.size() == NODE_COUNT - 1) {
            running = false;
            status = "Minimum spanning tree complete - total weight: " + totalWeight();
            return;
        }

        if (edgeCursor >= edges.size()) {
            running = false;
            status = "All edges considered";
            return;
        }

        Edge edge = edges.get(edgeCursor++);
        if (components.join(edge.first, edge.second)) {
            edge.selected = true;
            treeEdges.add(edge);
            status = "Accepted edge " + (edge.first + 1) + " - " + (edge.second + 1)
                    + " (weight " + edge.weight + ")";
        } else {
            edge.rejected = true;
            status = "Skipped edge " + (edge.first + 1) + " - " + (edge.second + 1)
                    + " (it would create a loop)";
        }
    }

    private int totalWeight() {
        int total = 0;
        for (Edge edge : treeEdges) {
            total += edge.weight;
        }
        return total;
    }

    private void drawHeader() {
        fill(30, 36, 48);
        noStroke();
        rect(0, 0, width, TOP_BAR_HEIGHT);
        fill(241, 245, 249);
        textSize(24);
        text("Kruskal's Algorithm", 24, 31);
        fill(153, 165, 181);
        textSize(14);
        text(status, 24, 56);
        text("SPACE step   A auto   R reset   UP/DOWN speed", width - 310, 27);
        text("Tree: " + treeEdges.size() + "/" + (NODE_COUNT - 1)
                + "   Checked: " + edgeCursor + "/" + edges.size(), width - 310, 51);
    }

    private void drawEdges() {
        for (Edge edge : edges) {
            Node first = nodes.get(edge.first);
            Node second = nodes.get(edge.second);
            if (edge.selected) {
                stroke(color(76, 201, 160));
            } else if (edge.rejected) {
                stroke(color(239, 102, 102));
            } else {
                stroke(color(76, 86, 105));
            }
            strokeWeight(edge.selected ? 5 : 2);
            line(first.x, first.y, second.x, second.y);

            fill(edge.selected ? color(177, 255, 224)
                    : edge.rejected ? color(255, 170, 170) : color(142, 151, 166));
            textSize(12);
            text(edge.weight, (first.x + second.x) / 2 + 5, (first.y + second.y) / 2 - 4);
        }
    }

    private void drawNodes() {
        for (int index = 0; index < nodes.size(); index++) {
            Node node = nodes.get(index);
            fill(color(246, 183, 76));
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

    private static final class Node {
        private final float x;
        private final float y;

        private Node(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }

    private static final class Edge {
        private final int first;
        private final int second;
        private final int weight;
        private boolean selected;
        private boolean rejected;

        private Edge(int first, int second, int weight) {
            this.first = first;
            this.second = second;
            this.weight = weight;
        }
    }

    private static final class UnionFind {
        private final int[] parent;
        private final int[] rank;

        private UnionFind(int size) {
            parent = new int[size];
            rank = new int[size];
            for (int index = 0; index < size; index++) {
                parent[index] = index;
            }
        }

        private int find(int node) {
            if (parent[node] != node) {
                parent[node] = find(parent[node]);
            }
            return parent[node];
        }

        private boolean join(int first, int second) {
            int firstRoot = find(first);
            int secondRoot = find(second);
            if (firstRoot == secondRoot) {
                return false;
            }
            if (rank[firstRoot] < rank[secondRoot]) {
                parent[firstRoot] = secondRoot;
            } else if (rank[firstRoot] > rank[secondRoot]) {
                parent[secondRoot] = firstRoot;
            } else {
                parent[secondRoot] = firstRoot;
                rank[firstRoot]++;
            }
            return true;
        }
    }
}
