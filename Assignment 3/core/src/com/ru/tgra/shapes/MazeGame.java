package com.ru.tgra.shapes;


import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import com.badlogic.gdx.utils.BufferUtils;

public class MazeGame extends ApplicationAdapter implements InputProcessor {

	private FloatBuffer matrixBuffer;

	private int renderingProgramID;
	private int vertexShaderID;
	private int fragmentShaderID;

	private int positionLoc;
	private int normalLoc;

	private int modelMatrixLoc;
	private int viewMatrixLoc;
	private int projectionMatrixLoc;

	private int colorLoc;
	
	private Maze myMaze;
	
	private Player myPlayer;
	
	@Override
	public void create () {
		
		Gdx.input.setInputProcessor(this);

		String vertexShaderString;
		String fragmentShaderString;

		vertexShaderString = Gdx.files.internal("shaders/simple3D.vert").readString();
		fragmentShaderString =  Gdx.files.internal("shaders/simple3D.frag").readString();

		vertexShaderID = Gdx.gl.glCreateShader(GL20.GL_VERTEX_SHADER);
		fragmentShaderID = Gdx.gl.glCreateShader(GL20.GL_FRAGMENT_SHADER);
	
		Gdx.gl.glShaderSource(vertexShaderID, vertexShaderString);
		Gdx.gl.glShaderSource(fragmentShaderID, fragmentShaderString);
	
		Gdx.gl.glCompileShader(vertexShaderID);
		Gdx.gl.glCompileShader(fragmentShaderID);
		
		System.out.println(Gdx.gl.glGetShaderInfoLog(vertexShaderID));
		System.out.println(Gdx.gl.glGetShaderInfoLog(fragmentShaderID));

		renderingProgramID = Gdx.gl.glCreateProgram();
	
		Gdx.gl.glAttachShader(renderingProgramID, vertexShaderID);
		Gdx.gl.glAttachShader(renderingProgramID, fragmentShaderID);
	
		Gdx.gl.glLinkProgram(renderingProgramID);

		positionLoc				= Gdx.gl.glGetAttribLocation(renderingProgramID, "a_position");
		Gdx.gl.glEnableVertexAttribArray(positionLoc);

		normalLoc				= Gdx.gl.glGetAttribLocation(renderingProgramID, "a_normal");
		Gdx.gl.glEnableVertexAttribArray(normalLoc);

		modelMatrixLoc			= Gdx.gl.glGetUniformLocation(renderingProgramID, "u_modelMatrix");
		viewMatrixLoc			= Gdx.gl.glGetUniformLocation(renderingProgramID, "u_viewMatrix");
		projectionMatrixLoc	= Gdx.gl.glGetUniformLocation(renderingProgramID, "u_projectionMatrix");

		colorLoc				= Gdx.gl.glGetUniformLocation(renderingProgramID, "u_color");

		Gdx.gl.glUseProgram(renderingProgramID);

		OrthographicProjection3D(0, Gdx.graphics.getWidth(), 0, Gdx.graphics.getHeight(), -2, 2);
		
		//COLOR IS SET HERE
		Gdx.gl.glUniform4f(colorLoc, 0.7f, 0.2f, 0, 1);

		//BoxGraphic.create(positionLoc, normalLoc);
		SphereGraphic.create(positionLoc, normalLoc);
		SincGraphic.create(positionLoc);
		CoordFrameGraphic.create(positionLoc);

		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

		ModelMatrix.main = new ModelMatrix();
		ModelMatrix.main.loadIdentityMatrix();
		ModelMatrix.main.setShaderMatrix(modelMatrixLoc);

		Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);

		//OrthographicProjection3D(-2, 2, -2, 2, 1, 100);
		PerspectiveProjection3D();
		
		Point3D startingPosition = new Point3D(0.5f, 0.5f, -0.5f);
		
		myPlayer = new Player(startingPosition);
		myPlayer.camera.setShaderMatrix(viewMatrixLoc);
		
