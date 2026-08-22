package com.example.demo.data_structures;

import java.util.ArrayList;
import java.util.List;

import processing.core.PApplet;

/**
 * Interactive visual demonstration of a trie, also called a prefix tree.
 *
 * A trie stores strings one character at a time. Words with the same prefix
 * share the same path from the root, so searching for a word takes O(L) time,
 * where L is the word's length. The search time depends on the word length,
 * not on how many other words are stored.
 *
 * Every node represents a prefix. A terminal node marks the end of a complete
 * word, which allows a word and a longer word with the same prefix to coexist.
 * For example, CAT and CATALOG share the path C -> A -> T, but only their final
 * nodes are marked as complete words.
 *
 * Pseudocode:
 *   insert(word):
 *       start at the root
 *       for each character:
 *           follow its child, creating the child if needed
 *       mark the final node as a complete word
 *
 *   search(word):
 *       start at the root
 *       follow one child for each character
 *       fail if a child is missing; succeed if the final node is a word
 *
 * Controls:
 *   T     - choose the next sample word
 *   S     - search for the selected word
 *   I     - insert the selected word
 *   SPACE - advance one character step
 *   A     - toggle automatic stepping
 *   R     - reset the trie
 *   UP/DOWN - change automatic step speed
 */
public class TrieApplication extends PApplet {

    private static final String[] SAMPLE_WORDS = {"CAT", "CAR", "CAN", "CART", "DOG", "DOT", "DO"};
    private static final int TOP_BAR_HEIGHT = 74;
    private static final int GRAPH_LEFT = 55;
    private static final int GRAPH_TOP = 145;
    private static final int LEVEL_GAP = 82;
    private static final int NODE_DIAMETER = 42;

    private TrieNode root;
    private final List<TrieNode> searchPath = new ArrayList<>();
    private int selectedWordIndex;
    private String selectedWord = SAMPLE_WORDS[0];
    private String operation = "idle";
    private int characterIndex;
    private TrieNode currentNode;
    private boolean found;
    private boolean running;
    private boolean finished;
    private int leafCursor;
    private int stepDelay = 18;
    private int frameCounter;
    private String status = "Selected word: CAT - press S to search";

    public static void main(String[] args) {
        PApplet.main(TrieApplication.class.getName());
    }

    @Override
    public void settings() {
        size(900, 650);
    }

    @Override
    public void setup() {
        surface.setTitle("Trie Sandbox");
        textFont(createFont("SansSerif", 16));
        resetTrie();
    }

    @Override
    public void draw() {
        background(21, 25, 34);
        drawHeader();
        drawTrie();
        drawWordPanel();
        if (running && frameCounter++ % stepDelay == 0) {
            advance();
        }
    }

    @Override
    public void keyPressed() {
        if (key == ' ' && ("search".equals(operation) || "insert".equals(operation)) && !finished) {
            advance();
        } else if (key == 'a' || key == 'A') {
            if (!finished && !"idle".equals(operation)) {
                running = !running;
                status = running ? "Running automatically" : "Paused - press SPACE to continue";
            }
        } else if (key == 't' || key == 'T') {
            selectedWordIndex = (selectedWordIndex + 1) % SAMPLE_WORDS.length;
            selectedWord = SAMPLE_WORDS[selectedWordIndex];
            resetOperation();
            status = "Selected word: " + selectedWord + " - press S to search";
        } else if (key == 's' || key == 'S') {
            beginSearch();
        } else if (key == 'i' || key == 'I') {
            beginInsert();
        } else if (key == 'r' || key == 'R') {
            resetTrie();
        } else if (keyCode == UP) {
            stepDelay = max(3, stepDelay - 3);
        } else if (keyCode == DOWN) {
            stepDelay = min(80, stepDelay + 3);
        }
    }

    private void resetTrie() {
        root = new TrieNode('\0');
        for (String word : SAMPLE_WORDS) {
            insertWord(word);
        }
        selectedWordIndex = 0;
        selectedWord = SAMPLE_WORDS[0];
        resetOperation();
        status = "Selected word: CAT - press S to search";
    }

    private void resetOperation() {
        operation = "idle";
        characterIndex = 0;
        currentNode = null;
        searchPath.clear();
        found = false;
        running = false;
        finished = false;
        frameCounter = 0;
    }

    private void beginSearch() {
        if (running) {
            return;
        }
        operation = "search";
        characterIndex = 0;
        currentNode = root;
        searchPath.clear();
        searchPath.add(root);
        found = false;
        finished = false;
        status = "Searching for " + selectedWord + " from the root - press SPACE";
    }

    private void beginInsert() {
        if (running) {
            return;
        }
        operation = "insert";
        characterIndex = 0;
        currentNode = root;
        searchPath.clear();
        searchPath.add(root);
        found = false;
        finished = false;
        status = "Inserting " + selectedWord + " from the root - press SPACE";
    }

