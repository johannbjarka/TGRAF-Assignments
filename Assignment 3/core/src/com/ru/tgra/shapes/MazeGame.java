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
	
	Shader shader;
	
	private Maze myMaze;
	
	private Player myPlayer;
	
	@Override
	public void create () {
		
		shader = new Shader();
		
		Gdx.input.setInputProcessor(this);

		OrthographicProjection3D(0, Gdx.graphics.getWidth(), 0, Gdx.graphics.getHeight(), -2, 2);

		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

		ModelMatrix.main = new ModelMatrix();
		ModelMatrix.main.loadIdentityMatrix();
		ModelMatrix.main.setShaderMatrix(shader.getModelMatrixPointer());

		Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);

		//OrthographicProjection3D(-2, 2, -2, 2, 1, 100);
		PerspectiveProjection3D();
		
		Point3D startingPosition = new Point3D(0.5f, 0.5f, -0.5f);
		
		myPlayer = new Player(startingPosition);
		myPlayer.camera.setShaderMatrix(shader.getViewMatrixPointer());
		
		new Pyramid(shader.getVertexPointer(), shader.getNormalPointer());
		
		myMaze = new Maze(shader.getVertexPointer(), shader.getNormalPointer());
		
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
		if(Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
			Gdx.graphics.setDisplayMode(500, 500, false);
			Gdx.app.exit();
		}
		
		myPlayer.camera.setShaderMatrix(shader.getViewMatrixPointer());
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
			if(!(zPos <= 0)) {
				playerCell = myMaze.cells[xPos][zPos - 1];
				cellsToCollide.add(playerCell);
			}
		}
		if(myMaze.CellCanGoLeft(playerCell)) {
			if(!(xPos <= 0)) {
				playerCell = myMaze.cells[xPos - 1][zPos];
				cellsToCollide.add(playerCell);
			}
		}
		if(myMaze.CellCanGoRight(playerCell)) {
			if(!(xPos + 1 >= myMaze.cells.length)) {
				playerCell = myMaze.cells[xPos + 1][zPos];
				cellsToCollide.add(playerCell);
			}
		}
		if(myMaze.CellCanGoUp(playerCell)) {
			if(!(zPos + 1 >= myMaze.cells[xPos].length)) {
				playerCell = myMaze.cells[xPos][zPos + 1];
				cellsToCollide.add(playerCell);
			}
		}
		if(!(xPos <= 0) && !(zPos <= 0)) {
			playerCell = myMaze.cells[xPos - 1][zPos - 1];
			cellsToCollide.add(playerCell);
		}
		if(!(xPos <= 0) && !(zPos + 1 >= myMaze.cells[xPos].length)) {
			playerCell = myMaze.cells[xPos - 1][zPos + 1];
			cellsToCollide.add(playerCell);
		}
		if(!(xPos + 1 >= myMaze.cells[xPos].length) && !(zPos + 1 >= myMaze.cells[xPos].length)) {
			playerCell = myMaze.cells[xPos + 1][zPos + 1];
			cellsToCollide.add(playerCell);
		}
		if(!(xPos + 1 >= myMaze.cells[xPos].length) && !(zPos <= 0)) {
			playerCell = myMaze.cells[xPos + 1][zPos - 1];
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
	}
	
	private void Collide(Cell cell, Player player) {
		if(cell.hasNorthWall) {
			// Check if player is trying to walk into the side of the wall
			if(Math.abs(player.position.z) >= cell.northWall.position.z - 0.05f && Math.abs(player.position.z) <= cell.northWall.position.z + 0.05f) {
				if(player.position.x >= (cell.northWall.position.x - 0.1f) && player.position.x <= (cell.northWall.position.x + cell.wallLength / 2)) {
					player.position.x = cell.northWall.position.x - 0.11f;
				}
			}
			// Check from the other side of the wall
			if(Math.abs(player.position.z) >= cell.northWall.position.z - 0.05f && Math.abs(player.position.z) <= cell.northWall.position.z + 0.05f) {
				if(player.position.x <= (cell.northWall.position.x + cell.wallLength + 0.1f) && player.position.x >= (cell.northWall.position.x + cell.wallLength / 2)) {
					player.position.x = cell.northWall.position.x + cell.wallLength + 0.11f;
				}
			}
			if(player.position.x > cell.northWall.position.x + cell.wallLength || player.position.x < cell.northWall.position.x) {
				// This means that the player is not in the cell we are colliding with right now
				if(cell.hasEastWall) {
					// Do not return
				} else {
					return;
				}
			} else if(Math.abs(player.position.z - 0.1) >= cell.northWall.position.z - 0.05 && Math.abs(player.position.z - 0.1) <= cell.northWall.position.z) {
				player.position.z = -cell.northWall.position.z + 0.15f;
				
			} else if(Math.abs(player.position.z + 0.1) <= cell.northWall.position.z + 0.05 && Math.abs(player.position.z + 0.1) >= cell.northWall.position.z) {
				player.position.z = -cell.northWall.position.z - 0.15f;
			}
		}
		if(cell.hasEastWall) {
			// Check if player is trying to walk into the side of the wall
			if(player.position.x >= cell.eastWall.position.x - 0.05f && player.position.x <= cell.eastWall.position.x + 0.05f) {
				if(Math.abs(player.position.z) >= (Math.abs(cell.eastWall.position.z) - 0.14f) && (Math.abs(player.position.z) <= (Math.abs(cell.eastWall.position.z) + cell.wallLength / 2))) {
					player.position.z = -(cell.eastWall.position.z - 0.15f);
				}
			}
			// Check from the other side of the wall
			if(player.position.x >= cell.eastWall.position.x - 0.05f && player.position.x <= cell.eastWall.position.x + 0.05f) {
				if(Math.abs(player.position.z) <= (Math.abs(cell.eastWall.position.z) + cell.wallLength + 0.14f) && (Math.abs(player.position.z) >= (Math.abs(cell.eastWall.position.z) + cell.wallLength / 2))) {
					player.position.z = -(cell.eastWall.position.z + cell.wallLength + 0.15f);
				}
			}
			if(Math.abs(player.position.z) > cell.eastWall.position.z + cell.wallLength || Math.abs(player.position.z) < cell.eastWall.position.z) {
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

		//shader.setEyePosition(1.0f, 1.0f, 1.0f, 0.8f);
		shader.setEyePosition(myPlayer.position.x, myPlayer.position.y, myPlayer.position.z, 0.7f);
		shader.setLightPosition(myPlayer.position.x, myPlayer.position.y, myPlayer.position.z, 0.8f);
		//shader.setLightPosition(1.0f, 1.0f, 1.0f, 0.8f);
		
		shader.setLightDiffuse(1.0f, 1.0f, 1.0f, 0.0f);
		shader.setMaterialDiffuse(0.2f, 0.1f, 0.8f, 0.0f);
		shader.setShininess(10000.0f);
		
		ModelMatrix.main.loadIdentityMatrix();
		
		myMaze.Draw();
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
		Gdx.gl.glUniformMatrix4fv(shader.getProjectionMatrixPointer(), 1, false, matrixBuffer);

		pm[0] = 1.0f; pm[4] = 0.0f; pm[8] = 0.0f; pm[12] = 0.0f;
		pm[1] = 0.0f; pm[5] = 1.0f; pm[9] = 0.0f; pm[13] = 0.0f;
		pm[2] = 0.0f; pm[6] = 0.0f; pm[10] = 1.0f; pm[14] = 0.0f;
		pm[3] = 0.0f; pm[7] = 0.0f; pm[11] = 0.0f; pm[15] = 1.0f;

		matrixBuffer = BufferUtils.newFloatBuffer(16);
		matrixBuffer.put(pm);
		matrixBuffer.rewind();
		Gdx.gl.glUniformMatrix4fv(shader.getViewMatrixPointer(), 1, false, matrixBuffer);
	}

	private void PerspectiveProjection3D() {
		float[] pm = new float[16];
		
		float n = 0.01f;
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
		Gdx.gl.glUniformMatrix4fv(shader.getProjectionMatrixPointer(), 1, false, matrixBuffer);

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