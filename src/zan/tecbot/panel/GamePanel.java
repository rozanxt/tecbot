package zan.tecbot.panel;

import static org.lwjgl.opengl.GL11.*;

import java.util.ArrayList;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector2f;

import zan.game.GameCore;
import zan.game.object.Collision;
import zan.game.object.Pair;
import zan.game.panel.IPanel;
import zan.game.sprite.TextManager;
import zan.game.util.GameUtility;
import zan.tecbot.object.*;

public class GamePanel implements IPanel {
	
	private boolean initialized;
	
	private GridMap gridMap;
	
	private Tecbot tecbot;
	private ArrayList<Block> blocks;
	private ArrayList<Bullet> bullets;
	private ArrayList<Pair> pairs;
	
	public GamePanel() {
		initialized = false;
		gridMap = new GridMap();
		blocks = new ArrayList<Block>();
		bullets = new ArrayList<Bullet>();
		pairs = new ArrayList<Pair>();
	}
	
	public boolean isInitialized() {return initialized;}
	
	public void init() {
		tecbot = new Tecbot();
		tecbot.setPos(100f, 200f);
		tecbot.setSize(100f);
		tecbot.spawn();
		
		gridMap.createMap(blocks);
		//for (int i=0;i<blocks.size();i++) if (!blocks.get(i).isActive()) blocks.get(i).spawn();
		//for (int i=0;i<blocks.size();i++) {pairs.add(new Pair(tecbot, blocks.get(i)));}
		
		initialized = true;
	}
	
	public void update() {
		
		if (Mouse.isButtonDown(0)) {
			float gunangle=tecbot.gunangle+GameUtility.getRnd().nextInt(20)*0.1f-1f;
			Bullet b = new Bullet();
			b.setPos(tecbot.getX()+(float)Math.cos(gunangle*(Math.PI/180f))*30f, tecbot.getY()-(float)Math.sin(gunangle*(Math.PI/180f))*30f);
			b.setVel((float)Math.cos(gunangle*(Math.PI/180f))*10f, -(float)Math.sin(gunangle*(Math.PI/180f))*10f);
			b.setSize(10f);
			b.setAngle(gunangle);
			b.spawn();
			bullets.add(b);
		}
		
		if (tecbot.isActive()) tecbot.update();
		for (int i=0;i<blocks.size();i++) {if (blocks.get(i).isActive()) blocks.get(i).update();}
		for (int i=0;i<bullets.size();i++) {
			if (i >= bullets.size()) break;
			if (bullets.get(i).isActive()) bullets.get(i).update();
			else {
				bullets.remove(i);
				i--;
			}
		}
		
		ArrayList<Block> inrange = new ArrayList<Block>();
		ArrayList<Float> distinrange = new ArrayList<Float>();
		for (int i=0;i<blocks.size();i++) {
			Vector2f dist = Vector2f.sub(tecbot.getPos(), blocks.get(i).getPos(), null);
			if (dist.lengthSquared() < 10000f) {
				blocks.get(i).color();
				inrange.add(blocks.get(i));
				distinrange.add(dist.length());
			}
		}
		while (distinrange.size() > 0) {
			float bestDist = Float.MAX_VALUE;
			int next = 0;
			for (int i=0;i<distinrange.size();i++) {
				if (distinrange.get(i) < bestDist) {
					bestDist = distinrange.get(i);
					next = i;
				}
			}
			pairs.add(new Pair(tecbot, inrange.get(next)));
			inrange.remove(next);
			distinrange.remove(next);
		}
		for (int i=0;i<pairs.size();i++) Collision.resolveCollision(pairs.get(i).pairA, pairs.get(i).pairB);
		pairs.clear();
		
		if (tecbot.isActive()) tecbot.correction();
		for (int i=0;i<blocks.size();i++) {if (blocks.get(i).isActive()) blocks.get(i).correction();}
	}
	
	public void render() {
		
		float vx = tecbot.getX()-(GameCore.GAME_WIDTH/2f)-GameCore.ScreenToLogic(GameCore.getScreenCrop()[0], GameCore.getScreenCrop()[1])[0];
		float vy = tecbot.getY()-(GameCore.GAME_HEIGHT/2f);
		float vw = GameCore.GAME_HEIGHT*GameCore.getScreenRatio();
		float vh = GameCore.GAME_HEIGHT;
		
		float ox = (Mouse.getX()-(GameCore.SCR_WIDTH/2f))*0.5f;
		float oy = (Mouse.getY()-(GameCore.SCR_HEIGHT/2f))*0.5f;
		
		if (Math.abs(ox) > 200f) ox = ox*200f/Math.abs(ox);
		if (Math.abs(oy) > 150f) oy = oy*150f/Math.abs(oy);
		
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0f, vw, 0f, vh, -1f, 1f);
		glTranslatef(-vx-ox, -vy-oy, 0f);
		glMatrixMode(GL_MODELVIEW);
		
		for (int i=0;i<blocks.size();i++) if (blocks.get(i).isActive()) blocks.get(i).render();
		for (int i=0;i<bullets.size();i++) {if (bullets.get(i).isActive()) bullets.get(i).render();}
		if (tecbot.isActive()) tecbot.render();
		
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0f, vw, 0f, vh, -1f, 1f);
		glMatrixMode(GL_MODELVIEW);
		
		TextManager.renderText("FPS: " + GameCore.getFPS(), "defont", 5f, GameCore.GAME_HEIGHT - 5f, 10f, 6);
		TextManager.renderText("Gun Angle: " + tecbot.gunangle, "defont", 5f, GameCore.GAME_HEIGHT - 15f, 10f, 6);
		TextManager.renderText("Bullets: " + bullets.size(), "defont", 5f, GameCore.GAME_HEIGHT - 25f, 10f, 6);
	}
	
}
