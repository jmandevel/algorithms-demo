package com.example.demo.dynamic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import processing.core.PApplet;

/**
 * Interactive visual demonstration of the minimum coin change algorithm.
 *
 * The goal is to make an amount using the fewest possible coins. Dynamic
 * programming stores the answer for every smaller amount so that larger
 * amounts can reuse those answers instead of solving the same subproblem over
 * and over.
 *
 * For each amount, the recurrence is:
 *
 *   dp[amount] = min(dp[amount - coin] + 1) for every coin that fits
 *
 * dp[0] is zero because no coins are needed to make amount zero. The algorithm
 * also remembers which coin produced each best answer, allowing it to rebuild
 * the actual solution after the target amount has been computed. The running
 * time is O(amount * number of coins), with O(amount) memory.
 *
 * Pseudocode:
 *   dp[0] = 0
 *   for every other amount, set dp[amount] to infinity
 *   for amount from 1 through target:
 *       for each coin that fits:
 *           dp[amount] = min(dp[amount], dp[amount - coin] + 1)
 *   trace the remembered coins backward from the target
 *
 * Controls:
 *   SPACE - advance one amount/coin calculation
 *   A     - toggle automatic stepping
 *   T     - switch between target amounts
 *   R     - reset the calculation
 *   UP/DOWN - change automatic step speed
 */
public class CoinChangeApplication extends PApplet {

    private static final int[] COINS = {1, 3, 4};
    private static final int[] TARGETS = {12, 24, 31};
    private static final int TOP_BAR_HEIGHT = 74;
    private static final int TABLE_LEFT = 55;
    private static final int TABLE_TOP = 195;
    private static final int CELL_WIDTH = 32;

    private int targetIndex;
    private int target;
    private int[] minimumCoins;
    private int[] chosenCoin;
    private final List<Integer> solution = new ArrayList<>();
    private int amount;
    private int coinIndex;
    private int currentCandidate = -1;
    private boolean running;
    private boolean finished;
    private int stepDelay = 18;
    private int frameCounter;
    private String status = "Press SPACE to start the calculation";

    public static void main(String[] args) {
        PApplet.main(CoinChangeApplication.class.getName());
    }

    @Override
    public void settings() {
        size(900, 650);
    }

    @Override
    public void setup() {
        surface.setTitle("Coin Change Sandbox");
        textFont(createFont("SansSerif", 16));
        resetCalculation();
    }

