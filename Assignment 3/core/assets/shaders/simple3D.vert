
#ifdef GL_ES
precision mediump float;
#endif

attribute vec3 a_position;
attribute vec3 a_normal;

uniform mat4 u_modelMatrix;
uniform mat4 u_viewMatrix;
uniform mat4 u_projectionMatrix;

uniform vec4 u_eyePosition;

uniform vec4 u_globalAmbient;

uniform vec4 u_lightPosition1;
uniform vec4 u_lightPosition2;

varying vec4 v_normal;
varying vec4 v_s1;
varying vec4 v_h1;
varying vec4 v_s2;
varying vec4 v_h2;

void main()
{
	vec4 position = vec4(a_position.x, a_position.y, a_position.z, 1.0);
	position = u_modelMatrix * position;

	vec4 normal = vec4(a_normal.x, a_normal.y, a_normal.z, 0.0);
	normal = u_modelMatrix * normal;
	
	// Preparation for lighting
	vec4 v = u_eyePosition - position; // Direction to the camera
	
	v_normal = normal;
	// For each light
	v_s1 = u_lightPosition1 - position; // Direction to the light	
	v_h1 = v_s1 + v;

	v_s2 = u_lightPosition2 - position; // Direction to the light
	v_h2 = v_s2 + v;
	// End for each light 
	
	position = u_viewMatrix * position;

	gl_Position = u_projectionMatrix * position;
}