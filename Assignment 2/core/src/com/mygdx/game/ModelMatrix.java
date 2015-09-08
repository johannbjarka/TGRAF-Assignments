package com.mygdx.game;

public class ModelMatrix extends Matrix {
	
	public ModelMatrix() {
		super();
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
}
