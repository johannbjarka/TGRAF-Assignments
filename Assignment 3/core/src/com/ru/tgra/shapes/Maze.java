package com.ru.tgra.shapes;

public class Maze {
	
	public Cell[][] cells;
	
	int numberOfCells = 10;
	public Maze(int positionLoc, int normalLoc) {
		cells = new Cell[numberOfCells][];
		
		for(int i = 0; i < cells.length; i++) {
			cells[i] = new Cell[numberOfCells];
			for(int j = 0; j < cells[i].length; j++) {
				cells[i][j] = new Cell(positionLoc, normalLoc);
			}
		}
	}
	
	public void Draw() {
		ModelMatrix.main.pushMatrix();
		for(int i = 0; i < cells.length; i++) {
			ModelMatrix.main.pushMatrix();
			for(int j = 0; j < cells[i].length; j++) {
				ModelMatrix.main.addTranslation(0, 0, -cells[i][j].wallLength);
				ModelMatrix.main.setShaderMatrix();
				cells[i][j].Draw();
			}
			ModelMatrix.main.popMatrix();
			ModelMatrix.main.addTranslation(cells[i][0].wallLength, 0, 0);
		}
		ModelMatrix.main.popMatrix();
		
		float outerWallLength = cells[0][0].wallLength * (float)numberOfCells;
		float outerWallHeight = cells[0][0].wallHeight;
		float outerWallThickness = cells[0][0].wallThickness;
		
		BoxGraphic outerWallSouth = new BoxGraphic(0, 0);
		ModelMatrix.main.pushMatrix();
		ModelMatrix.main.addTranslation(outerWallThickness / 2, outerWallHeight / 2, (-outerWallLength - 40) / 2);
		ModelMatrix.main.addScale(outerWallThickness, outerWallHeight, outerWallLength);
		ModelMatrix.main.setShaderMatrix();
		outerWallSouth.drawSolidCube();
		ModelMatrix.main.popMatrix();
		
		BoxGraphic outerWallWest = new BoxGraphic(0, 0);
		ModelMatrix.main.pushMatrix();
		ModelMatrix.main.addTranslation(outerWallLength / 2, outerWallHeight / 2, -20);
		ModelMatrix.main.addScale(outerWallLength, outerWallHeight, -outerWallThickness);
		ModelMatrix.main.setShaderMatrix();
		outerWallWest.drawSolidCube();
		ModelMatrix.main.popMatrix();
	}
}
