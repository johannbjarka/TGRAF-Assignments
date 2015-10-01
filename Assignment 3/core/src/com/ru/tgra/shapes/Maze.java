package com.ru.tgra.shapes;

public class Maze {
	
	public Cell[][] cells;

	public Maze(int positionLoc, int normalLoc) {
		cells = new Cell[10][];
		
		for(int i = 0; i < cells.length; i++) {
			cells[i] = new Cell[10];
			for(int j = 0; j < cells[i].length; j++) {
				cells[i][j] = new Cell(positionLoc, normalLoc);
			}
		}
		
	}
	
	public void Draw() {
		/*
		for(int i = 0; i < cells.length; i++) {
			for(int j = 0; j < cells[i].length; j++) {
				
				ModelMatrix.main.pushMatrix();
				ModelMatrix.main.addTranslation(20, 7.5f, -10f);
				ModelMatrix.main.addScale(1f, 15f, 20);
				ModelMatrix.main.setShaderMatrix();
				cells[i][j].Draw();
				ModelMatrix.main.popMatrix();
				
			}
		}
		*/
		
		ModelMatrix.main.pushMatrix();
		ModelMatrix.main.addTranslation(0, 0, 0);
		ModelMatrix.main.setShaderMatrix();
		cells[0][0].Draw();
		ModelMatrix.main.popMatrix();
		
		
		ModelMatrix.main.pushMatrix();
		ModelMatrix.main.addTranslation(0, 0, -20);
		ModelMatrix.main.setShaderMatrix();
		cells[0][1].Draw();
		ModelMatrix.main.popMatrix();
	}
}
