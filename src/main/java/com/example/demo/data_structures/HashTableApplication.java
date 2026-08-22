package com.example.demo.data_structures;

import java.util.ArrayList;
import java.util.List;

import processing.core.PApplet;

/**
 * Interactive visual demonstration of a hash table using separate chaining.
 *
 * A hash table stores key-value pairs by turning each key into an integer hash,
 * then using that hash to choose a bucket. This demo uses:
 *
 *   bucket = abs(key.hashCode()) % BUCKET_COUNT
 *
 * Different keys can produce the same bucket. That is called a collision. With
 * separate chaining, each bucket holds a linked list of entries, so colliding
 * keys can share a bucket without replacing one another. Average lookup,
 * insertion, and removal are O(1) when the table distributes keys evenly; a
 * badly distributed table can degrade to O(n).
 *
 * Pseudocode:
 *   hash the key to choose a bucket
 *   if searching or removing:
 *       walk that bucket's chain until the key is found or the chain ends
 *   if inserting:
 *       add a new entry to the bucket's chain
 *
 * Controls:
 *   S     - search for the selected key
 *   I     - insert the selected key
 *   T     - choose the next sample key
 *   SPACE - advance one operation step
 *   A     - toggle automatic stepping
 *   R     - reset the table
 *   Mouse - click an entry to select its key
 *   UP/DOWN - change automatic step speed
 */
public class HashTableApplication extends PApplet {

    private static final int BUCKET_COUNT = 7;
    private static final int BUCKET_LEFT = 80;
    private static final int BUCKET_TOP = 150;
    private static final int BUCKET_WIDTH = 100;
    private static final int ENTRY_WIDTH = 116;
    private static final int ENTRY_HEIGHT = 42;
    private static final int ENTRY_GAP = 12;

    private final String[] sampleKeys = {"JAVA", "GRAPH", "TREE", "SORT", "QUEUE", "STACK", "HASH", "NODE"};
    private final List<List<Entry>> buckets = new ArrayList<>();
    private int sampleIndex;
    private String selectedKey = sampleKeys[0];
    private String operation = "idle";
    private int targetBucket;
    private int chainIndex;
    private boolean running;
    private boolean finished;
    private int stepDelay = 18;
    private int frameCounter;
    private String status = "Selected key: JAVA - press S to search or I to insert";

    public HashTableApplication() {
        for (int index = 0; index < BUCKET_COUNT; index++) {
            buckets.add(new ArrayList<>());
        }
    }

    public static void main(String[] args) {
        PApplet.main(HashTableApplication.class.getName());
    }

    @Override
    public void settings() {
        size(900, 650);
    }

    @Override
    public void setup() {
        surface.setTitle("Hash Table Sandbox");
        textFont(createFont("SansSerif", 16));
        resetTable();
    }

