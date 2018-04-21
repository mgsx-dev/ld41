#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif

varying vec2 v_texCoords;
uniform sampler2D u_texture;

uniform vec3 u_scale;
uniform vec3 u_offset;

void main() {
	vec4 cBase = texture2D(u_texture, v_texCoords.xy);
    vec2 tc = gl_FragCoord.xy;
    vec3 gNormal = vec3(sin(tc.x * u_scale.x + u_offset.x), sin(tc.y * u_scale.y + u_offset.y), u_scale.z);
    vec3 cNormal = normalize(gNormal) * 0.5 + 0.5;
    gl_FragColor = vec4(cNormal, cBase.a + 1.0);
}
