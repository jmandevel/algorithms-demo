package com.example.demo.greedy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import processing.core.PApplet;

/**
 * Interactive visual demonstration of Huffman coding.
 *
 * Huffman coding creates short binary codes for common symbols and longer
 * codes for uncommon symbols. It starts with one node for every symbol and
 * its frequency. A min-heap always provides the two least-frequent nodes. The
 * algorithm merges those nodes into a parent whose frequency is their sum,
 * then puts the parent back into the heap.
 *
 * When one tree remains, walking left adds 0 to a code and walking right adds
 * 1. Because every symbol is stored at a leaf, no code is a prefix of another
 * code. This makes Huffman codes safe to decode from left to right. The greedy
 * construction minimizes the total weighted code length for the frequencies.
 *
 * Pseudocode:
 *   put every symbol and frequency into a min-heap
 *   while more than one tree remains:
 *       remove the two least-frequent trees
 *       merge them into a parent tree
 *       put the parent back into the heap
 *   walk from the root to assign 0 for left and 1 for right
 *
 * Controls:
 *   SPACE - merge the next two lowest-frequency trees
 *   A     - toggle automatic stepping
 *   R     - reset the frequency table
 *   UP/DOWN - change automatic step speed
 */
public class HuffmanCodingApplication extends PApplet {

    private static final char[] SYMBOLS = {'A', 'B', 'C', 'D', 'E', 'F'};
    private static final int[] FREQUENCIES = {5, 9, 12, 13, 16, 45};
    private static final int TOP_BAR_HEIGHT = 74;

    private final PriorityQueue<Node> heap = new PriorityQueue<>();
    private final List<Node> mergedTrees = new ArrayList<>();
    private final Map<Character, String> codes = new HashMap<>();
    private int nextId;
    private Node root;
    private Node latestMerge;
    private boolean running;
    private boolean finished;
    private int stepDelay = 18;
    private int frameCounter;
    private String status = "Press SPACE to merge the two lowest frequencies";

    public static void main(String[] args) {
        PApplet.main(HuffmanCodingApplication.class.getName());
    }

    @Override
    public void settings() {
        size(900, 650);
    }

    @Override
    public void setup() {
        surface.setTitle("Huffman Coding Sandbox");
        textFont(createFont("SansSerif", 16));
        resetCoding();
    }