		new Pyramid(positionLoc, normalLoc);
		
		myMaze = new Maze(positionLoc, normalLoc);
		
		Gdx.input.setCursorCatched(true);
	}

	private void input()
	{
		float deltaTime = Gdx.graphics.getDeltaTime();
		
		if(Gdx.input.isKeyPressed(Input.Keys.A)) {
			myPlayer.goLeft(myPlayer.speed * deltaTime);
		}
		if(Gdx.input.isKeyPressed(Input.Keys.D)) {
			myPlayer.goRight(myPlayer.speed * deltaTime);
		}
		if(Gdx.input.isKeyPressed(Input.Keys.W)) {
			myPlayer.goForward(myPlayer.speed * deltaTime);
		}
		if(Gdx.input.isKeyPressed(Input.Keys.S)) {
			myPlayer.goBack(myPlayer.speed * deltaTime);
		}
		
		myPlayer.camera.setShaderMatrix(viewMatrixLoc);
	}
	
	private void update()
	{
		//do all updates to the game
		ArrayList<Cell> cellsToCollide = new ArrayList<Cell>();
		
		int xPos = (int) myPlayer.position.x;
		int zPos = Math.abs((int) myPlayer.position.z);
		
		Cell playerCell = myMaze.cells[xPos][zPos];
		
		cellsToCollide.add(playerCell);
		
		if(myMaze.CellCanGoDown(playerCell)) {
			playerCell = myMaze.cells[xPos][zPos - 1];
			cellsToCollide.add(playerCell);
		}
		if(myMaze.CellCanGoLeft(playerCell)) {
			playerCell = myMaze.cells[xPos - 1][zPos];
			cellsToCollide.add(playerCell);
		}
		
		for(Cell cell: myMaze.southWall) {
			Collide(cell, myPlayer);
		}
		for(Cell cell: myMaze.westWall) {
			Collide(cell, myPlayer);
		}
		// Loop through relevant cells and collide player with them
		for(Cell cell : cellsToCollide) {
			Collide(cell, myPlayer);
		}
		
		cellsToCollide.clear();
		
		//System.out.println(cellsToCollide.get(0).position.x + " " + cellsToCollide.get(0).position.z);
		
	}
	
	private void Collide(Cell cell, Player player) {
		
		if(cell.hasNorthWall) {
			if(player.position.x > cell.northWall.position.x + cell.wallLength || player.position.x < cell.northWall.position.x - cell.wallLength ) {
				// This means that the player is not in the cell we are colliding with right now
				return;
			} else if(Math.abs(player.position.z - 0.1) >= cell.northWall.position.z - 0.05 && Math.abs(player.position.z - 0.1) <= cell.northWall.position.z) {
				player.position.z = -cell.northWall.position.z + 0.15f;
				
			} else if(Math.abs(player.position.z + 0.1) <= cell.northWall.position.z + 0.05 && Math.abs(player.position.z + 0.1) >= cell.northWall.position.z) {
				player.position.z = -cell.northWall.position.z - 0.15f;
			}
		}
		if(cell.hasEastWall) {
			if(Math.abs(player.position.z) > cell.eastWall.position.z + cell.wallLength || Math.abs(player.position.z) < cell.eastWall.position.z - cell.wallLength) {
				// This means that the player is not in the cell we are colliding with right now
				return;
			} else if(player.position.x + 0.1 >= cell.eastWall.position.x - 0.05 && player.position.x + 0.1 <= cell.eastWall.position.x) {
				player.position.x = cell.eastWall.position.x - 0.15f;
			} else if(player.position.x - 0.1 <= cell.eastWall.position.x + 0.05 && player.position.x - 0.1 >= cell.eastWall.position.x) {
				player.position.x = cell.eastWall.position.x + 0.15f;
			}
		}
	}
	
	private void display()
	{
		//do all actual drawing and rendering here
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		Gdx.gl.glUniform4f(colorLoc, 0.9f, 0.3f, 0.1f, 1.0f);

		ModelMatrix.main.loadIdentityMatrix();
		
		myMaze.Draw();
		
		ModelMatrix.main.pushMatrix();
		ModelMatrix.main.addScale(0.1f, 0.1f, 0.1f);
		ModelMatrix.main.setShaderMatrix();
		SphereGraphic.drawSolidSphere();
		ModelMatrix.main.popMatrix();
	}
	

	@Override
	public void render () {
		input();
		//put the code inside the update and display methods, depending on the nature of the code
		update();
		display();

	}

	

	private void OrthographicProjection3D(float left, float right, float bottom, float top, float near, float far) {
		float[] pm = new float[16];

		pm[0] = 2.0f / (right - left); pm[4] = 0.0f; pm[8] = 0.0f; pm[12] = -(right + left) / (right - left);
		pm[1] = 0.0f; pm[5] = 2.0f / (top - bottom); pm[9] = 0.0f; pm[13] = -(top + bottom) / (top - bottom);
		pm[2] = 0.0f; pm[6] = 0.0f; pm[10] = 2.0f / (near - far); pm[14] = (near + far) / (near - far);
		pm[3] = 0.0f; pm[7] = 0.0f; pm[11] = 0.0f; pm[15] = 1.0f;

		matrixBuffer = BufferUtils.newFloatBuffer(16);
		matrixBuffer.put(pm);
		matrixBuffer.rewind();
		Gdx.gl.glUniformMatrix4fv(projectionMatrixLoc, 1, false, matrixBuffer);

		pm[0] = 1.0f; pm[4] = 0.0f; pm[8] = 0.0f; pm[12] = 0.0f;
		pm[1] = 0.0f; pm[5] = 1.0f; pm[9] = 0.0f; pm[13] = 0.0f;
		pm[2] = 0.0f; pm[6] = 0.0f; pm[10] = 1.0f; pm[14] = 0.0f;
		pm[3] = 0.0f; pm[7] = 0.0f; pm[11] = 0.0f; pm[15] = 1.0f;

		matrixBuffer = BufferUtils.newFloatBuffer(16);
		matrixBuffer.put(pm);
		matrixBuffer.rewind();
		Gdx.gl.glUniformMatrix4fv(viewMatrixLoc, 1, false, matrixBuffer);
	}

	private void PerspectiveProjection3D() {
		float[] pm = new float[16];
		
		float n = 0.1f;
		float f = 0.15f;
		
		float eq1 = -(f+n/f-n);
		float eq2 = -((2*f*n)/f-n);

		pm[0] = 1.0f; pm[4] = 0.0f; pm[8] = 0.0f; pm[12] = 0.0f;
		pm[1] = 0.0f; pm[5] = 1.0f; pm[9] = 0.0f; pm[13] = 0.0f;
		pm[2] = 0.0f; pm[6] = 0.0f; pm[10] = eq1; pm[14] = eq2;
		pm[3] = 0.0f; pm[7] = 0.0f; pm[11] = -1.0f; pm[15] = 0.0f;

		matrixBuffer = BufferUtils.newFloatBuffer(16);
		matrixBuffer.put(pm);
		matrixBuffer.rewind();
		Gdx.gl.glUniformMatrix4fv(projectionMatrixLoc, 1, false, matrixBuffer);

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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		
		float deltaTime = Gdx.graphics.getDeltaTime();
		
		// Roll around the Y vector
		float xAngle = -((Gdx.graphics.getWidth() / 2) - screenX) * deltaTime * 5;
		myPlayer.camera.LookAround(xAngle);
		
		// Roll around the X vector
		float yAngle = -((Gdx.graphics.getHeight() / 2) - screenY) * deltaTime * 5;
		myPlayer.camera.Pitch(yAngle);
		
		Gdx.input.setCursorPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
		
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		myPlayer.camera.Slide(0, 0, amount);
		return false;
	}


}