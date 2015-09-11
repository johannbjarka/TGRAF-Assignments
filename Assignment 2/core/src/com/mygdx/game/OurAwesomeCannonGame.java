package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import com.badlogic.gdx.utils.BufferUtils;

public class OurAwesomeCannonGame extends ApplicationAdapter {

	private FloatBuffer projectionMatrix;

	private int renderingProgramID;
	private int vertexShaderID;
	private int fragmentShaderID;

	private int positionLoc;

	private int modelMatrixLoc;
	private int projectionMatrixLoc;

	private int colorLoc;
	
	private Cannon cannon;
	
	private ArrayList<Line> lines;

	@Override
	public void create () {

		String vertexShaderString;
		String fragmentShaderString;

		vertexShaderString = Gdx.files.internal("shaders/simple2D.vert").readString();
		fragmentShaderString =  Gdx.files.internal("shaders/simple2D.frag").readString();

		vertexShaderID = Gdx.gl.glCreateShader(GL20.GL_VERTEX_SHADER);
		fragmentShaderID = Gdx.gl.glCreateShader(GL20.GL_FRAGMENT_SHADER);
	
		Gdx.gl.glShaderSource(vertexShaderID, vertexShaderString);
		Gdx.gl.glShaderSource(fragmentShaderID, fragmentShaderString);
	
		Gdx.gl.glCompileShader(vertexShaderID);
		Gdx.gl.glCompileShader(fragmentShaderID);

		renderingProgramID = Gdx.gl.glCreateProgram();
	
		Gdx.gl.glAttachShader(renderingProgramID, vertexShaderID);
		Gdx.gl.glAttachShader(renderingProgramID, fragmentShaderID);
	
		Gdx.gl.glLinkProgram(renderingProgramID);

		positionLoc				= Gdx.gl.glGetAttribLocation(renderingProgramID, "a_position");
		Gdx.gl.glEnableVertexAttribArray(positionLoc);

		modelMatrixLoc			= Gdx.gl.glGetUniformLocation(renderingProgramID, "u_modelMatrix");
		projectionMatrixLoc	= Gdx.gl.glGetUniformLocation(renderingProgramID, "u_projectionMatrix");

		colorLoc				= Gdx.gl.glGetUniformLocation(renderingProgramID, "u_color");

		Gdx.gl.glUseProgram(renderingProgramID);

		float[] pm = new float[16];

		pm[0] = 2.0f / Gdx.graphics.getWidth(); pm[4] = 0.0f; pm[8] = 0.0f; pm[12] = -1.0f;
		pm[1] = 0.0f; pm[5] = 2.0f / Gdx.graphics.getHeight(); pm[9] = 0.0f; pm[13] = -1.0f;
		pm[2] = 0.0f; pm[6] = 0.0f; pm[10] = 1.0f; pm[14] = 0.0f;
		pm[3] = 0.0f; pm[7] = 0.0f; pm[11] = 0.0f; pm[15] = 1.0f;

		projectionMatrix = BufferUtils.newFloatBuffer(16);
		projectionMatrix.put(pm);
		projectionMatrix.rewind();
		Gdx.gl.glUniformMatrix4fv(projectionMatrixLoc, 1, false, projectionMatrix);

		//COLOR IS SET HERE
		Gdx.gl.glUniform4f(colorLoc, 0.7f, 0.2f, 0, 1);
		
		Gdx.gl.glClearColor(0.33f, 0.52f, 0.7f, 1.0f);
		
		ModelMatrix.main = new ModelMatrix();
		ModelMatrix.main.loadIdentityMatrix();
		
		ModelMatrix.main.setShaderMatrix(modelMatrixLoc);
		
		Circle.create(positionLoc);
		Rectangle.create(positionLoc);
		
		lines = new ArrayList<Line>();
		
		
		
		cannon = new Cannon();
	}
	
	private void update() {
		float deltaTime = Gdx.graphics.getDeltaTime();
		cannon.input(deltaTime);
		cannon.update(deltaTime);
		// Check for collisions
		for(Line line : lines) {
			Collide(line, cannon.cannonBall, deltaTime);
		}
		
	}
	
	private void input() {
		if(Gdx.input.justTouched()){
			System.out.println("mousedown");
			Line newLine = new Line();
			newLine.create(positionLoc, new Point3D(250, 500,1), new Point3D(750, 400,1));
			lines.add(newLine);
		}
	}
	
	private void display() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		cannon.display(colorLoc);
		
		
		for(Line line : lines) {
			ModelMatrix.main.pushMatrix();
			Gdx.gl.glUniform4f(colorLoc, 0, 0, 0, 1);
			ModelMatrix.main.setShaderMatrix();
			line.drawSolidLine();
			ModelMatrix.main.popMatrix();
		}
	
	}
	
	private void Collide(Line line, CannonBall cb, float deltaTime) {
		Vector3D n = new Vector3D(0, 0, 0);
		n.x = -(line.C.y - line.B.y);
		n.y = line.C.x - line.B.x; 
		
		float t_hit = (n.x * (line.B.x - cb.position.x) + n.y * (line.B.y - cb.position.y))
				/ (n.x * cb.velocity.x + n.y * cb.velocity.y);
				
		if(t_hit <= deltaTime && t_hit > 0) {
			Point3D p_hit = new Point3D(0, 0, 0);
			
			p_hit.x = cb.position.x + cb.velocity.x * t_hit;
			p_hit.y = cb.position.y * cb.velocity.y * t_hit;
			
			if((p_hit.x >= line.B.x && p_hit.x <= line.C.x) || (p_hit.x >= line.C.x && p_hit.x <= line.B.x)) {
				Vector3D reflectedMotion = new Vector3D(0, 0, 0);
				
				float lengthOfN = (float) Math.sqrt(n.x * n.x + n.y * n.y);
				n.x = n.x/lengthOfN;
				n.y = n.y/lengthOfN;
				
				float AdotN = cb.velocity.x * n.x + cb.velocity.y * n.y;
				reflectedMotion.x = cb.velocity.x - 2 * AdotN * n.x;
				reflectedMotion.y = cb.velocity.y - 2 * AdotN * n.y;
				
				cb.velocity = reflectedMotion;
			}
		}
		
		
	}

	@Override
	public void render () {
		update();
		display();
		input();
	}
}