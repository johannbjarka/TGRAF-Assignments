package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.GL20;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import com.badlogic.gdx.utils.BufferUtils;

public class OurAwesomeCannonGame extends ApplicationAdapter implements InputProcessor {

	private FloatBuffer projectionMatrix;

	private int renderingProgramID;
	private int vertexShaderID;
	private int fragmentShaderID;

	private int positionLoc;

	private int modelMatrixLoc;
	private int projectionMatrixLoc;

	private int colorLoc;
	
	private Cannon cannon;
	
	private ArrayList<Rectangle> rectangles;
	private ArrayList<Rectangle> tempRectangles;
	
	private ArrayList<Line> lines;
	private ArrayList<Line> tempLines;
	
	private Point3D startPoint;
	private Point3D endPoint;
	
	private boolean isDrawingLine;
	private boolean isDrawingRect;

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
		//Rectangle.create(positionLoc);
		
		lines = new ArrayList<Line>();
		tempLines = new ArrayList<Line>();
		
		rectangles = new ArrayList<Rectangle>();
		tempRectangles = new ArrayList<Rectangle>();
		
		isDrawingLine = false;
		isDrawingRect = false;
		
		cannon = new Cannon(positionLoc);
		
		Gdx.input.setInputProcessor(this);
	}
	
	private void update() {
		float deltaTime = Gdx.graphics.getDeltaTime();

		cannon.update(deltaTime);
		
		// Check for collisions
		for(Line line : lines) {
			Collide(line, cannon.cannonBall, deltaTime);
		}
		
		for(Rectangle rectangle : rectangles) {
			for(Line side : rectangle.sides) {
				Collide(side, cannon.cannonBall, deltaTime);
			}
		}
		
	}
	
	private void input() {
		float deltaTime = Gdx.graphics.getDeltaTime();
		cannon.input(deltaTime);
	}
	
	private void display() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		cannon.display(colorLoc);

		ModelMatrix.main.setShaderMatrix();
		for(Line line : lines) {
			Gdx.gl.glUniform4f(colorLoc, 0, 0, 0, 1);
			line.drawSolidLine();
		}
		
		ModelMatrix.main.setShaderMatrix();
		for(Line line : tempLines) {
			Gdx.gl.glUniform4f(colorLoc, 0, 0, 0, 1);
			line.drawSolidLine();
		}
		
		ModelMatrix.main.setShaderMatrix();
		for(Rectangle rectangle : rectangles) {
			Gdx.gl.glUniform4f(colorLoc, 0, 0, 0, 1);
			rectangle.drawOutlineSquare();
		}
		
		ModelMatrix.main.setShaderMatrix();
		for(Rectangle rectangle : tempRectangles) {
			Gdx.gl.glUniform4f(colorLoc, 0, 0, 0, 1);
			rectangle.drawOutlineSquare();
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

	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// Create the start point
		if(button == Buttons.RIGHT) {
			isDrawingLine = true;
		} else if(button == Buttons.LEFT) {
			isDrawingRect = true;
		}
		startPoint = new Point3D(screenX, Gdx.graphics.getHeight() - screenY, 1);
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// Save the end point
		endPoint = new Point3D(screenX, Gdx.graphics.getHeight() - screenY, 1);
		
		if(button == Buttons.RIGHT) {
			// Save the final line in the final line array
			isDrawingLine = false;
			Line newLine = new Line(positionLoc, startPoint, endPoint);
			lines.add(newLine);
		} else if(button == Buttons.LEFT) {
			// Save the final rectangle in the final rectangle array
			isDrawingRect = false;
			Rectangle newRectangle = new Rectangle(positionLoc, startPoint, endPoint);
			rectangles.add(newRectangle);
		}
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// Save the temporary end point
		Point3D tempEnd = new Point3D(screenX, Gdx.graphics.getHeight() - screenY, 1);
		
		if(isDrawingLine) {
			// Draw the lines
			tempLines.clear();
			
			Line tempLine = new Line(positionLoc, startPoint, tempEnd);
			
			tempLines.add(tempLine);
		} else if(isDrawingRect) {
			// Draw the rectangle
			tempRectangles.clear();
			
			Rectangle tempRectangle = new Rectangle(positionLoc, startPoint, tempEnd);
			
			tempRectangles.add(tempRectangle);
			
		}
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}
}