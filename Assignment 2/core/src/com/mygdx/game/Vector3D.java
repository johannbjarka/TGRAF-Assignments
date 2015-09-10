package com.mygdx.game;

public class Vector3D {
	public float x;
	public float y;
	public float z;
	
	public Vector3D(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public void scale(float factor) {
		this.x = factor * x;
		this.y = factor * y;
		this.z = factor * z;
	}
}
