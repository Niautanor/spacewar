package spacewar;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Matrix4f;
import static org.lwjgl.opengl.GL20.glUniformMatrix4;

import org.lwjgl.util.vector.Matrix4f;

/** A Class designed to simplify the process of feeding a Matrix into a shader
 * @author Tim
 */
public class ShaderMatrix4f extends Matrix4f {
	private static final long serialVersionUID = 1L;
	private int location;
	private FloatBuffer buffer;
	
	private void init() {
		location = -1;
		buffer = BufferUtils.createFloatBuffer(16);
	}
	
	public ShaderMatrix4f() {
		super();
		init();
	}
	
	public ShaderMatrix4f(Matrix4f src) {
		super(src);
		init();
	}
	
	/**
	 * Sets the location of the uniform this matrix will be stored in
	 * @param loc
	 */
	public void setLocation(int loc) {
		location = loc;
	}
	
	public int getLocation() {
		return location;
	}
	
	public void setUniform() {
		assert location != -1;
		store(buffer);
		buffer.flip();
		glUniformMatrix4(location, false, buffer);
	}
}