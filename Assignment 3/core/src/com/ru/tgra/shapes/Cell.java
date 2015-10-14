package com.ru.tgra.shapes;

import com.badlogic.gdx.Gdx;

public class Cell {
	
	BoxGraphic northWall;
	BoxGraphic eastWall;
	
	float wallThickness;
	float wallLength;
	float wallHeight;
	
	boolean hasNorthWall;
	boolean hasEastWall;
	
	boolean visited;
	private int colorLoc;
	
	Point3D position;
	
	public Cell(int positionLoc, int normalLoc, Point3D pos) {
		this.position = pos;
		this.northWall = new BoxGraphic(positionLoc, normalLoc);
		this.eastWall = new BoxGraphic(positionLoc, normalLoc);
		
		this.hasEastWall = true;
		this.hasNorthWall = true;
		this.visited = false;
		
		this.wallThickness = 0.1f;
		this.wallLength = 1f;
		this.wallHeight = 1f;
		
		northWall.position = new Point3D(pos.x, 0, pos.z + this.wallLength);
		eastWall.position = new Point3D(pos.x + this.wallLength, 0, pos.z);
	}
	
	public void Draw() {
		if(hasEastWall) {
			ModelMatrix.main.pushMatrix();
			ModelMatrix.main.addTranslation(wallLength, wallHeight / 2, -wallLength / 2);
			ModelMatrix.main.addScale(wallThickness, wallHeight, wallLength + wallThickness);
			//Gdx.gl.glUniform4f(colorLoc, 1.0f, 1.0f, 0.0f, 1.0f);
			ModelMatrix.main.setShaderMatrix();
			eastWall.drawSolidCube();
			ModelMatrix.main.popMatrix();
		}
		
		if(hasNorthWall) {
			ModelMatrix.main.pushMatrix();
			ModelMatrix.main.addTranslation(wallLength / 2, wallHeight / 2, -wallLength);
			ModelMatrix.main.addScale(wallLength, wallHeight, wallThickness);
			//Gdx.gl.glUniform4f(colorLoc, 0.9f, 0.3f, 0.8f, 1.0f);
			ModelMatrix.main.setShaderMatrix();
			northWall.drawSolidCube();
			ModelMatrix.main.popMatrix();
		}
		
	}
}
