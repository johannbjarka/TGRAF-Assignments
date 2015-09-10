package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class Cannon {
	public Point3D position;
	
	ModelMatrix orientation;
	
	public CannonBall cannonBall;
	
	public Cannon() {
		// Set the position of the cannon
		position = new Point3D();
		position.x = Gdx.graphics.getWidth() / 2;
		position.y = 0;
		
		orientation = new ModelMatrix();
		orientation.loadIdentityMatrix();
		orientation.addTranslation(position.x, position.y, 0);
		
		cannonBall = new CannonBall();
		cannonBall.position.x = position.x;
		cannonBall.position.y = position.y;
	}
	
	public void display(int colorLoc) {
		ModelMatrix.main.pushMatrix();
		
		ModelMatrix.main.addTransformation(orientation.matrix);
		
		// Draw the circle base
		ModelMatrix.main.pushMatrix();
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
		
		cannonBall.display(colorLoc);
	}
	
	public void update(float deltaTime) {
		cannonBall.update(deltaTime);
	}
	
	public void input(float deltaTime) {
		if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
			orientation.addRotationZ(180.0f * deltaTime);
		}
		if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
			orientation.addRotationZ(-180.0f * deltaTime);
		}
		if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
			shoot();
		}
	}
	
	public void shoot() {
		cannonBall.orientation = new ModelMatrix();
		cannonBall.orientation.loadIdentityMatrix();
		
		cannonBall.orientation.addTransformation(orientation.matrix);
		cannonBall.velocity = cannonBall.orientation.getB();
		cannonBall.velocity.scale(300);
		cannonBall.position.x = position.x;
		cannonBall.position.y = position.y;
	}
}
