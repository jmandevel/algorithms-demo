package com.example.demo.data_structures;

import com.example.demo.StepAlgorithmVisualizer;

/** A stack uses last-in, first-out ordering.
 * Pseudocode: push adds to the top; pop removes the most recently added item. */
public class StackApplication extends StepAlgorithmVisualizer {
 public static void main(String[] args) { processing.core.PApplet.main(StackApplication.class.getName()); }
 protected String algorithmName() { return "Stack"; }
 protected String[] algorithmSteps() { return new String[]{"Create an empty stack", "Push a new item on top", "Peek at the top item", "Pop the top item first"}; }
}