    @Override
    public void draw() {
        background(21, 25, 34);
        drawHeader();
        drawTable();
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
        } else if (key == 's' || key == 'S') {
            beginSearch();
        } else if (key == 'i' || key == 'I') {
            beginInsert();
        } else if (key == 't' || key == 'T') {
            sampleIndex = (sampleIndex + 1) % sampleKeys.length;
            selectedKey = sampleKeys[sampleIndex];
            resetOperation();
            status = "Selected key: " + selectedKey + " - press S to search or I to insert";
        } else if (key == 'r' || key == 'R') {
            resetTable();
        } else if (keyCode == UP) {
            stepDelay = max(3, stepDelay - 3);
        } else if (keyCode == DOWN) {
            stepDelay = min(80, stepDelay + 3);
        }
    }

    @Override
    public void mousePressed() {
        for (int bucketIndex = 0; bucketIndex < BUCKET_COUNT; bucketIndex++) {
            List<Entry> chain = buckets.get(bucketIndex);
            for (int entryIndex = 0; entryIndex < chain.size(); entryIndex++) {
                Entry entry = chain.get(entryIndex);
                float x = entryX(bucketIndex, entryIndex);
                float y = entryY(entryIndex);
                if (mouseX >= x && mouseX <= x + ENTRY_WIDTH
                        && mouseY >= y && mouseY <= y + ENTRY_HEIGHT && !running) {
                    selectedKey = entry.key;
                    status = "Selected key: " + selectedKey + " - press S to search or I to insert";
                    return;
                }
            }
        }
    }

    private void resetTable() {
        for (List<Entry> bucket : buckets) {
            bucket.clear();
        }
        String[] initialKeys = {"JAVA", "GRAPH", "TREE", "SORT", "QUEUE", "STACK", "NODE"};
        for (String key : initialKeys) {
            addEntry(key);
        }
        sampleIndex = 0;
        selectedKey = sampleKeys[0];
        resetOperation();
        status = "Selected key: JAVA - press S to search or I to insert";
    }

    private void resetOperation() {
        operation = "idle";
        targetBucket = -1;
        chainIndex = -1;
        running = false;
        finished = false;
        frameCounter = 0;
    }

    private void beginSearch() {
        if (running) {
            return;
        }
        operation = "search";
        targetBucket = bucketFor(selectedKey);
        chainIndex = 0;
        finished = false;
        status = "Hashed " + selectedKey + " to bucket " + targetBucket + " - press SPACE to inspect";
    }

    private void beginInsert() {
        if (running) {
            return;
        }
        operation = "insert";
        targetBucket = bucketFor(selectedKey);
        chainIndex = 0;
        finished = false;
        status = "Hashed " + selectedKey + " to bucket " + targetBucket + " - press SPACE to insert";
    }

    private void advance() {
        List<Entry> chain = buckets.get(targetBucket);
        if (chainIndex >= chain.size()) {
            if ("insert".equals(operation)) {
                addEntry(selectedKey);
                status = "Inserted " + selectedKey + " at the end of bucket " + targetBucket;
            } else {
                status = selectedKey + " was not found in bucket " + targetBucket;
            }
            running = false;
            finished = true;
            return;
        }

        Entry entry = chain.get(chainIndex);
        if (entry.key.equals(selectedKey)) {
            status = "Found " + selectedKey + " in bucket " + targetBucket;
            running = false;
            finished = true;
            return;
        }
        status = "Compared " + selectedKey + " with " + entry.key + " - follow the chain";
        chainIndex++;
    }

    private void addEntry(String key) {
        int bucket = bucketFor(key);
        for (Entry entry : buckets.get(bucket)) {
            if (entry.key.equals(key)) {
                return;
            }
        }
        buckets.get(bucket).add(new Entry(key, key.length() * 11));
    }

    private int bucketFor(String key) {
        return Math.floorMod(key.hashCode(), BUCKET_COUNT);
    }

    private void drawHeader() {
        fill(30, 36, 48);
        noStroke();
        rect(0, 0, width, 74);
        fill(241, 245, 249);
        textSize(24);
        text("Hash Table", 24, 31);
        fill(153, 165, 181);
        textSize(14);
        text(status, 24, 56);
        text("S search   I insert   T key   SPACE step   A auto", width - 350, 27);
        text("Selected: " + selectedKey + "   Speed: " + stepDelay, width - 350, 51);
    }

    private void drawTable() {
        fill(153, 165, 181);
        textSize(16);
        text("Buckets and collision chains", BUCKET_LEFT, 112);
        for (int bucketIndex = 0; bucketIndex < BUCKET_COUNT; bucketIndex++) {
            int x = BUCKET_LEFT + bucketIndex * BUCKET_WIDTH;
            fill(bucketIndex == targetBucket ? color(246, 183, 76) : color(45, 53, 68));
            stroke(76, 86, 105);
            rect(x, BUCKET_TOP, BUCKET_WIDTH - 8, 42);
            fill(241, 245, 249);
            textAlign(CENTER, CENTER);
            textSize(16);
            text("Bucket " + bucketIndex, x + (BUCKET_WIDTH - 8) / 2f, BUCKET_TOP + 21);
            textAlign(LEFT, BASELINE);

            List<Entry> chain = buckets.get(bucketIndex);
            for (int entryIndex = 0; entryIndex < chain.size(); entryIndex++) {
                Entry entry = chain.get(entryIndex);
                float entryX = entryX(bucketIndex, entryIndex);
                float entryY = entryY(entryIndex);
                boolean active = bucketIndex == targetBucket && entryIndex == chainIndex;
                fill(active ? color(246, 183, 76) : color(76, 125, 161));
                stroke(76, 86, 105);
                rect(entryX, entryY, ENTRY_WIDTH, ENTRY_HEIGHT);
                fill(241, 245, 249);
                textAlign(CENTER, CENTER);
                textSize(14);
                text(entry.key + " = " + entry.value, entryX + ENTRY_WIDTH / 2f, entryY + ENTRY_HEIGHT / 2f);
                textAlign(LEFT, BASELINE);
                if (entryIndex < chain.size() - 1) {
                    stroke(153, 165, 181);
                    line(entryX + ENTRY_WIDTH / 2f, entryY + ENTRY_HEIGHT,
                            entryX + ENTRY_WIDTH / 2f, entryY + ENTRY_HEIGHT + ENTRY_GAP);
                }
            }
        }
        fill(142, 151, 166);
        textSize(13);
        text("Gold: active bucket or comparison    Blue: stored key-value entry    Same bucket = collision chain",
                BUCKET_LEFT, 590);
    }

    private float entryX(int bucketIndex, int entryIndex) {
        return BUCKET_LEFT + bucketIndex * BUCKET_WIDTH - (ENTRY_WIDTH - BUCKET_WIDTH) / 2f;
    }

    private float entryY(int entryIndex) {
        return BUCKET_TOP + 58 + entryIndex * (ENTRY_HEIGHT + ENTRY_GAP);
    }

    private static final class Entry {
        private final String key;
        private final int value;

        private Entry(String key, int value) {
            this.key = key;
            this.value = value;
        }
    }
}
