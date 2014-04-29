package zan.tecbot.panel;

import static org.lwjgl.opengl.GL11.*;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import zan.game.GameCore;
import zan.game.input.InputManager;
import zan.game.object.BaseObject;
import zan.game.object.Collision;
import zan.game.object.Pair;
import zan.game.panel.IPanel;
import zan.game.sprite.TextManager;
import zan.game.util.CameraPort;
import zan.tecbot.mechanism.GridMap;
import zan.tecbot.mechanism.Player;
import zan.tecbot.object.block.Block;
import zan.tecbot.object.bullet.Bullet;
import zan.tecbot.object.collectible.Collectible;
import zan.tecbot.object.entity.BaseEntity;
import zan.tecbot.object.entity.Tecbot;
import zan.tecbot.resource.MapReader;

public class GamePanel implements IPanel {
	
	private boolean initialized;
	
	private GridMap gridMap;
	
	private Player gamePlayer;
	
	private Tecbot tecbot;
	private ArrayList<BaseEntity> entities;
	private ArrayList<Block> blocks;
	private ArrayList<Bullet> bullets;
	private ArrayList<Collectible> collectibles;
	private ArrayList<Pair> pairs;
	
	private int exitCount;
	
	public GamePanel() {
		initialized = false;
		gridMap = null;
		gamePlayer = null;
		tecbot = null;
		entities = new ArrayList<BaseEntity>();
		blocks = new ArrayList<Block>();
		bullets = new ArrayList<Bullet>();
		collectibles = new ArrayList<Collectible>();
		pairs = new ArrayList<Pair>();
		exitCount = 100;
	}
	
	public boolean isInitialized() {return initialized;}
	
	public void init() {
		MapReader mapReader = new MapReader("map0.lgm");
		gridMap = new GridMap(mapReader.getMapDatas());
		tecbot = new Tecbot();
		gamePlayer = new Player(tecbot, bullets);
		
		gridMap.initPlayerSpawn();
		createMap();
		gamePlayer.spawn();
		
		InputManager.setMouseGrabbed(true);
		CameraPort.init();
		initialized = true;
	}
	public void destroy() {initialized = false;}
	
	public void createMap() {
		entities.clear();
		blocks.clear();
		bullets.clear();
		collectibles.clear();
		
		gridMap.changeMap();
		gridMap.createMap(gamePlayer, blocks, entities, bullets, collectibles);
	}
	
	public void endGame() {
		GameCore.changePanel(GameCore.Panel.TITLE);
	}
	
	public void update() {
		if (InputManager.isMouseGrabbed()) {
			// Input
			gamePlayer.input();
			
			// Update Objects
			if (tecbot.isActive()) tecbot.update();
			else gamePlayer.respawn();
			
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
			for (int i=0;i<blocks.size();i++) {
				if (i >= blocks.size()) break;
				if (blocks.get(i).isActive()) blocks.get(i).update();
				else {
					gridMap.removeBlock(blocks.get(i).getTileX(), blocks.get(i).getTileY());
					blocks.remove(i); i--;
				}
			}
			for (int i=0;i<collectibles.size();i++) {
				if (i >= collectibles.size()) break;
				if (collectibles.get(i).isActive()) collectibles.get(i).update();
				else {collectibles.remove(i); i--;}
			}
			
			// Create Collision Pairs
			if (tecbot.isActive()) {
				tecbot.ObjectsInRange(pairs, new ArrayList<BaseObject>(blocks));
				tecbot.ObjectsInRange(pairs, new ArrayList<BaseObject>(entities));
				tecbot.ObjectsInRange(pairs, new ArrayList<BaseObject>(bullets));
				tecbot.ObjectsInRange(pairs, new ArrayList<BaseObject>(collectibles));
			}
			for (int i=0;i<entities.size();i++) if (entities.get(i).isActive()) {
				entities.get(i).ObjectsInRange(pairs, new ArrayList<BaseObject>(blocks));
				entities.get(i).ObjectsInRange(pairs, new ArrayList<BaseObject>(bullets));
			}
			for (int i=0;i<bullets.size();i++) if (bullets.get(i).isActive()) {
				bullets.get(i).ObjectsInRange(pairs, new ArrayList<BaseObject>(blocks));
			}
			
			// Resolve Collisions
			for (int i=0;i<pairs.size();i++) Collision.resolveCollision(pairs.get(i).pairA, pairs.get(i).pairB);
			pairs.clear();
			
			// Update Corrections
			if (tecbot.isActive()) tecbot.correction();
			for (int i=0;i<entities.size();i++) if (entities.get(i).isActive()) entities.get(i).correction();
			for (int i=0;i<bullets.size();i++) if (bullets.get(i).isActive()) bullets.get(i).correction();
			for (int i=0;i<blocks.size();i++) if (blocks.get(i).isActive()) blocks.get(i).correction();
			for (int i=0;i<collectibles.size();i++) if (collectibles.get(i).isActive()) collectibles.get(i).correction();
			
			if (gridMap.requestMapChange()) {
				createMap();
				gamePlayer.telespawn();
			}
			if (gridMap.inExit()) {
				exitCount--;
				if (exitCount <= 0) endGame();
			} else exitCount = 100;
			if (gamePlayer.getPlayerLife() <= 0) endGame();
		} else {
			if (InputManager.isKeyPressed(Keyboard.KEY_Q)) endGame();
		}
	}
	
