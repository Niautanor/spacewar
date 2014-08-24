#version 150

uniform sampler2D sprite_texture;

//Note: alpha test was deprecated in openGL 3.0 so I had to reimplement it here
uniform float alpha_test;

in vec2 pass_texCoord;

out vec4 o_FragColor;

void main() {
	o_FragColor = texture(sprite_texture, pass_texCoord);
	//this *might* cause performance problems
	//if rendering multiple objects takes a long time it might be beneficial
	//to write another shader that only deals with collision detection
	if (o_FragColor.a < alpha_test) {
		discard;
	}
}