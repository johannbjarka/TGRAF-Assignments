package com.ru.tgra.shapes;

public class Cell {
	
	BoxGraphic northWall;
	BoxGraphic eastWall;
	
	float wallThickness;
	float wallLength;
	float wallHeight;
	
	boolean hasNorthWall;
	boolean hasEastWall;
	
	boolean visited;
	Shader shader;
	Point3D position;
	
	public Cell(Shader shader, Point3D pos) {
		this.shader = shader;
		this.position = pos;
		this.northWall = new BoxGraphic(shader.getVertexPointer(),shader.getNormalPointer());
		this.eastWall = new BoxGraphic(shader.getVertexPointer(),shader.getNormalPointer());
		
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
			shader.setMaterialDiffuse(0.48f, 0.45f, 0.37f, 1.0f);
			ModelMatrix.main.setShaderMatrix();
			eastWall.drawSolidCube();
			ModelMatrix.main.popMatrix();
		}
		
		if(hasNorthWall) {
			ModelMatrix.main.pushMatrix();
			ModelMatrix.main.addTranslation(wallLength / 2, wallHeight / 2, -wallLength);
			ModelMatrix.main.addScale(wallLength, wallHeight, wallThickness);
			shader.setMaterialDiffuse(0.48f, 0.45f, 0.37f, 1.0f);
			ModelMatrix.main.setShaderMatrix();
			northWall.drawSolidCube();
			ModelMatrix.main.popMatrix();
		}
		ModelMatrix.main.pushMatrix();
		ModelMatrix.main.addTranslation(wallLength / 2, 0, -wallLength / 2);
		ModelMatrix.main.addScale(wallLength, 0.1f, wallLength);
		shader.setMaterialDiffuse(0.54f, 0.27f, 0.07f, 1.0f);
		ModelMatrix.main.setShaderMatrix();
		northWall.drawSolidCube();
		ModelMatrix.main.popMatrix();
		
		ModelMatrix.main.pushMatrix();
		ModelMatrix.main.addTranslation(wallLength / 2, wallHeight, -wallLength / 2);
		ModelMatrix.main.addScale(wallLength, 0.1f, wallLength);
		shader.setMaterialDiffuse(0.48f, 0.45f, 0.37f, 1.0f);
		ModelMatrix.main.setShaderMatrix();
		northWall.drawSolidCube();
		ModelMatrix.main.popMatrix();
		
		
		
	}
}
