#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif

uniform vec3 u_scale;
uniform vec3 u_offset;

varying vec2 v_color;

void main() {
	vec2 tc = gl_FragCoord.xy;
	vec3 gNormal = vec3(sin(tc.x * u_scale.x + u_offset.x), sin(tc.y * u_scale.y + u_offset.y), u_scale.z);
	
	vec2 lPos = v_color.xy * 2.0 - vec2(1.0, 1.0);
    vec3 normal = -normalize(vec3(lPos, 1.0) * gNormal);
    vec3 cNormal = normal * 0.5 + 0.5;
    float distance = min(1.0, length(lPos));
    gl_FragColor = vec4(cNormal, clamp(1.0 - distance, 0.0, 1.0));
}
