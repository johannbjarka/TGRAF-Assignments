package com.mygdx.game;

import com.badlogic.gdx.Gdx;

public class CannonBall {
	public Point3D position;
	
	public Vector3D velocity;
	
	public ModelMatrix orientation;
	
	public static boolean isActive = false;
	
	public CannonBall() {
		position = new Point3D(0, 0, 0);
		velocity = new Vector3D(0, 0, 0);
		orientation = new ModelMatrix();
	}
	
	public void display(int colorLoc) {
		ModelMatrix.main.pushMatrix();
		ModelMatrix.main.addTransformation(orientation.matrix);
		
		// Draw the circle base
		ModelMatrix.main.pushMatrix();
		//ModelMatrix.main.addTranslation(position.x, position.y, 1);
		ModelMatrix.main.addScale(10, 10, 1);
		Gdx.gl.glUniform4f(colorLoc, 0, 0, 0, 1);
		ModelMatrix.main.setShaderMatrix();
		Circle.drawSolidCircle();
		ModelMatrix.main.popMatrix();
		
		ModelMatrix.main.popMatrix();
	}
	
	public void update(float deltaTime) {
		position.x += velocity.x * deltaTime;
		position.y += velocity.y * deltaTime;
		
		orientation.matrix.put(12, orientation.matrix.get(12) + velocity.x * deltaTime);
		orientation.matrix.put(13, orientation.matrix.get(13) + velocity.y * deltaTime);
		
		// Check if cannonball is out of bounds
		if(isActive && (position.x >= Gdx.graphics.getWidth() || position.x < 0 
				|| position.y >= Gdx.graphics.getHeight() || position.y < 0)) {
			isActive = false;
			position.x = 0;
			position.y = 0;
			OurAwesomeCannonGame.clearGame();
		}
	}	
}
