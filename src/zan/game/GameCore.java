package zan.game;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.gluUnProject;
import static org.lwjgl.util.glu.GLU.gluProject;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.util.Rectangle;

import zan.game.input.InputManager;
import zan.game.panel.IPanel;
import zan.game.resource.ResourceReader;
import zan.game.sound.SoundManager;
import zan.game.sprite.SpriteManager;
import zan.game.sprite.TextManager;
import zan.game.sprite.TextureManager;
import zan.game.util.CameraPort;
import zan.game.util.GameUtility;
import zan.game.util.IconLoader;
import zan.tecbot.panel.GamePanel;
import zan.tecbot.panel.TitlePanel;

/** The main GameCore class */
public class GameCore {
	private static final String LOGNAME = "GameCore :: ";
	
	/** Game title */
	private static final String title = "Tecbot";
	
	/** Screen resolutions */
	public static int SCR_WIDTH, SCR_HEIGHT;
	
	/** Game dimensions */
	public static int GAME_WIDTH = 800, GAME_HEIGHT = 600;
	
	/** Game started state */
	private static boolean started = false;
	/** Game running state */
	private static boolean running = false;
	
	/** Fullscreen mode */
	private static boolean fullScreen = false;
	
	/** Vertical synchronization */
	private static boolean vSync = true;
	
	/** Viewport */
	private static Rectangle viewPort;
	
	/** Frame rate */
	private static final int FRAME_RATE = 60;
	
	/** Timing statistics */
	private static final int[] fpsStore = new int[60];
	private static int currentFPS = 60;
	private static int frameCount = 0;
	
	/** Singleton instance */
	private static GameCore gameCore = null;
	
	/** Panels */
	public static enum Panel {TITLE, GAME}
	private static IPanel corePanel;
	
	/** Constructor */
	private GameCore() {}
	
	/** Start game */
	private static void start() {
		if (started) return;
		started = true;
		running = false;
		
		initUtilities();
		initDisplay();
		initInput();
		initGL();
		loadResources();
		
		gameCore = new GameCore();
		gameCore.run();
		
		exit();
	}
	
	/** Exit cleanup */
	private static void exit() {
		started = false;
		running = false;
		
		TextureManager.destroy();
		SoundManager.destroy();
		Display.destroy();
		System.exit(0);
	}
	
	/** Initialize utilities */
	private static void initUtilities() {
		GameUtility.init();
		TextureManager.init();
		SpriteManager.init();
		TextManager.init();
		SoundManager.init();
		CameraPort.init();
	}
	