    @Override
    public void draw() {
        background(21, 25, 34);
        drawHeader();
        drawCoins();
        drawTable();
        drawSolution();

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
        } else if (key == 't' || key == 'T') {
            targetIndex = (targetIndex + 1) % TARGETS.length;
            resetCalculation();
        } else if (key == 'r' || key == 'R') {
            resetCalculation();
        } else if (keyCode == UP) {
            stepDelay = max(3, stepDelay - 3);
        } else if (keyCode == DOWN) {
            stepDelay = min(80, stepDelay + 3);
        }
    }

    private void resetCalculation() {
        target = TARGETS[targetIndex];
        minimumCoins = new int[target + 1];
        chosenCoin = new int[target + 1];
        for (int index = 1; index <= target; index++) {
            minimumCoins[index] = Integer.MAX_VALUE;
        }
        minimumCoins[0] = 0;
        solution.clear();
        amount = 1;
        coinIndex = 0;
        currentCandidate = -1;
        running = false;
        finished = false;
        frameCounter = 0;
        status = "Target: " + target + " - press SPACE to start";
    }

    private void advance() {
        if (amount > target) {
            buildSolution();
            running = false;
            finished = true;
            status = "Minimum found: " + solution.size() + " coin(s) for " + target;
            return;
        }

        int coin = COINS[coinIndex];
        currentCandidate = coin;
        if (coin <= amount && minimumCoins[amount - coin] != Integer.MAX_VALUE) {
            int candidate = minimumCoins[amount - coin] + 1;
            if (candidate < minimumCoins[amount]) {
                minimumCoins[amount] = candidate;
                chosenCoin[amount] = coin;
                status = "Amount " + amount + ": coin " + coin + " improves the answer to " + candidate;
            } else {
                status = "Amount " + amount + ": coin " + coin + " is not better";
            }
        } else {
            status = "Amount " + amount + ": coin " + coin + " does not fit";
        }

        coinIndex++;
        if (coinIndex == COINS.length) {
            coinIndex = 0;
            amount++;
        }
    }

    private void buildSolution() {
        solution.clear();
        int remaining = target;
        while (remaining > 0 && chosenCoin[remaining] != 0) {
            int coin = chosenCoin[remaining];
            solution.add(coin);
            remaining -= coin;
        }
        Collections.sort(solution, Collections.reverseOrder());
    }

    private void drawHeader() {
        fill(30, 36, 48);
        noStroke();
        rect(0, 0, width, TOP_BAR_HEIGHT);
        fill(241, 245, 249);
        textSize(24);
        text("Coin Change", 24, 31);
        fill(153, 165, 181);
        textSize(14);
        text(status, 24, 56);
        text("SPACE step   A auto   T target   R reset", width - 285, 27);
        text("Amount: " + Math.min(amount, target) + "/" + target + "   Speed: " + stepDelay,
                width - 285, 51);
    }

    private void drawCoins() {
        fill(153, 165, 181);
        textSize(17);
        text("Available coins", TABLE_LEFT, 112);
        for (int index = 0; index < COINS.length; index++) {
            int x = TABLE_LEFT + index * 90;
            fill(index == coinIndex && !finished ? color(246, 183, 76) : color(76, 125, 161));
            stroke(76, 86, 105);
            ellipse(x + 25, 145, 45, 45);
            fill(241, 245, 249);
            textAlign(CENTER, CENTER);
            textSize(16);
            text(COINS[index], x + 25, 144);
            textAlign(LEFT, BASELINE);
        }
    }

    private void drawTable() {
        fill(153, 165, 181);
        textSize(17);
        text("Dynamic programming table", TABLE_LEFT, 175);
        for (int index = 0; index <= target; index++) {
            int x = TABLE_LEFT + index * CELL_WIDTH;
            boolean active = index == amount && !finished;
            boolean known = minimumCoins[index] != Integer.MAX_VALUE;
            fill(active ? color(246, 183, 76) : known ? color(76, 201, 160) : color(45, 53, 68));
            stroke(56, 66, 82);
            rect(x, TABLE_TOP, CELL_WIDTH - 2, 48);
            fill(241, 245, 249);
            textAlign(CENTER, CENTER);
            textSize(12);
                text(known ? String.valueOf(minimumCoins[index]) : "-",
                    x + CELL_WIDTH / 2f, TABLE_TOP + 17);
            fill(153, 165, 181);
            textSize(10);
            text(index, x + CELL_WIDTH / 2f, TABLE_TOP + 38);
            textAlign(LEFT, BASELINE);
        }
        fill(142, 151, 166);
        textSize(13);
        text("Value above = fewest coins needed    Number below = amount    Gold = current amount",
                TABLE_LEFT, TABLE_TOP + 78);
    }

    private void drawSolution() {
        fill(30, 36, 48);
        stroke(76, 86, 105);
        rect(TABLE_LEFT, 395, 790, 120);
        fill(153, 165, 181);
        textSize(17);
        text("Solution", TABLE_LEFT + 20, 425);
        fill(241, 245, 249);
        textSize(18);
        String result = finished ? solution.toString() : "The chosen coins will appear here when the table is complete.";
        text(result, TABLE_LEFT + 20, 465);
        fill(142, 151, 166);
        textSize(13);
        text("Current candidate coin: " + (currentCandidate < 0 ? "none" : currentCandidate),
                TABLE_LEFT + 20, 495);
    }
}
