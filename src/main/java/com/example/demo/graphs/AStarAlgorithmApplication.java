package com.example.demo.graphs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import processing.core.PApplet;

/**
 * Interactive visual demonstration of the A* pathfinding algorithm.
 *
 * A* finds a low-cost path from a start point to a goal point. For every cell,
 * it combines two distances:
 *
 *   g = the distance already traveled from the start
 *   h = the estimated distance remaining to the goal
 *   f = g + h
 *
 * This sketch uses the point distance formula for h:
 *
 *   distance = sqrt((x2 - x1)^2 + (y2 - y1)^2)
 *
 * Other common distance heuristics are useful for different movement rules:
 *
 *   Manhattan: |x2 - x1| + |y2 - y1|
 *     Best when movement is limited to up, down, left, and right, as in this
 *     grid. It counts the number of grid steps without using diagonals.
 *
 *   Euclidean: sqrt((x2 - x1)^2 + (y2 - y1)^2)
 *     Useful when movement can follow any angle, or when diagonal movement
 *     should cost the same as its real geometric length.
 *
 *   Chebyshev: max(|x2 - x1|, |y2 - y1|)
 *     Useful when diagonal and straight moves both cost one step, such as a
 *     game board where a piece can move to any of its eight neighboring cells.
 *
 *   Octile: max(dx, dy) + (sqrt(2) - 1) * min(dx, dy)
 *     Useful when diagonal moves are allowed but cost sqrt(2), while straight
 *     moves cost one. A good heuristic should match the movement costs.
 *
 * A* always explores the open cell with the smallest f value. It keeps the
 * previous cell for each explored cell, so the path can be traced backward
 * after the goal is reached. Green cells are waiting to be explored, blue
 * cells have been explored, and yellow cells form the final path.
 *
 * Controls:
 *   SPACE - advance one search step
 *   A     - toggle automatic stepping
 *   R     - generate a new maze and reset
 *   UP/DOWN - change automatic step speed
 *   Mouse - click empty cells to add or remove obstacles, then press SPACE
 */
public class AStarAlgorithmApplication extends PApplet {

    private static final int COLUMNS = 20;
    private static final int ROWS = 14;
    private static final int CELL_SIZE = 32;
    private static final int GRID_LEFT = 130;
    private static final int GRID_TOP = 108;
    private static final int TOP_BAR_HEIGHT = 74;

    private final Random random = new Random();
    private Cell[][] grid;
    private Point start;
    private Point goal;
    private final List<Cell> open = new ArrayList<>();
    private final List<Cell> path = new ArrayList<>();
    private boolean running;
    private boolean finished;
    private int stepDelay = 12;
    private int frameCounter;
    private String status = "Press SPACE to start A*";

    public static void main(String[] args) {
        PApplet.main(AStarAlgorithmApplication.class.getName());
    }

    @Override
    public void settings() {
        size(900, 650);
    }

    @Override
    public void setup() {
        surface.setTitle("A* Algorithm Sandbox");
        textFont(createFont("SansSerif", 16));
        generateGrid();
    }

