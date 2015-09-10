package com.mygdx.game;

import com.badlogic.gdx.Gdx;


public class CannonBall {
	Point3D position;
	
	public Vector3D velocity;
	
	public ModelMatrix orientation;
	
	public CannonBall() {
		velocity = new Vector3D(0, 0, 0);
		orientation = new ModelMatrix();
	}
	
	public void display(int colorLoc) {
		ModelMatrix.main.pushMatrix();
		ModelMatrix.main.addTransformation(orientation.matrix);
		
		// Draw the circle base
		ModelMatrix.main.pushMatrix();
		//ModelMatrix.main.addTranslation(position.x, position.y, 1);
		ModelMatrix.main.addScale(15, 15, 1);
		Gdx.gl.glUniform4f(colorLoc, 0, 0, 0, 1);
		ModelMatrix.main.setShaderMatrix();
		Circle.drawSolidCircle();
		ModelMatrix.main.popMatrix();
		
		ModelMatrix.main.popMatrix();
	}
	
	public void update(float deltaTime) {
		orientation.matrix.put(12, orientation.matrix.get(12) + velocity.x * deltaTime);
		orientation.matrix.put(13, orientation.matrix.get(13) + velocity.y * deltaTime);
	}
	
}
