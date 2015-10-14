package com.ru.tgra.shapes;

import java.util.Arrays;
import java.util.Random;
import java.util.Stack;

public class Maze {
	
	public Cell[][] cells;
	public Cell[] westWall, southWall;
	
	int positionLoc;
	int normalLoc;
	
	int numberOfCells = 5;
	Random rand;
	
	Maze(int positionLoc, int normalLoc, int level) {
		this.numberOfCells = level * 5;
		this.rand = new Random();
		this.positionLoc = positionLoc;
		this.normalLoc = normalLoc;
		this.cells = new Cell[numberOfCells][];

		for(int i = 0; i < cells.length; i++) {
			cells[i] = new Cell[numberOfCells];
			for(int j = 0; j < cells[i].length; j++) {
				Point3D pos = new Point3D(i,0,j);
				cells[i][j] = new Cell(positionLoc, normalLoc, pos);
			}
		}
		
		this.GenerateMaze();
		
		westWall = new Cell[cells.length];
		southWall = new Cell[cells.length];
		Point3D pos;
		
		for(int i = 0; i < westWall.length; i++) {
			pos = new Point3D(-1, 0, i);
			westWall[i] = new Cell(positionLoc, normalLoc, pos);
			westWall[i].hasNorthWall = false;
			
			pos = new Point3D(i, 0, -1);
			southWall[i] = new Cell(positionLoc, normalLoc, pos);
			southWall[i].hasEastWall = false;
		}
		
		for(int i = 0; i < cells.length; i++) {
			for(int j = 0; j < cells[i].length; j++) {
				cells[i][j].visited = false;
			}
		}
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
		
		ModelMatrix.main.pushMatrix();
		ModelMatrix.main.addTranslation(-1, 0, 1);
		for(int i = 0; i < westWall.length; i++) {
			ModelMatrix.main.addTranslation(0, 0, -westWall[i].wallLength);
			ModelMatrix.main.setShaderMatrix();
			westWall[i].Draw();
		}
		ModelMatrix.main.popMatrix();
		
		ModelMatrix.main.pushMatrix();
		ModelMatrix.main.addTranslation(-1, 0, 1);
		for(int i = 0; i < southWall.length; i++) {
			ModelMatrix.main.addTranslation(southWall[i].wallLength, 0, 0);
			ModelMatrix.main.setShaderMatrix();
			southWall[i].Draw();
		}
		ModelMatrix.main.popMatrix();
		
		
	}
	
	public void GenerateMaze() {
		int x = rand.nextInt(cells.length);
		int y = rand.nextInt(cells.length);
		
		Stack<Cell> cellStack = new Stack<Cell>();
		
		Cell curCell = this.cells[x][y];
		
		// Start the procedure
		cellStack.push(curCell);
		curCell.visited = true;
		
		boolean[] directions = new boolean[4];
		
		while(!cellStack.isEmpty()) {
			
			// Check adjacent cells
			if(CellCanGoLeft(curCell)) {
				directions[0] = true;
			}
			if(CellCanGoUp(curCell)) {
				directions[1] = true;
			}
			if(CellCanGoRight(curCell)) {
				directions[2] = true;
			}
			if(CellCanGoDown(curCell)) {
				directions[3] = true;
			}
			
			if(isDeadEnd(directions)) {
				// pop stack and continue
				curCell = cellStack.pop();
				continue;
			}
			
			// Choose a random available direction
			int direction = chooseDirection(directions);
			
			while(direction < 0) {
				direction = chooseDirection(directions);
			}
			
			// Break the wall down and go to the direction
			int xPos = (int)curCell.position.x;
			int zPos = (int)curCell.position.z;
			
			if(direction == 0) {
				// go left
				curCell = cells[xPos - 1][zPos];
				curCell.hasEastWall = false;
			} else if(direction == 1) {
				// go up
				curCell.hasNorthWall = false;
				curCell = cells[xPos][zPos + 1];
			} else if(direction == 2) {
				// go right
				curCell.hasEastWall = false;
				curCell = cells[xPos + 1][zPos];
			} else if(direction == 3) {
				// go down
				curCell = cells[xPos][zPos - 1];
				curCell.hasNorthWall = false;
			}
			// Mark the cell and push it
			curCell.visited = true;
			cellStack.push(curCell);
			
			// Reset the directions array
			Arrays.fill(directions, false);
		}
	}
	
	public int chooseDirection(boolean[] directions) {
		int index = rand.nextInt(directions.length);
		
		if(directions[index]) {
			return index;
		} else {
			return -1;
		}

	}
	
	public boolean isDeadEnd(boolean[] directions) {
		for(boolean b : directions) {
			if(b) {
				return false;
			}
		}
	    return true;
	}
	
	public boolean CellCanGoLeft(Cell curCell) {
		int xPos = (int)curCell.position.x;
		int zPos = (int)curCell.position.z;
		
		if(curCell.position.x <= 0){
			return false;
		} else if(cells[xPos - 1][zPos].visited) {
			return false;
		}
		return true;
	}
	
	public boolean CellCanGoUp(Cell curCell) {
		int xPos = (int)curCell.position.x;
		int zPos = (int)curCell.position.z;
		
		if(curCell.position.z >= cells.length - 1) {
			return false;
		} else if(cells[xPos][zPos + 1].visited) {
			return false;
		}
		return true;
	}
	
	public boolean CellCanGoRight(Cell curCell) {
		int xPos = (int)curCell.position.x;
		int zPos = (int)curCell.position.z;
		
		if(curCell.position.x >= cells.length - 1) {
			return false;
		} else if(cells[xPos + 1][zPos].visited) {
			return false;
		}
		return true;
	}
	
	public boolean CellCanGoDown(Cell curCell) {
		int xPos = (int)curCell.position.x;
		int zPos = (int)curCell.position.z;
		
		if(curCell.position.z <= 0) {
			return false;
		} else if(cells[xPos][zPos - 1].visited) {
			return false;
		}
		return true;
	}
}