	/** Initialize display */
	private static void initDisplay() {
		try {
			Display.setTitle(title);
			Display.setIcon(IconLoader.loadIcon("icon.png"));
			Display.setResizable(false);
			adjustDisplayMode();
			Display.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	/** Initialize input */
	private static void initInput() {
		try {
			Keyboard.create();
			Mouse.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(0);
		}
		Keyboard.enableRepeatEvents(true);
		InputManager.init();
	}
	
	/** Initialize graphics library */
	private static void initGL() {
		glEnable(GL_TEXTURE_2D);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		
		adjustViewPort();
	}
	
	/** Load resources */
	private static void loadResources() {
		TextureManager.loadTextureData(new ResourceReader("sprites.res").getData().getNode("textures"));
		SpriteManager.loadSpriteData(new ResourceReader("sprites.res").getData().getNode("sprites"));
		TextManager.loadFontFile(new ResourceReader("fonts.res").getData().getNode("defont"));
		SoundManager.loadSoundData(new ResourceReader("sounds.res").getData().getNode("sounds"));
	}
	
	// -------------------------------------------------------------------------------------------------------- //
	
	/** Run game loop */
	private void run() {
		if (running) return;
		running = true;
		init();
		
		long timeThen = getTicks();
		long timeNow, timeElapsed;
		long timeBefore, timeAfter, timeDiff;
		int updateToDo = 1;
		long frameTicked = 0L;
		
		while (running) {
			if (Display.isCloseRequested()) {
				running = false;
				break;
			}
			if (Display.isFullscreen() != fullScreen) {
				adjustDisplayMode();
				adjustViewPort();
			}
			
			if (Display.isActive() || Display.isDirty()) {
				timeNow = getTicks();
				
				if (timeNow > timeThen) {
					timeElapsed = timeNow - timeThen;
					double updateTicked = (double)(FRAME_RATE * timeElapsed) / 1000.0;
					updateToDo = (int)Math.max(0.0, updateTicked - frameTicked);
					
					if (updateToDo > 20) {
						updateToDo = 1;
						timeThen = timeNow;
						frameTicked = 0;
					}
				} else if (timeNow < timeThen) {
					updateToDo = 0;
					timeThen = timeNow;
					frameTicked = 0;
				} else {
					updateToDo = 0;
				}
				
				if (updateToDo > 0) {
					for (int i = 0; i < updateToDo; i ++) {
						if (i > 0) Display.processMessages();
						
						timeBefore = getTicks();
						update();
						
						timeAfter = getTicks();
						timeDiff = timeAfter - timeBefore;
						
						if (timeDiff < 0 || timeDiff > (double)(1000.0 / 60.0)) {
							updateToDo = 0;
							timeThen = timeAfter;
							frameTicked = 1;
						}
						Thread.yield();
					}
					if (updateToDo > 0) {
						storeFPS(updateToDo);
						frameTicked += updateToDo;
					}
					
					render();
					Display.update();
				}
				
			} else {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {}
				
				if (Display.isVisible() || Display.isDirty()) render();
				Display.update();
				
				InputManager.setMouseGrabbed(false);
			}
		}
	}
	
	// -------------------------------------------------------------------------------------------------------- //
	
	public static void changePanel(Panel sp) {
		if (corePanel != null) corePanel.destroy();
		switch (sp) {
			case TITLE:
				corePanel = new TitlePanel();
				break;
			case GAME:
				corePanel = new GamePanel();
				break;
		}
		corePanel.init();
	}
	
	/** Init game */
	private void init() {
		changePanel(Panel.TITLE);
	}
	
	/** Update game */
	private void update() {
		InputManager.poll();
		
		if (corePanel != null && corePanel.isInitialized()) corePanel.update();
		
		if (InputManager.isKeyPressed(Keyboard.KEY_F11)) setFullScreen(!isFullScreen());
	}
	
	/** Render game */
	private void render() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		
		if (corePanel != null && corePanel.isInitialized()) corePanel.render();
	}
	
	// -------------------------------------------------------------------------------------------------------- //
	
	/** @return SCR_WIDTH to SCR_HEIGHT ratio */
	public static float getScreenRatio() {
		return (float)SCR_WIDTH / (float)SCR_HEIGHT;
	}
	/** @return GAME_WIDTH to GAME_HEIGHT ratio */
	public static float getGameRatio() {
		return (float)GAME_WIDTH / (float)GAME_HEIGHT;
	}
	
	/** @return screen scale factor */
	public static float getScreenScale() {
		float scale = 1f;
		
		if(getScreenRatio() > getGameRatio()) scale = ((float)SCR_HEIGHT / (float)GAME_HEIGHT);
		else if (getScreenRatio() < getGameRatio()) scale = ((float)SCR_WIDTH / (float)GAME_WIDTH);
		
		return scale;
	}
	
	/** @return screen crop length */
	public static float[] getScreenCrop() {
		float scale = getScreenScale();
		float crop_x = 0f;
		float crop_y = 0f;
		
		if(getScreenRatio() > getGameRatio()) crop_x = ((float)SCR_WIDTH - (float)GAME_WIDTH*scale)/2f;
		else if (getScreenRatio() < getGameRatio()) crop_y = ((float)SCR_HEIGHT - (float)GAME_HEIGHT*scale)/2f;
		
		float[] scalecrop = new float[2];
		scalecrop[0] = crop_x;
		scalecrop[1] = crop_y;
		
		return scalecrop;
	}
	
	/** Convert screen coordinates into logic coordinates */
	public static float[] ScreenToLogic(float scrx, float scry) {
		FloatBuffer modelview = ByteBuffer.allocateDirect(16*8).order(ByteOrder.nativeOrder()).asFloatBuffer();
		FloatBuffer projection = ByteBuffer.allocateDirect(16*8).order(ByteOrder.nativeOrder()).asFloatBuffer();
		IntBuffer viewport = ByteBuffer.allocateDirect(4*16).order(ByteOrder.nativeOrder()).asIntBuffer();
		FloatBuffer result = ByteBuffer.allocateDirect(3*8).order(ByteOrder.nativeOrder()).asFloatBuffer();
		
		glGetFloat(GL_MODELVIEW_MATRIX, modelview);
		glGetFloat(GL_PROJECTION_MATRIX, projection);
		glGetInteger(GL_VIEWPORT, viewport);
		
		gluUnProject(scrx, scry, 0f, modelview, projection, viewport, result);
		
		float[] lgccoord = new float[2];
		lgccoord[0] = result.get(0);
		lgccoord[1] = result.get(1);
		
		return lgccoord;
	}
	
	/** Convert logic coordinates into screen coordinates */
	public static float[] LogicToScreen(float lgcx, float lgcy) {
		FloatBuffer modelview = ByteBuffer.allocateDirect(16*8).order(ByteOrder.nativeOrder()).asFloatBuffer();
		FloatBuffer projection = ByteBuffer.allocateDirect(16*8).order(ByteOrder.nativeOrder()).asFloatBuffer();
		IntBuffer viewport = ByteBuffer.allocateDirect(4*16).order(ByteOrder.nativeOrder()).asIntBuffer();
		FloatBuffer result = ByteBuffer.allocateDirect(3*8).order(ByteOrder.nativeOrder()).asFloatBuffer();
		
		glGetFloat(GL_MODELVIEW_MATRIX, modelview);
		glGetFloat(GL_PROJECTION_MATRIX, projection);
		glGetInteger(GL_VIEWPORT, viewport);
		
		gluProject(lgcx, lgcy, 0f, modelview, projection, viewport, result);
		
		float[] scrcoord = new float[2];
		scrcoord[0] = result.get(0);
		scrcoord[1] = result.get(1);
		
		return scrcoord;
	}
	
	/** @return screen to logic unit ratio */
	public static float getScreenToLogicRatio() {
		return (ScreenToLogic(1f, 0f)[0] - ScreenToLogic(0f, 0f)[0])*getScreenScale();
	}
	
	/** @return screen boundaries */
	public static float[] getScreenOrigin() {
		float[] bottomleft = ScreenToLogic(0f, 0f);
		float[] topright = ScreenToLogic((float)SCR_WIDTH, (float)SCR_HEIGHT);
		
		float[] origin = new float[4];
		origin[0] = bottomleft[0];
		origin[1] = bottomleft[1];
		origin[2] = topright[0];
		origin[3] = topright[1];
		
		return origin;
	}
	
	/** @return game boundaries */
	public static float[] getLogicOrigin() {
		float[] bottomleft = ScreenToLogic(0f, 0f);
		float[] topright = ScreenToLogic((float)SCR_WIDTH, (float)SCR_HEIGHT);
		float ratio = getScreenToLogicRatio();
		
		float[] crop = getScreenCrop();
		float scale = getScreenScale();
		
		float[] origin = new float[4];
		origin[0] = bottomleft[0] + (crop[0]/scale)*ratio;
		origin[1] = bottomleft[1] + (crop[1]/scale)*ratio;
		origin[2] = topright[0] - (crop[0]/scale)*ratio;
		origin[3] = topright[1] - (crop[1]/scale)*ratio;
		
		return origin;
	}
	
	/** Set fullscreen mode */
	public static void setFullScreen(boolean fs) {fullScreen = fs;}
	/** @return true if fullscreen mode is on */
	public static boolean isFullScreen() {return fullScreen;}
	
	/** Set vertical synchronization */
	public static void setVSync(boolean vs) {vSync = vs;}
	/** @return true if vertical synchronized */
	public static boolean isVSync() {return vSync;}
	/** Initialize vertical synchronization */
	private static void initVSync() {
		Display.setVSyncEnabled(vSync);
	}
	
	/** Set viewport */
	private static void adjustViewPort() {
		glViewport(viewPort.getX(), viewPort.getY(), viewPort.getWidth(), viewPort.getHeight());
	}
	
	/** Adjust display mode */
	private static void adjustDisplayMode() {
		if (fullScreen) {
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			SCR_WIDTH = (int)screenSize.getWidth();
			SCR_HEIGHT = (int)screenSize.getHeight();
		} else {
			SCR_WIDTH = (int)GAME_WIDTH;
			SCR_HEIGHT = (int)GAME_HEIGHT;
		}
		
		setDisplayMode(SCR_WIDTH, SCR_HEIGHT, fullScreen);
		fullScreen = Display.isFullscreen();
		
		initVSync();
		
		viewPort = new Rectangle(0, 0, SCR_WIDTH, SCR_HEIGHT);
	}
	
	/** Set display mode */
	private static void setDisplayMode(int width, int height, boolean fs) {
		if ((Display.getDisplayMode().getWidth() == width) && (Display.getDisplayMode().getHeight() == height) && (Display.isFullscreen() == fs)) return;
		
		try {
			DisplayMode targetDisplayMode = null;
			
			if (fs) {
				DisplayMode[] modes = Display.getAvailableDisplayModes();
				int freq = 0;
				
				for (int i=0;i<modes.length;i++) {
					DisplayMode current = modes[i];
					
					if ((current.getWidth() == width) && (current.getHeight() == height)) {
						if ((targetDisplayMode == null) || (current.getFrequency() >= freq)) {
							if ((targetDisplayMode == null) || (current.getBitsPerPixel() > targetDisplayMode.getBitsPerPixel())) {
								targetDisplayMode = current;
								freq = targetDisplayMode.getFrequency();
							}
						}
						
						if ((current.getBitsPerPixel() == Display.getDesktopDisplayMode().getBitsPerPixel()) &&
						    (current.getFrequency() == Display.getDesktopDisplayMode().getFrequency())) {
							targetDisplayMode = current;
							break;
						}
					}
				}
			} else {
				targetDisplayMode = new DisplayMode(width,height);
			}
			
			if (targetDisplayMode == null) {
				System.err.println(LOGNAME + "Failed to find value display mode: " + width + "x" + height + " fullscreen=" + fs);
				return;
			}
			
			Display.setDisplayMode(targetDisplayMode);
			Display.setFullscreen(fs);
			
		} catch (LWJGLException e) {
			System.err.println(LOGNAME + "Unable to setup display mode " + width + "x" + height + " fullscreen=" + fs + ":\n" + e);
		}
	}
	
	/** Update FPS statistics */
	private static void storeFPS(int updateToDo) {
		frameCount++;
		if (frameCount == fpsStore.length) {
			frameCount = 0;
		}
		fpsStore[frameCount] = FRAME_RATE / updateToDo;
		int totalF = 0;
		for (int i = 0; i < fpsStore.length; i ++) {
			totalF += fpsStore[i];
		}
		currentFPS = totalF / fpsStore.length;
	}
	
	/** @return current FPS */
	public static int getFPS() {
		return currentFPS;
	}
	
	/** @return current time ticks */
	public static long getTicks() {
		return (Sys.getTime() * 1000) / Sys.getTimerResolution();
	}
	
	/** @return GameCore singleton instance */
	public static GameCore getInstance() {
		if (!started) return null;
		return gameCore;
	}
	
	// -------------------------------------------------------------------------------------------------------- //
	
	/** Main launcher */
	public static void main(String[] args) {
		GameCore.start();
	}
	
}
