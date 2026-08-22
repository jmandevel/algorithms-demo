package com.example.demo;

import processing.core.PApplet;

public class DemoApplication extends PApplet {

	public static void main(String[] args) {
		PApplet.main(DemoApplication.class.getName());
	}

	@Override
	public void settings() {
		size(800, 600);
	}

	@Override
	public void setup() {
		background(255, 0, 0);
		noLoop();
	}

}