    @Override
    public void draw() {
        background(21, 25, 34);
        drawHeader();
        drawGrid();

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
            generateGrid();
        } else if (keyCode == UP) {
            stepDelay = max(2, stepDelay - 2);
        } else if (keyCode == DOWN) {
            stepDelay = min(60, stepDelay + 2);
        }
    }

    @Override
    public void mousePressed() {
        int column = (mouseX - GRID_LEFT) / CELL_SIZE;
        int row = (mouseY - GRID_TOP) / CELL_SIZE;
        if (!insideGrid(column, row) || running || finished
                || (column == start.x && row == start.y)
                || (column == goal.x && row == goal.y)) {
            return;
        }
        grid[row][column].blocked = !grid[row][column].blocked;
    }

    private void generateGrid() {
        grid = new Cell[ROWS][COLUMNS];
        open.clear();
        path.clear();
        running = false;
        finished = false;
        frameCounter = 0;
        start = new Point(1, ROWS / 2);
        goal = new Point(COLUMNS - 2, ROWS / 2);
        status = "New maze generated - press SPACE to start";

        for (int row = 0; row < ROWS; row++) {
            for (int column = 0; column < COLUMNS; column++) {
                boolean blocked = random.nextFloat() < 0.20f;
                if ((column == start.x && row == start.y)
                        || (column == goal.x && row == goal.y)) {
                    blocked = false;
                }
                grid[row][column] = new Cell(column, row, blocked);
            }
        }
        grid[start.y][start.x].gScore = 0;
        grid[start.y][start.x].fScore = pointDistance(start, goal);
        open.add(grid[start.y][start.x]);
    }

    private void advance() {
        if (finished) {
            running = false;
            return;
        }
        if (open.isEmpty()) {
            running = false;
            finished = true;
            status = "No path found - press R to generate another maze";
            return;
        }

        Cell current = lowestScoreCell();
        open.remove(current);
        current.closed = true;

        if (current.x == goal.x && current.y == goal.y) {
            buildPath(current);
            running = false;
            finished = true;
            status = "Path found - length: " + path.size() + " cells";
            return;
        }

        for (Cell neighbor : neighbors(current)) {
            if (neighbor.blocked || neighbor.closed) {
                continue;
            }

            float possibleScore = current.gScore + pointDistance(current.point(), neighbor.point());
            if (possibleScore < neighbor.gScore) {
                neighbor.parent = current;
                neighbor.gScore = possibleScore;
                neighbor.fScore = possibleScore + pointDistance(neighbor.point(), goal);
                if (!open.contains(neighbor)) {
                    open.add(neighbor);
                }
            }
        }
        status = "Exploring (" + current.x + ", " + current.y + ") - lowest f = "
                + format(current.fScore);
    }

    private Cell lowestScoreCell() {
        Cell best = open.get(0);
        for (Cell candidate : open) {
            if (candidate.fScore < best.fScore
                    || (candidate.fScore == best.fScore && candidate.gScore < best.gScore)) {
                best = candidate;
            }
        }
        return best;
    }

    private List<Cell> neighbors(Cell cell) {
        List<Cell> result = new ArrayList<>();
        for (int rowOffset = -1; rowOffset <= 1; rowOffset++) {
            for (int columnOffset = -1; columnOffset <= 1; columnOffset++) {
                if (rowOffset == 0 && columnOffset == 0
                        || abs(rowOffset) + abs(columnOffset) != 1) {
                    continue;
                }
                int column = cell.x + columnOffset;
                int row = cell.y + rowOffset;
                if (insideGrid(column, row)) {
                    result.add(grid[row][column]);
                }
            }
        }
        Collections.shuffle(result, random);
        return result;
    }

    private void buildPath(Cell current) {
        path.clear();
        while (current != null) {
            path.add(current);
            current = current.parent;
        }
        Collections.reverse(path);
    }

    private float pointDistance(Point first, Point second) {
        float horizontal = second.x - first.x;
        float vertical = second.y - first.y;
        return sqrt(horizontal * horizontal + vertical * vertical);
    }

    private boolean insideGrid(int column, int row) {
        return column >= 0 && column < COLUMNS && row >= 0 && row < ROWS;
    }

    private String format(float value) {
        return nf(value, 0, 1);
    }

    private void drawHeader() {
        fill(30, 36, 48);
        noStroke();
        rect(0, 0, width, TOP_BAR_HEIGHT);
        fill(241, 245, 249);
        textSize(24);
        text("A* Pathfinding", 24, 31);
        fill(153, 165, 181);
        textSize(14);
        text(status, 24, 56);
        text("SPACE step   A auto   R reset   UP/DOWN speed", width - 310, 27);
        text("Open: " + open.size() + "   Path: " + path.size() + "   Speed: " + stepDelay,
                width - 310, 51);
    }

    private void drawGrid() {
        for (int row = 0; row < ROWS; row++) {
            for (int column = 0; column < COLUMNS; column++) {
                Cell cell = grid[row][column];
                if (cell.blocked) {
                    fill(45, 53, 68);
                } else if (path.contains(cell)) {
                    fill(246, 183, 76);
                } else if (cell == grid[start.y][start.x]) {
                    fill(76, 201, 160);
                } else if (cell == grid[goal.y][goal.x]) {
                    fill(239, 102, 102);
                } else if (cell.closed) {
                    fill(76, 125, 161);
                } else if (open.contains(cell)) {
                    fill(76, 201, 160);
                } else {
                    fill(30, 36, 48);
                }
                stroke(56, 66, 82);
                strokeWeight(1);
                rect(GRID_LEFT + column * CELL_SIZE, GRID_TOP + row * CELL_SIZE,
                        CELL_SIZE, CELL_SIZE);
            }
        }
        fill(153, 165, 181);
        textSize(13);
        text("Start", GRID_LEFT, GRID_TOP + ROWS * CELL_SIZE + 28);
        text("Goal", GRID_LEFT + 55, GRID_TOP + ROWS * CELL_SIZE + 28);
        text("Green: open    Blue: explored    Gold: path    Gray: obstacle",
                GRID_LEFT + 120, GRID_TOP + ROWS * CELL_SIZE + 28);
    }

    private static final class Point {
        private final int x;
        private final int y;

        private Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    private static final class Cell {
        private final int x;
        private final int y;
        private final Point point;
        private boolean blocked;
        private boolean closed;
        private float gScore = Float.POSITIVE_INFINITY;
        private float fScore = Float.POSITIVE_INFINITY;
        private Cell parent;

        private Cell(int x, int y, boolean blocked) {
            this.x = x;
            this.y = y;
            this.point = new Point(x, y);
            this.blocked = blocked;
        }

        private Point point() {
            return point;
        }
    }
}
