package spacewar;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.glu.GLU;

class Main {
	public static void main(String[] args) {
		PixelFormat pixelFormat = new PixelFormat(8, 8, 8);
		ContextAttribs attribs = new ContextAttribs(3, 2)
				.withForwardCompatible(true).withProfileCore(true);
		try {
			Display.setDisplayMode(new DisplayMode(1280, 720));
			Display.create(pixelFormat, attribs);
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(0);
		}

		exitOnGLError("Init");

		Game game = new Game(1280,720);

		try {
			game.init();
		} catch (Exception e) {
			System.out.println("Error initializing Game: " + e.getMessage());
			e.printStackTrace();
			System.exit(-1);
		}

		exitOnGLError("init");

		long t1, t2;
		double delta = 0.0d;

		try {
			while (!Display.isCloseRequested()) {
				t1 = Sys.getTime();
				
				game.update(delta);
				game.render();
				
				Display.update();
				exitOnGLError("Rendering");
				
				t2 = Sys.getTime();
				delta = ((double) (t2 - t1)) / Sys.getTimerResolution();
				//debugging
				if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)) delta *= 0.1d;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Display.destroy();
			System.exit(-2);
		}

		game.cleanup();

		Display.destroy();
	}

	public static void exitOnGLError(String errorMessage) {
		int errorValue = GL11.glGetError();

		if (errorValue != GL11.GL_NO_ERROR) {
			String errorString = GLU.gluErrorString(errorValue);
			System.err.println("ERROR - " + errorMessage + ": " + errorString);

			if (Display.isCreated())
				Display.destroy();
			System.exit(-1);
		}
	}
}
