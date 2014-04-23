package zan.tecbot.panel;

import static org.lwjgl.opengl.GL11.*;

import java.util.ArrayList;

import org.lwjgl.input.Mouse;

import zan.game.GameCore;
import zan.game.input.InputManager;
import zan.game.object.Collision;
import zan.game.object.Pair;
import zan.game.panel.IPanel;
import zan.game.sprite.TextManager;
import zan.game.util.CameraPort;
import zan.tecbot.object.block.Block;
import zan.tecbot.object.bullet.Bullet;
import zan.tecbot.object.entity.BaseEntity;
import zan.tecbot.object.entity.Tecbot;

public class GamePanel implements IPanel {
	
	private boolean initialized;
	
	private GridMap gridMap;
	
	private Player gamePlayer;
	
	private Tecbot tecbot;
	private ArrayList<BaseEntity> entities;
	private ArrayList<Block> blocks;
	private ArrayList<Bullet> bullets;
	private ArrayList<Pair> pairs;
	
	public GamePanel() {
		initialized = false;
		gridMap = null;
		gamePlayer = null;
		tecbot = null;
		entities = new ArrayList<BaseEntity>();
		blocks = new ArrayList<Block>();
		bullets = new ArrayList<Bullet>();
		pairs = new ArrayList<Pair>();
	}
	
	public boolean isInitialized() {return initialized;}
	
	public void init() {
		MapReader mapReader = new MapReader("map0.lgm");
		gridMap = new GridMap(mapReader.getMapData(), mapReader.getMapWidth(), mapReader.getMapHeight());
		gridMap.createMap(blocks, entities);
		
		tecbot = new Tecbot();
		tecbot.setPos(gridMap.getPlayerSpawn().getX(), gridMap.getPlayerSpawn().getY());
		tecbot.spawn();
		
		gamePlayer = new Player(this);
		
		InputManager.setMouseGrabbed(true);
		initialized = true;
	}
	
	public Tecbot getTecbot() {return tecbot;}
	public ArrayList<Bullet> getBullets() {return bullets;}
	
	public void update() {
		if (InputManager.isMouseGrabbed()) {
			// Input
			gamePlayer.input();
			
			// Update Objects
			if (tecbot.isActive()) tecbot.update();
			else {
				tecbot.setPos(gridMap.getPlayerSpawn().getX(), gridMap.getPlayerSpawn().getY());
				tecbot.spawn();
			}
			for (int i=0;i<entities.size();i++) {
				if (i >= entities.size()) break;
				if (entities.get(i).isActive()) entities.get(i).update();
				else {entities.remove(i); i--;}
			}
			for (int i=0;i<bullets.size();i++) {
				if (i >= bullets.size()) break;
				if (bullets.get(i).isActive()) bullets.get(i).update();
				else {bullets.remove(i); i--;}
			}
			for (int i=0;i<blocks.size();i++) if (blocks.get(i).isActive()) blocks.get(i).update();
			
			// Create Collision Pairs
			if (tecbot.isActive()) {tecbot.BlocksInRange(pairs, blocks); tecbot.EntitiesInRange(pairs, entities);}
			for (int i=0;i<entities.size();i++) if (entities.get(i).isActive()) entities.get(i).BlocksInRange(pairs, blocks);
			for (int i=0;i<bullets.size();i++) if (bullets.get(i).isActive()) {bullets.get(i).BlocksInRange(pairs, blocks); bullets.get(i).EntitiesInRange(pairs, entities);}
			
			// Resolve Collisions
			for (int i=0;i<pairs.size();i++) Collision.resolveCollision(pairs.get(i).pairA, pairs.get(i).pairB);
			pairs.clear();
			
			// Update Corrections
			if (tecbot.isActive()) tecbot.correction();
			for (int i=0;i<entities.size();i++) if (entities.get(i).isActive()) entities.get(i).correction();
			for (int i=0;i<bullets.size();i++) if (bullets.get(i).isActive()) bullets.get(i).correction();
			for (int i=0;i<blocks.size();i++) if (blocks.get(i).isActive()) blocks.get(i).correction();
		}
	}
	
	public void render() {
		CameraPort.viewDynamicCam(tecbot.getX(), tecbot.getY(), 0.4f);
		for (int i=0;i<blocks.size();i++) if (blocks.get(i).isActive()) blocks.get(i).render();
		for (int i=0;i<bullets.size();i++) if (bullets.get(i).isActive()) bullets.get(i).render();
		for (int i=0;i<entities.size();i++) if (entities.get(i).isActive()) entities.get(i).render();
		if (tecbot.isActive()) tecbot.render();
		
		float[] mp = GameCore.ScreenToLogic(InputManager.getMouseX(), InputManager.getMouseY());
		if (Mouse.isGrabbed()) {
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
		}
		
		CameraPort.viewGUI();
		TextManager.renderText("FPS: " + GameCore.getFPS(), "defont", 5f, GameCore.GAME_HEIGHT - 5f, 10f, 6);
		TextManager.renderText("Mouse Position: " + mp[0] + " " + mp[1], "defont", 5f, GameCore.GAME_HEIGHT - 15f, 10f, 6);
		TextManager.renderText("Bullets: " + bullets.size(), "defont", 5f, GameCore.GAME_HEIGHT - 25f, 10f, 6);
		TextManager.renderText("Enemies: " + entities.size(), "defont", 5f, GameCore.GAME_HEIGHT - 35f, 10f, 6);
		TextManager.renderText("Health: " + tecbot.getHealth() + " / " + tecbot.getMaxHealth(), "defont", 5f, GameCore.GAME_HEIGHT - 45f, 10f, 6);
		
		if (!Mouse.isGrabbed()) {
			glDisable(GL_TEXTURE_2D);
			glColor4f(0f, 0f, 0f, 0.5f);
			glBegin(GL_QUADS);
				glVertex2f(0f, 0f);
				glVertex2f(0f, GameCore.SCR_HEIGHT);
				glVertex2f(GameCore.SCR_WIDTH, GameCore.SCR_HEIGHT);
				glVertex2f(GameCore.SCR_WIDTH, 0f);
			glEnd();
			glEnable(GL_TEXTURE_2D);
			
			glColor4f(0f, 0.6f, 0.8f, 1f);
			TextManager.renderText("Click to resume game...", "defont", GameCore.GAME_HEIGHT*GameCore.getScreenRatio()/2f, GameCore.GAME_HEIGHT/2f, 12f, 1);
			glColor4f(1f, 1f, 1f, 1f);
		}
	}
	
}
