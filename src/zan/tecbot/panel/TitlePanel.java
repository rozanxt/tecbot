package zan.tecbot.panel;

import static org.lwjgl.opengl.GL11.glColor4f;
import zan.game.GameCore;
import zan.game.input.InputManager;
import zan.game.panel.IPanel;
import zan.game.sprite.TextManager;
import zan.game.util.CameraPort;

public class TitlePanel implements IPanel {
	
	private boolean initialized;
	
	public TitlePanel() {
		initialized = false;
	}
	
	public boolean isInitialized() {return initialized;}
	
	public void init() {
		initialized = true;
	}
	public void destroy() {initialized = false;}
	
	public void update() {
		if (InputManager.isButtonDown(0) || InputManager.isButtonDown(1) || InputManager.isButtonDown(2)) {
			GameCore.changePanel(GameCore.Panel.GAME);
		}
	}
	
	public void render() {		
		CameraPort.viewGUI();
		glColor4f(0f, 0.6f, 0.8f, 1f);
		TextManager.renderText("Tecbot: Click to play!", "defont", GameCore.GAME_HEIGHT*GameCore.getScreenRatio()/2f, GameCore.GAME_HEIGHT/2f, 12f, 1);
		glColor4f(1f, 1f, 1f, 1f);
	}
	
}
