package com.example.demo.graphs;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import processing.core.PApplet;

/**
 * Interactive visual demonstration of breadth-first search.
 *
 * Breadth-first search, or BFS, explores an unweighted graph level by level.
 * It begins at a start node, visits that node's neighbors, then visits the
 * neighbors of those nodes. A first-in, first-out queue controls this order:
 * nodes discovered earlier are processed before nodes discovered later.
 *
 * Marking a node visited when it enters the queue prevents duplicate work. The
 * first time BFS reaches a node, it has found a shortest path to that node when
 * every edge has the same cost. The running time is O(V + E), where V is the
 * number of nodes and E is the number of edges.
 *
 * Pseudocode:
 *   put the start node in a queue and mark it visited
 *   while the queue is not empty:
 *       remove the oldest node
 *       visit each unvisited neighbor
 *       mark each new neighbor visited and add it to the queue
 *
 * Controls:
 *   SPACE - process the next node in the queue
 *   A     - toggle automatic stepping
 *   R     - reset the traversal
 *   UP/DOWN - change automatic step speed
 */
public class BreadthFirstSearchApplication extends PApplet {

    private static final int NODE_COUNT = 10;
    private static final int TOP_BAR_HEIGHT = 74;
    private static final int QUEUE_LEFT = 630;
    private static final int QUEUE_TOP = 155;

    private final List<Node> nodes = new ArrayList<>();
    private final List<Edge> edges = new ArrayList<>();
    private final List<List<Integer>> adjacency = new ArrayList<>();
    private final Queue<Integer> queue = new ArrayDeque<>();
    private boolean[] discovered;
    private int[] visitOrder;
    private int visitedCount;
    private int currentNode = -1;
    private boolean running;
    private boolean finished;
    private int stepDelay = 18;
    private int frameCounter;
    private String status = "Press SPACE to begin BFS at node 1";

    public static void main(String[] args) {
        PApplet.main(BreadthFirstSearchApplication.class.getName());
    }

    @Override
    public void settings() {
        size(900, 650);
    }

    @Override
    public void setup() {
        surface.setTitle("Breadth-First Search Sandbox");
        textFont(createFont("SansSerif", 16));
        createGraph();
        resetSearch();
    }

    @Override
    public void draw() {
        background(21, 25, 34);
        drawHeader();
        drawGraph();
        drawQueue();
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
            resetSearch();
        } else if (keyCode == UP) {
            stepDelay = max(3, stepDelay - 3);
        } else if (keyCode == DOWN) {
            stepDelay = min(80, stepDelay + 3);
        }
    }

    private void createGraph() {
        for (int index = 0; index < NODE_COUNT; index++) {
            float angle = TWO_PI * index / NODE_COUNT - HALF_PI;
            nodes.add(new Node(width / 2f - 120 + cos(angle) * 230,
                    TOP_BAR_HEIGHT + 265 + sin(angle) * 190));
            adjacency.add(new ArrayList<>());
        }
        connect(0, 1);
        connect(0, 2);
        connect(1, 3);
        connect(1, 4);
        connect(2, 4);
        connect(2, 5);
        connect(3, 6);
        connect(4, 6);
        connect(4, 7);
        connect(5, 7);
        connect(5, 8);
        connect(6, 9);
        connect(7, 9);
        connect(8, 9);
    }

    private void connect(int first, int second) {
        edges.add(new Edge(first, second));
        adjacency.get(first).add(second);
        adjacency.get(second).add(first);
    }

    private void resetSearch() {
        discovered = new boolean[NODE_COUNT];
        visitOrder = new int[NODE_COUNT];
        visitedCount = 0;
        currentNode = -1;
        queue.clear();
        queue.add(0);
        discovered[0] = true;
        running = false;
        finished = false;
        frameCounter = 0;
        status = "Start node 1 is in the queue - press SPACE";
    }

    private void advance() {
        if (queue.isEmpty()) {
            running = false;
            finished = true;
            status = "BFS complete - visited " + visitedCount + " nodes";
            return;
        }

        currentNode = queue.remove();
        visitOrder[currentNode] = ++visitedCount;
        for (int neighbor : adjacency.get(currentNode)) {
            if (!discovered[neighbor]) {
                discovered[neighbor] = true;
                queue.add(neighbor);
            }
        }
        status = "Visited node " + (currentNode + 1) + " and queued its new neighbors";
        if (queue.isEmpty()) {
            running = false;
            finished = true;
            status = "BFS complete - visited " + visitedCount + " nodes";
        }
    }

    private void drawHeader() {
        fill(30, 36, 48);
        noStroke();
        rect(0, 0, width, TOP_BAR_HEIGHT);
        fill(241, 245, 249);
        textSize(24);
        text("Breadth-First Search", 24, 31);
        fill(153, 165, 181);
        textSize(14);
        text(status, 24, 56);
        text("SPACE step   A auto   R reset   UP/DOWN speed", width - 310, 27);
        text("Visited: " + visitedCount + "/" + NODE_COUNT + "   Speed: " + stepDelay,
                width - 310, 51);
    }

    private void drawGraph() {
        fill(153, 165, 181);
        textSize(16);
        text("Graph", 24, 112);
        for (Edge edge : edges) {
            Node first = nodes.get(edge.first);
            Node second = nodes.get(edge.second);
            stroke(edgeTouchesCurrent(edge) ? color(246, 183, 76) : color(76, 86, 105));
            strokeWeight(edgeTouchesCurrent(edge) ? 4 : 2);
            line(first.x, first.y, second.x, second.y);
        }
        for (int index = 0; index < nodes.size(); index++) {
            Node node = nodes.get(index);
            if (index == currentNode) {
                fill(246, 183, 76);
            } else if (visitOrder[index] > 0) {
                fill(76, 201, 160);
            } else if (discovered[index]) {
                fill(129, 140, 248);
            } else {
                fill(76, 125, 161);
            }
            stroke(245, 245, 245);
            strokeWeight(2);
            ellipse(node.x, node.y, 48, 48);
            fill(21, 25, 34);
            textAlign(CENTER, CENTER);
            textSize(16);
            text(index + 1, node.x, node.y - 2);
            if (visitOrder[index] > 0) {
                fill(241, 245, 249);
                textSize(11);
                text("#" + visitOrder[index], node.x, node.y + 35);
            }
            textAlign(LEFT, BASELINE);
        }
    }

    private boolean edgeTouchesCurrent(Edge edge) {
        return edge.first == currentNode || edge.second == currentNode;
    }

    private void drawQueue() {
        fill(153, 165, 181);
        textSize(17);
        text("Queue (front to back)", QUEUE_LEFT, 115);
        int index = 0;
        for (int nodeIndex : queue) {
            int x = QUEUE_LEFT + (index % 2) * 115;
            int y = QUEUE_TOP + (index / 2) * 56;
            fill(129, 140, 248);
            stroke(76, 86, 105);
            rect(x, y, 94, 38);
            fill(21, 25, 34);
            textAlign(CENTER, CENTER);
            textSize(15);
            text("Node " + (nodeIndex + 1), x + 47, y + 19);
            textAlign(LEFT, BASELINE);
            index++;
        }
        fill(142, 151, 166);
        textSize(13);
        text("Green: visited    Purple: discovered and waiting    Gold: current node",
                QUEUE_LEFT, 525);
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

        private Edge(int first, int second) {
            this.first = first;
            this.second = second;
        }
    }
}
