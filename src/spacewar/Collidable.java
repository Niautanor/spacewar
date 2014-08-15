package spacewar;

import org.lwjgl.util.vector.Vector2f;

public interface Collidable {
	public Vector2f getPos();
	public float getRadius();
	
	/**
	 * Render the object. The collision detection algorithm works with the stencil buffer, so the stencil buffer is taboo here.
	 */
	public void render();
}