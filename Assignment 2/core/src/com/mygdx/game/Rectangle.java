package com.mygdx.game;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.BufferUtils;

class Rectangle {
	private FloatBuffer vertexBuffer;
	private int vertexPointer;
	
	public ArrayList<Line> sides;
	
	public Rectangle(int vertexPointer, Point3D start, Point3D end) {
		this.vertexPointer = vertexPointer;
		
		sides = new ArrayList<Line>();
		
		Point3D point1 = new Point3D(start.x, start.y, 1);
		Point3D point2 = new Point3D(start.x, end.y, 1);
		Point3D point3 = new Point3D(end.x, end.y, 1);
		Point3D point4 = new Point3D(end.x, start.y, 1);
		
		sides.add(new Line(vertexPointer, point1, point2));
		sides.add(new Line(vertexPointer, point2, point3));
		sides.add(new Line(vertexPointer, point3, point4));
		sides.add(new Line(vertexPointer, point4, point1));
		
		float[] array = {start.x, start.y,
						 start.x, end.y,
						 end.x, end.y,
						 end.x, start.y};

		vertexBuffer = BufferUtils.newFloatBuffer(8);
		vertexBuffer.put(array);
		vertexBuffer.rewind();
	}
	
	public void drawSolidSquare() {
		Gdx.gl.glVertexAttribPointer(vertexPointer, 2, GL20.GL_FLOAT,
				false, 0, vertexBuffer);
		
		Gdx.gl.glDrawArrays(GL20.GL_TRIANGLE_FAN, 0, 4);
	}
	
	public void drawOutlineSquare() {
		Gdx.gl.glVertexAttribPointer(vertexPointer, 2, GL20.GL_FLOAT,
				false, 0, vertexBuffer);
		
		Gdx.gl.glDrawArrays(GL20.GL_LINE_LOOP, 0, 4);
	}

}
