package com.mygdx.game;

public class Cannon {
	Point3D position;
	
	public void create() {

	}
	
	public void display() {
		ModelMatrix.main.pushMatrix();
		ModelMatrix.main.addTranslation(5, 5, 1);
		ModelMatrix.main.addScale(50, 50, 1);
		ModelMatrix.main.setShaderMatrix();
		Circle.drawSolidCircle();
		ModelMatrix.main.popMatrix();
	}
}
