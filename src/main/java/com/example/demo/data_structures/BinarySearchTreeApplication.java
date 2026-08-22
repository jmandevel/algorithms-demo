package com.example.demo.data_structures;

import java.util.List;

import processing.core.PApplet;

/**
 * Interactive visual demonstration of a binary search tree.
 *
 * A binary search tree stores values in an ordered structure. Every value in
 * the left subtree is smaller than its parent, and every value in the right
 * subtree is larger. This rule lets search follow one branch at each step
 * instead of checking every value.
 *
 * To search for a target, begin at the root. If the target is smaller, move
 * left; if it is larger, move right; otherwise the target has been found. A
 * balanced tree can search in O(log n) time, but an unbalanced tree can take
 * O(n) time and behave like a linked list.
 *
 * Pseudocode:
 *   search(node, target):
 *       if node is empty, report "not found"
 *       if node.value equals target, report "found"
 *       if target is smaller, search(node.left, target)
 *       otherwise, search(node.right, target)
 *
 * Controls:
 *   Mouse - click a node to select its value as the target
 *   +/- - adjust the target by one
 *   S     - start a search for the target
 *   I     - insert the target into the tree
 *   SPACE - advance one operation step
 *   A     - toggle automatic stepping
 *   R     - reset the tree
 *   UP/DOWN - change automatic step speed
 */
public class BinarySearchTreeApplication extends PApplet {

    private static final int TOP_BAR_HEIGHT = 74;
    private static final int ROOT_X = 450;
    private static final int ROOT_Y = 145;
    private static final int LEVEL_GAP = 78;
    private static final int NODE_DIAMETER = 42;

    private final List<Integer> startingValues = List.of(50, 25, 75, 12, 37, 62, 88, 6, 18, 31, 44);
    private Node root;
    private Node current;
    private Node lastInserted;
    private int target = 44;
    private boolean searching;
    private boolean inserting;
    private boolean running;
    private boolean finished;
    private int stepDelay = 18;
    private int frameCounter;
    private String status = "Target: 44 - press S to search";

    public static void main(String[] args) {
        PApplet.main(BinarySearchTreeApplication.class.getName());
    }

    @Override
    public void settings() {
        size(900, 650);
    }

    @Override
    public void setup() {
        surface.setTitle("Binary Search Tree Sandbox");
        textFont(createFont("SansSerif", 16));
        resetTree();
    }

    @Override
    public void draw() {
        background(21, 25, 34);
        drawHeader();
        drawTree(root, ROOT_X, ROOT_Y, 220);

        if (running && frameCounter++ % stepDelay == 0) {
            advance();
        }
    }

    @Override
    public void keyPressed() {
        if (key == ' ' && (searching || inserting) && !finished) {
            advance();
        } else if (key == 'a' || key == 'A') {
            if ((searching || inserting) && !finished) {
                running = !running;
                status = running ? "Running automatically" : "Paused - press SPACE to continue";
            }
        } else if (key == 's' || key == 'S') {
            beginSearch();
        } else if (key == 'i' || key == 'I') {
            beginInsert();
        } else if (key == 'r' || key == 'R') {
            resetTree();
        } else if ((key == '+' || key == '=') && !running) {
            target++;
            resetOperation();
        } else if (key == '-' && !running) {
            target--;
            resetOperation();
        } else if (keyCode == UP) {
            stepDelay = max(3, stepDelay - 3);
        } else if (keyCode == DOWN) {
            stepDelay = min(80, stepDelay + 3);
        }
    }

    @Override
    public void mousePressed() {
        Node clicked = findNode(root, mouseX, mouseY);
        if (clicked != null && !running) {
            target = clicked.value;
            resetOperation();
        }
    }

    private void resetTree() {
        root = null;
        for (int value : startingValues) {
            insertValue(value);
        }
        target = 44;
        resetOperation();
        status = "Target: 44 - press S to search";
    }

    private void resetOperation() {
        current = null;
        lastInserted = null;
        searching = false;
        inserting = false;
        running = false;
        finished = false;
        frameCounter = 0;
        status = "Target: " + target + " - press S to search or I to insert";
    }

