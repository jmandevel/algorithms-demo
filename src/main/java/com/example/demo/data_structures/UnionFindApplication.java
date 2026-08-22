package com.example.demo.data_structures;

import com.example.demo.StepAlgorithmVisualizer;

/** Union-find tracks connected components with near-constant operations.
 * Pseudocode: find roots with path compression; join roots by rank. */
public class UnionFindApplication extends StepAlgorithmVisualizer {
 public static void main(String[] args) { processing.core.PApplet.main(UnionFindApplication.class.getName()); }
 protected String algorithmName() { return "Union-Find"; }
 protected String[] algorithmSteps() { return new String[]{"Make each item its own set", "Find each item's representative", "Join two different representatives", "Compress paths during later finds"}; }
}
