package spacewar;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL20.*;

public class Texture {
	private int id;
	
	public Texture(int id) {
		this.id = id;
	}
	
	public void bind() {
		//we only use one texture unit in this project
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, id);
	}
	
	public void unbind() {
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, 0);
	}

	public boolean isTexture() {
		return glIsTexture(id);
	}
}
