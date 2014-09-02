package spacewar;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLE_STRIP;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glUniformMatrix4;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL21.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.*;
import static org.lwjgl.opengl.GL32.*;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class Sprite implements Collidable {
	private int vao,vbo;
	
	public Vector2f pos;
	public float rot;
	private float radius;
	
	private Texture tex;
	private ShaderMatrix4f modelMatrix;
	
	private static int modelMatrixUniformLocation = -1;
	private static int posAttribLocation = -1;
	private static int texAttribLocation = -1;
	
	public static void setShaderDataLocation(int iPos, int iTex, int modelMatrix) {
		//I probably will only need to set this once as I don't plan to have multiple shaders
		assert modelMatrixUniformLocation == -1
				&& posAttribLocation == -1
				&& texAttribLocation == -1;
		posAttribLocation = iPos;
		texAttribLocation = iTex;
		modelMatrixUniformLocation = modelMatrix;
	}
	
	public Sprite(float width, float height, float x, float y, float rotation, Texture texture) {
		//if this assertion fails rendering would have failed anyway
		//It wouldn't crash the program though, so I might remove it later
		assert modelMatrixUniformLocation != -1;
		
		
		//Prepare the raw vertex data
		FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(16);
		vertexBuffer.put(width/2).put(-height/2);
		vertexBuffer.put(width/2).put(height/2);
		vertexBuffer.put(-width/2).put(-height/2);
		vertexBuffer.put(-width/2).put(height/2);
		
		//add the texture coordinates
		//or don't because aparently this confuses the crap out of opengl occlusion query
		vertexBuffer.put(1.0f).put(1.0f);
		vertexBuffer.put(1.0f).put(0.0f);
		vertexBuffer.put(0.0f).put(1.0f);
		vertexBuffer.put(0.0f).put(0.0f);
		
		//signal that we are done writing to the buffer
		vertexBuffer.flip();
		
		modelMatrix = new ShaderMatrix4f();
		modelMatrix.setLocation(modelMatrixUniformLocation);
		pos = new Vector2f();
		
		pos.x = x;
		pos.y = y;
		radius = (float) Math.sqrt(width*width+height*height);
		rot = rotation;
		
		//generate and bind a vertex Array
		//a vertex Array stores all the information required to render an object
		//this includes vertex data location (you can user multiple buffers)
		vao = glGenVertexArrays();		
		glBindVertexArray(vao);
		
		//generate and bind a buffer to store our vertex data
		vbo = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);		
		
		//tell opengl (and our vertex Array object) where the vertex position data is in our buffer
		//0 is the index that is commonly used for position data in vertex shaders
		glVertexAttribPointer(posAttribLocation, 2, GL_FLOAT, false, 0, 0);
		//we need to enable this attribute or else bad things happen
		glEnableVertexAttribArray(posAttribLocation);
		
		//bind location of texture coordinates (normalized: true i suppose?) and the position data in front of it takes 8*4 bytes
		glVertexAttribPointer(texAttribLocation, 2, GL_FLOAT, true, 0, 8 * 4);
		glEnableVertexAttribArray(texAttribLocation);
		
		//after we are done configuring the object we can unbind the vertex array
		glBindVertexArray(0);
		
		//check if the texture is correct
		assert texture.isTexture();
		tex = texture;
	}
	
	public void cleanup() {
		glDeleteVertexArrays(vao);
		glDeleteBuffers(vbo);
	}
	
	public void render() {
		modelMatrix.setIdentity();
		modelMatrix.translate(pos);
		modelMatrix.rotate(rot, new Vector3f(0,0,1));
		modelMatrix.setUniform();
		
		tex.bind();
		
		glBindVertexArray(vao);
		glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
		glBindVertexArray(0);
		
		tex.unbind();
	}

	@Override
	public Vector2f getPos() {
		return pos;
	}

	@Override
	public float getRadius() {
		return radius;
	}
}
