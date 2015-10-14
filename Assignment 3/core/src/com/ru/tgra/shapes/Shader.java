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
	
	private int globalAmbLoc;
	//private int colorLoc;
	private int lightPosition1Loc;
	private int lightPosition2Loc;
	private int spotDirectionLoc;
	private int lightColor1Loc;
	private int lightColor2Loc;
	private int materialDiffuseLoc;
	private int materialSpecLoc;
	private int materialEmisLoc;
	
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
		globalAmbLoc = Gdx.gl.glGetUniformLocation(renderingProgramID, "u_globalAmbient");
		lightPosition1Loc = Gdx.gl.glGetUniformLocation(renderingProgramID, "u_lightPosition1");
		lightColor1Loc = Gdx.gl.glGetUniformLocation(renderingProgramID, "u_lightColor1");
		lightPosition2Loc = Gdx.gl.glGetUniformLocation(renderingProgramID, "u_lightPosition2");
		spotDirectionLoc = Gdx.gl.glGetUniformLocation(renderingProgramID, "u_spotDirection");
		lightColor2Loc = Gdx.gl.glGetUniformLocation(renderingProgramID, "u_lightColor2");
		materialDiffuseLoc = Gdx.gl.glGetUniformLocation(renderingProgramID, "u_materialDiffuse");
		materialSpecLoc = Gdx.gl.glGetUniformLocation(renderingProgramID, "u_materialSpecular");
		materialShininessLoc = Gdx.gl.glGetUniformLocation(renderingProgramID, "u_materialShininess");
		materialEmisLoc = Gdx.gl.glGetUniformLocation(renderingProgramID, "u_materialEmission");
		

		Gdx.gl.glUseProgram(renderingProgramID);
	}

	public void setEyePosition(float x, float y, float z, float w) {
		Gdx.gl.glUniform4f(eyePositionLoc, x, y, z, w);
	}
	public void setGlobalAmbient(float red, float green, float blue, float alpha) {
		Gdx.gl.glUniform4f(globalAmbLoc, red, green, blue, alpha);
	}
	public void setLightPosition1(float x, float y, float z, float w) {
		Gdx.gl.glUniform4f(lightPosition1Loc, x, y, z, w);
	}
	public void setLightColor1(float red, float green, float blue, float alpha) {
		Gdx.gl.glUniform4f(lightColor1Loc, red, green, blue, alpha);
	}
	public void setLightPosition2(float x, float y, float z, float w) {
		Gdx.gl.glUniform4f(lightPosition2Loc, x, y, z, w);
	}
	public void setSpotDirection(float x, float y, float z, float w) {
		Gdx.gl.glUniform4f(spotDirectionLoc, x, y, z, w);
	}
	public void setLightColor2(float red, float green, float blue, float alpha) {
		Gdx.gl.glUniform4f(lightColor2Loc, red, green, blue, alpha);
	}
	public void setMaterialDiffuse(float red, float green, float blue, float alpha) {
		Gdx.gl.glUniform4f(materialDiffuseLoc, red, green, blue, alpha);
	}
	public void setMaterialSpecular(float red, float green, float blue, float alpha) {
		Gdx.gl.glUniform4f(materialSpecLoc, red, green, blue, alpha);
	}
	public void setMaterialEmission(float red, float green, float blue, float alpha) {
		Gdx.gl.glUniform4f(materialEmisLoc, red, green, blue, alpha);
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
