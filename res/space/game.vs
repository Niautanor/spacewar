#version 150
in vec2 iPos;
in vec2 iTex;

out vec2 pass_texCoord;

uniform mat4 modelMatrix;

void main() {
	gl_Position = modelMatrix * vec4(iPos,0.0,1.0);
	pass_texCoord = iTex;
}