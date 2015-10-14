
#ifdef GL_ES
precision mediump float;
#endif

uniform vec4 u_globalAmbient;

uniform vec4 u_lightColor1;
uniform vec4 u_lightColor2;
uniform vec4 u_spotDirection;

uniform vec4 u_materialDiffuse;
uniform vec4 u_materialSpecular;
uniform float u_materialShininess;
uniform vec4 u_materialEmission;

varying vec4 v_normal;
varying vec4 v_s1;
varying vec4 v_h1;
varying vec4 v_s2;
varying vec4 v_h2;

void main()
{
	// Lighting
	
	// For each light
	float lambert1 = max(0.0, dot(v_normal, v_s1) / (length(v_normal) * length(v_s1))); // Intensity
	float phong1 = max(0.0, dot(v_normal, v_h1) / (length(v_normal) * length(v_h1)));
	
	vec4 diffuseColor1 = lambert1 * u_lightColor1 * u_materialDiffuse;
	vec4 specularColor1 = pow(phong1, u_materialShininess) * u_lightColor1 * u_materialSpecular;
	
	vec4 light1CalcColor = diffuseColor1 + specularColor1;
	
	float lambert2 = max(0.0, dot(v_normal, v_s2) / (length(v_normal) * length(v_s2))); // Intensity
	float phong2 = max(0.0, dot(v_normal, v_h2) / (length(v_normal) * length(v_h2)));
	
	vec4 diffuseColor2 = lambert2 * u_lightColor2 * u_materialDiffuse;
	vec4 specularColor2 = pow(phong2, u_materialShininess) * u_lightColor2 * u_materialSpecular;
	
	float spotAttenuation = max(0.0, dot(-v_s2, u_spotDirection) / (length(v_s2) * length(u_spotDirection)));
	
	vec4 light2CalcColor = spotAttenuation * (diffuseColor2 + specularColor2);
	// End for each light
	gl_FragColor = u_globalAmbient * u_materialDiffuse + u_materialEmission + light1CalcColor + light2CalcColor;
}