package spacewar;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;

/** Manages Textures to avoid loading the same texture multiple times
 * (and more importantly to avoid storing it in video memory multiple times)
 */
public class TextureManager {
	private HashMap<String, Texture> textureMap;
	
	public TextureManager() {
		textureMap = new HashMap<String, Texture>();
	}
	
	/** loads and creates the texture but doesn't put it in the textureMap yet
	 * @param filename the texture file to load
	 */
	private Texture createTexture(String filename) throws IOException {
		InputStream in = new FileInputStream(filename);
		BufferedImage img = ImageIO.read(in);
		in.close();
		
		int tWidth = img.getWidth();
		int tHeight = img.getHeight();
		
		int[] pixels = new int[tWidth * tHeight];
		img.getRGB(0, 0, tWidth, tHeight, pixels, 0, tWidth);
		ByteBuffer buf = BufferUtils.createByteBuffer(tWidth * tHeight * 4);
		
		for (int y = 0; y < tHeight; y++) {
			for (int x = 0; x < tWidth; x++) {
				buf.putInt(pixels[y * tWidth + x]);
			}
		}
		
		buf.flip();
		
		int texId = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, texId);
		
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, tWidth, tHeight, 0, GL_RGBA, GL_UNSIGNED_BYTE, buf);
		
		if (glGetError() != GL_NO_ERROR) {
			throw new IOException("couldn't create texture");
		}
		return new Texture(texId);
	}
	
	public Texture getTexture(String filename) throws IOException {
		Texture tex = textureMap.get(filename);
		if(tex == null) {
			tex = createTexture(filename);
			textureMap.put(filename, tex);
		}
		return tex;
	}
}
