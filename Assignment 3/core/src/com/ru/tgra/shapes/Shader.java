package com.ru.tgra.shapes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

public class Shader {
	private int renderingProgramID;
	private int vertexShaderID;
	private int fragmentShaderID;

	private int positionLoc;
	private int normalLoc;
	
	private int modelMatrixLoc;
	private int viewMatrixLoc;
	private int projectionMatrixLoc;

	private int eyePositionLoc;
	//private int colorLoc;
	private int lightPositionLoc;
	private int lightDiffuseLoc;
	private int materialDiffuseLoc;
	private int materialShininessLoc;
	
	
	public Shader() {
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

		positionLoc = Gdx.gl.glGetAttribLocation(renderingProgramID, "a_position");
		Gdx.gl.glEnableVertexAttribArray(positionLoc);

		normalLoc = Gdx.gl.glGetAttribLocation(renderingProgramID, "a_normal");
		Gdx.gl.glEnableVertexAttribArray(normalLoc);

		modelMatrixLoc = Gdx.gl.glGetUniformLocation(renderingProgramID, "u_modelMatrix");
		viewMatrixLoc = Gdx.gl.glGetUniformLocation(renderingProgramID, "u_viewMatrix");
		projectionMatrixLoc	= Gdx.gl.glGetUniformLocation(renderingProgramID, "u_projectionMatrix");
		
		eyePositionLoc = Gdx.gl.glGetUniformLocation(renderingProgramID, "u_eyePosition");
		//colorLoc = Gdx.gl.glGetUniformLocation(renderingProgramID, "u_color");
		lightPositionLoc = Gdx.gl.glGetUniformLocation(renderingProgramID, "u_lightPosition");
		lightDiffuseLoc = Gdx.gl.glGetUniformLocation(renderingProgramID, "u_lightDiffuse");
		materialDiffuseLoc = Gdx.gl.glGetUniformLocation(renderingProgramID, "u_materialDiffuse");
		
		materialShininessLoc = Gdx.gl.glGetUniformLocation(renderingProgramID, "u_materialShininess");
		

		Gdx.gl.glUseProgram(renderingProgramID);
	}

	public void setEyePosition(float x, float y, float z, float w) {
		Gdx.gl.glUniform4f(eyePositionLoc, x, y, z, w);
	}
	public void setLightPosition(float x, float y, float z, float w) {
		Gdx.gl.glUniform4f(lightPositionLoc, x, y, z, w);
	}
	public void setLightDiffuse(float red, float green, float blue, float alpha) {
		Gdx.gl.glUniform4f(lightDiffuseLoc, red, green, blue, alpha);
	}
	public void setMaterialDiffuse(float red, float green, float blue, float alpha) {
		Gdx.gl.glUniform4f(materialDiffuseLoc, red, green, blue, alpha);
	}
	
	public void setShininess(float shine) {
		Gdx.gl.glUniform1f(materialShininessLoc, shine);
	}
	
	
	public int getVertexPointer() {
		return positionLoc;
	}
	public int getNormalPointer() {
		return normalLoc;
	}
	public int getViewMatrixPointer() {
		return viewMatrixLoc;
	}
	public int getModelMatrixPointer() {
		return modelMatrixLoc;
	}
	public int getProjectionMatrixPointer() {
		return projectionMatrixLoc;
	}
}
