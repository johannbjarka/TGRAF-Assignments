package com.mygdx.game;

import com.badlogic.gdx.Gdx;

public class Cannon {
	Point3D position;
	
	public Cannon() {
		// Set the position of the cannon
		position = new Point3D();
		position.x = Gdx.graphics.getWidth() / 2;
		position.y = 0;
	}
	
	public void display(int colorLoc) {
		
		// Draw the circle base
		ModelMatrix.main.pushMatrix();
		ModelMatrix.main.addTranslation(position.x, position.y, 1);
		ModelMatrix.main.addScale(20, 20, 1);
		Gdx.gl.glUniform4f(colorLoc, 0, 0, 0, 1);
		ModelMatrix.main.setShaderMatrix();
		Circle.drawSolidCircle();
		ModelMatrix.main.popMatrix();
		
		// Draw the cannon shaft
		ModelMatrix.main.pushMatrix();
		ModelMatrix.main.addTranslation(position.x, position.y + 25, 1);
		ModelMatrix.main.addScale(20, 80, 1);
		Gdx.gl.glUniform4f(colorLoc, 0, 0, 0, 1);
		ModelMatrix.main.setShaderMatrix();
		Rectangle.drawSolidSquare();
		ModelMatrix.main.popMatrix();
	}
	
	public void update() {
		
	}
}
