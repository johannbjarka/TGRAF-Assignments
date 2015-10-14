
#ifdef GL_ES
precision mediump float;
#endif

uniform vec4 u_lightDiffuse;

uniform vec4 u_materialDiffuse;

uniform float u_materialShininess;

varying vec4 v_normal;
varying vec4 v_s;
varying vec4 v_h;

void main()
{
	// Lighting
	
	float lambert = max(0.0, dot(v_normal, v_s) / (length(v_normal) * length(v_s))); // Intensity
	float phong = max(0.0, dot(v_normal, v_h) / (length(v_normal) * length(v_h)));
	
	gl_FragColor = lambert * u_lightDiffuse * u_materialDiffuse + pow(phong, u_materialShininess) * u_lightDiffuse * vec4(1,1,1,1);
}