package spacewar;

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

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix2f;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;

public class Game {
	private Shader shader;
	//window width / height
	private int WW,WH;

	// unsure
	private int occlusionQuery;
	private ShaderMatrix4f projectionMatrix, viewMatrix;

	// testing
	private Sinusthing x, y;
	private Sprite screen;
	private int alphaTestLocation;
	private TextureManager textureManager;

	public Game(int width, int height) {
		shader = new Shader();
		WW = width;
		WH = height;
		
		occlusionQuery = 0;
		
		projectionMatrix = new ShaderMatrix4f();
		
		//currently unused
		viewMatrix = new ShaderMatrix4f();
	}

	public void init() throws Exception {
		shader.init();
		// just bind it now and clean up later
		shader.bind();
		Sprite.setShaderDataLocation(shader.getAttribLoc("iPos"),
				shader.getAttribLoc("iTex"),
				shader.getUniformLoc("modelMatrix"));

		//unsure - probably yes
		occlusionQuery = glGenQueries();
		
		//initialize an orthogonal projection
		projectionMatrix.setIdentity();
		projectionMatrix.m00 = 2.0f / WW;
		projectionMatrix.m11 = 2.0f / WH;
		
		//I currently don't have a camera //just some zoom so that you can see something
		viewMatrix.setIdentity();
		viewMatrix.m00 = 360.0f;
		viewMatrix.m11 = 360.0f;
		
		projectionMatrix.setLocation(shader.getUniformLoc("projectionMatrix"));
		viewMatrix.setLocation(shader.getUniformLoc("viewMatrix"));

		// testing
		textureManager = new TextureManager();
		Texture tex1 = textureManager.getTexture("res/spaceship.png");
		Texture tex2 = textureManager.getTexture("res/white.png");
		x = new Sinusthing(false, true, tex1);
		y = new Sinusthing(true, false, tex1);
		screen = new Sprite(2.0f, 2.0f, 0.0f, 0.0f, 0.0f, tex2);
		alphaTestLocation = shader.getUniformLoc("alpha_test");
	}

	public void cleanup() {
		shader.unbind();
		shader.cleanup();
	}

	public void render() throws Exception {
		//set projection and view matrices
		projectionMatrix.setUniform();
		viewMatrix.setUniform();
		
		//glColorMask effects glClear
		//if I didn't put this here it would create artifacts when collision detection sets the color mask to 0
		glColorMask(true, true, true, true);
		glClear(GL_COLOR_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);

		//that means almost no fragment will be discarded
		//except for those with 0.0f as alpha
		glUniform1f(alphaTestLocation, 0.1f);
		
		glEnable(GL_STENCIL_TEST);
		glStencilFunc(GL_ALWAYS, 0, 0);
		glStencilOp(GL_INCR, GL_INCR,GL_INCR);
		
		//depth test enabled could cause z fighting
		//I don't want z fighting
		glDisable(GL_DEPTH_TEST);
		
		//blending to get cool transparency effects
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		//render the objects
		x.render();
		y.render();
		
		glStencilFunc(GL_EQUAL, 2, 0xFF);
		screen.render();

	}

	private boolean testCollision(Collidable a, Collidable b) {
		if (Vector2f.sub(a.getPos(), b.getPos(), null).lengthSquared() < Math
				.pow(a.getRadius() + b.getRadius(), 2)) {
			// Pixel perfect collision detection taken from
			// https://code.google.com/p/tf4r/wiki/PixelPerfectCollisionDetection
			
			//set projection and view matrices
			projectionMatrix.setUniform();
			viewMatrix.setUniform();

			// clear the stencil buffer
			glClear(GL_STENCIL_BUFFER_BIT);

			// enable stencil testing
			glEnable(GL_STENCIL_TEST);

			// we don't want to draw any images and only care about the stencil
			// buffer
			glColorMask(false, false, false, false);

			// set the stencil func/op so that the first sprite always sets bits
			// in the stencil buffer
			glStencilFunc(GL_ALWAYS, 1, 1);
			glStencilOp(GL_REPLACE, GL_REPLACE, GL_REPLACE);
			
			//only pixels with an alpha value above .5 will be considered
			glUniform1f(alphaTestLocation, 0.5f);
			
			//blending could cause unwanted effects
			glDisable(GL_BLEND);

			// render a into the stencil buffer
			a.render();

			glStencilFunc(GL_EQUAL, 1, 1);
			glStencilOp(GL_KEEP, GL_KEEP, GL_KEEP);

			glBeginQuery(GL_SAMPLES_PASSED, occlusionQuery);

			b.render();

			glEndQuery(GL_SAMPLES_PASSED);

			int result;
			do {
				result = glGetQueryObjecti(occlusionQuery,
						GL_QUERY_RESULT_AVAILABLE);
			} while (result == 0);
			// TODO: this loops around 400 times before getting the result
			// it might be possible to optimize it by having a queue of
			// occlusion queries
			// and rendering other collisions while waiting for the result of
			// the first query

			return glGetQueryObjecti(occlusionQuery, GL_QUERY_RESULT) > 0;
		}
		return false;
	}

	//testing
	private boolean collided = false;
	
	public void update(double delta) throws Exception {
		x.update(delta);
		y.update(delta);

		boolean col = testCollision(y,x);
		if (col && !collided) {
			collided = true;
			System.out.println("collision");
		} else if(!col && collided) {
			collided = false;
			System.out.println("no collision");
		}

	}
}
