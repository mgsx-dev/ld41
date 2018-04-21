#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif

varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform sampler2D u_texture1;

uniform float u_amount;

void main() {
    vec2 tc = v_texCoords;
    vec4 cNormal = texture2D(u_texture1, tc);
    vec3 gNormal = normalize(cNormal.xyz * 2.0 - 1.0);
    vec4 cColor = texture2D(u_texture, tc + gNormal.xy * u_amount * cNormal.a);
    gl_FragColor = cColor;
}
