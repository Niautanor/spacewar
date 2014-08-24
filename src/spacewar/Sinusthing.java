package spacewar;

import org.lwjgl.Sys;
import org.lwjgl.util.vector.Vector2f;

public class Sinusthing implements Collidable {
	private double t;
	private Sprite sprite;
	private boolean x,y;
	
	public Sinusthing(boolean x, boolean y, Texture tex) {
		sprite = new Sprite(0.25f,0.25f,0.0f,0.0f,0.0f, tex);
		this.x = x;
		this.y = y;
		t = 0;
	}
	
	public void render() {
		sprite.render();
	}
	
	public Vector2f getPos() {
		return sprite.getPos();
	}
	
	public float getRadius() {
		return sprite.getRadius();
	}
	
	public void update(double delta) {
		t += delta;
		if(x) {
			sprite.pos.y = (float) Math.sin(t);
		}
		if(y) {
			sprite.pos.x = (float) Math.sin(t);
		}
		sprite.rot = (float) t;
	}
}
