#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif

varying vec2 v_color;

void main() {
	vec2 lPos = v_color.xy * 2.0 - vec2(1.0, 1.0);
    vec3 normal = -normalize(vec3(lPos, 0.0));
    vec3 cNormal = normal * 0.5 + 0.5;
    float distance = min(1.0, length(lPos));
    gl_FragColor = vec4(cNormal, clamp(1.0 - distance, 0.0, 1.0));
}
