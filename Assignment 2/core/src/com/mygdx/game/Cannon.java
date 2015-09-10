package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class Cannon {
	Point3D position;
	
	ModelMatrix orientation;
	
	public Cannon() {
		// Set the position of the cannon
		position = new Point3D();
		position.x = Gdx.graphics.getWidth() / 2;
		position.y = 0;
		
		orientation = new ModelMatrix();
		orientation.loadIdentityMatrix();
		orientation.addTranslation(position.x, position.y, 0);
	}
	
	public void display(int colorLoc) {
		ModelMatrix.main.pushMatrix();
		
		ModelMatrix.main.addTransformation(orientation.matrix);
		
		// Draw the circle base
		ModelMatrix.main.pushMatrix();
		//ModelMatrix.main.addTranslation(position.x, position.y, 1);
		ModelMatrix.main.addScale(20, 20, 1);
		Gdx.gl.glUniform4f(colorLoc, 0, 0, 0, 1);
		ModelMatrix.main.setShaderMatrix();
		Circle.drawSolidCircle();
		ModelMatrix.main.popMatrix();
		
		// Draw the cannon shaft
		ModelMatrix.main.pushMatrix();
		ModelMatrix.main.addTranslation(0, 25, 1);
		ModelMatrix.main.addScale(20, 80, 1);
		Gdx.gl.glUniform4f(colorLoc, 0, 0, 0, 1);
		ModelMatrix.main.setShaderMatrix();
		Rectangle.drawSolidSquare();
		ModelMatrix.main.popMatrix();
		
		ModelMatrix.main.popMatrix();
	}
	
	public void update(float deltaTime) {
		
	}
	
	public void input(float deltaTime) {
		if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
			orientation.addRotationZ(180.0f * deltaTime);
		}
		if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
			orientation.addRotationZ(-180.0f * deltaTime);
		}
	}
}
