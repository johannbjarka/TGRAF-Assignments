package com.mygdx.game;

import java.nio.FloatBuffer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.BufferUtils;

class Circle {
	private static FloatBuffer vertexBuffer;
	private static int vertexPointer;
	
	private static int verticesPerCircle;
	
	public static void create(int vertexPointer) {
		verticesPerCircle = 500;
		Circle.vertexPointer = vertexPointer;

		vertexBuffer = BufferUtils.newFloatBuffer(2*verticesPerCircle);
		
		double var = 0.0f;
		for(int i = 0; i < verticesPerCircle; i++) {
			vertexBuffer.put(2*i, (float) Math.cos(var));
			vertexBuffer.put(2*i + 1,(float) Math.sin(var));
			
			var += 2.0 * Math.PI / (double) verticesPerCircle;
		}
		vertexBuffer.rewind();
	}
	
	public static void drawSolidCircle() {
		Gdx.gl.glVertexAttribPointer(vertexPointer, 2, GL20.GL_FLOAT,
				false, 0, vertexBuffer);
		
		Gdx.gl.glDrawArrays(GL20.GL_TRIANGLE_FAN, 0, verticesPerCircle);
	}
}