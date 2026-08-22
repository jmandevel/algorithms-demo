package com.example.demo.greedy;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import processing.core.PApplet;

/**
 * Interactive visual demonstration of the activity selection algorithm.
 *
 * The goal is to select as many non-overlapping activities as possible. Each
 * activity has a start and finish time. The greedy strategy sorts activities
 * by finish time, then accepts the next activity whose start is not before the
 * finish of the last accepted activity.
 *
 * Choosing the activity that finishes earliest leaves the most time available
 * for future activities. This produces an optimal maximum-size schedule when
 * all activities have equal value. Sorting takes O(n log n) time, followed by
 * one O(n) selection pass.
 *
 * Pseudocode:
 *   sort activities by finish time
 *   lastFinish = negative infinity
 *   for each activity in sorted order:
 *       if activity.start >= lastFinish:
 *           select the activity
 *           lastFinish = activity.finish
 *
 * Controls:
 *   SPACE - inspect the next activity
 *   A     - toggle automatic stepping
 *   R     - reset and sort the activities again
 *   UP/DOWN - change automatic step speed
 */
public class ActivitySelectionApplication extends PApplet {

    private static final int TOP_BAR_HEIGHT = 74;
    private static final int TIMELINE_LEFT = 100;
    private static final int TIMELINE_TOP = 150;
    private static final int ROW_HEIGHT = 42;
    private static final int TIME_WIDTH = 55;
    private static final int TIME_COUNT = 14;

    private final List<Activity> activities = new ArrayList<>();
    private boolean[] accepted;
    private boolean[] rejected;
    private int currentIndex;
    private int lastFinish;
    private boolean running;
    private boolean finished;
    private int stepDelay = 18;
    private int frameCounter;
    private String status = "Press SPACE to choose the earliest-finishing activity";

    public static void main(String[] args) {
        PApplet.main(ActivitySelectionApplication.class.getName());
    }

    @Override
    public void settings() {
        size(900, 650);
    }

    @Override
    public void setup() {
        surface.setTitle("Activity Selection Sandbox");
        textFont(createFont("SansSerif", 16));
        createActivities();
        resetSchedule();
    }

    @Override
    public void draw() {
        background(21, 25, 34);
        drawHeader();
        drawTimeline();
        drawSchedule();
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
            resetSchedule();
        } else if (keyCode == UP) {
            stepDelay = max(3, stepDelay - 3);
        } else if (keyCode == DOWN) {
            stepDelay = min(80, stepDelay + 3);
        }
    }

    private void createActivities() {
        addActivity("A", 1, 4);
        addActivity("B", 3, 5);
        addActivity("C", 0, 6);
        addActivity("D", 5, 7);
        addActivity("E", 3, 8);
        addActivity("F", 5, 9);
        addActivity("G", 6, 10);
        addActivity("H", 8, 11);
        addActivity("I", 8, 12);
        addActivity("J", 2, 13);
    }

    private void addActivity(String name, int start, int finish) {
        activities.add(new Activity(name, start, finish));
    }

    private void resetSchedule() {
        activities.sort(Comparator.comparingInt(activity -> activity.finish));
        accepted = new boolean[activities.size()];
        rejected = new boolean[activities.size()];
        currentIndex = 0;
        lastFinish = -1;
        running = false;
        finished = false;
        frameCounter = 0;
        status = "Sorted by finish time - press SPACE to start";
    }

    private void advance() {
        if (currentIndex >= activities.size()) {
            running = false;
            finished = true;
            status = "Complete - selected " + countAccepted() + " non-overlapping activities";
            return;
        }

        Activity activity = activities.get(currentIndex);
        if (activity.start >= lastFinish) {
            accepted[currentIndex] = true;
            lastFinish = activity.finish;
            status = "Accepted " + activity.name + " - it starts after time " + (lastFinish - (activity.finish - activity.start));
        } else {
            rejected[currentIndex] = true;
            status = "Rejected " + activity.name + " - it overlaps the current schedule";
        }
        currentIndex++;
        if (currentIndex == activities.size()) {
            running = false;
            finished = true;
            status = "Complete - selected " + countAccepted() + " non-overlapping activities";
        }
    }

    private int countAccepted() {
        int count = 0;
        for (boolean selected : accepted) {
            if (selected) {
                count++;
            }
        }
        return count;
    }

    private void drawHeader() {
        fill(30, 36, 48);
        noStroke();
        rect(0, 0, width, TOP_BAR_HEIGHT);
        fill(241, 245, 249);
        textSize(24);
        text("Activity Selection", 24, 31);
        fill(153, 165, 181);
        textSize(14);
        text(status, 24, 56);
        text("SPACE step   A auto   R reset   UP/DOWN speed", width - 310, 27);
        text("Selected: " + countAccepted() + "   Speed: " + stepDelay, width - 310, 51);
    }

    private void drawTimeline() {
        fill(153, 165, 181);
        textSize(16);
        text("Activities sorted by finish time", TIMELINE_LEFT, 112);
        for (int time = 0; time <= TIME_COUNT; time++) {
            int x = TIMELINE_LEFT + time * TIME_WIDTH;
            stroke(56, 66, 82);
            line(x, TIMELINE_TOP - 15, x, TIMELINE_TOP + activities.size() * ROW_HEIGHT);
            fill(153, 165, 181);
            textSize(11);
            textAlign(CENTER, CENTER);
            text(time, x, TIMELINE_TOP - 28);
        }
        textAlign(LEFT, BASELINE);
    }

    private void drawSchedule() {
        for (int index = 0; index < activities.size(); index++) {
            Activity activity = activities.get(index);
            int y = TIMELINE_TOP + index * ROW_HEIGHT;
            boolean current = index == currentIndex && !finished;
            if (accepted[index]) {
                fill(76, 201, 160);
            } else if (rejected[index]) {
                fill(239, 102, 102);
            } else if (current) {
                fill(246, 183, 76);
            } else {
                fill(76, 125, 161);
            }
            noStroke();
            rect(TIMELINE_LEFT + activity.start * TIME_WIDTH, y,
                    (activity.finish - activity.start) * TIME_WIDTH, 28);
            fill(21, 25, 34);
            textSize(14);
            textAlign(CENTER, CENTER);
            text(activity.name, TIMELINE_LEFT + (activity.start + activity.finish) * TIME_WIDTH / 2f, y + 14);
            textAlign(LEFT, BASELINE);
            fill(153, 165, 181);
            textSize(11);
            text(activity.start + " - " + activity.finish, TIMELINE_LEFT + TIME_COUNT * TIME_WIDTH + 20, y + 17);
        }
        fill(142, 151, 166);
        textSize(13);
        text("Green: selected    Red: overlaps    Gold: current activity    Times shown at right",
                TIMELINE_LEFT, 610);
    }

    private static final class Activity {
        private final String name;
        private final int start;
        private final int finish;

        private Activity(String name, int start, int finish) {
            this.name = name;
            this.start = start;
            this.finish = finish;
        }
    }
}
