package spacewar;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

import static org.lwjgl.opengl.GL20.*;

/**
 * Creates and initializes the shader used in the game
 */
public class Shader {
	private int prog,vshader,fshader;
	
	private static final String vshader_file = "res/space/game.vs";
	private static final String fshader_file = "res/space/game.fs";
	
	public Shader() {
		prog = vshader = fshader = 0;
	}
	
	public void init() throws IOException {
		prog = glCreateProgram();
		vshader = glCreateShader(GL_VERTEX_SHADER);
		fshader = glCreateShader(GL_FRAGMENT_SHADER);
		
		glShaderSource(vshader, readFile(vshader_file));
		glShaderSource(fshader, readFile(fshader_file));
		
		glCompileShader(vshader);
		glCompileShader(fshader);
		
		System.out.println("Vertex Shader compiled:\n" + glGetShaderInfoLog(vshader, 2048));
		System.out.println("Fragment Shader compiled:\n" + glGetShaderInfoLog(fshader, 2048));
		
		glAttachShader(prog, vshader);
		glAttachShader(prog, fshader);
		
		glLinkProgram(prog);
		glValidateProgram(prog);
		
		//Print linking log
		System.out.println("Program linked:\n" + glGetProgramInfoLog(prog, 2048));
	}
	
	public void cleanup() {
		glDetachShader(prog, vshader);
		glDetachShader(prog, fshader);
		glDeleteShader(fshader);
		glDeleteShader(vshader);
		glDeleteProgram(prog);
	}
	
	public int getUniformLoc(String uniform) {
		return glGetUniformLocation(prog, uniform);
	}
	
	public int getAttribLoc(String attribute) {
		return glGetAttribLocation(prog, attribute);
	}
	
	public void bind() {
		glUseProgram(prog);
	}
	
	public void unbind() {
		glUseProgram(0);
	}
	
	private static String readFile(String filename) throws IOException {
		FileInputStream stream = new FileInputStream(new File(filename));
			try {
				FileChannel fc = stream.getChannel();
				MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
				/* Instead of using default, pass in a decoder. */
				return Charset.defaultCharset().decode(bb).toString();
			}
			finally {
				stream.close();
			}
	}
}
