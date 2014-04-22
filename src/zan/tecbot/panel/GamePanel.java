package zan.tecbot.panel;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector2f;

import zan.game.object.Collision;
import zan.game.object.Pair;
import zan.game.panel.IPanel;
import zan.tecbot.object.*;

public class GamePanel implements IPanel {
	
	private boolean initialized;
	
	private GridMap gridMap;
	
	private Tecbot tecbot;
	private ArrayList<Block> blocks;
	private ArrayList<Pair> pairs;
	
	public GamePanel() {
		initialized = false;
		gridMap = new GridMap();
		blocks = new ArrayList<Block>();
		pairs = new ArrayList<Pair>();
	}
	
	public boolean isInitialized() {return initialized;}
	
	public void init() {
		tecbot = new Tecbot();
		tecbot.setPos(100f, 200f);
		tecbot.setSize(100f);
		tecbot.spawn();
		
		gridMap.createMap(blocks);
		for (int i=0;i<blocks.size();i++) if (!blocks.get(i).isActive()) blocks.get(i).spawn();
		//for (int i=0;i<blocks.size();i++) {pairs.add(new Pair(tecbot, blocks.get(i)));}
		
		initialized = true;
	}
	
	public void update() {
		if (tecbot.isActive()) tecbot.update();
		for (int i=0;i<blocks.size();i++) {if (blocks.get(i).isActive()) blocks.get(i).update();}
		
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
		for (int i=0;i<blocks.size();i++) if (blocks.get(i).isActive()) blocks.get(i).render();
		if (tecbot.isActive()) tecbot.render();
	}
	
}