	public void render() {		
		CameraPort.viewDynamicCam(tecbot.getX(), tecbot.getY(), 0.4f);
		for (int i=0;i<blocks.size();i++) if (blocks.get(i).isActive()) blocks.get(i).render();
		for (int i=0;i<bullets.size();i++) if (bullets.get(i).isActive()) bullets.get(i).render();
		for (int i=0;i<entities.size();i++) if (entities.get(i).isActive()) entities.get(i).render();
		for (int i=0;i<collectibles.size();i++) if (collectibles.get(i).isActive()) collectibles.get(i).render();
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
		TextManager.renderText("Mouse Position: " + GridMap.getTileX(mp[0]) + " " + GridMap.getTileY(mp[1]), "defont", 5f, GameCore.GAME_HEIGHT - 15f, 10f, 6);
		TextManager.renderText("Enemies: " + entities.size(), "defont", 5f, GameCore.GAME_HEIGHT - 25f, 10f, 6);
		TextManager.renderText("Health: " + tecbot.getHealth() + " / " + tecbot.getMaxHealth() + " (Life: " + gamePlayer.getPlayerLife() + ")", "defont", 5f, GameCore.GAME_HEIGHT - 35f, 10f, 6);
		TextManager.renderText("Energy: " + gamePlayer.getEnergy() + " / " + gamePlayer.getMaxEnergy(), "defont", 5f, GameCore.GAME_HEIGHT - 45f, 10f, 6);
		TextManager.renderText("Load: " + gamePlayer.getEnergyLoad(), "defont", 5f, GameCore.GAME_HEIGHT - 55f, 10f, 6);
		if (gamePlayer.isBurnedOut()) glColor4f(1f, 0f, 0f, 0.8f);
		TextManager.renderText("Ammo: " + gamePlayer.getAmmo() + " / " + gamePlayer.getMaxAmmo(), "defont", 5f, GameCore.GAME_HEIGHT - 65f, 10f, 6);
		glColor4f(1f, 1f, 1f, 1f);
		String weaponname = "None";
		if (gamePlayer.getWeapon() == 1) weaponname = "Plasma Cannon";
		else if (gamePlayer.getWeapon() == 2) weaponname = "Gatling Gun";
		TextManager.renderText("Weapon: " + weaponname, "defont", 5f, GameCore.GAME_HEIGHT - 75f, 10f, 6);
		
		if (Mouse.isGrabbed()) {
			if (exitCount < 100) {
				glDisable(GL_TEXTURE_2D);
				glColor4f(0f, 0f, 0f, (100-exitCount)/100f);
				glBegin(GL_QUADS);
					glVertex2f(0f, 0f);
					glVertex2f(0f, GameCore.SCR_HEIGHT);
					glVertex2f(GameCore.SCR_WIDTH, GameCore.SCR_HEIGHT);
					glVertex2f(GameCore.SCR_WIDTH, 0f);
				glEnd();
				glEnable(GL_TEXTURE_2D);
			}
		}
		
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