    private void beginSearch() {
        if (running) {
            return;
        }
        current = root;
        searching = true;
        inserting = false;
        finished = false;
        status = "Searching for " + target + " from the root";
    }

    private void beginInsert() {
        if (running) {
            return;
        }
        current = root;
        searching = false;
        inserting = true;
        finished = false;
        status = "Finding the insertion point for " + target;
    }

    private void advance() {
        if (current == null) {
            running = false;
            finished = true;
            if (inserting) {
                insertValue(target);
                lastInserted = findNode(root, target, true);
                status = "Inserted " + target + " into the tree";
            } else {
                status = "Target " + target + " was not found";
            }
            return;
        }

        if (current.value == target) {
            running = false;
            finished = true;
            if (inserting) {
                status = "Target already exists - no duplicate inserted";
            } else {
                status = "Found target " + target;
            }
            return;
        }

        boolean moveLeft = target < current.value;
        status = current.value + (moveLeft ? " is larger - move left" : " is smaller - move right");
        current = moveLeft ? current.left : current.right;
    }

    private void insertValue(int value) {
        if (root == null) {
            root = new Node(value);
            return;
        }
        Node node = root;
        while (true) {
            if (value == node.value) {
                return;
            }
            if (value < node.value) {
                if (node.left == null) {
                    node.left = new Node(value);
                    return;
                }
                node = node.left;
            } else {
                if (node.right == null) {
                    node.right = new Node(value);
                    return;
                }
                node = node.right;
            }
        }
    }

    private Node findNode(Node node, int value, boolean exact) {
        while (node != null) {
            if (node.value == value) {
                return node;
            }
            node = value < node.value ? node.left : node.right;
        }
        return exact ? null : null;
    }

    private Node findNode(Node node, int mouseX, int mouseY) {
        if (node == null) {
            return null;
        }
        if (dist(mouseX, mouseY, node.screenX, node.screenY) < NODE_DIAMETER / 2f) {
            return node;
        }
        Node leftResult = findNode(node.left, mouseX, mouseY);
        return leftResult == null ? findNode(node.right, mouseX, mouseY) : leftResult;
    }

    private void drawHeader() {
        fill(30, 36, 48);
        noStroke();
        rect(0, 0, width, TOP_BAR_HEIGHT);
        fill(241, 245, 249);
        textSize(24);
        text("Binary Search Tree", 24, 31);
        fill(153, 165, 181);
        textSize(14);
        text(status, 24, 56);
        text("S search   I insert   SPACE step   A auto   R reset", width - 330, 27);
        text("Target: " + target + "   Speed: " + stepDelay, width - 330, 51);
    }

    private void drawTree(Node node, float x, float y, float horizontalGap) {
        if (node == null) {
            return;
        }
        node.screenX = x;
        node.screenY = y;
        if (node.left != null) {
            stroke(76, 86, 105);
            strokeWeight(2);
            line(x, y, x - horizontalGap, y + LEVEL_GAP);
            drawTree(node.left, x - horizontalGap, y + LEVEL_GAP, horizontalGap * 0.55f);
        }
        if (node.right != null) {
            stroke(76, 86, 105);
            strokeWeight(2);
            line(x, y, x + horizontalGap, y + LEVEL_GAP);
            drawTree(node.right, x + horizontalGap, y + LEVEL_GAP, horizontalGap * 0.55f);
        }

        if (node == lastInserted) {
            fill(246, 183, 76);
        } else if (node == current) {
            fill(239, 102, 102);
        } else if (node.value == target) {
            fill(76, 201, 160);
        } else {
            fill(76, 125, 161);
        }
        stroke(245, 245, 245);
        strokeWeight(2);
        ellipse(x, y, NODE_DIAMETER, NODE_DIAMETER);
        fill(21, 25, 34);
        textAlign(CENTER, CENTER);
        textSize(14);
        text(node.value, x, y - 1);
        textAlign(LEFT, BASELINE);
    }

    private static final class Node {
        private final int value;
        private Node left;
        private Node right;
        private float screenX;
        private float screenY;

        private Node(int value) {
            this.value = value;
        }
    }
}
