package com.mygdx.game;

import java.nio.FloatBuffer;

import com.badlogic.gdx.utils.BufferUtils;

public class ModelMatrix extends Matrix {
	
	float[] M2;
	public ModelMatrix() {
		super();
		M2 = new float[16];
	}
	
	public void addTranslation(float Tx, float Ty, float Tz) {
		matrix.put(12, matrix.get(0) * Tx + matrix.get(4) * Ty + matrix.get(8) * Tz + matrix.get(12));
		matrix.put(13, matrix.get(1) * Tx + matrix.get(5) * Ty + matrix.get(9) * Tz + matrix.get(13));
		matrix.put(14, matrix.get(2) * Tx + matrix.get(6) * Ty + matrix.get(10) * Tz + matrix.get(14));
	}
	
	public void addScale(float Sx, float Sy, float Sz) {
		matrix.put(0, Sx * matrix.get(0));
		matrix.put(1, Sx * matrix.get(1));
		matrix.put(2, Sx * matrix.get(2));
		
		matrix.put(4, Sx * matrix.get(4));
		matrix.put(5, Sx * matrix.get(5));
		matrix.put(6, Sx * matrix.get(6));
		
		matrix.put(8, Sx * matrix.get(8));
		matrix.put(9, Sx * matrix.get(9));
		matrix.put(10, Sx * matrix.get(10));
	}
	
	public void addRotationZ(float angle) {
		float c = (float)Math.cos((double)angle * Math.PI / 180.0);
		float s = (float)Math.sin((double)angle * Math.PI / 180.0);
		
		M2[0] = c; M2[4] = -s; M2[8] = 0; M2[12] = 0;
		M2[1] = s; M2[5] = c; M2[9] = 0; M2[13] = 0;
		M2[2] = 0; M2[6] = 0; M2[10] = 1; M2[14] = 0;
		M2[3] = 0; M2[7] = 0; M2[11] = 0; M2[15] = 1;
		
		this.addTransformationMatrix(M2);
	}
	
	public void pushMatrix() {
		M2[0] = matrix.get(0);
		M2[1] = matrix.get(1);
		M2[2] = matrix.get(2);
		M2[3] = matrix.get(3);
		M2[4] = matrix.get(4);
		M2[5] = matrix.get(5);
		M2[6] = matrix.get(6);
		M2[7] = matrix.get(7);
		M2[8] = matrix.get(8);
		M2[9] = matrix.get(9);
		M2[10] = matrix.get(10);
		M2[11] = matrix.get(11);
		M2[12] = matrix.get(12);
		M2[13] = matrix.get(13);
		M2[14] = matrix.get(14);
		M2[15] = matrix.get(15);
		
		FloatBuffer tmp = BufferUtils.newFloatBuffer(16);
		tmp.put(M2);
		matrixStack.push(tmp);
	}
	
	public void popMatrix() {
		FloatBuffer tmp = matrixStack.pop();
		matrix.put(0, tmp.get(0));
		matrix.put(1, tmp.get(1));
		matrix.put(2, tmp.get(2));
		matrix.put(3, tmp.get(3));
		matrix.put(4, tmp.get(4));
		matrix.put(5, tmp.get(5));
		matrix.put(6, tmp.get(6));
		matrix.put(7, tmp.get(7));
		matrix.put(8, tmp.get(8));
		matrix.put(9, tmp.get(9));
		matrix.put(10, tmp.get(10));
		matrix.put(11, tmp.get(11));
		matrix.put(12, tmp.get(12));
		matrix.put(13, tmp.get(13));
		matrix.put(14, tmp.get(14));
		matrix.put(15, tmp.get(15));
	}
	
}
