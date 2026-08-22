package com.example.demo.math;

import com.example.demo.StepAlgorithmVisualizer;

/** The sieve finds all primes up to a chosen limit.
 * Pseudocode: mark multiples of each prime starting at its square. */
public class SieveOfEratosthenesApplication extends StepAlgorithmVisualizer {
 public static void main(String[] args) { processing.core.PApplet.main(SieveOfEratosthenesApplication.class.getName()); }
 protected String algorithmName() { return "Sieve of Eratosthenes"; }
 protected String[] algorithmSteps() { return new String[]{"Assume every number is prime", "Choose the next unmarked number", "Mark its multiples as composite", "Stop after the square root of the limit"}; }
}
