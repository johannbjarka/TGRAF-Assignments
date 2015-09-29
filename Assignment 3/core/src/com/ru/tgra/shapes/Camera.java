package com.ru.tgra.shapes;

import java.nio.FloatBuffer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.BufferUtils;

public class Camera {
	
	Point3D eye;
	Vector3D u, v, n;
	
	private FloatBuffer matrixBuffer;
	
	public Camera(Point3D _eye) {
		eye = _eye;
		u = new Vector3D(0,0,0);
		v = new Vector3D(0,0,0);
		n = new Vector3D(0,0,0);
	}
	
	public void Look3D(Point3D eye, Point3D center, Vector3D up) {
		
		this.n = Vector3D.difference(eye, center);
		this.u = up.cross(n);
		
		n.normalize();
		u.normalize();
		
		this.v = n.cross(u);
	}
	
	public void Slide(float delU, float delV, float delN) {
		eye.x += delU * u.x + delV * v.x + delN * n.x;
		eye.y += delU * u.y + delV * v.y + delN * n.y;
		eye.z += delU * u.z + delV * v.z + delN * n.z;
	}
	
	public void RotateAxis(Vector3D a, Vector3D b, float angle) {
		float ang = (float) (Math.PI/180 * angle);
		float C = (float) Math.cos(ang), S = (float) Math.sin(ang);
		
		Vector3D t = new Vector3D(C * a.x + S * b.x, C * a.y + S * b.y, C * a.z + S * b.z);
		b.set(-S * a.x + C * b.x, -S * a.y + C * b.y, -S * a.z + C * b.z);
		a.set(t.x, t.y, t.z);
		
	}
	
	public void Roll(float angle)
	{
		RotateAxis( u, v, angle );
	}
	
	public void Pitch(float angle) {
		
		RotateAxis( n, v, angle );
	}
	public void Yaw(float angle) {
		
		RotateAxis( u, n, angle );
	}
	public void setShaderMatrix(int viewMatrixLoc) {
		Vector3D minusEye = new Vector3D(-eye.x, -eye.y, -eye.z);
		
		float[] pm = new float[16];

		pm[0] = u.x; pm[4] = u.y; pm[8] = u.z; pm[12] = minusEye.dot(u);
		pm[1] = v.x; pm[5] = v.y; pm[9] = v.z; pm[13] = minusEye.dot(v);
		pm[2] = n.x; pm[6] = n.y; pm[10] = n.z; pm[14] = minusEye.dot(n);
		pm[3] = 0.0f; pm[7] = 0.0f; pm[11] = 0.0f; pm[15] = 1.0f;

		matrixBuffer = BufferUtils.newFloatBuffer(16);
		matrixBuffer.put(pm);
		matrixBuffer.rewind();
		Gdx.gl.glUniformMatrix4fv(viewMatrixLoc, 1, false, matrixBuffer);
	}

}
