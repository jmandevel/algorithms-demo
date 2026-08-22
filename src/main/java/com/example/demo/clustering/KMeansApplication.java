package com.example.demo.clustering;

import java.util.Random;

import processing.core.PApplet;

/**
 * Interactive visual demonstration of the K-means clustering algorithm.
 *
 * K-means groups points into a chosen number of clusters. It starts with one
 * center for each cluster, assigns every point to its nearest center, then
 * moves each center to the average position of the points assigned to it.
 * These two steps repeat until the assignments stop changing.
 *
 * The point-to-center distance uses the Euclidean point distance formula:
 *
 *   distance = sqrt((x2 - x1)^2 + (y2 - y1)^2)
 *
 * K-means is useful for discovering groups in data when the groups are not
 * labeled ahead of time. The result depends on the initial centers, so this
 * sketch can be reset to try a new starting arrangement. The algorithm can
 * also settle on a local solution rather than the best possible grouping.
 *
 * Pseudocode:
 *   choose K starting centers
 *   repeat until assignments stop changing:
 *       assign each point to its nearest center
 *       replace each center with the mean of its assigned points
 *
 * Controls:
 *   SPACE - advance one assignment or center-update step
 *   A     - toggle automatic stepping
 *   R     - generate new points and reset
 *   UP/DOWN - change automatic step speed
 *   Mouse - click an empty area to add a point, then press R
 */
public class KMeansApplication extends PApplet {

    private static final int POINT_COUNT = 72;
    private static final int CLUSTER_COUNT = 3;
    private static final int TOP_BAR_HEIGHT = 74;
    private static final int GRAPH_LEFT = 44;
    private static final int GRAPH_TOP = 110;
    private static final int GRAPH_RIGHT = 856;
    private static final int GRAPH_BOTTOM = 585;

    private final Random random = new Random();
    private final Point[] points = new Point[POINT_COUNT];
    private final Center[] centers = new Center[CLUSTER_COUNT];
    private boolean running;
    private boolean finished;
    private boolean assignmentsChanged;
    private boolean hasAssignments;
    private int iteration;
    private int stepDelay = 24;
    private int frameCounter;
    private String phase = "ready";
    private String status = "Press SPACE to assign points to their nearest center";

    public static void main(String[] args) {
        PApplet.main(KMeansApplication.class.getName());
    }

    @Override
    public void settings() {
        size(900, 650);
    }

    @Override
    public void setup() {
        surface.setTitle("K-Means Clustering Sandbox");
        textFont(createFont("SansSerif", 16));
        generateData();
    }

