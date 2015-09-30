package com.ru.tgra.shapes;

public class Player {

	public Camera camera;
	public Point3D position;
	
	public Player(Point3D pos) {
		this.position = pos;
		this.camera = new Camera();
		
		Point3D center = new Point3D(0,0,0);
		Vector3D upVector = new Vector3D(0,1,0);
		
		this.camera.Look3D(position, center, upVector);
	}
	
}
