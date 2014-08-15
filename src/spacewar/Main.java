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

class Main {
	public static void main(String[] args) {
		PixelFormat pixelFormat = new PixelFormat(8, 8, 8);
		ContextAttribs attribs = new ContextAttribs(3, 2)
				.withForwardCompatible(true).withProfileCore(true);
		try {
			Display.setDisplayMode(new DisplayMode(400, 400));
			Display.create(pixelFormat, attribs);
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(0);
		}

		exitOnGLError("Init");

		Game game = new Game();

		try {
			game.init();
		} catch (Exception e) {
			System.out.println("Error initializing Game " + e.getMessage());
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
				//10 fps for debugging
				//Display.sync(5);
				exitOnGLError("Rendering");
				
				t2 = Sys.getTime();
				delta = ((double) (t2 - t1)) / Sys.getTimerResolution();
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
			String errorString = "Da hast du wohl die Arschkarte gezogen";
			System.err.println("ERROR - " + errorMessage + ": " + errorString);

			if (Display.isCreated())
				Display.destroy();
			System.exit(-1);
		}
	}
}