    @Override
    public void draw() {
        background(21, 25, 34);
        drawHeader();
        drawPlot();

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
            generateData();
        } else if (keyCode == UP) {
            stepDelay = max(4, stepDelay - 4);
        } else if (keyCode == DOWN) {
            stepDelay = min(100, stepDelay + 4);
        }
    }

    @Override
    public void mousePressed() {
        if (mouseX < GRAPH_LEFT || mouseX > GRAPH_RIGHT
                || mouseY < GRAPH_TOP || mouseY > GRAPH_BOTTOM || running || finished) {
            return;
        }
        Point point = points[random.nextInt(POINT_COUNT)];
        point.x = mouseX;
        point.y = mouseY;
        hasAssignments = false;
        phase = "ready";
        status = "Point sample changed - press SPACE to assign clusters";
    }

    private void generateData() {
        for (int index = 0; index < POINT_COUNT; index++) {
            float groupAngle = TWO_PI * (index % CLUSTER_COUNT) / CLUSTER_COUNT;
            float groupX = width / 2f + cos(groupAngle) * 230;
            float groupY = GRAPH_TOP + 235 + sin(groupAngle) * 145;
            points[index] = new Point(groupX + randomGaussian() * 58,
                    groupY + randomGaussian() * 42);
            points[index].cluster = -1;
        }
        for (int index = 0; index < CLUSTER_COUNT; index++) {
            centers[index] = new Center(random(100, width - 100), random(GRAPH_TOP + 40, GRAPH_BOTTOM - 30));
        }
        iteration = 0;
        phase = "ready";
        hasAssignments = false;
        assignmentsChanged = true;
        running = false;
        finished = false;
        frameCounter = 0;
        status = "New points and centers generated - press SPACE to start";
    }

    private void advance() {
        if ("assign".equals(phase) || "ready".equals(phase)) {
            assignmentsChanged = false;
            for (Point point : points) {
                int nearest = nearestCenter(point);
                if (point.cluster != nearest) {
                    assignmentsChanged = true;
                    point.cluster = nearest;
                }
            }
            hasAssignments = true;
            phase = "update";
            status = "Assigned points to their nearest center - press SPACE to move centers";
        } else if ("update".equals(phase)) {
            updateCenters();
            iteration++;
            if (!assignmentsChanged) {
                phase = "complete";
                running = false;
                finished = true;
                status = "Converged after " + iteration + " iteration(s)";
            } else {
                phase = "assign";
                status = "Moved centers to cluster means - press SPACE to assign again";
            }
        }
    }

    private int nearestCenter(Point point) {
        int nearest = 0;
        float nearestDistance = distance(point, centers[0]);
        for (int index = 1; index < centers.length; index++) {
            float candidateDistance = distance(point, centers[index]);
            if (candidateDistance < nearestDistance) {
                nearest = index;
                nearestDistance = candidateDistance;
            }
        }
        return nearest;
    }

    private void updateCenters() {
        for (int centerIndex = 0; centerIndex < centers.length; centerIndex++) {
            float sumX = 0;
            float sumY = 0;
            int count = 0;
            for (Point point : points) {
                if (point.cluster == centerIndex) {
                    sumX += point.x;
                    sumY += point.y;
                    count++;
                }
            }
            if (count > 0) {
                centers[centerIndex].x = sumX / count;
                centers[centerIndex].y = sumY / count;
                centers[centerIndex].size = count;
            }
        }
    }

    private float distance(Point point, Center center) {
        float horizontal = point.x - center.x;
        float vertical = point.y - center.y;
        return sqrt(horizontal * horizontal + vertical * vertical);
    }

    private void drawHeader() {
        fill(30, 36, 48);
        noStroke();
        rect(0, 0, width, TOP_BAR_HEIGHT);
        fill(241, 245, 249);
        textSize(24);
        text("K-Means Clustering", 24, 31);
        fill(153, 165, 181);
        textSize(14);
        text(status, 24, 56);
        text("SPACE step   A auto   R reset   UP/DOWN speed", width - 310, 27);
        text("Iteration: " + iteration + "   Speed: " + stepDelay, width - 310, 51);
    }

    private void drawPlot() {
        fill(30, 36, 48);
        stroke(56, 66, 82);
        rect(GRAPH_LEFT, GRAPH_TOP, GRAPH_RIGHT - GRAPH_LEFT, GRAPH_BOTTOM - GRAPH_TOP);
        fill(153, 165, 181);
        textSize(16);
        text("Points assigned to their nearest center", GRAPH_LEFT, 96);

        if (hasAssignments) {
            for (Point point : points) {
                fill(clusterColor(point.cluster));
                noStroke();
                ellipse(point.x, point.y, 13, 13);
            }
        } else {
            for (Point point : points) {
                fill(142, 151, 166);
                noStroke();
                ellipse(point.x, point.y, 13, 13);
            }
        }

        for (int index = 0; index < centers.length; index++) {
            Center center = centers[index];
            stroke(clusterColor(index));
            strokeWeight(3);
            noFill();
            ellipse(center.x, center.y, 34, 34);
            line(center.x - 12, center.y, center.x + 12, center.y);
            line(center.x, center.y - 12, center.x, center.y + 12);
            fill(241, 245, 249);
            textSize(12);
            text("C" + (index + 1) + " (" + center.size + ")", center.x + 20, center.y - 15);
        }

        fill(142, 151, 166);
        textSize(13);
        text("Colored points: current cluster    Crosshairs: centers    Number: points in cluster",
                GRAPH_LEFT, GRAPH_BOTTOM + 28);
    }

    private int clusterColor(int cluster) {
        if (cluster == 0) {
            return color(76, 201, 160);
        }
        if (cluster == 1) {
            return color(246, 183, 76);
        }
        return color(129, 140, 248);
    }

    private static final class Point {
        private float x;
        private float y;
        private int cluster;

        private Point(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }

    private static final class Center {
        private float x;
        private float y;
        private int size;

        private Center(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }
}
