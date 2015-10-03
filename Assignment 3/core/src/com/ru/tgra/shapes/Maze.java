package com.ru.tgra.shapes;

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

public class Maze {
	
	public Cell[][] cells;
	
	int positionLoc;
	int normalLoc;
	
	int numberOfCells = 10;
	public Maze(int positionLoc, int normalLoc) {
		this.positionLoc = positionLoc;
		this.normalLoc = normalLoc;
		cells = new Cell[numberOfCells][];

		for(int i = 0; i < cells.length; i++) {
			cells[i] = new Cell[numberOfCells];
			for(int j = 0; j < cells[i].length; j++) {
				cells[i][j] = new Cell(positionLoc, normalLoc);
			}
		}
		
		this.GenerateMaze();
	}
	
	public void Draw() {
		ModelMatrix.main.pushMatrix();
		for(int i = 0; i < cells.length; i++) {
			ModelMatrix.main.pushMatrix();
			for(int j = 0; j < cells[i].length; j++) {
				if(j != 0){
					ModelMatrix.main.addTranslation(0, 0, -cells[i][j].wallLength);
				}
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
		
		BoxGraphic outerWallSouth = new BoxGraphic(this.positionLoc, this.normalLoc);
		ModelMatrix.main.pushMatrix();
		ModelMatrix.main.addTranslation(0, outerWallHeight / 2, -outerWallLength / 2);
		ModelMatrix.main.addScale(outerWallThickness, outerWallHeight, outerWallLength);
		ModelMatrix.main.setShaderMatrix();
		outerWallSouth.drawSolidCube();
		ModelMatrix.main.popMatrix();
		
		BoxGraphic outerWallWest = new BoxGraphic(this.positionLoc, this.normalLoc);
		ModelMatrix.main.pushMatrix();
		ModelMatrix.main.addTranslation(outerWallLength / 2, outerWallHeight / 2, 0);
		ModelMatrix.main.addScale(outerWallLength, outerWallHeight, -outerWallThickness);
		ModelMatrix.main.setShaderMatrix();
		outerWallWest.drawSolidCube();
		ModelMatrix.main.popMatrix();
		
	}
	
	public void GenerateMaze() {
		Random randNumberGenerator = new Random();
		int x = 5;//randNumberGenerator.nextInt(cells.length);
		int y = 5;//randNumberGenerator.nextInt(cells.length);
		
		Stack<Cell> cellsVisited = new Stack<Cell>();
		
		int numCells = cells.length * cells.length;
		
		Cell curCell;
		curCell = this.cells[x][y];
		ArrayList<Integer> directions = new ArrayList<Integer>();
		
		while(numCells >= 0) {
			cellsVisited.push(curCell);
			
			curCell.visited = true;
			
			if(!this.cells[x][y+1].visited) {
				directions.add(1);
			}else if(!this.cells[x][y-1].visited) {
				directions.add(3);
			}else if(!this.cells[x+1][y].visited) {
				directions.add(2);
			}else if(!this.cells[x-1][y].visited) {
				directions.add(0);
			}
			
			int index = randNumberGenerator.nextInt(directions.size());
			int direction = directions.get(index);
			
			if(direction == 0) {
				// Go left
				System.out.println("going left");
				curCell = this.cells[x-1][y];
				curCell.hasEastWall = false;
				curCell.visited = true;
			}else if(direction == 1) {
				// Go up
				System.out.println("going up");
				curCell.hasNorthWall = false;
				curCell = this.cells[x][y+1];
				curCell.visited = true;
			}else if(direction == 2) {
				// Go right
				System.out.println("going right");
				curCell.hasEastWall = false;
				curCell = this.cells[x+1][y];
				curCell.visited = true;
			}else if(direction == 3) {
				// Go down
				System.out.println("going down");
				curCell = this.cells[x][y-1];
				curCell.hasNorthWall = false;
				curCell.visited = true;
			}
			numCells -= 1;
			
			
		}
	}
}