    @Override
    public void draw() {
        background(21, 25, 34);
        drawHeader();
        drawFrequencyTable();
        drawHeap();
        drawTree();
        drawCodes();
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
            resetCoding();
        } else if (keyCode == UP) {
            stepDelay = max(3, stepDelay - 3);
        } else if (keyCode == DOWN) {
            stepDelay = min(80, stepDelay + 3);
        }
    }

    private void resetCoding() {
        heap.clear();
        mergedTrees.clear();
        codes.clear();
        root = null;
        latestMerge = null;
        nextId = 0;
        running = false;
        finished = false;
        frameCounter = 0;
        for (int index = 0; index < SYMBOLS.length; index++) {
            heap.add(new Node(SYMBOLS[index], FREQUENCIES[index], nextId++));
        }
        status = "Frequency nodes added to the min-heap - press SPACE";
    }

    private void advance() {
        if (heap.size() <= 1) {
            root = heap.peek();
            if (root != null) {
                assignCodes(root, "");
            }
            running = false;
            finished = true;
            status = "Complete - codes assigned from the Huffman tree";
            return;
        }

        Node first = heap.remove();
        Node second = heap.remove();
        Node parent = new Node('\0', first.frequency + second.frequency, nextId++);
        parent.left = first;
        parent.right = second;
        heap.add(parent);
        latestMerge = parent;
        mergedTrees.add(parent);
        status = "Merged " + label(first) + " (" + first.frequency + ") and "
                + label(second) + " (" + second.frequency + ") into " + parent.frequency;
    }

    private void assignCodes(Node node, String code) {
        if (node.isLeaf()) {
            codes.put(node.symbol, code.isEmpty() ? "0" : code);
            return;
        }
        assignCodes(node.left, code + "0");
        assignCodes(node.right, code + "1");
    }

    private String label(Node node) {
        return node.isLeaf() ? String.valueOf(node.symbol) : "tree";
    }

    private void drawHeader() {
        fill(30, 36, 48);
        noStroke();
        rect(0, 0, width, TOP_BAR_HEIGHT);
        fill(241, 245, 249);
        textSize(24);
        text("Huffman Coding", 24, 31);
        fill(153, 165, 181);
        textSize(14);
        text(status, 24, 56);
        text("SPACE step   A auto   R reset   UP/DOWN speed", width - 310, 27);
        text("Trees in heap: " + heap.size() + "   Speed: " + stepDelay, width - 310, 51);
    }

    private void drawFrequencyTable() {
        fill(153, 165, 181);
        textSize(16);
        text("Symbol frequencies", 28, 112);
        for (int index = 0; index < SYMBOLS.length; index++) {
            int x = 28 + index * 72;
            fill(76, 125, 161);
            stroke(76, 86, 105);
            rect(x, 130, 58, 55);
            fill(241, 245, 249);
            textAlign(CENTER, CENTER);
            textSize(18);
            text(SYMBOLS[index], x + 29, 147);
            textSize(12);
            text(FREQUENCIES[index], x + 29, 169);
            textAlign(LEFT, BASELINE);
        }
    }

    private void drawHeap() {
        fill(153, 165, 181);
        textSize(16);
        text("Min-heap roots", 510, 112);
        List<Node> roots = new ArrayList<>(heap);
        roots.sort(null);
        for (int index = 0; index < roots.size(); index++) {
            Node node = roots.get(index);
            int x = 510 + (index % 3) * 112;
            int y = 130 + (index / 3) * 55;
            fill(index < 2 && !finished ? color(246, 183, 76) : color(76, 125, 161));
            stroke(76, 86, 105);
            rect(x, y, 96, 36);
            fill(241, 245, 249);
            textAlign(CENTER, CENTER);
            textSize(13);
            text(label(node) + ": " + node.frequency, x + 48, y + 18);
            textAlign(LEFT, BASELINE);
        }
    }

    private void drawTree() {
        fill(153, 165, 181);
        textSize(16);
        text("Latest merged tree", 55, 260);
        if (latestMerge == null) {
            fill(142, 151, 166);
            textSize(14);
            text("The first merge will appear here", 55, 300);
            return;
        }
        drawTreeNode(latestMerge, 230, 330, 115);
    }

    private void drawTreeNode(Node node, float x, float y, float gap) {
        if (node.left != null) {
            stroke(76, 86, 105);
            line(x, y, x - gap, y + 75);
            drawTreeNode(node.left, x - gap, y + 75, gap * 0.55f);
        }
        if (node.right != null) {
            stroke(76, 86, 105);
            line(x, y, x + gap, y + 75);
            drawTreeNode(node.right, x + gap, y + 75, gap * 0.55f);
        }
        fill(node.isLeaf() ? color(76, 201, 160) : color(246, 183, 76));
        stroke(245, 245, 245);
        ellipse(x, y, 42, 42);
        fill(21, 25, 34);
        textAlign(CENTER, CENTER);
        textSize(13);
        text(node.isLeaf() ? String.valueOf(node.symbol) : String.valueOf(node.frequency), x, y);
        textAlign(LEFT, BASELINE);
    }

    private void drawCodes() {
        fill(153, 165, 181);
        textSize(16);
        text("Generated codes", 610, 330);
        for (int index = 0; index < SYMBOLS.length; index++) {
            char symbol = SYMBOLS[index];
            fill(codes.containsKey(symbol) ? color(76, 201, 160) : color(45, 53, 68));
            noStroke();
            rect(610, 350 + index * 38, 190, 30);
            fill(241, 245, 249);
            textSize(14);
            text(symbol + "   " + (codes.containsKey(symbol) ? codes.get(symbol) : "..."),
                    625, 370 + index * 38);
        }
        fill(142, 151, 166);
        textSize(12);
        text("Gold: newest merged parent    Green: completed code", 610, 600);
    }

    private static final class Node implements Comparable<Node> {
        private final char symbol;
        private final int frequency;
        private final int id;
        private Node left;
        private Node right;

        private Node(char symbol, int frequency, int id) {
            this.symbol = symbol;
            this.frequency = frequency;
            this.id = id;
        }

        private boolean isLeaf() {
            return left == null && right == null;
        }

        @Override
        public int compareTo(Node other) {
            int frequencyComparison = Integer.compare(frequency, other.frequency);
            return frequencyComparison == 0 ? Integer.compare(id, other.id) : frequencyComparison;
        }
    }
}
