package com.ru.tgra.shapes;

public class Cell {
	
	BoxGraphic northWall;
	BoxGraphic eastWall;
	
	float wallThickness;
	float wallLength;
	float wallHeight;
	
	public Cell(int positionLoc, int normalLoc) {
		
		this.northWall = new BoxGraphic(positionLoc, normalLoc);
		this.eastWall = new BoxGraphic(positionLoc, normalLoc);
		
		this.wallThickness = 1.0f;
		this.wallLength = 20.0f;
		this.wallHeight = 15.0f;
	}
	
	public void Draw() {
		
		ModelMatrix.main.pushMatrix();
		ModelMatrix.main.addTranslation(wallLength, wallHeight / 2, -wallLength / 2);
		ModelMatrix.main.addScale(wallThickness, wallHeight, wallLength);
		ModelMatrix.main.setShaderMatrix();
		eastWall.drawSolidCube();
		ModelMatrix.main.popMatrix();

		ModelMatrix.main.pushMatrix();
		ModelMatrix.main.addTranslation(wallLength / 2, wallHeight / 2, -wallLength);
		ModelMatrix.main.addScale(wallLength, wallHeight, wallThickness);
		ModelMatrix.main.setShaderMatrix();
		northWall.drawSolidCube();
		ModelMatrix.main.popMatrix();
	}
}
