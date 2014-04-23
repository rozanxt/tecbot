package zan.game.util;

import static org.lwjgl.opengl.GL11.*;

import zan.game.GameCore;
import zan.game.input.InputManager;

public class CameraPort {
	
	private static float dcam_x, dcam_y;
	
	public static void init() {
		dcam_x = 0f;
		dcam_y = 0f;
	}
	
	public static void viewGUI() {
		float vw = GameCore.GAME_HEIGHT*GameCore.getScreenRatio();
		float vh = GameCore.GAME_HEIGHT;
		
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0f, vw, 0f, vh, -1f, 1f);
		glMatrixMode(GL_MODELVIEW);
	}
	
	public static void viewDynamicCam(float sx, float sy, float factor) {
		float vw = GameCore.GAME_HEIGHT*GameCore.getScreenRatio();
		float vh = GameCore.GAME_HEIGHT;
		
		if (InputManager.isMouseGrabbed()) {
			float vx = sx-(GameCore.GAME_WIDTH/2f)-GameCore.ScreenToLogic(GameCore.getScreenCrop()[0], GameCore.getScreenCrop()[1])[0];
			float vy = sy-(GameCore.GAME_HEIGHT/2f);
			float ox = (InputManager.getMouseX()-(GameCore.SCR_WIDTH/2f))*factor;
			float oy = (InputManager.getMouseY()-(GameCore.SCR_HEIGHT/2f))*factor;
			
			//if (Math.abs(ox) > 200f) ox = ox*200f/Math.abs(ox);
			//if (Math.abs(oy) > 150f) oy = oy*150f/Math.abs(oy);
			
			dcam_x += ((-vx-ox)-dcam_x)*0.1f; dcam_y += ((-vy-oy)-dcam_y)*0.1f;
		}
		
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0f, vw, 0f, vh, -1f, 1f);
		glTranslatef(dcam_x, dcam_y, 0f);
		glMatrixMode(GL_MODELVIEW);
	}
	
}