    private void advance() {
        if (characterIndex >= selectedWord.length()) {
            if ("insert".equals(operation)) {
                currentNode.wordEnd = true;
                found = true;
                status = "Inserted " + selectedWord + " and marked its final node";
            } else {
                found = currentNode.wordEnd;
                status = found ? "Found complete word " + selectedWord : "Prefix found, but not a complete word";
            }
            running = false;
            finished = true;
            return;
        }

        int childIndex = selectedWord.charAt(characterIndex) - 'A';
        TrieNode child = currentNode.children[childIndex];
        if (child == null) {
            if ("insert".equals(operation)) {
                child = new TrieNode(selectedWord.charAt(characterIndex));
                currentNode.children[childIndex] = child;
                status = "Created node for '" + child.character + "'";
            } else {
                status = "Missing node for '" + selectedWord.charAt(characterIndex) + "' - word not found";
                running = false;
                finished = true;
                return;
            }
        } else {
            status = "Followed the existing node for '" + child.character + "'";
        }
        currentNode = child;
        searchPath.add(currentNode);
        characterIndex++;
    }

    private void insertWord(String word) {
        TrieNode node = root;
        for (int index = 0; index < word.length(); index++) {
            int childIndex = word.charAt(index) - 'A';
            if (node.children[childIndex] == null) {
                node.children[childIndex] = new TrieNode(word.charAt(index));
            }
            node = node.children[childIndex];
        }
        node.wordEnd = true;
    }

    private void drawHeader() {
        fill(30, 36, 48);
        noStroke();
        rect(0, 0, width, TOP_BAR_HEIGHT);
        fill(241, 245, 249);
        textSize(24);
        text("Trie", 24, 31);
        fill(153, 165, 181);
        textSize(14);
        text(status, 24, 56);
        text("T word   S search   I insert   SPACE step   A auto", width - 350, 27);
        text("Selected: " + selectedWord + "   Speed: " + stepDelay, width - 350, 51);
    }

    private void drawTrie() {
        fill(153, 165, 181);
        textSize(17);
        text("Prefix tree", GRAPH_LEFT, 112);
        leafCursor = 0;
        layout(root, 0);
        drawEdges(root);
        drawNodes(root);
    }

    private float layout(TrieNode node, int depth) {
        List<TrieNode> children = childrenOf(node);
        if (children.isEmpty()) {
            node.x = GRAPH_LEFT + leafCursor++ * 76;
        } else {
            float firstX = layout(children.get(0), depth + 1);
            float lastX = firstX;
            for (int index = 1; index < children.size(); index++) {
                lastX = layout(children.get(index), depth + 1);
            }
            node.x = (firstX + lastX) / 2f;
        }
        node.y = GRAPH_TOP + depth * LEVEL_GAP;
        return node.x;
    }

    private List<TrieNode> childrenOf(TrieNode node) {
        List<TrieNode> children = new ArrayList<>();
        for (TrieNode child : node.children) {
            if (child != null) {
                children.add(child);
            }
        }
        return children;
    }

    private void drawEdges(TrieNode node) {
        for (TrieNode child : childrenOf(node)) {
            stroke(searchPath.contains(child) ? color(246, 183, 76) : color(76, 86, 105));
            strokeWeight(searchPath.contains(child) ? 4 : 2);
            line(node.x, node.y, child.x, child.y);
            drawEdges(child);
        }
    }

    private void drawNodes(TrieNode node) {
        boolean active = searchPath.contains(node);
        fill(node == currentNode ? color(246, 183, 76)
                : node.wordEnd ? color(76, 201, 160)
                : active ? color(129, 140, 248) : color(76, 125, 161));
        stroke(245, 245, 245);
        strokeWeight(2);
        ellipse(node.x, node.y, NODE_DIAMETER, NODE_DIAMETER);
        fill(21, 25, 34);
        textAlign(CENTER, CENTER);
        textSize(16);
        text(node == root ? "root" : String.valueOf(node.character), node.x, node.y);
        textAlign(LEFT, BASELINE);
        for (TrieNode child : childrenOf(node)) {
            drawNodes(child);
        }
    }

    private void drawWordPanel() {
        fill(153, 165, 181);
        textSize(17);
        text("Words in the trie", 640, 115);
        for (int index = 0; index < SAMPLE_WORDS.length; index++) {
            fill(SAMPLE_WORDS[index].equals(selectedWord) ? color(246, 183, 76) : color(76, 125, 161));
            noStroke();
            rect(640, 135 + index * 35, 150, 27);
            fill(241, 245, 249);
            textSize(14);
            text(SAMPLE_WORDS[index], 655, 153 + index * 35);
        }
        fill(142, 151, 166);
        textSize(12);
        text("Purple/gold: current search path", 640, 410);
        text("Green: complete word", 640, 430);
        text("Gold node: current character", 640, 450);
        if (finished) {
            fill(found ? color(76, 201, 160) : color(239, 102, 102));
            textSize(16);
            text(found ? "Result: word found" : "Result: word not found", 640, 495);
        }
    }

    private static final class TrieNode {
        private final char character;
        private final TrieNode[] children = new TrieNode[26];
        private boolean wordEnd;
        private float x;
        private float y;

        private TrieNode(char character) {
            this.character = character;
        }
    }
}
