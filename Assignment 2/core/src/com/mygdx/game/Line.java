package com.mygdx.game;

import java.nio.FloatBuffer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.BufferUtils;

public class Line {
	
	public Point3D B;
	public Point3D C;
	
	private FloatBuffer vertexBuffer;
	private int vertexPointer;
	
	public Line(int vertexPointer, Point3D start, Point3D end) {
		this.vertexPointer = vertexPointer;

		B = start;
		C = end;
		
		float[] array = {start.x, start.y,
						 end.x, end.y};

		vertexBuffer = BufferUtils.newFloatBuffer(8);
		vertexBuffer.put(array);
		vertexBuffer.rewind();

	}
	
	public void drawSolidLine() {
		Gdx.gl.glVertexAttribPointer(vertexPointer, 2, GL20.GL_FLOAT,
				false, 0, vertexBuffer);
		
		Gdx.gl.glDrawArrays(GL20.GL_LINES, 0, 2);
	}
}
