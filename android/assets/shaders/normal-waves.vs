uniform mat4 u_projModelView;
attribute vec4 a_position;
attribute vec2 a_color;
varying vec2 v_color;

void main()
{
	v_color = a_color;
	gl_Position =  u_projModelView * a_position;
}
