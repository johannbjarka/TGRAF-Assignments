package com.ru.tgra.shapes;

public class Player {

	public Camera camera;
	public Point3D position;
	float speed, maxSpeed;
	
	public Player(Point3D pos) {
		this.speed = 1f;
		this.maxSpeed = 5;
		this.position = pos;
		this.camera = new Camera();
		
		Point3D center = new Point3D(0,0,0);
		Vector3D upVector = new Vector3D(0,1,0);
		
		this.camera.Look3D(position, center, upVector);
	}
	
	public void goForward(float distance) {
		camera.SlideForward(distance);
	}
	public void goBack(float distance) {
		camera.SlideForward(-distance);
	}
	public void goLeft(float distance) {
		camera.Slide(-distance, 0, 0);
	}
	public void goRight(float distance) {
		camera.Slide(distance, 0, 0);
	}
}
