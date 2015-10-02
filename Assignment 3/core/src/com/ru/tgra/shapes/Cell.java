package com.ru.tgra.shapes;

public class Cell {
	
	BoxGraphic northWall;
	BoxGraphic eastWall;
	
	float wallThickness;
	float wallLength;
	float wallHeight;
	
	boolean hasNorthWall;
	boolean hasEastWall;
	
	public Cell(int positionLoc, int normalLoc) {
		
		this.northWall = new BoxGraphic(positionLoc, normalLoc);
		this.eastWall = new BoxGraphic(positionLoc, normalLoc);
		
		this.hasEastWall = true;
		this.hasNorthWall = true;
		
		this.wallThickness = 0.1f;
		this.wallLength = 1f;
		this.wallHeight = 1f;
	}
	
	public void Draw() {
		if(hasEastWall) {
			ModelMatrix.main.pushMatrix();
			ModelMatrix.main.addTranslation(wallLength, wallHeight / 2, -wallLength / 2);
			ModelMatrix.main.addScale(wallThickness, wallHeight, wallLength);
			ModelMatrix.main.setShaderMatrix();
			eastWall.drawSolidCube();
			ModelMatrix.main.popMatrix();
		}
		
		if(hasNorthWall) {
			ModelMatrix.main.pushMatrix();
			ModelMatrix.main.addTranslation(wallLength / 2, wallHeight / 2, -wallLength);
			ModelMatrix.main.addScale(wallLength, wallHeight, wallThickness);
			ModelMatrix.main.setShaderMatrix();
			northWall.drawSolidCube();
			ModelMatrix.main.popMatrix();
		}
		
	}
}
