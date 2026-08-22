package com.example.demo.graphs;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import processing.core.PApplet;

/**
 * Interactive visual demonstration of topological sorting.
 *
 * A topological ordering is a sequence of nodes in a directed acyclic graph
 * where every prerequisite appears before the node that depends on it. This
 * demo uses Kahn's algorithm. It counts each node's incoming edges, called its
 * indegree, then places every zero-indegree node in a queue.
 *
 * Removing a node from the queue means it can safely appear next in the order.
 * The algorithm then removes that node's outgoing edges by lowering the
 * indegree of each neighbor. When a neighbor reaches indegree zero, it enters
 * the queue. If the queue becomes empty before every node is output, the graph
 * contains a cycle and no topological ordering exists.
 *
 * Pseudocode:
 *   count the indegree of every node
 *   put every zero-indegree node in a queue
 *   while the queue is not empty:
 *       remove a node and append it to the ordering
 *       lower the indegree of each outgoing neighbor
 *       queue any neighbor whose indegree becomes zero
 *   if not every node was output, report a cycle
 *
 * Controls:
 *   SPACE - process the next queued node
 *   A     - toggle automatic stepping
 *   R     - reset the ordering
 *   UP/DOWN - change automatic step speed
 */
public class TopologicalSortApplication extends PApplet {

    private static final int NODE_COUNT = 8;
    private static final int NODE_DIAMETER = 50;
    private static final int TOP_BAR_HEIGHT = 74;

    private final float[] nodeX = {100, 310, 520, 730, 205, 415, 625, 415};
    private final float[] nodeY = {180, 180, 180, 180, 335, 335, 335, 490};
    private final List<Edge> edges = new ArrayList<>();
    private final List<List<Integer>> outgoing = new ArrayList<>();
    private final Queue<Integer> queue = new ArrayDeque<>();
    private int[] indegree;
    private int[] order;
    private int orderedCount;
    private int currentNode = -1;
    private boolean running;
    private boolean finished;
    private int stepDelay = 18;
    private int frameCounter;
    private String status = "Press SPACE to begin topological sort";

    public static void main(String[] args) {
        PApplet.main(TopologicalSortApplication.class.getName());
    }

    @Override
    public void settings() {
        size(900, 650);
    }

    @Override
    public void setup() {
        surface.setTitle("Topological Sort Sandbox");
        textFont(createFont("SansSerif", 16));
        createGraph();
        resetAlgorithm();
    }

