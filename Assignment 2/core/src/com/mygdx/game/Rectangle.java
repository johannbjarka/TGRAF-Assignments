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
		
		for(int i = 0; i < 4; i++) {
			//Line newSide = new Line(vertexPointer, )
		}
		
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
