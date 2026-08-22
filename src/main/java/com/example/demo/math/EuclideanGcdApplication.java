package com.example.demo.math;

import com.example.demo.StepAlgorithmVisualizer;

/** Euclid's algorithm finds the greatest common divisor.
 * Pseudocode: while b is not zero, replace (a,b) with (b,a modulo b). */
public class EuclideanGcdApplication extends StepAlgorithmVisualizer {
 public static void main(String[] args) { processing.core.PApplet.main(EuclideanGcdApplication.class.getName()); }
 protected String algorithmName() { return "Euclidean GCD"; }
 protected String[] algorithmSteps() { return new String[]{"Start with two positive numbers", "Compute the remainder", "Replace the pair with divisor and remainder", "Stop when the remainder is zero"}; }
}
