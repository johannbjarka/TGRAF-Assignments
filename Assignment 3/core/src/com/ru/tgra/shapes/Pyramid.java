package com.ru.tgra.shapes;

public class Pyramid {
	
	public float spaceBetweenBlocks;
	Point3D position;
	BoxGraphic newBox;
	
	public Pyramid(int positionLoc, int normalLoc) {
		this.spaceBetweenBlocks = 1.3f;
		this.position = new Point3D(0,0,0);
		this.newBox = new BoxGraphic(positionLoc, normalLoc);
	}

	public void Draw() {
		
		drawCubeLine(9, 1);
		drawCubeLine(9, 3);
		drawCubeLine(9, 0);
		drawCubeLine(9, 2);
		
		position.x += spaceBetweenBlocks / 2;
		position.y += spaceBetweenBlocks / 1.5;
		position.z += spaceBetweenBlocks / 2;
		
		drawCubeLine(8, 1);
		drawCubeLine(8, 3);
		drawCubeLine(8, 0);
		drawCubeLine(8, 2);
		
		
		position.x += spaceBetweenBlocks / 2;
		position.y += spaceBetweenBlocks / 1.5;
		position.z += spaceBetweenBlocks / 2;
		
		drawCubeLine(7, 1);
		drawCubeLine(7, 3);
		drawCubeLine(7, 0);
		drawCubeLine(7, 2);
		
		position.x += spaceBetweenBlocks / 2;
		position.y += spaceBetweenBlocks / 1.5;
		position.z += spaceBetweenBlocks / 2;
		
		drawCubeLine(6, 1);
		drawCubeLine(6, 3);
		drawCubeLine(6, 0);
		drawCubeLine(6, 2);
		
		position.x += spaceBetweenBlocks / 2;
		position.y += spaceBetweenBlocks / 1.5;
		position.z += spaceBetweenBlocks / 2;
		
		drawCubeLine(5, 1);
		drawCubeLine(5, 3);
		drawCubeLine(5, 0);
		drawCubeLine(5, 2);
		
		position.x += spaceBetweenBlocks / 2;
		position.y += spaceBetweenBlocks / 1.5;
		position.z += spaceBetweenBlocks / 2;
		
		drawCubeLine(4, 1);
		drawCubeLine(4, 3);
		drawCubeLine(4, 0);
		drawCubeLine(4, 2);
		
		position.x += spaceBetweenBlocks / 2;
		position.y += spaceBetweenBlocks / 1.5;
		position.z += spaceBetweenBlocks / 2;
		
		drawCubeLine(3, 1);
		drawCubeLine(3, 3);
		drawCubeLine(3, 0);
		drawCubeLine(3, 2);
		
		position.x += spaceBetweenBlocks / 2;
		position.y += spaceBetweenBlocks / 1.5;
		position.z += spaceBetweenBlocks / 2;
		
		drawCubeLine(2, 1);
		drawCubeLine(2, 3);
		drawCubeLine(2, 0);
		drawCubeLine(2, 2);
		
		position.x += spaceBetweenBlocks;
		position.y += spaceBetweenBlocks / 1.5;
		position.z += spaceBetweenBlocks;
		
		drawCubeLine(1, 1);
		
		// Reset position so pyramid won't fly off somewhere
		this.position = new Point3D(0,0,0);
	}
	
	public void drawCubeLine(int num, int direction) {
		for(int i = 0; i < num; i++) {
			if(direction == -2){
				drawCubeAtPos();
				position.y -= spaceBetweenBlocks;
				
			} else if(direction == -1) {
				drawCubeAtPos();
				position.y += spaceBetweenBlocks;
				
			} else if(direction == 0) {
				drawCubeAtPos();
				position.x -= spaceBetweenBlocks;
				
			} else if(direction == 1) {
				drawCubeAtPos();
				position.x += spaceBetweenBlocks;
				
			} else if(direction == 2) {
				drawCubeAtPos();
				position.z -= spaceBetweenBlocks;
				
			} else if(direction == 3) {
				drawCubeAtPos();
				position.z += spaceBetweenBlocks;
				
			}
		}
	}
	
	public void drawCubeAtPos() {
		ModelMatrix.main.pushMatrix();
		ModelMatrix.main.addTranslation(position.x, position.y, position.z);
		ModelMatrix.main.addScale(1f, 1f, 1f);
		ModelMatrix.main.setShaderMatrix();
		newBox.drawSolidCube();
		ModelMatrix.main.popMatrix();
	}
}