    @Override
    public void draw() {
        background(21, 25, 34);
        drawHeader();
        drawGraph();
        drawQueueAndOrder();
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
            outgoing.add(new ArrayList<>());
        }
        connect(0, 4);
        connect(0, 5);
        connect(1, 5);
        connect(1, 6);
        connect(2, 6);
        connect(3, 6);
        connect(4, 7);
        connect(5, 7);
        connect(6, 7);
    }

    private void connect(int from, int to) {
        edges.add(new Edge(from, to));
        outgoing.get(from).add(to);
    }

    private void resetAlgorithm() {
        indegree = new int[NODE_COUNT];
        order = new int[NODE_COUNT];
        orderedCount = 0;
        currentNode = -1;
        queue.clear();
        for (Edge edge : edges) {
            indegree[edge.to]++;
        }
        for (int index = 0; index < NODE_COUNT; index++) {
            if (indegree[index] == 0) {
                queue.add(index);
            }
        }
        running = false;
        finished = false;
        frameCounter = 0;
        status = "Queued all nodes with indegree zero - press SPACE";
    }

    private void advance() {
        if (queue.isEmpty()) {
            running = false;
            finished = true;
            status = orderedCount == NODE_COUNT
                    ? "Complete - valid topological ordering found"
                    : "Cycle detected - no topological ordering exists";
            return;
        }

        currentNode = queue.remove();
        order[orderedCount++] = currentNode;
        for (int neighbor : outgoing.get(currentNode)) {
            indegree[neighbor]--;
            if (indegree[neighbor] == 0) {
                queue.add(neighbor);
            }
        }
        status = "Output node " + (currentNode + 1) + " and reduced its neighbors' indegrees";
        if (orderedCount == NODE_COUNT) {
            running = false;
            finished = true;
            status = "Complete - valid topological ordering found";
        }
    }

    private void drawHeader() {
        fill(30, 36, 48);
        noStroke();
        rect(0, 0, width, TOP_BAR_HEIGHT);
        fill(241, 245, 249);
        textSize(24);
        text("Topological Sort", 24, 31);
        fill(153, 165, 181);
        textSize(14);
        text(status, 24, 56);
        text("SPACE step   A auto   R reset   UP/DOWN speed", width - 310, 27);
        text("Output: " + orderedCount + "/" + NODE_COUNT + "   Speed: " + stepDelay,
                width - 310, 51);
    }

    private void drawGraph() {
        fill(153, 165, 181);
        textSize(16);
        text("Directed acyclic graph", 24, 112);
        for (Edge edge : edges) {
            stroke(edge.from == currentNode ? color(246, 183, 76) : color(76, 86, 105));
            strokeWeight(edge.from == currentNode ? 4 : 2);
            line(nodeX[edge.from], nodeY[edge.from], nodeX[edge.to], nodeY[edge.to]);
            drawArrow(edge.from, edge.to);
        }
        for (int index = 0; index < NODE_COUNT; index++) {
            fill(index == currentNode ? color(246, 183, 76)
                    : orderPosition(index) >= 0 ? color(76, 201, 160)
                    : indegree[index] == 0 ? color(129, 140, 248) : color(76, 125, 161));
            stroke(245, 245, 245);
            strokeWeight(2);
            ellipse(nodeX[index], nodeY[index], NODE_DIAMETER, NODE_DIAMETER);
            fill(21, 25, 34);
            textAlign(CENTER, CENTER);
            textSize(16);
            text(index + 1, nodeX[index], nodeY[index] - 5);
            textSize(10);
            text("in: " + indegree[index], nodeX[index], nodeY[index] + 12);
            textAlign(LEFT, BASELINE);
        }
    }

    private void drawArrow(int from, int to) {
        float angle = atan2(nodeY[to] - nodeY[from], nodeX[to] - nodeX[from]);
        float arrowX = nodeX[to] - cos(angle) * 29;
        float arrowY = nodeY[to] - sin(angle) * 29;
        fill(76, 86, 105);
        noStroke();
        triangle(arrowX, arrowY,
                arrowX - cos(angle - QUARTER_PI) * 10,
                arrowY - sin(angle - QUARTER_PI) * 10,
                arrowX - cos(angle + QUARTER_PI) * 10,
                arrowY - sin(angle + QUARTER_PI) * 10);
    }

    private void drawQueueAndOrder() {
        fill(153, 165, 181);
        textSize(16);
        text("Zero-indegree queue", 610, 115);
        int index = 0;
        for (int node : queue) {
            fill(129, 140, 248);
            stroke(76, 86, 105);
            rect(610 + index * 82, 135, 70, 34);
            fill(21, 25, 34);
            textAlign(CENTER, CENTER);
            textSize(14);
            text("Node " + (node + 1), 645 + index * 82, 152);
            index++;
        }
        textAlign(LEFT, BASELINE);
        fill(153, 165, 181);
        text("Topological ordering", 610, 230);
        fill(246, 183, 76);
        textSize(18);
        StringBuilder ordering = new StringBuilder();
        for (int position = 0; position < orderedCount; position++) {
            if (position > 0) ordering.append(" -> ");
            ordering.append(order[position] + 1);
        }
        text(ordering.length() == 0 ? "(empty)" : ordering.toString(), 610, 265);
        fill(142, 151, 166);
        textSize(12);
        text("Purple: ready to output    Green: already output    Gold: current node", 610, 505);
    }

    private int orderPosition(int node) {
        for (int index = 0; index < orderedCount; index++) {
            if (order[index] == node) {
                return index;
            }
        }
        return -1;
    }

    private static final class Edge {
        private final int from;
        private final int to;

        private Edge(int from, int to) {
            this.from = from;
            this.to = to;
        }
    }
}
