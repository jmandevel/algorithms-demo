package com.example.demo.graphs;

import com.example.demo.StepAlgorithmVisualizer;

/** Depth-first search follows one branch before backtracking.
 * Pseudocode: visit a node; for each unvisited neighbor, recursively search it. */
public class DepthFirstSearchApplication extends StepAlgorithmVisualizer {
 public static void main(String[] args) { processing.core.PApplet.main(DepthFirstSearchApplication.class.getName()); }
 protected String algorithmName() { return "Depth-First Search"; }
 protected String[] algorithmSteps() { return new String[]{"Visit the current node", "Choose an unvisited neighbor", "Continue down that branch", "Backtrack when no neighbor remains"}; }
}
