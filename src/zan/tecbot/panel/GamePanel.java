package zan.tecbot.panel;

import static org.lwjgl.opengl.GL11.*;

import java.util.ArrayList;

import zan.game.GameCore;
import zan.game.input.InputManager;
import zan.game.object.Collision;
import zan.game.object.Pair;
import zan.game.panel.IPanel;
import zan.game.sprite.TextManager;
import zan.game.util.CameraPort;
import zan.tecbot.object.*;

public class GamePanel implements IPanel {
	
	private boolean initialized;
	
	private GridMap gridMap;
	
	private Player gamePlayer;
	
	private Tecbot tecbot;
	private ArrayList<Block> blocks;
	private ArrayList<Bullet> bullets;
	private ArrayList<Pair> pairs;
	
	public GamePanel() {
		initialized = false;
		gridMap = null;
		gamePlayer = null;
		tecbot = null;
		blocks = new ArrayList<Block>();
		bullets = new ArrayList<Bullet>();
		pairs = new ArrayList<Pair>();
	}
	
	public boolean isInitialized() {return initialized;}
	
	public void init() {
		gridMap = new GridMap();
		gridMap.createMap(blocks);
		
		tecbot = new Tecbot();
		tecbot.setPos(100f, 200f);
		tecbot.setSize(100f);
		tecbot.spawn();
		
		gamePlayer = new Player(this, tecbot);
		
		initialized = true;
	}
	
	public ArrayList<Bullet> getBullets() {return bullets;}
	
	public void update() {
		// Input
		gamePlayer.input();
		
		// Update Objects
		if (tecbot.isActive()) tecbot.update();
		for (int i=0;i<bullets.size();i++) {
			if (i >= bullets.size()) break;
			if (bullets.get(i).isActive()) bullets.get(i).update();
			else {bullets.remove(i); i--;}
		}
		for (int i=0;i<blocks.size();i++) if (blocks.get(i).isActive()) blocks.get(i).update();
		
		// Create Collision Pairs
		if (tecbot.isActive()) tecbot.BlocksInRange(pairs, blocks);
		for (int i=0;i<bullets.size();i++) if (bullets.get(i).isActive()) bullets.get(i).BlocksInRange(pairs, blocks);
		
		// Resolve Collisions
		for (int i=0;i<pairs.size();i++) Collision.resolveCollision(pairs.get(i).pairA, pairs.get(i).pairB);
		pairs.clear();
		
		// Update Corrections
		if (tecbot.isActive()) tecbot.correction();
		for (int i=0;i<bullets.size();i++) if (bullets.get(i).isActive()) bullets.get(i).correction();
		for (int i=0;i<blocks.size();i++) if (blocks.get(i).isActive()) blocks.get(i).correction();
	}
	
	public void render() {
		CameraPort.viewDynamicCam(tecbot.getX(), tecbot.getY(), 0.4f);
		for (int i=0;i<blocks.size();i++) if (blocks.get(i).isActive()) blocks.get(i).render();
		for (int i=0;i<bullets.size();i++) if (bullets.get(i).isActive()) bullets.get(i).render();
		if (tecbot.isActive()) tecbot.render();
		
		float[] mp = GameCore.ScreenToLogic(InputManager.getMouseX(), InputManager.getMouseY());
		
		glDisable(GL_TEXTURE_2D);
		glPushMatrix();
		
		glTranslatef(mp[0], mp[1], 0f);
		glScalef(3f, 3f, 1f);
		
		glColor4f(0f, 1f, 0f, 1f);
		glBegin(GL_LINES);
			glVertex2f(-2f, -2f);
			glVertex2f(-2f, -1f);
			
			glVertex2f(-2f, 1f);
			glVertex2f(-2f, 2f);
			
			glVertex2f(-2f, 2f);
			glVertex2f(-1f, 2f);
			
			glVertex2f(1f, 2f);
			glVertex2f(2f, 2f);
			
			glVertex2f(2f, 2f);
			glVertex2f(2f, 1f);
			
			glVertex2f(2f, -1f);
			glVertex2f(2f, -2f);
			
			glVertex2f(2f, -2f);
			glVertex2f(1f, -2f);
			
			glVertex2f(-1f, -2f);
			glVertex2f(-2f, -2f);
			
			glVertex2f(-3f, 0f);
			glVertex2f(-1.5f, 0f);
			
			glVertex2f(0f, 3f);
			glVertex2f(0f, 1.5f);
			
			glVertex2f(3f, 0f);
			glVertex2f(1.5f, 0f);
			
			glVertex2f(0f, -3f);
			glVertex2f(0f, -1.5f);
		glEnd();
		glColor4f(1f, 1f, 1f, 1f);
		
		glPopMatrix();
		glEnable(GL_TEXTURE_2D);
		
		CameraPort.viewGUI();
		TextManager.renderText("FPS: " + GameCore.getFPS(), "defont", 5f, GameCore.GAME_HEIGHT - 5f, 10f, 6);
		TextManager.renderText("Gun Angle: " + tecbot.gunangle, "defont", 5f, GameCore.GAME_HEIGHT - 15f, 10f, 6);
		TextManager.renderText("Bullets: " + bullets.size(), "defont", 5f, GameCore.GAME_HEIGHT - 25f, 10f, 6);
	}
	
}
