package com.ru.tgra.shapes;

public class Pyramid {
	
	public float spaceBetweenBlocks, radius;
	Point3D position, startPos;
	BoxGraphic newBox;
	public boolean breatheOut;
	Shader shader;
	public boolean golden;
	float angle = 0;
	
	public Pyramid(Shader shader, Point3D pos) {
		this.shader = shader;
		this.spaceBetweenBlocks = 0.03f;
		this.position = new Point3D(pos);
		this.startPos = new Point3D(pos);
		this.newBox = new BoxGraphic(shader.getVertexPointer(), shader.getNormalPointer());
		this.breatheOut = true;
		this.radius = 0.05f;
		this.golden = false;
	}

	public void Draw() {
		
		drawCubeAtPos();
		
		position.x -= spaceBetweenBlocks * (9/2) + spaceBetweenBlocks / 2;
		position.z -= spaceBetweenBlocks * (9/2) + spaceBetweenBlocks / 2;
		
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
		this.position = new Point3D(this.startPos);
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
		if(angle < 90) {
			angle += 0.5f;
			ModelMatrix.main.pushMatrix();
			ModelMatrix.main.addTranslation(position.x, position.y, position.z);
			ModelMatrix.main.addScale(0.02f, 0.02f, 0.02f);
			ModelMatrix.main.addRotationY(angle);
			ModelMatrix.main.setShaderMatrix();
			newBox.drawSolidCube();
			ModelMatrix.main.popMatrix();
		} else {
			angle = 0;
		}
		
	}
}
